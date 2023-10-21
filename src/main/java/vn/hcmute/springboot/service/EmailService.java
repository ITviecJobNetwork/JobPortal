package vn.hcmute.springboot.service;

import jakarta.mail.MessagingException;
import vn.hcmute.springboot.model.User;

public interface EmailService {
  void sendOtpToEmail(String email, String otp) throws MessagingException;

  void sendNewPasswordToEmail(String email, String newPassword) throws MessagingException;

}
