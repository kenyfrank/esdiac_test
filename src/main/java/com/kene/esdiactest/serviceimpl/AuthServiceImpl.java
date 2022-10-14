package com.kene.esdiactest.serviceimpl;

import com.kene.esdiactest.dao.PortalUserRepository;
import com.kene.esdiactest.dto.AuthRequest;
import com.kene.esdiactest.dto.AuthToken;
import com.kene.esdiactest.dto.User;
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
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.Date;

@Slf4j
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
    public String generateAccessToken(PortalUser user) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getUserId(), user.getUsername()))
                .setIssuer("CodeJava")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRY))
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
                .compact();

    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("JWT expired", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Token is null, empty or only whitespace", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("JWT is not supported", ex);
        } catch (SignatureException ex) {
            log.error("Signature validation failed");
        }

        return false;
    }

    @Override
    public String getUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.split(" ")[1].trim();
        String[] jwtSubject = getSubject(token).split(",");
        if (jwtSubject.length > 0) {
            return jwtSubject[0];
        }
        return null;
    }

    @Override
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    /* Below methods are deprecated*/
    @Override
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

}
