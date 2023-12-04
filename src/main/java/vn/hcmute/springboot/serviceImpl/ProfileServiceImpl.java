package vn.hcmute.springboot.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.CandidateEducationResponse;
import vn.hcmute.springboot.response.CandidateExperienceResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;
import vn.hcmute.springboot.service.ProfileService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

  private final UserRepository userRepository;
  private final CandidateEducationRepository candidateEducationRepository;
  private final CandidateExperienceRepository candidateExperienceRepository;
  private final SkillRepository skillRepository;
  private final CandidateSkillRepository candidateSkillRepository;

  @Override
  public MessageResponse updateUserProfile(ProfileUpdateRequest request) throws IOException {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userRepository.findByUsernameIgnoreCase(userName)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    user.setFullName(request.getFullName());
    user.setUsername(request.getEmail());
    user.setAvatar(request.getAvatar());
    user.setAddress(request.getAddress());
    user.setPosition(request.getPosition());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setBirthDate(request.getBirthdate());
    user.setLinkWebsiteProfile(request.getLinkWebsiteProfile());
    user.setGender(request.getGender());
    user.setCity(request.getCity());
    userRepository.save(user);
    return MessageResponse.builder()
            .message("Cập nhật thông tin thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  @Transactional
  public UserProfileResponse getUserProfile() {

    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user) {
      CandidateEducationResponse educationResponse = null;
      if (user.getEducation() != null) {
        educationResponse = convertToCandidateEducationResponse(user.getEducation());
      }
      if(user.getBirthDate() != null){
        user.setBirthDate(user.getBirthDate());
      }
      else{
        user.setBirthDate(null);
      }
      return UserProfileResponse.builder()
              .id(user.getId())
              .fullName(user.getFullName())
              .aboutMe(user.getAboutMe())
              .avatar(user.getAvatar())
              .email(user.getUsername())
              .location(user.getLocation())
              .address(user.getAddress())
              .position(user.getPosition())
              .phoneNumber(user.getPhoneNumber())
              .linkWebsiteProfile(user.getLinkWebsiteProfile())
              .birthdate(user.getBirthDate())
              .city(user.getCity())
              .education(educationResponse)
              .experience(user.getExperiences() != null ?
                      user.getExperiences().stream().map(this::convertToCandidateExperienceResponse).toList() :
                      Collections.emptyList())
              .gender(user.getGender())
              .userStatus(user.getStatus())
              .skills(user.getSkills().stream().map(CandidateSkill::getTitle).toList())
              .build();
    } else {
      throw new NotFoundException("Không tìm thấy hồ sơ");
    }
  }

  @Override
  public MessageResponse addEducation(AddEducationRequest request) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = userRepository.findByUsernameIgnoreCase(userName)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    if (user.getEducation() != null) {
      var existingEducation = candidateEducationRepository.findById(user.getEducation().getId())
              .orElseThrow(() -> new NotFoundException("Chưa có thông tin học vấn"));
      existingEducation.setSchool(request.getSchool());
      existingEducation.setMajor(request.getMajor());
      existingEducation.setStartDate(request.getStartDate());
      existingEducation.setEndDate(request.getEndDate());
      candidateEducationRepository.save(existingEducation);
      return MessageResponse.builder()
              .message("Cập nhật thông tin học vấn thành công")
              .status(HttpStatus.OK)
              .build();
    } else {
      CandidateEducation candidateEducation = new CandidateEducation();
      candidateEducation.setSchool(request.getSchool());
      candidateEducation.setMajor(request.getMajor());
      candidateEducation.setStartDate(request.getStartDate());
      candidateEducation.setEndDate(request.getEndDate());
      candidateEducationRepository.save(candidateEducation);
      user.setEducation(candidateEducation);
      userRepository.save(user);
      return MessageResponse.builder()
              .message("Thêm thông tin học vấn thành công")
              .status(HttpStatus.OK)
              .build();

    }


  }

  @Override
  public MessageResponse addExperience(Integer id, AddExperienceRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    if (id!=null) {
      var existingExperience = candidateExperienceRepository.findById(id)
              .orElseThrow(() -> new NotFoundException("Không tìm thấy kinh nghiệm"));
      existingExperience.setCompanyName(request.getCompanyName());
      existingExperience.setJobTitle(request.getJobTitle());
      existingExperience.setStartTime(request.getStartDate());
      existingExperience.setEndTime(request.getEndDate());
      candidateExperienceRepository.save(existingExperience);
      return MessageResponse.builder()
              .message("Cập nhật thông tin thành công")
              .status(HttpStatus.OK)
              .build();
    } else {
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
    return MessageResponse.builder()
            .message("Tạo mới thông tin thành công")
            .status(HttpStatus.OK)
            .build();

  }


  @Override
  public MessageResponse deleteEducation(Integer id) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    var education = candidateEducationRepository.findById(id);
    if (education.isPresent() && user.getEducation().getId().equals(id)) {
      user.setEducation(null);
      userRepository.save(user);
      candidateEducationRepository.deleteById(id);
    } else {
      throw new NotFoundException("Chưa có học vấn");
    }
    return MessageResponse.builder()
            .message("Xóa thông tin học vấn thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse deleteExperience(Integer id) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    var experience = candidateExperienceRepository.findById(id).orElseThrow();
    if (experience.getId().equals(id)) {
        for(var experiences:user.getExperiences()){
          if(experiences.getId().equals(id)){
            user.getExperiences().remove(experiences);
            break;
          }
        }
        userRepository.save(user);
        candidateExperienceRepository.deleteById(id);
      return new MessageResponse("Xóa kinh nghiêm thành công", HttpStatus.OK);

    }
    return new MessageResponse("Chưa có thông tin kinh nghiêm", HttpStatus.BAD_REQUEST);

  }

  @Override
  public MessageResponse writeAboutMe(AboutMeRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

    String message;
    if (user.getAboutMe() == null || user.getAboutMe().isEmpty()) {
      user.setAboutMe(request.getAboutMe());
      message = "Viết giới thiệu bản thân thành công";
    } else {
      user.setAboutMe(request.getAboutMe());
      message = "Cập nhật giới thiệu bản thân thành công";
    }

    userRepository.save(user);

    return MessageResponse.builder()
            .message(message)
            .status(HttpStatus.OK)
            .build();
  }


  @Override
  public MessageResponse addSkill(AddSkillRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    var skill = skillRepository.findByTitleIn(request.getSkillName());
    if(skill.isEmpty()){
      throw new NotFoundException("Không tìm thấy kỹ năng");
    }

    boolean skillExist = user.getSkills().stream().anyMatch(candidateSkill -> candidateSkill.getTitle().equals(request.getSkillName().toString()));
    if (skillExist) {
      throw new NotFoundException("Kỹ năng đã tồn tại");
    }
    else {
      CandidateSkill candidateSkill = new CandidateSkill();
      candidateSkill.setTitle(request.getSkillName().toString());
      candidateSkill.setUsers(Collections.singletonList(user));
      candidateSkillRepository.save(candidateSkill);
      user.getSkills().add(candidateSkill);
      userRepository.save(user);
    }

    return MessageResponse.builder()
            .message("Thêm kỹ năng thành công")
            .status(HttpStatus.OK)
            .build();
  }

  public CandidateExperienceResponse convertToCandidateExperienceResponse(CandidateExperience experience) {
    return CandidateExperienceResponse.builder()
            .id(experience.getId())
            .companyName(experience.getCompanyName())
            .jobTitle(experience.getJobTitle())
            .startTime(experience.getStartTime())
            .endTime(experience.getEndTime())
            .build();
  }

  public CandidateEducationResponse convertToCandidateEducationResponse(CandidateEducation education) {
    return CandidateEducationResponse.builder()
            .id(education.getId())
            .school(education.getSchool())
            .major(education.getMajor())
            .startDate(education.getStartDate())
            .endDate(education.getEndDate())
            .build();
  }


}
