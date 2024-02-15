package com.example.splitwise.models.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpResponse {
    private String email;
    private String token;
}
