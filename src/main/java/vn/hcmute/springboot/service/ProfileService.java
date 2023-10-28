package vn.hcmute.springboot.service;


import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.request.ProfileUpdateRequest;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;


public interface ProfileService {
  MessageResponse updateUserProfile (ProfileUpdateRequest request) throws IOException;
  UserProfileResponse getUserProfile();


}
