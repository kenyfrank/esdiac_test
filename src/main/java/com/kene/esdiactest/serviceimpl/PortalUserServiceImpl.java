package com.kene.esdiactest.serviceimpl;

import com.kene.esdiactest.config.ErrorResponse;
import com.kene.esdiactest.dao.PortalUserRepository;
import com.kene.esdiactest.dto.UserDetailsPojo;
import com.kene.esdiactest.dto.UserRegistrationDto;
import com.kene.esdiactest.model.PortalUser;
import com.kene.esdiactest.service.PortalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PortalUserServiceImpl implements PortalUserService {

    @Autowired
    private PortalUserRepository portalUserRepository;

    @Transactional
    @Override
    public PortalUser createPortalUser(UserRegistrationDto request) {
        if (portalUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ErrorResponse(HttpStatus.CONFLICT.value(), "Username already exists");
        }
        if (portalUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ErrorResponse(HttpStatus.CONFLICT.value(), "Email is already used");
        }
        PortalUser portalUser = new PortalUser();
        portalUser.setUserId(UUID.randomUUID().toString());
        portalUser.setUsername(request.getUsername());
        portalUser.setFirstName(request.getFirstName());
        portalUser.setLastName(request.getLastName());
        portalUser.setEmail(request.getEmail());
        portalUser.setDateCreated(LocalDateTime.now());
        portalUser.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        portalUserRepository.save(portalUser);
        return portalUser;
    }

    @Override
    public UserDetailsPojo fetchUserDetails(String userId) {
        UserDetailsPojo pojo = new UserDetailsPojo();
        portalUserRepository.findByUserId(userId).ifPresent(it -> {
            pojo.setUserId(it.getUserId());
            pojo.setUsername(it.getUsername());
            pojo.setFirstName(it.getFirstName());
            pojo.setLastName(it.getLastName());
            pojo.setEmail(it.getEmail());
            pojo.setAddress(it.getAddress());
            pojo.setDateCreated(it.getDateCreated());
            pojo.setStatus(it.getStatus().toString());
        });
        return pojo;
    }

    @Override
    public PortalUser getPortalUser(Long id) {
        return portalUserRepository.findById(id).orElse(null);
    }
}
