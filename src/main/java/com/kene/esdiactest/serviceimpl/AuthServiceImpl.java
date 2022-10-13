package com.kene.esdiactest.serviceimpl;

import com.kene.esdiactest.dao.PortalUserRepository;
import com.kene.esdiactest.dto.AuthRequest;
import com.kene.esdiactest.dto.AuthToken;
import com.kene.esdiactest.model.PortalUser;
import com.kene.esdiactest.service.AuthService;
import com.kene.esdiactest.service.PortalUserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);

    @Inject
    private PortalUserService userService;
    @Inject
    private PortalUserRepository portalUserRepository;

    @Value("${jwt.token.secret}")
    private String TOKEN_SECRET;

    @Value("${jwt.accessTokenValidityInMilliseconds}")
    private Long TOKEN_EXPIRY;

    @Override
    public JWTClaimsSet decodeToken(String authHeader) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(getSerializedToken(authHeader));
        if (signedJWT.verify(new MACVerifier(TOKEN_SECRET))) {

            if (signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date())) {
                throw new JOSEException("Token Expired");
            }
            return signedJWT.getJWTClaimsSet();
        } else {
            throw new JOSEException("Signature verification failed");
        }
    }

    @Override
    public ResponseEntity<?> authenticate(AuthRequest authRequest) throws IOException, JOSEException {
        JWTClaimsSet jwtClaimsSet = null;
        Date expirationDate = new Date(TOKEN_EXPIRY);
        try {
            jwtClaimsSet = decodeTokenWithoutVerification(authRequest.getToken());
        } catch (JOSEException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (jwtClaimsSet != null) {
            expirationDate = jwtClaimsSet.getExpirationTime();
        }

        String username = "";
        PortalUser portalUser = portalUserRepository.findByUsername(username).orElse(null);

        AuthToken authToken = null;
        if (portalUser != null) {
            authToken = this.generateToken(portalUser, expirationDate);
        }
        return ResponseEntity.ok().body(authToken);
    }

    @Override
    public JWTClaimsSet decodeTokenWithoutVerification(String authHeader) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(authHeader);

        if (signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date())) {
            throw new JOSEException("Token Expired");
        }
        return signedJWT.getJWTClaimsSet();
    }

    private String getSerializedToken(String authHeader) {
        return authHeader.split(" ")[1];
    }

    public AuthToken generateToken(PortalUser user, Date expirationDate) throws JOSEException {

        String accessToken = this.createToken(user.getId().toString(), expirationDate);

        return AuthToken.builder()
                .token(accessToken)
                .expiresIn(String.valueOf(expirationDate.getTime()))
                .userId(user.getUserId())
                .build();
    }

    public String createToken(String subject, Date expirationDate) throws JOSEException {
        JWTClaimsSet.Builder claimBuilder = new JWTClaimsSet.Builder();
        claimBuilder.expirationTime(expirationDate);
        claimBuilder.issueTime(new Date());
        // claimBuilder.issuer(host);
        claimBuilder.subject(String.valueOf(subject));
        JWTClaimsSet claimsSet = claimBuilder.build();
        JWSSigner signer = new MACSigner(TOKEN_SECRET);
        SignedJWT jwt = new SignedJWT(JWT_HEADER, claimsSet);
        jwt.sign(signer);

        return jwt.serialize();
    }

}
