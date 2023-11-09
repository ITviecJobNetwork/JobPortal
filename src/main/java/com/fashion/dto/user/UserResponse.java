package com.fashion.dto.user;

import com.fashion.constant.RoleConstant;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Data
@Getter
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Date createdDate;
    private Boolean active;
    private RoleConstant role;
    private Boolean isOtp;
}
