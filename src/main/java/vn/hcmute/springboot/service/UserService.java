package vn.hcmute.springboot.service;

import java.io.IOException;
import java.util.List;

import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.request.ApplyJobRequest;
import vn.hcmute.springboot.request.FavouriteJobRequest;
import vn.hcmute.springboot.request.WriteReviewRequest;
import vn.hcmute.springboot.response.*;

public interface UserService {
  MessageResponse sendNewPasswordToEmail(String email);
  MessageResponse changePassword(String currentPassword, String newPassword, String confirmPassword);

  MessageResponse changeNickName (String newNickName);

  ApplyJobResponse applyJob(ApplyJobRequest request) throws IOException;

  MessageResponse uploadUserCv(String fileCv) throws IOException;

  MessageResponse writeCoverLetter (String coverLetter);


  MessageResponse resetPassword(String email,String currentPassword, String newPassword, String confirmPassword);

  void saveJob(Integer jobId) throws IOException;

  List<SaveJobResponse> getSavedJobs(User user);

  Page<JobApplyResponse>  getAppliedJobs(User user,Pageable pageRequest);


  MessageResponse deleteSaveJobs(Integer id);

  MessageResponse saveFavouriteJobType(FavouriteJobRequest request);

  MessageResponse writeCompanyReview(Integer companyId,WriteReviewRequest request);

  void followCompany(Integer companyId);

  MessageResponse activeAccount(String userName, String adminEmail) throws MessagingException;
}
