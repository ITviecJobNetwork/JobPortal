package vn.hcmute.springboot.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import vn.hcmute.springboot.model.Recruiters;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.*;

public interface RecruiterService {
  MessageResponse registerRecruiter(RecruiterRegisterRequest recruiterRegisterRequest);

  JwtResponse loginRecruiter(LoginRequest request);
  void saveUserToken(Recruiters recruiter, String token);

  void revokeAllUserTokens(Recruiters recruiter);
  void refreshToken(HttpServletRequest request,
      HttpServletResponse response) throws IOException;

  MessageResponse changePassword(String currentPassword, String newPassword, String confirmPassword);

  MessageResponse changeNickName(String newNickName);

  MessageResponse resetPassword(String email,String currentPassword, String newPassword, String confirmPassword);

  MessageResponse sendNewPasswordToEmail(String email);

  MessageResponse updateProfile(UpdateProfileRecruiterRequest request);

  RecruiterProfileResponse getProfile();

  MessageResponse createCompany(PostInfoCompanyRequest request) throws IOException;
  MessageResponse updateCompany(UpdateInfoCompanyRequest request) throws IOException;
  MessageResponse deleteCompany();

  MessageResponse postJob(PostJobRequest request);
  MessageResponse updateJob(Integer jobId, UpdateJobRequest request);
  MessageResponse deleteJob(Integer jobId);

  MessageResponse updateStatusJob(Integer applicationId, UpdateApplicationRequest request) throws MessagingException;

  GetJobResponse getJobById(Integer jobId);
  List<ApplicationFormResponse> getAppliedJob();

  ApplicationFormResponse getApplicationById(Integer applicationId);


}
