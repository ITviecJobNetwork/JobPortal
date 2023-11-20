package vn.hcmute.springboot.service;


import java.io.IOException;

import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;


public interface ProfileService {
  MessageResponse updateUserProfile (ProfileUpdateRequest request) throws IOException;
  UserProfileResponse getUserProfile();
  MessageResponse addEducation (AddEducationRequest request);

  MessageResponse addExperience (Integer id,AddExperienceRequest request);

  MessageResponse deleteEducation (Integer id);
  MessageResponse deleteExperience (Integer id);

  MessageResponse writeAboutMe (AboutMeRequest request);

  MessageResponse addSkill (AddSkillRequest request);
}
