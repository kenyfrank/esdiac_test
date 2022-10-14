package com.kene.esdiactest.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AuthLoginRequest {

    @NotNull(message = "username cannot be blank")
    private String username;

    @NotNull(message = "password cannot be blank")
    private String password;
}
