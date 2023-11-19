package vn.hcmute.springboot.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.CandidateEducation;
import vn.hcmute.springboot.model.CandidateExperience;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;


@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

  private final ProfileServiceImpl profileService;
  private final UserRepository userRepository;
  private final FileUploadServiceImpl fileUploadService;
  private final CandidateEducationRepository candidateEducationRepository;
  private final CandidateExperienceRepository candidateExperienceRepository;

  @PutMapping(value = "/updateProfile", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> updateProfile(
          @Valid @RequestBody ProfileUpdateRequest request)
          throws IOException {

    var profile = profileService.updateUserProfile(request);
    return new ResponseEntity<>(profile, HttpStatus.OK);
  }

  @GetMapping(value = "/getProfile")
  public ResponseEntity<UserProfileResponse> getUserProfile() {
    var userProfileResponse = profileService.getUserProfile();
    return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
  }

  @PostMapping(value = "/uploadAvatar")
  public ResponseEntity<MessageResponse> uploadImage(
          @RequestBody String avatar) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }
    var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
    user.setAvatar(avatar);
    userRepository.save(user);

    return new ResponseEntity<>(new MessageResponse("Upload thành công", HttpStatus.OK),
            HttpStatus.OK);
  }

  @PostMapping(value = "/addEducation")
  public ResponseEntity<MessageResponse> addEducation(
          @Valid @RequestBody AddEducationRequest request) throws IOException {
    profileService.addEducation(request);
    return new ResponseEntity<>(new MessageResponse("Thêm education thành công", HttpStatus.OK),
            HttpStatus.OK);
  }

  @PostMapping(value = "/addExperience")
  public ResponseEntity<MessageResponse> addExperience(
          @Valid @RequestBody AddExperienceRequest request) throws IOException {
    profileService.addExperience(request);
    return new ResponseEntity<>(new MessageResponse("Thêm experience thành công", HttpStatus.OK),
            HttpStatus.OK);
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
    if (user.getAvatar() == null) {
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
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MessageResponse> deleteEducation(@PathVariable Integer id) {
      var education = profileService.deleteEducation(id);
      return new ResponseEntity<>(education, HttpStatus.OK);
  }

  @DeleteMapping(value = "/experience/{id}")
  public ResponseEntity<MessageResponse> deleteExperience(@PathVariable Integer id)
          throws IOException {
    var experience = profileService.deleteExperience(id);
    return new ResponseEntity<>(experience, HttpStatus.OK);

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


  @GetMapping("/downloadCv")
  public ResponseEntity<Resource> downloadCv() throws Exception {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    var user = userRepository.findByUsername(userName).orElseThrow();
    if (user.getLinkCV() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    String cloudinaryPDFUrl = user.getLinkCV();
    try {
      URI uri = new URI(cloudinaryPDFUrl);
      URL url = uri.toURL();
      InputStream pdfInputStream = url.openStream();
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Disposition", "attachment; filename=%s".formatted(cloudinaryPDFUrl));

      return ResponseEntity
              .ok()
              .headers(headers)
              .contentLength(pdfInputStream.available())
              .contentType(MediaType.APPLICATION_PDF)
              .body(new InputStreamResource(pdfInputStream));
    } catch (IOException e) {
      return ResponseEntity.notFound().build();
    }
  }

}


