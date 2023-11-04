package vn.hcmute.springboot.controller;

import com.cloudinary.Cloudinary;
import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

  private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
  private final ProfileServiceImpl profileService;
  private final UserRepository userRepository;
  private final FileUploadServiceImpl fileUploadService;
  private final SkillRepository skillRepository;
  private final CandidateEducationRepository candidateEducationRepository;
  private final CandidateExperienceRepository candidateExperienceRepository;
  private final Cloudinary cloudinary;

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
    if (request.getId() != null) {
      return new ResponseEntity<>(
          new MessageResponse("Cập nhật education thành công", HttpStatus.OK),
          HttpStatus.OK);

    } else {
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
    if (request.getId() != null) {
      return new ResponseEntity<>(
          new MessageResponse("Cập nhật experience thành công", HttpStatus.OK),
          HttpStatus.OK);

    } else {
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
    return new ResponseEntity<>(
        new MessageResponse("Không có education để xóa", HttpStatus.BAD_REQUEST),
        HttpStatus.BAD_REQUEST);

  }

  @DeleteMapping(value = "/experience/{id}")
  public ResponseEntity<MessageResponse> deleteExperience(@PathVariable Integer id)
      throws IOException {
    CandidateExperience experience = candidateExperienceRepository.findById(id).orElseThrow();
    if (experience.getId() != null) {
      for (User user : experience.getUsers()) {
        user.getExperiences().remove(experience);
        userRepository.save(user);
      }
      experience.getUsers().clear();
      candidateExperienceRepository.delete(experience);
      candidateExperienceRepository.delete(experience);
      return new ResponseEntity<>(
          new MessageResponse("Xóa thông tin kinh nghiêm thành công", HttpStatus.OK),
          HttpStatus.OK);
    }
    return new ResponseEntity<>(
        new MessageResponse("Không có thông tin kinh nghiêm để xóa", HttpStatus.BAD_REQUEST),
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
      headers.add("Content-Disposition", "inline; filename=cv.pdf");

      return ResponseEntity
          .ok()
          .headers(headers)
          .contentLength(pdfInputStream.available())
          .contentType(MediaType.APPLICATION_PDF)
          .body(new InputStreamResource(pdfInputStream));
    } catch (IOException e) {
      // Xử lý lỗi
      return ResponseEntity.notFound().build();
    }
  }


  private byte[] downloadFileFromUrl(String fileUrl) throws IOException {
    try {
      URI uri = new URI(fileUrl);
      HttpURLConnection httpURLConnection = (HttpURLConnection) uri.toURL().openConnection();
      try (InputStream in = httpURLConnection.getInputStream()) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = in.read(data, 0, data.length)) != -1) {
          buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
      }
    } catch (Exception e) {
      throw new IOException("Failed to download file from URL: " + fileUrl, e);
    }
  }
}


