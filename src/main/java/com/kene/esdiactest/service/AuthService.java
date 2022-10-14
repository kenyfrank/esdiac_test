package com.kene.esdiactest.service;

import com.kene.esdiactest.dto.AuthRequest;
import com.kene.esdiactest.dto.User;
import com.kene.esdiactest.model.PortalUser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public interface AuthService {
    String generateAccessToken(PortalUser user);
    boolean validateAccessToken(String token);
    String getUserIdFromRequest(HttpServletRequest request);
    String getSubject(String token);
    /* Below methods are deprecated*/
    String createToken(String subject, Date expirationDate) throws JOSEException;
    JWTClaimsSet decodeToken(String authHeader)throws JOSEException, ParseException;
    ResponseEntity<?> authenticate(AuthRequest authRequest) throws IOException, JOSEException;
    JWTClaimsSet decodeTokenWithoutVerification(String authHeader) throws JOSEException, ParseException;
}
