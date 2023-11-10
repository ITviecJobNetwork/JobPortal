package vn.hcmute.springboot.service;

import jakarta.mail.MessagingException;
import vn.hcmute.springboot.model.ApplicationForm;

public interface EmailService {
  void sendOtpToEmail(String email, String otp) throws MessagingException;

  void sendNewPasswordToEmail(String email, String newPassword) throws MessagingException;

  void sendConfirmRegistrationToRecruiter(String email,String password) throws MessagingException;

  void sendApplicationUpdateEmail(ApplicationForm applicationForm) throws MessagingException;


}
