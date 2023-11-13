package vn.hcmute.springboot.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import vn.hcmute.springboot.model.Admin;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.UserProfileResponse;
import vn.hcmute.springboot.response.UserResponse;

import java.io.IOException;

public interface AdminService {
  JwtResponse loginAdmin(LoginRequest request);
  void saveAdminToken(Admin admin, String token);

  void revokeAllAdminTokens(Admin admin);
  void refreshToken(HttpServletRequest request,
                    HttpServletResponse response) throws IOException;

  void changePassword(String currentPassword, String newPassword, String confirmPassword);

  void changeFullName(String fullName);
  Page<UserResponse> getAllUser(int page, int size);

  UserProfileResponse getUserById(Integer id);

  void deActiveUser(Integer id,String reason, UserStatus status) throws MessagingException;

}
