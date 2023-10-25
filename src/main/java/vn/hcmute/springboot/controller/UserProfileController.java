package vn.hcmute.springboot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.request.ProfileUpdateRequest;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;
import vn.hcmute.springboot.serviceImpl.ProfileServiceImpl;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {
  private final ProfileServiceImpl profileService;
  @PutMapping(value="/updateProfile" ,consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> updateProfile(@Valid @ModelAttribute ProfileUpdateRequest request) {
    return ResponseEntity.ok(profileService.updateUserProfile(request));
  }

  @GetMapping(value="/getProfile")
  public ResponseEntity<UserProfileResponse> getUserProfile() {
    UserProfileResponse userProfileResponse = profileService.getUserProfile();
    return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
  }



}


