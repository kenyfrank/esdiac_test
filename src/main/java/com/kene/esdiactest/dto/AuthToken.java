package com.kene.esdiactest.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthToken {
    String token;
    String expiresIn;
    String userId;
    String role;
}
