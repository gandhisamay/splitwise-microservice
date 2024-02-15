package com.example.splitwise.models.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonSerialize
public class SplitUserCompressed {

    private int userId;
    private String name;
    private String email;
}
