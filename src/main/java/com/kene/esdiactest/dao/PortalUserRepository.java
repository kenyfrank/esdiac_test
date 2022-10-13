package com.kene.esdiactest.dao;

import com.kene.esdiactest.model.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {

    Optional<PortalUser> findByUserId(String userId);
    Optional<PortalUser> findByUsername(String username);
    Optional<PortalUser> findByEmail(String email);
    Optional<PortalUser> findByUsernameAndEmail(String username, String email);
}
