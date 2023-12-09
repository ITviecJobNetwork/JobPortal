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
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.SkillResponse;
import vn.hcmute.springboot.response.UserProfileResponse;
import vn.hcmute.springboot.service.FileUploadService;
import vn.hcmute.springboot.service.ProfileService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;


@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

  private final ProfileService profileService;
  private final UserRepository userRepository;
  private final FileUploadService fileUploadService;

  @PutMapping(value = "/updateProfile")
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
          @RequestBody UploadFileRequest avatar) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }
    var user = userRepository.findByUsername(authentication.getName()).orElseThrow();
    user.setAvatar(avatar.getFile());
    userRepository.save(user);

    return new ResponseEntity<>(new MessageResponse("Upload thành công", HttpStatus.OK),
            HttpStatus.OK);
  }

  @PostMapping(value = "/add-education")
  public ResponseEntity<MessageResponse> addEducation(
                                                       @Valid @RequestBody AddEducationRequest request) throws IOException {
    var education = profileService.addEducation(request);
    return new ResponseEntity<>(education, HttpStatus.OK);
  }

  @PostMapping(value = "/add-experience")
  public ResponseEntity<MessageResponse> addExperience(@RequestParam(required=false) Integer id ,
          @Valid @RequestBody AddExperienceRequest request) throws IOException {
    var experience = profileService.addExperience(id,request);
    return new ResponseEntity<>(experience, HttpStatus.OK);
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

  @PostMapping("write-about-me")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MessageResponse> writeAboutMe(@RequestBody AboutMeRequest request) {
    var aboutMe=profileService.writeAboutMe(request);
    return new ResponseEntity<>(aboutMe,HttpStatus.OK);
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
  @PostMapping("/add-skill")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MessageResponse> addSkill(@RequestBody AddSkillRequest request) {
    var skill=profileService.addSkill(request);
    return new ResponseEntity<>(skill,HttpStatus.OK);
  }

  @GetMapping("/get-all-skill")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<SkillResponse> getAllSkill() {
    var skill=profileService.getAllSkill();
    return new ResponseEntity<>(skill,HttpStatus.OK);
  }
}


