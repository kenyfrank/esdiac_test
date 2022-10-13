/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kene.esdiactest.config.interceptor;

import com.kene.esdiactest.model.PortalUser;
import com.kene.esdiactest.service.AuthService;
import com.kene.esdiactest.service.PortalUserService;
import com.kene.esdiactest.service.RequestPrincipal;
import com.kene.esdiactest.serviceimpl.RequestPrincipalImpl;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@Component
public class RemoteAddressConstraintHandlerInterceptor implements HandlerInterceptor {

    AuthService authService;

    @Inject
    ApplicationContext applicationContext;

    @Inject
    PortalUserService userService;


    public RemoteAddressConstraintHandlerInterceptor(ApplicationContext applicationContext) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String ipAddress = request.getRemoteAddr();

        if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
            ipAddress = StringUtils.defaultIfBlank(
                    request.getHeader("X-FORWARDED-FOR"),
                    request.getRemoteAddr());
        }

        try {

                String authHeader = StringUtils.defaultString(request.getHeader(HttpHeaders.AUTHORIZATION), "");
                if (StringUtils.isBlank(authHeader)) {
                    authHeader = StringUtils.defaultIfBlank(request.getParameter("access_token"), "");
                    if (StringUtils.isNotBlank(authHeader)) {
                        authHeader = "Bearer " + authHeader;
                    }
                }
                if (StringUtils.isNotBlank(authHeader)) {
                    JWTClaimsSet jwtClaimsSet = null;
                    Map<String, Object> claims = null;

                    jwtClaimsSet = authService.decodeToken(authHeader);
                    claims = jwtClaimsSet.getClaims();


                    RequestAttributes currentRequestAttributes = RequestContextHolder.currentRequestAttributes();
                        PortalUser user = userService.getPortalUser(Long.valueOf(jwtClaimsSet.getSubject()));
                        assert user != null;

                        RequestPrincipalImpl requestPrincipal = new RequestPrincipalImpl(user.getUserId(),
                                StringUtils.defaultIfBlank(request.getHeader("X-FORWARDED-FOR"), request.getRemoteAddr())) {

                            @Override
                            public String getUserName() {
                                return user.getUsername();
                            }
                        };
                        applicationContext.getAutowireCapableBeanFactory().autowireBean(requestPrincipal);
                        currentRequestAttributes.setAttribute(PortalUser.class.getName(),
                                user,
                                RequestAttributes.SCOPE_REQUEST);
                        currentRequestAttributes.setAttribute(RequestPrincipal.class.getName(),
                                requestPrincipal,
                                RequestAttributes.SCOPE_REQUEST);

                    }else{
                        response.setStatus(403);
                        response.getWriter().append("Forbidden");
                        return false;
                    }


        } catch (IllegalStateException e) {
            log.error(e.getMessage(), e);
        }

        return true;
    }

    public static FactoryBean<RequestPrincipal> requestPrincipal() {
        return new FactoryBean<RequestPrincipal>() {

            public RequestPrincipal getObject() {
                return (RequestPrincipal) RequestContextHolder.currentRequestAttributes().getAttribute(RequestPrincipal.class.getName(),
                        RequestAttributes.SCOPE_REQUEST);
            }

            @Override
            public Class<?> getObjectType() {
                return RequestPrincipal.class;
            }

            @Override
            public boolean isSingleton() {
                return false;
            }
        };
    }
}
