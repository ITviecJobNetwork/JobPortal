package vn.hcmute.springboot.service;


import java.io.IOException;
import vn.hcmute.springboot.request.AddEducationRequest;
import vn.hcmute.springboot.request.AddSkillRequest;
import vn.hcmute.springboot.request.ProfileUpdateRequest;
import vn.hcmute.springboot.request.AddExperienceRequest;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;


public interface ProfileService {
  MessageResponse updateUserProfile (ProfileUpdateRequest request) throws IOException;
  UserProfileResponse getUserProfile();
  MessageResponse addEducation (AddEducationRequest request);

  MessageResponse addExperience (AddExperienceRequest request);

  MessageResponse deleteEducation (Integer id);
  MessageResponse deleteExperience (Integer id);

  MessageResponse writeAboutMe (String aboutMe);

  void addSkill (AddSkillRequest request);
}
