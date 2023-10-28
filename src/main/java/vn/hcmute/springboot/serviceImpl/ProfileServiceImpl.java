package vn.hcmute.springboot.serviceImpl;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.CandidateEducation;
import vn.hcmute.springboot.model.CandidateExperience;
import vn.hcmute.springboot.model.Skill;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.repository.CandidateEducationRepository;
import vn.hcmute.springboot.repository.CandidateExperienceRepository;
import vn.hcmute.springboot.repository.SkillRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.ProfileUpdateRequest;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;
import vn.hcmute.springboot.service.ProfileService;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

  private final UserRepository userRepository;
  private final SkillRepository skillRepository;
  private final CandidateEducationRepository candidateEducationRepository;
  private final FileUploadServiceImpl fileStorageService;
  private final CandidateExperienceRepository candidateExperienceRepository;

  public void handleUserStatus() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      MessageResponse.builder()
          .message("truy cập không hợp lệ")
          .status(HttpStatus.UNAUTHORIZED)
          .build();
    }
  }


  @Override
  public MessageResponse updateUserProfile(ProfileUpdateRequest request) throws IOException {
    handleUserStatus();
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userRepository.findByUsernameIgnoreCase(userName)
        .orElseThrow(() -> new NotFoundException("không-tìm-thấy-user"));
    CandidateEducation candidateEducation = user.getEducationId();
    if (candidateEducation == null) {
      candidateEducation = new CandidateEducation();
      candidateEducation.setCandidate(user);
    }
    CandidateExperience candidateExperience = user.getWorkExperienceId();
    if (candidateExperience == null) {
      candidateExperience = new CandidateExperience();
      candidateExperience.setCandidate(user);
    }
    user.setFullName(request.getFullName());
    user.setAboutMe(request.getAboutMe());
    user.setUsername(request.getEmail());
    user.setLocation(request.getLocation());
    user.setAddress(request.getAddress());
    user.setPosition(request.getPosition());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setBirthDate(request.getBirthdate());
    user.setLinkWebsiteProfile(request.getLinkWebsiteProfile());
    user.setCoverLetter(request.getCoverLetter());
    if (request.getSchool() != null && request.getMajor() != null) {
      candidateEducation.setSchool(request.getSchool().toString());
      candidateEducation.setMajor(request.getMajor().toString());
      candidateEducation.setCandidate(user);
      user.setEducationId(candidateEducation);
    }
    if(request.getWorkExperience() != null){
      candidateExperience.setCompanyName(request.getCompanyName());
      candidateExperience.setJobTitle(request.getJobTitle());
      candidateExperience.setStartTime(request.getStartDate());
      candidateExperience.setEndTime(request.getEndDate());
      candidateExperience.setCandidate(user);
      user.setWorkExperienceId(candidateExperience);
    }

    MultipartFile avatarFile = request.getAvatar();
    if(!isImageFile(avatarFile.getOriginalFilename())){
      return MessageResponse.builder()
          .message("file-không-phải-định-dạng-image")
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    String urlOfAvatar = fileStorageService.uploadFile(avatarFile);
    user.setAvatar(urlOfAvatar);

    if (request.getSkills() != null && !request.getSkills().isEmpty()) {
      List<Skill> skills = skillRepository.findByTitleIn(request.getSkills());
      if (skills.isEmpty()) {
        skills = request.getSkills().stream().map(skill -> {
          Skill newSkill = new Skill();
          newSkill.setTitle(skill);
          return newSkill;
        }).toList();
      }

      user.setSkills(skills);
    }
    user.setCity(request.getCity());
    user.setGender(request.getGender());
    user.setSchool(request.getSchool().toString());
    user.setMajor(request.getMajor().toString());
    candidateEducationRepository.save(candidateEducation);
    candidateExperienceRepository.save(candidateExperience);
    userRepository.save(user);
    return MessageResponse.builder()
        .message("cập-nhật-thông-tin-thành-công")
        .status(HttpStatus.OK)
        .build();

  }

  @Override
  @Transactional
  public UserProfileResponse getUserProfile() {
    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user) {
      return UserProfileResponse.builder()
          .fullName(user.getFullName())
          .aboutMe(user.getAboutMe())
          .avatar(user.getAvatar())
          .email(user.getUsername())
          .location(user.getLocation())
          .address(user.getAddress())
          .position(user.getPosition())
          .phoneNumber(user.getPhoneNumber())
          .birthdate(user.getBirthDate())
          .linkWebsiteProfile(user.getLinkWebsiteProfile())
          .coverLetter(user.getCoverLetter())
          .city(user.getCity())
          .gender(user.getGender())
          .school(Collections.singletonList(user.getSchool()))
          .major(Collections.singletonList(user.getMajor()))
          .skills(user.getSkills().stream().map(Skill::getTitle).toList())
          .status(HttpStatus.OK)
          .build();
    } else {
      return UserProfileResponse.builder()
          .message("không-tìm-thấy-user")
          .status(HttpStatus.NOT_FOUND)
          .build();
    }
  }

  private boolean isImageFile(String fileName) {
    // Determine if the file has an image extension or content type
    String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};

    for (String extension : imageExtensions) {
      if (fileName.toLowerCase().endsWith(extension)) {
        return true;
      }
    }
    return false;
  }


}
