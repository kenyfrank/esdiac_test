package com.kene.esdiactest.controller;

import com.kene.esdiactest.dto.AuthRequest;
import com.kene.esdiactest.service.AuthService;
import com.nimbusds.jose.JOSEException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;

@RestController
public class AuthenticationController {

    @Inject
    private AuthService authService;

    @PostMapping("token")
    public ResponseEntity<?> auth(@RequestBody @Valid AuthRequest authRequest) throws IOException, JOSEException {
        return authService.authenticate(authRequest);
    }
}
