package vn.hcmute.springboot.controller;

import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.repository.SkillRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.ProfileUpdateRequest;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;
import vn.hcmute.springboot.serviceImpl.ProfileServiceImpl;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {
  private final ProfileServiceImpl profileService;
  private final UserRepository userRepository;
  private final SkillRepository skillRepository;
  @PutMapping(value="/updateProfile" ,consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> updateProfile(@Valid @ModelAttribute ProfileUpdateRequest request)
      throws IOException {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if(userName == null) {
      return new ResponseEntity<>(new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    MultipartFile avatarFile = request.getAvatar();
    if(!isImageFile(avatarFile.getOriginalFilename())) {
      return new ResponseEntity<>(new MessageResponse("File không phải định dạng image", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    var profile = profileService.updateUserProfile(request);
    return new ResponseEntity<>(profile, HttpStatus.OK);
  }

  @GetMapping(value="/getProfile")
  public ResponseEntity<UserProfileResponse> getUserProfile() {
    UserProfileResponse userProfileResponse = profileService.getUserProfile();
    if(userProfileResponse == null) {
      return new ResponseEntity<>(new UserProfileResponse("Profile của người dùng không tồn tại", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
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


