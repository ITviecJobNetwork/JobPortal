package com.fashion.dto.user;

import lombok.Data;

import java.util.Date;

@Data
public class OtpDTO {
    private String email;
    private String otp;
    private Date createdDate = new Date();
}
