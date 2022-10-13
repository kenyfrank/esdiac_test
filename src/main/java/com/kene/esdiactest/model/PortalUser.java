package com.kene.esdiactest.model;

import com.kene.esdiactest.model.enumeration.GenericStatusConstant;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
@Table(name = "PORTAL_USER")
public class PortalUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "DATE_CREATED", nullable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "ADDRESS", nullable = true)
    private String address;

    @Basic(optional = false)
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    protected GenericStatusConstant status = GenericStatusConstant.ACTIVE;

    @Column(name = "DATE_DEACTIVATED")
    protected LocalDateTime dateDeactivated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GenericStatusConstant getStatus() {
        return status;
    }

    public void setStatus(GenericStatusConstant status) {
        this.status = status;
    }

    public LocalDateTime getDateDeactivated() {
        return dateDeactivated;
    }

    public void setDateDeactivated(LocalDateTime dateDeactivated) {
        this.dateDeactivated = dateDeactivated;
    }

}
