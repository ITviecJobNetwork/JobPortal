package vn.hcmute.springboot.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface UserService {
  String sendNewPasswordToEmail(String email);
  String changePassword(String currentPassword, String newPassword, String confirmPassword);

}
