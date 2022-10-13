package com.kene.esdiactest.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AuthLoginRequest {

    @NotNull @Length(min = 4, max = 50)
    private String username;

    @NotNull @Length(min = 8, max = 25)
    private String password;
}
