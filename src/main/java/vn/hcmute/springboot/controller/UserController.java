package vn.hcmute.springboot.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.exception.UnauthorizedException;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.*;
import vn.hcmute.springboot.service.UserService;

import java.util.Optional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final CompanyReviewRepository companyReviewRepository;
  private final CompanyFollowRepository companyFollowRepository;
  private final SkillRepository skillRepository;
  private final JobRepository jobRepository;
  @PostMapping("/forgot-password")
  public ResponseEntity<MessageResponse> forgotPassword(
          @Valid @RequestBody ForgotPasswordRequest request) {

    var service = userService.sendNewPasswordToEmail(request.getEmail());
    return new ResponseEntity<>(service, HttpStatus.OK);
  }

  @PostMapping("/change-password")
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ChangePasswordRequest request) {

    var resetPassword = userService.changePassword(request.getCurrentPassword(),
            request.getNewPassword(), request.getConfirmPassword());
    return new ResponseEntity<>(resetPassword, HttpStatus.OK);
  }


  @PostMapping("/change-nickname")
  public ResponseEntity<MessageResponse> changeNickname(
          @RequestBody ChangeNickNameRequest request) {
    var changeNickName = userService.changeNickName(request.getNewNickName());
    return new ResponseEntity<>(changeNickName, HttpStatus.OK);
  }

  @PostMapping(value = "/applyJob")
  public ResponseEntity<ApplyJobResponse> applyJob(
          @Valid @RequestBody ApplyJobRequest request) throws IOException {

    return new ResponseEntity<>(userService.applyJob(request), HttpStatus.OK);
  }

  @GetMapping("/appliedJobs")
  public ResponseEntity<Page<JobApplyResponse>> getAppliedJobs(
          @RequestParam(value = "sort", defaultValue = "Ngày ứng tuyển gần nhất") String sort,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      throw new UnauthorizedException("Người dùng chưa đăng nhập");
    }
    var user = userRepository.findByUsername(userName);
    PageRequest pageRequest = PageRequest.of(page, size);

    Page<JobApplyResponse> appliedJob = userService.getAppliedJobs(user.get(), pageRequest);
    List<Job> job = appliedJob.getContent().stream()
            .map(JobApplyResponse::getJobId)
            .map(jobRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    List<Job> newestSubmitted = job
            .stream()
            .sorted((job1, job2) -> {
              LocalDate submittedAt1 = getLatestSubmittedAt(user.get(), job1);
              LocalDate submittedAt2 = getLatestSubmittedAt(user.get(), job2);
              return submittedAt2.compareTo(submittedAt1);
            })
            .collect(Collectors.toList());

    List<Job> oldestSubmitted = job
            .stream()
            .sorted((job1, job2) -> {
              LocalDate submittedAt1 = getOldestSubmittedAt(user.get(), job1);
              LocalDate submittedAt2 = getOldestSubmittedAt(user.get(), job2);
              return submittedAt1.compareTo(submittedAt2);
            })
            .collect(Collectors.toList());

    List<Job> sortedAppliedJob =
            sort.equals("Ngày ứng tuyển gần nhất") ? oldestSubmitted : newestSubmitted;
    Page<Job> sortedPage = new PageImpl<>(sortedAppliedJob, pageRequest,
            appliedJob.getTotalElements());

    if (appliedJob.isEmpty()) {
      throw new NotFoundException("Bạn chưa ứng tuyển công việc nào");
    }

    List<JobApplyResponse> applyJobResponse = sortedPage.getContent()
            .stream()
            .map(this::mapToApplyJobResponse)
            .toList();
    Page<JobApplyResponse> jobApplyResponsePage = new PageImpl<>(applyJobResponse, pageRequest,
            appliedJob.getTotalElements());
    return new ResponseEntity<>(jobApplyResponsePage, HttpStatus.OK);
  }
  private JobApplyResponse mapToApplyJobResponse(Job job){
    var user = userRepository.findByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName());
    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle)
            .toList();
    var applicationForm = job.getApplicationForms();
    var submittedAt = applicationForm.stream()
            .filter(applicationForm1 -> applicationForm1.getCandidate().equals(user.get()))
            .map(ApplicationForm::getSubmittedAt)
            .findFirst()
            .orElse(null);

    return JobApplyResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .companyId(job.getCompany().getId())
            .address(job.getCompany().getAddress())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt().toLocalDate())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .minSalary(job.getMinSalary())
            .maxSalary(job.getMaxSalary())
            .appliedDate(submittedAt)
            .isApplied(true)
            .build();

  }
  private LocalDate getOldestSubmittedAt(User user, Job job) {
    return job.getApplicationForms()
            .stream()
            .filter(applicationForm -> applicationForm.getCandidate().equals(user))
            .map(ApplicationForm::getSubmittedAt)
            .min(LocalDate::compareTo)
            .orElse(null);
  }

  private LocalDate getLatestSubmittedAt(User user, Job job) {
    return job.getApplicationForms()
            .stream()
            .filter(applicationForm -> applicationForm.getCandidate().equals(user))
            .map(ApplicationForm::getSubmittedAt)
            .max(LocalDate::compareTo)
            .orElse(null);
  }





  @PostMapping("/writeCoverLetter")
  public ResponseEntity<MessageResponse> writeCoverLetter(@RequestBody String coverLetter) {
    return new ResponseEntity<>(userService.writeCoverLetter(coverLetter), HttpStatus.OK);
  }

  @PostMapping(value = "/uploadUserCv")
  public ResponseEntity<MessageResponse> uploadUserCv(
          @Valid @RequestBody String fileCv) throws IOException {
    var uploadCv = userService.uploadUserCv(fileCv);
    return new ResponseEntity<>(uploadCv, HttpStatus.OK);
  }

  @PostMapping("/reset-password")
  @Valid
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
    var resetPassword = userService.resetPassword(request.getEmail(), request.getCurrentPassword(),
            request.getNewPassword(), request.getConfirmPassword());
    return new ResponseEntity<>(resetPassword, HttpStatus.OK);
  }

  @GetMapping("/userCv")
  public ResponseEntity<UserCvResponse> getUserCv() {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
              new UserCvResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName);
    if (user.isEmpty()) {
      return new ResponseEntity<>(
              (new UserCvResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND)),
              HttpStatus.NOT_FOUND);
    }
    var linkCv = user.get().getLinkCV();
    if (linkCv == null) {
      return new ResponseEntity<>(
              (new UserCvResponse("Bạn chưa tải CV lên hệ thống", HttpStatus.NOT_FOUND)),
              HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>((new UserCvResponse(linkCv)), HttpStatus.OK);
  }

  @PostMapping("/saveJob/{jobId}")
  public ResponseEntity<MessageResponse> saveJob(@PathVariable Integer jobId) throws IOException {


    userService.saveJob(jobId);
    return new ResponseEntity<>(new MessageResponse("Lưu công việc thành công", HttpStatus.OK),
            HttpStatus.OK);
  }

  @GetMapping("/savedJobs")
  public ResponseEntity<List<SaveJobResponse>> getSavedJobs(
          @RequestParam(value = "sort", defaultValue = "Ngày hết hạn gần nhất") String sort) {

    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      throw new UnauthorizedException("Người dùng chưa đăng nhập");
    }

    var user = userRepository.findByUsername(userName);
    List<SaveJobResponse> savedJobs = userService.getSavedJobs(user.get());

    if (savedJobs.isEmpty()) {
      throw new NotFoundException("Bạn chưa lưu công việc nào");
    }

    if ("Ngày hết hạn gần nhất".equals(sort)) {
      savedJobs = savedJobs.stream()
              .sorted(Comparator.comparing(SaveJobResponse::getExpiredDate))
              .collect(Collectors.toList());
    } else {
      savedJobs = savedJobs.stream()
              .sorted(Comparator.comparing(SaveJobResponse::getCreatedDate).reversed())
              .collect(Collectors.toList());
    }

    if (savedJobs.size() > 20) {
      throw new NotFoundException("Bạn chỉ có thể lưu tối đa 20 công việc");
    }

    List<Job> jobs = savedJobs.stream()
            .map(SaveJobResponse::getJobId)
            .map(jobRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

    List<SaveJobResponse> saveJobResponse = jobs.stream()
            .map(this::mapToSaveJobResponse)
            .toList();

    return new ResponseEntity<>(saveJobResponse, HttpStatus.OK);
  }

  private SaveJobResponse mapToSaveJobResponse(Job job){
    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
            .toList();
    return SaveJobResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyId(job.getCompany().getId())
            .companyName(job.getCompany().getName())
            .address(job.getCompany().getAddress())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt().toLocalDate())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .isSaved(true)
            .build();

  }

  @DeleteMapping("/saveJobs/{id}")
  public ResponseEntity<MessageResponse> deleteSaveJobs(@PathVariable Integer id) {
    var saveJobs = userService.deleteSaveJobs(id);
    return new ResponseEntity<>(saveJobs, HttpStatus.OK);

  }

  @PostMapping(value = "/saveFavouriteJobType", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> saveFavouriteJobType(
          @Valid @ModelAttribute FavouriteJobRequest request) {
    var saveJob = userService.saveFavouriteJobType(request);
    return new ResponseEntity<>(saveJob, HttpStatus.OK);
  }

  @PostMapping(value = "/{companyId}/writeCompanyReview")
  public ResponseEntity<MessageResponse> writeCompanyReview(@PathVariable Integer companyId,
                                                            @Valid @RequestBody WriteReviewRequest content) {
    var writeReview = userService.writeCompanyReview(companyId, content);
    return new ResponseEntity<>(writeReview, HttpStatus.OK);
  }

  @GetMapping("/companyReview/{companyId}")
  public ResponseEntity<Page<CompanyReviewResponse>> getCompanyReview(
          @PathVariable Integer companyId,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "20") int size
  ) {
    var user = userRepository.findByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName());
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    var company = companyRepository.findById(companyId);
    if (company.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    Pageable pageable = PageRequest.of(page, size);
    Page<CompanyReview> companyReviewPage = companyReviewRepository.findByCompanyId(
            company.get().getId(), pageable);

    if (companyReviewPage.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    List<CompanyReview> companyReviews = companyReviewPage.getContent();
    List<CompanyReviewResponse> companyReviewResponses = companyReviews.stream()
            .map(companyReview -> CompanyReviewResponse.builder()
                    .id(companyReview.getId())
                    .content(companyReview.getContent())
                    .rating(companyReview.getRating())
                    .title(companyReview.getTitle())
                    .localDate(companyReview.getCreatedDate())
                    .username(companyReview.getCandidate().getUsername())
                    .status(companyReview.getStatus())
                    .build())
            .toList();
    Page<CompanyReviewResponse> companyReviewResponsePage = new PageImpl<>(
            companyReviewResponses, pageable, companyReviewPage.getTotalElements());
    return ResponseEntity.ok(companyReviewResponsePage);
  }

  @PostMapping("/followCompany/{companyId}")
  public ResponseEntity<MessageResponse> followCompany(@PathVariable Integer companyId) {
    userService.followCompany(companyId);
    return ResponseEntity.ok(new MessageResponse("Theo dõi thành công", HttpStatus.OK));

  }

  @DeleteMapping("/removeFollowCompany/{companyId}")
  public ResponseEntity<MessageResponse> removeFollowCompany(@PathVariable Integer companyId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    var user = userRepository.findByUsername(authentication.getName());
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.NOT_FOUND));
    }
    var company = companyRepository.findById(companyId);
    if (company.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new MessageResponse("Công ty không tồn tại", HttpStatus.NOT_FOUND));
    }
    var companyFollow = companyFollowRepository.findByUserIdAndCompanyId(user.get().getId(),
            company.get().getId());
    if (companyFollow == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(
                      new MessageResponse("Bạn chưa theo dõi công ty này", HttpStatus.BAD_REQUEST));
    }
    companyFollowRepository.delete(companyFollow);

    return ResponseEntity.ok(new MessageResponse("Bỏ theo dõi thành công", HttpStatus.OK));


  }

  @PostMapping("/activeAccount")
  public ResponseEntity<MessageResponse> activeAccount(@RequestBody ActiveAccountRequest request) throws MessagingException {
    var activeAccount = userService.activeAccount(request.getUserName(), request.getAdminEmail());
    return new ResponseEntity<>(activeAccount, HttpStatus.OK);
  }


  private GetJobResponse mapToGetJobResponse(Job job) {

    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle)
            .toList();
    return GetJobResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .companyId(job.getCompany().getId())
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


}


