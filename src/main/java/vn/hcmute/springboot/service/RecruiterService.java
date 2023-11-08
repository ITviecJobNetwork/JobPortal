package vn.hcmute.springboot.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.hcmute.springboot.model.Recruiters;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;

public interface RecruiterService {
  MessageResponse registerRecruiter(RecruiterRegisterRequest recruiterRegisterRequest);

  JwtResponse loginRecruiter(LoginRequest request);
  void saveUserToken(Recruiters recruiter, String token);

  void revokeAllUserTokens(Recruiters recruiter);
  void refreshToken(HttpServletRequest request,
      HttpServletResponse response) throws IOException;

  void changePassword(String currentPassword, String newPassword, String confirmPassword);

  void changeNickName(String newNickName);

  void resetPassword(String email,String currentPassword, String newPassword, String confirmPassword);

  MessageResponse sendNewPasswordToEmail(String email);

  void updateProfile(UpdateProfileRecruiterRequest request);

  void createCompany(PostInfoCompanyRequest request) throws IOException;
  void updateCompany(UpdateInfoCompanyRequest request) throws IOException;
  void deleteCompany();
}
