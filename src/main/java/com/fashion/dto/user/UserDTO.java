package com.fashion.dto.user;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class UserDTO {

    @NotBlank(message = "Tên không được trống")
    private String firstName;

    @NotBlank(message = "Họ không được trống")
    private String lastName;

    @NotBlank(message = "Email không được trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được trống")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được trống")
    private String repeatPassword;
}
