package com.kene.esdiactest.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDetailsPojo {

    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private LocalDateTime dateCreated;
    private String status;
}
