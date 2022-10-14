package com.kene.esdiactest.controller;

import com.kene.esdiactest.config.ErrorResponse;
import com.kene.esdiactest.dao.PortalUserRepository;
import com.kene.esdiactest.dto.AuthLoginRequest;
import com.kene.esdiactest.dto.AuthRequest;
import com.kene.esdiactest.dto.AuthToken;
import com.kene.esdiactest.dto.User;
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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AuthenticationController {

    @Inject
    private AuthService authService;
    @Inject
    private AuthenticationManager authManager;
    @Inject
    private PortalUserRepository portalUserRepository;

    @PostMapping("token")
    public ResponseEntity<?> auth(@RequestBody @Valid AuthRequest authRequest) throws IOException, JOSEException {
        return authService.authenticate(authRequest);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthToken> login(@RequestBody @Valid AuthLoginRequest request) {
        try {
            log.info("Attempting auth login....");
            PortalUser portalUser = portalUserRepository.findByUsername(request.getUsername()).orElseThrow(()
                    -> new ErrorResponse(HttpStatus.NOT_FOUND, "Invalid username or password"));

            if(!BCrypt.checkpw(request.getPassword(), portalUser.getPassword())) {
                throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid username or password");
            }
//            Authentication authentication = authManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getUsername(), request.getPassword())
//            );
            log.info("authentication after... ");

//            User user = (User) authentication.getPrincipal();
            String accessToken = authService.generateAccessToken(portalUser);
            AuthToken response = AuthToken.builder()
                    .token(accessToken)
                    .userId(portalUser.getUserId())
                    .build();


            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
