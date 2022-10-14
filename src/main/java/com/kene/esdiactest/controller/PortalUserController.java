package com.kene.esdiactest.controller;

import com.kene.esdiactest.config.ErrorResponse;
import com.kene.esdiactest.dao.PortalUserRepository;
import com.kene.esdiactest.dto.UserDetailsPojo;
import com.kene.esdiactest.dto.UserRegistrationDto;
import com.kene.esdiactest.model.PortalUser;
import com.kene.esdiactest.model.enumeration.GenericStatusConstant;
import com.kene.esdiactest.service.AuthService;
import com.kene.esdiactest.service.PortalUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("user")
public class PortalUserController {

    @Autowired
    private PortalUserRepository portalUserRepository;
    @Autowired
    private PortalUserService portalUserService;
    @Autowired
    private AuthService authService;

    @Transactional
    @GetMapping("/me")
    public ResponseEntity<UserDetailsPojo> getCurrentUser(HttpServletRequest request) {
        String userId = authService.getUserIdFromRequest(request);

        PortalUser portalUser = portalUserRepository.findByUserId(userId).orElse(null);
        if (portalUser == null) {
            throw new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found");
        }
        if (!portalUser.getStatus().equals(GenericStatusConstant.ACTIVE)){
            throw new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "User is not active");
        }
        return ResponseEntity.ok().body(portalUserService.fetchUserDetails(portalUser.getUserId()));
    }


    @PostMapping("/create")
    public ResponseEntity<PortalUser> createUser(@RequestBody @Valid UserRegistrationDto request) {
        log.info("About to create user...");
        return ResponseEntity.status(HttpStatus.CREATED).body(portalUserService.createPortalUser(request));
    }
}
