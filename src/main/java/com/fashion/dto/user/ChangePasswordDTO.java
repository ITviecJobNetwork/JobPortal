package com.fashion.dto.user;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ChangePasswordDTO {

    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String repeatPassword;
}
