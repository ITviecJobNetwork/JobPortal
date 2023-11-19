package vn.hcmute.springboot.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import vn.hcmute.springboot.model.Admin;
import vn.hcmute.springboot.model.CompanyReviewStatus;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.response.*;

import java.io.IOException;
import java.sql.Date;

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

  Page<RecruiterResponse> getAllRecruiter (int page, int size);

  RecruiterProfileResponse getRecruiterById(Integer id);

  MessageResponse activeUser(String email) throws MessagingException;

  CountResponse countUserByDate(Date startDate, Date endDate);

  void updateStatusCompanyReview (Integer id, CompanyReviewStatus status);

}
