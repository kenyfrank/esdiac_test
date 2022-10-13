package com.kene.esdiactest.service;

import com.kene.esdiactest.dto.AuthRequest;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.ParseException;

public interface AuthService {
    JWTClaimsSet decodeToken(String authHeader)throws JOSEException, ParseException;

    ResponseEntity<?> authenticate(AuthRequest authRequest) throws IOException, JOSEException;

    JWTClaimsSet decodeTokenWithoutVerification(String authHeader) throws JOSEException, ParseException;
}
