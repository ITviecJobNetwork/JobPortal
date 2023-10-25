package vn.hcmute.springboot.service;


import java.util.Optional;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.request.ProfileUpdateRequest;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;


public interface ProfileService {
  MessageResponse updateUserProfile (ProfileUpdateRequest request);
  UserProfileResponse getUserProfile();
}
