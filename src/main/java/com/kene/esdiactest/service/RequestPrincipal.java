package com.kene.esdiactest.service;



import com.kene.esdiactest.model.PortalUser;

public interface RequestPrincipal {

    String getUserId();

    default String getUserName() {
        return null;
    }

    default String getIpAddress() {
        return null;
    }

    boolean isAuthenticated();

    default PortalUser getPortalUser() {
        return null;
    }

    enum PrincipalType {
        GUEST, USER, CLIENT
    }
}
