package vn.hcmute.springboot.service;

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.request.ApplyJobRequest;
import vn.hcmute.springboot.response.ApplyJobResponse;
import vn.hcmute.springboot.response.MessageResponse;

public interface UserService {
  MessageResponse sendNewPasswordToEmail(String email);
  MessageResponse changePassword(String currentPassword, String newPassword, String confirmPassword);

  MessageResponse changeNickName (String newNickName);

  ApplyJobResponse applyJob(ApplyJobRequest request) throws IOException;

  MessageResponse uploadUserCv(MultipartFile fileCv) throws IOException;

  MessageResponse writeCoverLetter (String coverLetter);


  MessageResponse resetPassword(String email,String currentPassword, String newPassword, String confirmPassword);

  void saveJob(Integer jobId) throws IOException;

  List<Job> getSavedJobs();

  List<Job> getAppliedJobs();


  MessageResponse deleteSaveJobs(Integer id);
}
