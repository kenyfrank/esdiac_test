package com.kene.esdiactest.serviceimpl;

import com.kene.esdiactest.dao.PortalUserRepository;
import com.kene.esdiactest.model.PortalUser;
import com.kene.esdiactest.service.PortalUserService;
import com.kene.esdiactest.service.RequestPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public class RequestPrincipalImpl implements RequestPrincipal {

    private final String userId;
    private final String ipAddress;
    private PortalUser portalUser;

    @Autowired
    private PortalUserService userService;

    @Autowired
    private PortalUserRepository portalUserRepository;

    public RequestPrincipalImpl(String userId, String ipAddress) {
        this.userId = userId;
        this.ipAddress = ipAddress;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }


    @Override
    @Transactional
    public PortalUser getPortalUser() {
        if (portalUser == null) {
            portalUser = portalUserRepository.findByUserId(this.userId).orElse(null);
        }
        return portalUser;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

}
