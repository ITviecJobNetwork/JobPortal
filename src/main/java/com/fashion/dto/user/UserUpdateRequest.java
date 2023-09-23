package com.fashion.dto.user;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class UserUpdateRequest {
    private Long id;

    @NotBlank
    @Email
    private String email;

    private String phone;
    private String fullName;
    private String address;
}
