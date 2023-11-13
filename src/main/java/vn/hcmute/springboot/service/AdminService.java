package vn.hcmute.springboot.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hcmute.springboot.model.Admin;
import vn.hcmute.springboot.model.Recruiters;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.response.JwtResponse;

import java.io.IOException;

public interface AdminService {
  JwtResponse loginAdmin(LoginRequest request);
  void saveAdminToken(Admin admin, String token);

  void revokeAllAdminTokens(Admin admin);
  void refreshToken(HttpServletRequest request,
                    HttpServletResponse response) throws IOException;

  void changePassword(String currentPassword, String newPassword, String confirmPassword);

  void changeFullName(String fullName);

}
