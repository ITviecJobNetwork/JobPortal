package vn.hcmute.springboot.controller;


import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.ApplicationFormResponse;
import vn.hcmute.springboot.response.GetJobResponse;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.EmailService;
import vn.hcmute.springboot.service.RecruiterService;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {
  private final RecruiterRepository recruiterRepository;
  private final RecruiterService recruiterService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final CompanyRepository companyRepository;
  private final JobRepository jobRepository;
  private final ApplicationFormRepository applicationFormRepository;
  private final EmailService emailService;
  private final SkillRepository skillRepository;
  @PostMapping("/register")
  public ResponseEntity<MessageResponse> register(@RequestBody RecruiterRegisterRequest request) {
    var recruiterRegister = recruiterService.registerRecruiter(request);

    return new ResponseEntity<>(recruiterRegister, HttpStatus.OK);
  }
  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {


    var userLogin = recruiterService.loginRecruiter(loginRequest);
    return new ResponseEntity<>(userLogin, HttpStatus.OK);
  }
  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    recruiterService.refreshToken(request, response);
  }

  @PostMapping("/change-password")
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ChangePasswordRequest request) {
    recruiterService.changePassword(request.getCurrentPassword(),
        request.getNewPassword(), request.getConfirmPassword());

    return ResponseEntity.ok(new MessageResponse("Đổi mật khẩu thành công", HttpStatus.OK));

  }
  @PostMapping("/change-nickname")
  public ResponseEntity<MessageResponse> changeNickname(
      @RequestBody ChangeNickNameRequest request) {

    recruiterService.changeNickName(request.getNewNickName());
    return new ResponseEntity<>(new MessageResponse("Thay đổi biệt danh thành công", HttpStatus.OK),
        HttpStatus.OK);
  }

  @PostMapping("/reset-password")
  @Valid
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
    recruiterService.resetPassword(request.getEmail(), request.getCurrentPassword(),
        request.getNewPassword(), request.getConfirmPassword());
    return new ResponseEntity<>(new MessageResponse("Thay đổi mật khẩu thành công", HttpStatus.OK),HttpStatus.OK);
  }


  @PostMapping("/forgot-password")
  public ResponseEntity<MessageResponse> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest request) {
    return new ResponseEntity<>(recruiterService.sendNewPasswordToEmail(request.getEmail()),
        HttpStatus.OK);
  }
  @PostMapping("/update-profile")
  public ResponseEntity<MessageResponse> updateRecruiterProfile(@Valid @RequestBody
      UpdateProfileRecruiterRequest request) {
    recruiterService.updateProfile(request);
    return new ResponseEntity<>(new MessageResponse("Cập nhật thông tin thành công", HttpStatus.OK),
        HttpStatus.OK);

  }


  @PostMapping(value="/create-company", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> createCompany(@Valid @ModelAttribute PostInfoCompanyRequest request) throws IOException {
    recruiterService.createCompany(request);
    return new ResponseEntity<>(new MessageResponse("Tạo thông tin công ty thành công", HttpStatus.OK),
            HttpStatus.OK);
  }

  @PostMapping(value="/update-company",consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> updateCompany(@Valid @ModelAttribute UpdateInfoCompanyRequest request) throws IOException {

    recruiterService.updateCompany(request);
    return new ResponseEntity<>(new MessageResponse("Cập nhât thông tin công ty thành công", HttpStatus.OK),
            HttpStatus.OK);
  }
  @DeleteMapping("/delete-company")
  public ResponseEntity<MessageResponse> deleteCompany() throws IOException {
    recruiterService.deleteCompany();
    return new ResponseEntity<>(new MessageResponse("Xóa thông tin công ty thành công", HttpStatus.OK),
            HttpStatus.OK);
  }
  @PostMapping(value="/post-job")
  public ResponseEntity<MessageResponse> postJob(@Valid @RequestBody PostJobRequest request) throws IOException {
    recruiterService.postJob(request);
    return new ResponseEntity<>(new MessageResponse("Tạo thông tin công việc thành công", HttpStatus.OK),
            HttpStatus.OK);
  }
  @PostMapping(value="/update-job")
  public ResponseEntity<MessageResponse> updateJob(@RequestParam("id")Integer jobId,@Valid @RequestBody UpdateJobRequest request) throws IOException {


    recruiterService.updateJob(jobId,request);
    return new ResponseEntity<>(new MessageResponse("Cập nhât thông tin công việc thành công", HttpStatus.OK),
            HttpStatus.OK);
  }
  @DeleteMapping("/delete-job")
  public ResponseEntity<MessageResponse> deleteJob(@RequestParam("id") Integer jobId) throws IOException {
    recruiterService.deleteJob(jobId);
    return new ResponseEntity<>(new MessageResponse("Xóa thông tin công việc thành công", HttpStatus.OK),
            HttpStatus.OK);
  }

  @GetMapping("/getJob/{jobId}")
  public ResponseEntity<GetJobResponse> getJobById(@PathVariable Integer jobId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if(recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)){
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    var job = jobRepository.findByIdAndRecruiterId(jobId, recruiter.get().getId());
    return ResponseEntity.ok().body(createGetJobResponse(job));

  }


  @GetMapping("getAppliedJob")
  public ResponseEntity<List<ApplicationFormResponse>> getAppliedJob() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    var applicationForms = applicationFormRepository.findByJobCompanyRecruiter(recruiter.get());
    List<ApplicationFormResponse> applicationFormResponses = applicationForms.stream()
            .map(this::mapToApplicationFormResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok().body(applicationFormResponses);
  }


  @GetMapping("/getApplicationById/{applicationId}")
  public ResponseEntity<ApplicationFormResponse> getApplicationById(@PathVariable Integer applicationId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    Optional<ApplicationForm> optionalApplicationForm = Optional.ofNullable(applicationFormRepository.findByIdAndCompanyRecruiter(applicationId, recruiter.get()));
    if (optionalApplicationForm.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    ApplicationForm applicationForm = optionalApplicationForm.get();

    return ResponseEntity.ok().body(mapToApplicationFormResponse(applicationForm));
  }
  @PostMapping("/updateApplication/{applicationId}")
  public ResponseEntity<MessageResponse> updateApplication(@PathVariable Integer applicationId, @RequestBody UpdateApplicationRequest updateRequest) throws MessagingException {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      return new ResponseEntity<>(new MessageResponse("Unauthorized", HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(new MessageResponse("Recruiter not found", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    if (recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)) {
      return new ResponseEntity<>(new MessageResponse("Recruiter is inactive", HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }

    Optional<ApplicationForm> optionalApplicationForm = Optional.ofNullable(applicationFormRepository.findByIdAndCompanyRecruiter(applicationId, recruiter.get()));
    if (optionalApplicationForm.isEmpty()) {
      return new ResponseEntity<>(new MessageResponse("Application not found", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    ApplicationForm applicationForm = optionalApplicationForm.get();
    applicationForm.setStatus(updateRequest.getStatus());
    applicationFormRepository.save(applicationForm);
    emailService.sendApplicationUpdateEmail(applicationForm);

    return new ResponseEntity<>(new MessageResponse("Application updated successfully", HttpStatus.OK), HttpStatus.OK);
  }
  private GetJobResponse createGetJobResponse(Job job) {
    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
            .toList();
    return GetJobResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .address(job.getCompany().getAddress())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt().toLocalDate())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .build();
  }
  private ApplicationFormResponse mapToApplicationFormResponse(ApplicationForm applicationForm) {
    return ApplicationFormResponse.builder()
            .linkCV(applicationForm.getLinkCV())
            .jobId(applicationForm.getJob().getId())
            .jobTitle(applicationForm.getJob().getTitle())
            .candidateName(applicationForm.getCandidateName())
            .submittedAt(applicationForm.getSubmittedAt())
            .coverLetter(applicationForm.getCoverLetter())
            .status(applicationForm.getStatus())
            .build();
  }
}
