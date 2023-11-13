package vn.hcmute.springboot.serviceImpl;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.CandidateEducation;
import vn.hcmute.springboot.model.CandidateExperience;
import vn.hcmute.springboot.model.Skill;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.repository.CandidateEducationRepository;
import vn.hcmute.springboot.repository.CandidateExperienceRepository;
import vn.hcmute.springboot.repository.SkillRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.AddEducationRequest;
import vn.hcmute.springboot.request.AddExperienceRequest;
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
          .message("Ngươi dùng chưa đăng nhập")
          .status(HttpStatus.UNAUTHORIZED)
          .build();
    }
  }


    @Override
    public MessageResponse updateUserProfile(ProfileUpdateRequest request) throws IOException {
      handleUserStatus();
      var userName = SecurityContextHolder.getContext().getAuthentication().getName();
      var user = userRepository.findByUsernameIgnoreCase(userName)
              .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy User"));
      MultipartFile fileAvatar = request.getAvatar();
      if (request.getAvatar() != null) {
        if (!isImageFile(fileAvatar.getOriginalFilename())) {
          throw new BadRequestException("File không đúng định dạng");
        }
        String profileAvatar = fileStorageService.uploadFile(fileAvatar);
        user.setAvatar(profileAvatar);
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
      user.setCity(request.getCity());
      user.setGender(request.getGender());
      if (request.getSkills() != null && !request.getSkills().isEmpty()) {
        List<String> skillNames = request.getSkills();
        List<Skill> existingSkills = skillRepository.findByTitleIn(skillNames);

        if (existingSkills.size() < skillNames.size()) {
          List<String> missingSkills = skillNames.stream()
                  .filter(skillName -> existingSkills.stream()
                          .noneMatch(skill -> skill.getTitle().equals(skillName)))
                  .collect(Collectors.toList());

          String errorMessage = "Các kỹ năng sau không tồn tại: " + String.join(", ", missingSkills);
          throw new BadRequestException(errorMessage);

        }
        user.setSkills(existingSkills);


      }
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
          .education(user.getEducations())
          .experience(user.getExperiences())
          .gender(user.getGender())
          .skills(user.getSkills().stream().map(Skill::getTitle).toList())
          .status(HttpStatus.OK)
          .build();
    } else {
      throw new NotFoundException("Không Tìm Thấy Profile của User");
    }
  }

  @Override
  public void addEducation(AddEducationRequest request) {
    handleUserStatus();
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userRepository.findByUsernameIgnoreCase(userName)
        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy User"));
    if(request.getId()!=null) {
      var existingEducation = candidateEducationRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy Education"));
      existingEducation.setSchool(request.getSchool());
      existingEducation.setMajor(request.getMajor());
      existingEducation.setStartTime(request.getStartDate());
      existingEducation.setEndTime(request.getEndDate());
      candidateEducationRepository.save(existingEducation);
      MessageResponse.builder()
          .message("Cập nhật thông tin thành công")
          .status(HttpStatus.OK)
          .build();
    }


    else {
      CandidateEducation candidateEducation = new CandidateEducation();
      candidateEducation.setSchool(request.getSchool());
      candidateEducation.setMajor(request.getMajor());
      candidateEducation.setStartTime(request.getStartDate());
      candidateEducation.setEndTime(request.getEndDate());
      List<CandidateEducation> educations = user.getEducations();
      educations.add(candidateEducation);
      candidateEducationRepository.save(candidateEducation);
      userRepository.save(user);
      MessageResponse.builder()
              .message("Tạo mới thông tin thành công")
              .status(HttpStatus.OK)
              .build();

    }



  }

  @Override
  public void addExperience(AddExperienceRequest request) {
    handleUserStatus();
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy User"));
    if(request.getId()!=null){
      var existingExperience = candidateExperienceRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy Experience"));
      existingExperience.setCompanyName(request.getCompanyName());
      existingExperience.setJobTitle(request.getJobTitle());
      existingExperience.setStartTime(request.getStartDate());
      existingExperience.setEndTime(request.getEndDate());
      candidateExperienceRepository.save(existingExperience);
      MessageResponse.builder()
              .message("Cập nhật thông tin thành công")
              .status(HttpStatus.OK)
              .build();
    }
    else{
      CandidateExperience candidateExperience = new CandidateExperience();
      if (request.getCompanyName() != null) {
        candidateExperience.setCompanyName(request.getCompanyName());
        candidateExperience.setJobTitle(request.getJobTitle());
        candidateExperience.setStartTime(request.getStartDate());
        candidateExperience.setEndTime(request.getEndDate());
      }
      List<CandidateExperience> experience = user.getExperiences();
      experience.add(candidateExperience);
      candidateExperienceRepository.save(candidateExperience);
      userRepository.save(user);
    }
    MessageResponse.builder()
        .message("Tạo mới thông tin thành công")
        .status(HttpStatus.OK)
        .build();

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
