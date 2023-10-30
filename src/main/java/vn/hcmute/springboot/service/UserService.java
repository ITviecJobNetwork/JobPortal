package vn.hcmute.springboot.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.User;
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


}
