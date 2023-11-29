package vn.hcmute.springboot.service;

import jakarta.mail.MessagingException;
import vn.hcmute.springboot.model.ApplicationForm;
import vn.hcmute.springboot.model.JobStatus;
import vn.hcmute.springboot.model.UserStatus;

public interface EmailService {
  void sendOtpToEmail(String fullName,String email, String otp) throws MessagingException;

  void sendNewPasswordToEmail(String fullName,String email, String newPassword) throws MessagingException;

  void sendConfirmRegistrationToRecruiter(String email,String password) throws MessagingException;

  void sendApplicationUpdateEmail(ApplicationForm applicationForm,String reason) throws MessagingException;

  void sendReasonDeActiveUser(String email, String reason, UserStatus status) throws MessagingException;

  void sendReasonToActiveFromUser(String userName,String adminEmail) throws MessagingException;


  void sendEmailActiveFromAdmin(String email) throws MessagingException;

  void sendEmailUpdateStatusPostJobForRecruiter(String email, JobStatus status) throws MessagingException;


}
