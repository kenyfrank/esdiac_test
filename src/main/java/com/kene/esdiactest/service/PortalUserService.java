package com.kene.esdiactest.service;

import com.kene.esdiactest.dto.UserDetailsPojo;
import com.kene.esdiactest.dto.UserRegistrationDto;
import com.kene.esdiactest.model.PortalUser;

public interface PortalUserService {
    PortalUser createPortalUser(UserRegistrationDto request);
    UserDetailsPojo fetchUserDetails(String userId);
    PortalUser getPortalUser(Long id);
}
