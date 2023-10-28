package vn.hcmute.springboot.controller;

import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.repository.SkillRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.ProfileUpdateRequest;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;
import vn.hcmute.springboot.serviceImpl.FileUploadServiceImpl;
import vn.hcmute.springboot.serviceImpl.ProfileServiceImpl;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

  private final ProfileServiceImpl profileService;
  private final UserRepository userRepository;
  private final FileUploadServiceImpl fileUploadService;

  @PutMapping(value = "/updateProfile", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> updateProfile(
      @Valid @ModelAttribute ProfileUpdateRequest request)
      throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }
    var userName = authentication.getName();
    if (userName == null) {
      return new ResponseEntity<>(
          new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
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
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
          new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName).orElseThrow();
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


