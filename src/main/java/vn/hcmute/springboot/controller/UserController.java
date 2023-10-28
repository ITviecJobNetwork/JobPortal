package vn.hcmute.springboot.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.ApplicationForm;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.repository.ApplicationFormRepository;
import vn.hcmute.springboot.repository.JobRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.ChangeNickNameRequest;
import vn.hcmute.springboot.request.ChangePasswordRequest;
import vn.hcmute.springboot.request.ForgotPasswordRequest;
import vn.hcmute.springboot.response.ApplyJobResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.UserProfileResponse;
import vn.hcmute.springboot.serviceImpl.EmailServiceImpl;
import vn.hcmute.springboot.serviceImpl.OtpServiceImpl;
import vn.hcmute.springboot.serviceImpl.UserServiceImpl;
import vn.hcmute.springboot.request.ApplyJobRequest;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserServiceImpl userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JobRepository jobRepository;
  private final ApplicationFormRepository applicationFormRepository;


  @PostMapping("/forgot-password")
  public ResponseEntity<MessageResponse> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest request) {
  
    var user = userRepository.findByUsername(request.getEmail());
    if(user.isEmpty()){
      return new ResponseEntity<>((new MessageResponse("Người dùng không tồn tại",HttpStatus.NOT_FOUND)),HttpStatus.NOT_FOUND);
    }
    if(request.getEmail() == null){
      return new ResponseEntity<>((new MessageResponse("Không thể gửi mật khẩu mới tới email của bạn", HttpStatus.BAD_REQUEST)),HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(userService.sendNewPasswordToEmail(request.getEmail()),HttpStatus.OK);
  }

  @PostMapping("/change-password")
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ChangePasswordRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }

    String userName = authentication.getName();

    var user = userRepository.findByUsername(userName);
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND));
    }


    String initialPassword = user.get().getPassword();
    if(request.getCurrentPassword().equals(request.getNewPassword())){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Mật khẩu mới và mật khẩu hiện tại không được giống nhau", HttpStatus.BAD_REQUEST));
    }
    if (!passwordEncoder.matches(request.getCurrentPassword(), initialPassword)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Mật khẩu hiện tại không đúng", HttpStatus.BAD_REQUEST));
    }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Mật khẩu mới và xác nhận mật khẩu không khớp", HttpStatus.BAD_REQUEST));
    }

    MessageResponse response = userService.changePassword(request.getCurrentPassword(), request.getNewPassword(), request.getConfirmPassword());

    if (response.getStatus() == HttpStatus.OK) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(response.getStatus()).body(response);
    }

  }


  @PostMapping("/change-nickname")
  public ResponseEntity<MessageResponse> changeNickname(
      @RequestBody ChangeNickNameRequest request) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if(userName == null) {
      return new ResponseEntity<>(new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    var existNickName = userRepository.existsByNickname(request.getNewNickName());
    if(existNickName){
      return new ResponseEntity<>((new MessageResponse("Biệt danh đã tồn tại", HttpStatus.BAD_REQUEST)),HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(userService.changeNickName(request.getNewNickName()),
        HttpStatus.OK);
  }

  @PostMapping(value = "/applyJob", consumes = {"multipart/form-data"})
  public ResponseEntity<ApplyJobResponse> applyJob(
      @Valid @ModelAttribute ApplyJobRequest request) throws IOException {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if(userName == null) {
      return new ResponseEntity<>(new ApplyJobResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName);
    var job = jobRepository.findById(request.getJobId());
    if(job.isEmpty()){
      return new ResponseEntity<>((new ApplyJobResponse("Công việc không tồn tại",HttpStatus.NOT_FOUND)),HttpStatus.NOT_FOUND);
    }
    if(hasAlreadyApplied(user.get(), job.get())){
      return new ResponseEntity<>((new ApplyJobResponse("Bạn đã ứng tuyển công việc này trước đó",HttpStatus.BAD_REQUEST)),HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(userService.applyJob(request),HttpStatus.OK);
  }

  @PostMapping("/writeCoverLetter")
  public ResponseEntity<MessageResponse> writeCoverLetter(@RequestBody String coverLetter) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if(userName == null) {
      return new ResponseEntity<>(new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(userService.writeCoverLetter(coverLetter), HttpStatus.OK);
  }

  @PostMapping(value = "/uploadUserCv", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> uploadUserCv(
      @Valid @RequestParam("fileCv") MultipartFile fileCv) throws IOException {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if(userName == null) {
      return new ResponseEntity<>(new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(userService.uploadUserCv(fileCv), HttpStatus.OK);
  }
  private boolean hasAlreadyApplied(User candidate, Job job) {

    List<ApplicationForm> applicationForms = applicationFormRepository.findByCandidateAndJob(candidate, job);
    return !applicationForms.isEmpty();
  }

}

