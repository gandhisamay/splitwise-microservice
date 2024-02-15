package com.example.splitwise.models;

import lombok.Data;
import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus statusCode, String message) {
}
