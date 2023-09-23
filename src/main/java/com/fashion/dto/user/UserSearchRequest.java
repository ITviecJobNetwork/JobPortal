package com.fashion.dto.user;

import lombok.Data;

@Data
public class UserSearchRequest {
    private String email;
    private Boolean active;
}
