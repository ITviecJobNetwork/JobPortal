package vn.hcmute.springboot.serviceImpl;


import jakarta.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.ApplicationForm;
import vn.hcmute.springboot.model.ApplicationStatus;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.repository.ApplicationFormRepository;
import vn.hcmute.springboot.repository.JobRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.ApplyJobRequest;
import vn.hcmute.springboot.response.ApplyJobResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final OtpServiceImpl otpService;
  private final PasswordEncoder encoder;
  private final EmailServiceImpl emailService;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationFormRepository applicationFormRepository;
  private final JobRepository jobRepository;
  private final FileUploadServiceImpl fileService;
  public void handleUserStatus() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      MessageResponse.builder()
          .message("Truy cập không được ủy quyền")
          .status(HttpStatus.UNAUTHORIZED)
          .build();
    }
  }

  @Override
  public MessageResponse sendNewPasswordToEmail(String email) {
    var user = userRepository.findByUsername(email)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    if (user.getStatus().equals(UserStatus.INACTIVE)) {
      String messageError = "Người dùng chưa xác thực";
      return MessageResponse.builder()
          .status(HttpStatus.UNAUTHORIZED)
          .message(messageError)
          .build();
    }
    try {
      String newPassword = String.valueOf(otpService.generateNewPassword());
      user.setPassword(encoder.encode(newPassword));
      emailService.sendNewPasswordToEmail(email, newPassword);
    } catch (MessagingException e) {
      String messageError = "Không thể gửi mật khẩu mới, vui lòng thử lại";
      return MessageResponse.builder()
          .status(HttpStatus.BAD_REQUEST)
          .message(messageError)
          .build();
    }
    userRepository.save(user);
    return MessageResponse.builder()
        .status(HttpStatus.OK)
        .message("Mật khẩu mới đã được gửi đến email của bạn")
        .build();
  }

  @Override
  public MessageResponse changePassword(String currentPassword, String newPassword, String confirmPassword) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      String message = "Người dùng chưa được ủy quyền";
      return MessageResponse.builder()
          .message(message)
          .status(HttpStatus.UNAUTHORIZED)
          .build();
    }
    var user = userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    String initialPassword = user.getPassword();
    if(Objects.equals(currentPassword, newPassword)){
      String message = "Mật khẩu mới và mật khẩu hiện tại không được giống nhau";
      return MessageResponse.builder()
          .message(message)
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    if (!passwordEncoder.matches(currentPassword, initialPassword)) {
      String message = "Mật khẩu hiện tại không đúng";
      return MessageResponse.builder()
          .message(message)
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    if (!newPassword.equals(confirmPassword)) {
      String message = "Mật khẩu mới và xác nhận mật khẩu không khớp";
      return MessageResponse.builder()
          .message(message)
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    user.setPassword(encoder.encode(newPassword));
    userRepository.save(user);
    return MessageResponse.builder()
        .message("Thay đổi mật khẩu thành công")
        .status(HttpStatus.OK)
        .build();
  }

  @Override
  public MessageResponse changeNickName(String newNickName) {
    handleUserStatus();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    boolean existUser = userRepository.existsByNickname(newNickName);
    if (existUser) {
      return MessageResponse.builder()
          .message("Biệt danh đã tồn tại")
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    user.setNickname(newNickName);
    userRepository.save(user);
    return MessageResponse.builder()
        .message("Thay đổi biệt danh thành công")
        .status(HttpStatus.OK)
        .build();
  }

  @Override
  public ApplyJobResponse applyJob(ApplyJobRequest request) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    var job = jobRepository.findById(request.getJobId()).orElse(null);
    if (job == null) {
      return ApplyJobResponse.builder()
          .message("Không tìm thấy công việc")
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    if (hasAlreadyApplied(user, job)) {
      return ApplyJobResponse.builder()
          .message("Bạn đã nộp đơn cho công việc này")
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }

    ApplicationForm applicationForm = job.getApplicationForms().stream()
        .filter(applicationForm1 -> Objects.equals(applicationForm1.getCandidate().getId(),
            user.getId()))
        .findFirst()
        .orElseGet(() -> {
          ApplicationForm newApplicationForm = new ApplicationForm();
          newApplicationForm.setCandidate(user);
          newApplicationForm.setJob(job);
          return newApplicationForm;
        });

    applicationForm.setCandidateName(request.getCandidateName());
    applicationForm.setCoverLetter(request.getCoverLetter());
    applicationForm.setStatus(ApplicationStatus.SUBMITTED);

    MultipartFile fileCV = request.getLinkCv();
    if (!isExactFile(fileCV.getOriginalFilename())) {
      return ApplyJobResponse.builder()
          .message("File không phải định dạng .word hoặc .pdf")
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }

    String urlCv = fileService.uploadCv(fileCV);
    applicationForm.setLinkCV(urlCv);
    applicationForm.setSubmittedAt(LocalDate.from(LocalDateTime.now()));
    applicationFormRepository.save(applicationForm);

    return ApplyJobResponse.builder()
        .userId(user.getId())
        .candidateName(applicationForm.getCandidateName())
        .coverLetter(applicationForm.getCoverLetter())
        .submittedAt(applicationForm.getSubmittedAt())
        .linkCv(applicationForm.getLinkCV())
        .jobId(String.valueOf(applicationForm.getJob().getId()))
        .companyName(applicationForm.getJob().getCompany().getName())
        .jobName(applicationForm.getJob().getTitle())
        .applicationStatus(ApplicationStatus.SUBMITTED)
        .message("Nộp đơn ứng tuyển thành công")
        .status(HttpStatus.OK)
        .build();
  }

  @Override
  public MessageResponse uploadUserCv(MultipartFile fileCv) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    if (!isExactFile(fileCv.getOriginalFilename())) {
      return MessageResponse.builder()
          .message("File không phải định dạng .word hoặc .pdf")
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    String urlCv = fileService.uploadCv(fileCv);
    user.setLinkCV(urlCv);
    userRepository.save(user);
    return MessageResponse.builder()
        .message("Upload CV thành công")
        .status(HttpStatus.OK)
        .build();
  }

  @Override
  public MessageResponse writeCoverLetter(String coverLetter) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    user.setCoverLetter(coverLetter);
    userRepository.save(user);
    return MessageResponse.builder()
        .message("Viết cover-letter thành công")
        .status(HttpStatus.OK)
        .build();
  }


  private boolean isExactFile(String fileName) {
    // Determine if the file has an image extension or content type
    String[] fileExtensions = {".word", ".pdf", ".docx"};

    for (String extension : fileExtensions) {
      if (fileName.toLowerCase().endsWith(extension)) {
        return true;
      }
    }
    return false;
  }
  private boolean hasAlreadyApplied(User candidate, Job job) {

    List<ApplicationForm> applicationForms = applicationFormRepository.findByCandidateAndJob(candidate, job);
    return !applicationForms.isEmpty();
  }



}