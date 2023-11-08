package vn.hcmute.springboot.service;

import jakarta.mail.MessagingException;

public interface EmailService {
  void sendOtpToEmail(String email, String otp) throws MessagingException;

  void sendNewPasswordToEmail(String email, String newPassword) throws MessagingException;

  void sendConfirmRegistrationToRecruiter(String email,String password) throws MessagingException;


}
