package com.kene.esdiactest.controller;

import com.kene.esdiactest.dto.AuthLoginRequest;
import com.kene.esdiactest.dto.AuthRequest;
import com.kene.esdiactest.dto.AuthToken;
import com.kene.esdiactest.model.PortalUser;
import com.kene.esdiactest.service.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
public class AuthenticationController {

    @Inject
    private AuthService authService;
    @Inject
    private AuthenticationManager authManager;

    @PostMapping("token")
    public ResponseEntity<?> auth(@RequestBody @Valid AuthRequest authRequest) throws IOException, JOSEException {
        return authService.authenticate(authRequest);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthLoginRequest request) {
        try {
            log.info("Attempting auth login....");
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            PortalUser user = (PortalUser) authentication.getPrincipal();
            String accessToken = authService.generateAccessToken(user);
            AuthToken response = AuthToken.builder()
                    .token(accessToken)
                    .userId(user.getUserId())
                    .build();


            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }
}
