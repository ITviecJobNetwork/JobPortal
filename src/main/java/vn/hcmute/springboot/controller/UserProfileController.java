package vn.hcmute.springboot.controller;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
import vn.hcmute.springboot.serviceImpl.FileUploadServiceImpl;
import vn.hcmute.springboot.serviceImpl.ProfileServiceImpl;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

  private final ProfileServiceImpl profileService;
  private final UserRepository userRepository;
  private final FileUploadServiceImpl fileUploadService;
  private final SkillRepository skillRepository;
  private final CandidateEducationRepository candidateEducationRepository;
  private final CandidateExperienceRepository candidateExperienceRepository;


  @PutMapping(value = "/updateProfile", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> updateProfile(
      @Valid @ModelAttribute ProfileUpdateRequest request)
      throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }
    List<String> skillNames = request.getSkills();
    List<Skill> existingSkills = skillRepository.findByTitleIn(skillNames);

    if (existingSkills.size() < skillNames.size()) {
      List<String> missingSkills = skillNames.stream()
          .filter(skillName -> existingSkills.stream()
              .noneMatch(skill -> skill.getTitle().equals(skillName)))
          .collect(Collectors.toList());

      String errorMessage = "Các kỹ năng sau không tồn tại: " + String.join(", ", missingSkills);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponse(errorMessage, HttpStatus.BAD_REQUEST));
    }



    var profile = profileService.updateUserProfile(request);
    return new ResponseEntity<>(profile, HttpStatus.OK);
  }

  @GetMapping(value = "/getProfile")
  public ResponseEntity<UserProfileResponse> getUserProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new UserProfileResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }
    UserProfileResponse userProfileResponse = profileService.getUserProfile();
    if (userProfileResponse == null) {
      return new ResponseEntity<>(
          new UserProfileResponse("Profile của người dùng không tồn tại", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
  }

  @PostMapping(value = "/uploadAvatar", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> uploadImage(
      @RequestPart(value = "avatar", required = false) MultipartFile file) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }
    var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
    if (!isImageFile(file.getOriginalFilename())) {
      return new ResponseEntity<>(
          new MessageResponse("File không phải là ảnh", HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);
    }
    String profileAvatar = fileUploadService.uploadFile(file);
    user.setAvatar(profileAvatar);
    userRepository.save(user);



    return new ResponseEntity<>(new MessageResponse("Upload thành công", HttpStatus.OK),
        HttpStatus.OK);
  }
  @PostMapping(value = "/addEducation", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> addEducation(
      @Valid @ModelAttribute AddEducationRequest request) throws IOException {
    profileService.addEducation(request);
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    if (userName == null) {
      return new ResponseEntity<>(
          new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }
    if(request.getId()!=null){
      return new ResponseEntity<>(new MessageResponse("Cập nhật education thành công", HttpStatus.OK),
          HttpStatus.OK);

    }
    else{
      return new ResponseEntity<>(new MessageResponse("Thêm education thành công", HttpStatus.OK),
          HttpStatus.OK);
    }



  }
  @PostMapping(value = "/addExperience", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> addExperience(
      @Valid @ModelAttribute AddExperienceRequest request) throws IOException {
    profileService.addExperience(request);
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
          new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }
    if(request.getId()!=null){
      return new ResponseEntity<>(new MessageResponse("Cập nhật experience thành công", HttpStatus.OK),
          HttpStatus.OK);

    }
    else{
      return new ResponseEntity<>(new MessageResponse("Thêm experience thành công", HttpStatus.OK),
          HttpStatus.OK);
    }


  }
  @DeleteMapping(value = "/deleteAvatar")
  public ResponseEntity<MessageResponse> deleteImage() throws IOException {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
          new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName).orElseThrow();
    if(user.getAvatar() == null){
      return new ResponseEntity<>(
          new MessageResponse("Không có avatar để xóa", HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);
    }
    fileUploadService.deleteFile(user.getAvatar());
    user.setAvatar(null);
    userRepository.save(user);
    return new ResponseEntity<>(new MessageResponse("Xóa avatar thành công", HttpStatus.OK),
        HttpStatus.OK);
  }

  @DeleteMapping("/educations/{id}")
  public ResponseEntity<?> deleteEducation(@PathVariable Integer id) {
    CandidateEducation education = candidateEducationRepository.findById(id).orElseThrow();

    if (education.getId() != null) {
      for (User user : education.getUsers()) {
        user.getEducations().remove(education);
        userRepository.save(user);
      }

      education.getUsers().clear();
      candidateEducationRepository.delete(education);
      return new ResponseEntity<>(new MessageResponse("Xóa education thành công", HttpStatus.OK),
          HttpStatus.OK);
    }
    return new ResponseEntity<>(new MessageResponse("Không có education để xóa", HttpStatus.BAD_REQUEST),
        HttpStatus.BAD_REQUEST);

  }
  @DeleteMapping(value = "/experience/{id}")
  public ResponseEntity<MessageResponse> deleteExperience(@PathVariable Integer id) throws IOException {
    CandidateExperience experience = candidateExperienceRepository.findById(id).orElseThrow();
    if(experience.getId() != null){
      for (User user : experience.getUsers()) {
        user.getExperiences().remove(experience);
        userRepository.save(user);
      }
      experience.getUsers().clear();
      candidateExperienceRepository.delete(experience);
      candidateExperienceRepository.delete(experience);
      return new ResponseEntity<>(new MessageResponse("Xóa thông tin kinh nghiêm thành công", HttpStatus.OK),
          HttpStatus.OK);
    }
    return new ResponseEntity<>(new MessageResponse("Không có thông tin kinh nghiêm để xóa", HttpStatus.BAD_REQUEST),
        HttpStatus.BAD_REQUEST);

  }
  private boolean isImageFile(String fileName) {
    String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};

    for (String extension : imageExtensions) {
      if (fileName.toLowerCase().endsWith(extension)) {
        return true;
      }
    }
    return false;
  }











}


