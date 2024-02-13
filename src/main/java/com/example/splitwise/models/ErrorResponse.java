package com.example.splitwise.models;

import lombok.Data;

public record ErrorResponse(int statusCode, String message) {
}
