package vn.hcmute.springboot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.*;
import vn.hcmute.springboot.serviceImpl.UserServiceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserServiceImpl userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JobRepository jobRepository;
  private final ApplicationFormRepository applicationFormRepository;
  private final SaveJobRepository saveJobRepository;
  private final CompanyRepository companyRepository;
  private final CompanyReviewRepository companyReviewRepository;
  private final CompanyFollowRepository companyFollowRepository;
  private final SkillRepository skillRepository;

  @PostMapping("/forgot-password")
  public ResponseEntity<MessageResponse> forgotPassword(
          @Valid @RequestBody ForgotPasswordRequest request) {

    var user = userRepository.findByUsername(request.getEmail());
    if (user.isEmpty()) {
      return new ResponseEntity<>(
              (new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND)),
              HttpStatus.NOT_FOUND);
    }
    if (request.getEmail() == null) {
      return new ResponseEntity<>(
              (new MessageResponse("Không thể gửi mật khẩu mới tới email của bạn",
                      HttpStatus.BAD_REQUEST)), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(userService.sendNewPasswordToEmail(request.getEmail()),
            HttpStatus.OK);
  }

  @PostMapping("/change-password")
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ChangePasswordRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }

    String userName = authentication.getName();

    var user = userRepository.findByUsername(userName);
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND));
    }

    String initialPassword = user.get().getPassword();
    if (request.getCurrentPassword().equals(request.getNewPassword())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
              new MessageResponse("Mật khẩu mới và mật khẩu hiện tại không được giống nhau",
                      HttpStatus.BAD_REQUEST));
    }
    if (!passwordEncoder.matches(request.getCurrentPassword(), initialPassword)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new MessageResponse("Mật khẩu hiện tại không đúng", HttpStatus.BAD_REQUEST));
    }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
              new MessageResponse("Mật khẩu mới và xác nhận mật khẩu không khớp",
                      HttpStatus.BAD_REQUEST));
    }

    MessageResponse response = userService.changePassword(request.getCurrentPassword(),
            request.getNewPassword(), request.getConfirmPassword());

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
    if (userName == null) {
      return new ResponseEntity<>(
              new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var existNickName = userRepository.existsByNickname(request.getNewNickName());
    if (existNickName) {
      return new ResponseEntity<>(
              (new MessageResponse("Biệt danh đã tồn tại", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(userService.changeNickName(request.getNewNickName()),
            HttpStatus.OK);
  }

  @PostMapping(value = "/applyJob")
  public ResponseEntity<ApplyJobResponse> applyJob(
          @Valid @RequestBody ApplyJobRequest request) throws IOException {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
              new ApplyJobResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName);
    var job = jobRepository.findById(request.getJobId());

    if (job.isEmpty()) {
      return new ResponseEntity<>(
              (new ApplyJobResponse("Công việc không tồn tại", HttpStatus.NOT_FOUND)),
              HttpStatus.NOT_FOUND);
    }

    if (job.get().getExpireAt().isBefore(java.time.LocalDate.now())) {
      return new ResponseEntity<>(
              (new ApplyJobResponse("Công việc đã hết hạn", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }
    if (hasAlreadyApplied(user.get(), job.get())) {
      return new ResponseEntity<>(
              (new ApplyJobResponse("Bạn đã ứng tuyển công việc này trước đó", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }
    if (request.getCoverLetter().length() > 500) {
      return new ResponseEntity<>(
              (new ApplyJobResponse("Thư xin việc không được quá 500 ký tự", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }
    if (request.getLinkCv() == null) {
      return new ResponseEntity<>(
              (new ApplyJobResponse("Bạn chưa đính kèm CV", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }
    if (user.get().getLinkCV() == null && request.getLinkCv() == null) {
      return new ResponseEntity<>(
              (new ApplyJobResponse("Bạn chưa tải CV lên hệ thống", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }
    userService.applyJob(request);
    var relatedJobs = jobRepository.findSimilarJobsByTitleAndLocation(request.getJobId(),
            job.get().getTitle(), job.get().getLocation().getCityName());
    List<Job> top5RelatedJobs = relatedJobs.stream().limit(5).toList();
    String position = job.get().getTitle();
    String company = job.get().getCompany().getName();
    String message = String.format(
            "Chúng tôi đã nhận được CV của bạn cho:%n\nVị trí: %s%nCông ty: %s%nCV của bạn sẽ được gửi tới nhà tuyển dụng sau khi được JobPortal xét duyệt. Vui lòng theo dõi email %s để cập nhật thông tin về tình trạng CV.",
            position, company, userName);
    SaveJobs saveJobs = saveJobRepository.findByCandidateAndJob(user.get(), job.get());
    if (saveJobs != null) {
      saveJobRepository.delete(saveJobs);
    }
    ApplyJobResponse applyJobResponse = ApplyJobResponse.builder()
            .message(message)
            .status(HttpStatus.OK)
            .relatedJobs(mapJobsToGetJobResponses(top5RelatedJobs))
            .build();
    return new ResponseEntity<>(applyJobResponse, HttpStatus.OK);
  }

  @GetMapping("/appliedJobs")
  public ResponseEntity<GetJobApplyResponse> getAppliedJobs(
          @RequestParam(value = "sort", defaultValue = "Ngày ứng tuyển gần nhất") String sort,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "20") int size) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
              new GetJobApplyResponse("Người dùng chưa đăng nhập", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName);
    PageRequest pageRequest = PageRequest.of(page, size);

    Page<Job> appliedJob = userService.getAppliedJobs(user.get(), pageRequest);

    List<Job> newestSubmitted = appliedJob.getContent()
            .stream()
            .sorted((job1, job2) -> {
              LocalDate submittedAt1 = getLatestSubmittedAt(user.get(), job1);
              LocalDate submittedAt2 = getLatestSubmittedAt(user.get(), job2);
              return submittedAt2.compareTo(submittedAt1);
            })
            .collect(Collectors.toList());

    List<Job> oldestSubmitted = appliedJob.getContent()
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
      return new ResponseEntity<>(
              new GetJobApplyResponse("Bạn có 0 việc làm ứng tuyển", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    List<GetJobResponse> getJobResponses = sortedPage.getContent().stream()
            .map(this::mapToGetJobResponse) // Assuming there's a method to map Job to GetJobResponse
            .collect(Collectors.toList());
    Page<GetJobResponse> getJobResponsesPage = new PageImpl<>(getJobResponses, pageRequest, appliedJob.getTotalElements());
    return new ResponseEntity<>(new GetJobApplyResponse(getJobResponsesPage), HttpStatus.OK);

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
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
              new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(userService.writeCoverLetter(coverLetter), HttpStatus.OK);
  }

  @PostMapping(value = "/uploadUserCv", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> uploadUserCv(
          @Valid @RequestParam("fileCv") MultipartFile fileCv) throws IOException {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
              new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(userService.uploadUserCv(fileCv), HttpStatus.OK);
  }

  @PostMapping("/reset-password")
  @Valid
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
    Optional<User> user = userRepository.findByUsername(request.getEmail());
    if (user.isEmpty()) {
      return new ResponseEntity<>(
              (new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND)),
              HttpStatus.NOT_FOUND);
    }

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.get().getPassword())
            && !Objects.equals(request.getCurrentPassword(), request.getNewPassword())) {
      String message = "Mật khẩu hiện tại không đúng";
      return new ResponseEntity<>(new MessageResponse(message, HttpStatus.BAD_REQUEST),
              HttpStatus.BAD_REQUEST);
    }
    if (Objects.equals(request.getCurrentPassword(), request.getNewPassword())) {
      String message = "Mật khẩu mới và mật khẩu hiện tại không được giống nhau";
      return new ResponseEntity<>(new MessageResponse(message, HttpStatus.BAD_REQUEST),
              HttpStatus.BAD_REQUEST);
    }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      String message = "Mật khẩu mới và xác nhận mật khẩu không khớp";
      return new ResponseEntity<>(new MessageResponse(message, HttpStatus.BAD_REQUEST),
              HttpStatus.BAD_REQUEST);
    }

    var resetPassword = userService.resetPassword(request.getEmail(), request.getCurrentPassword(),
            request.getNewPassword(), request.getConfirmPassword());
    return new ResponseEntity<>(resetPassword, HttpStatus.OK);
  }

  @GetMapping("/userCv")
  public ResponseEntity<UserCvResponse> getUserCv() {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
              new UserCvResponse("Người dùng chưa đăng nhập", HttpStatus.NOT_FOUND),
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
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName);
    var job = jobRepository.findById(jobId);
    boolean jobAlreadySaved = saveJobRepository.existsByCandidateAndJob(user.get(), job.get());

    if (jobAlreadySaved) {
      return new ResponseEntity<>(
              new MessageResponse("Công việc đã được lưu trước đó", HttpStatus.BAD_REQUEST),
              HttpStatus.BAD_REQUEST);
    }

    if (hasAlreadyApplied(user.get(), job.get())) {
      return new ResponseEntity<>(
              (new MessageResponse("Bạn đã ứng tuyển công việc này trước đó", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }

    userService.saveJob(jobId);
    return new ResponseEntity<>(new MessageResponse("Lưu công việc thành công", HttpStatus.OK),
            HttpStatus.OK);
  }

  @GetMapping("/savedJobs")
  public ResponseEntity<SaveJobResponse> getSavedJobs(
          @RequestParam(value = "sort", defaultValue = "Ngày hết hạn gần nhất") String sort) {

    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
              SaveJobResponse.builder()
                      .message("Người dùng chưa đăng nhập")
                      .status(HttpStatus.NOT_FOUND)
                      .build(),
              HttpStatus.NOT_FOUND);
    }

    var user = userRepository.findByUsername(userName);
    List<Job> savedJobs = userService.getSavedJobs(user.get());

    if (savedJobs.isEmpty()) {
      return new ResponseEntity<>(
              SaveJobResponse.builder()
                      .message("Bạn có 0 Việc làm đã lưu")
                      .status(HttpStatus.NOT_FOUND)
                      .build(),
              HttpStatus.NOT_FOUND);
    }

    if ("Ngày hết hạn gần nhất".equals(sort)) {
      savedJobs = savedJobs.stream()
              .sorted(Comparator.comparing(Job::getExpireAt))
              .collect(Collectors.toList());
    } else {
      savedJobs = savedJobs.stream()
              .sorted(Comparator.comparing(Job::getCreatedAt).reversed())
              .collect(Collectors.toList());
    }

    List<GetJobResponse> getJobResponses = savedJobs.stream()
            .map(this::mapToGetJobResponse) // Assuming there's a method to map Job to GetJobResponse
            .collect(Collectors.toList());

    if (savedJobs.size() > 20) {
      return new ResponseEntity<>(
              SaveJobResponse.builder()
                      .message("Bạn chỉ có thể lưu tối đa 20 công việc")
                      .status(HttpStatus.BAD_REQUEST)
                      .build(),
              HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(
            SaveJobResponse.builder()
                    .jobs(getJobResponses)
                    .status(HttpStatus.OK)
                    .build(),
            HttpStatus.OK);
  }

  private boolean hasAlreadyApplied(User candidate, Job job) {

    List<ApplicationForm> applicationForms = applicationFormRepository.findByCandidateAndJob(
            candidate, job);
    return !applicationForms.isEmpty();
  }

  @DeleteMapping("/saveJobs/{id}")
  public ResponseEntity<MessageResponse> deleteSaveJobs(@PathVariable Integer id) {
    var saveJobs = userService.deleteSaveJobs(id);
    if (saveJobs == null) {
      return new ResponseEntity<>(
              (new MessageResponse("Bạn chưa lưu công việc này", HttpStatus.NOT_FOUND)),
              HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(saveJobs, HttpStatus.OK);

  }

  @PostMapping(value = "/saveFavouriteJobType", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> saveFavouriteJobType(
          @Valid @ModelAttribute FavouriteJobRequest request) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
              new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName);
    if (user.isEmpty()) {
      return new ResponseEntity<>(
              (new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND)),
              HttpStatus.NOT_FOUND);
    }
    if (request.getSkills().size() > 5) {
      return new ResponseEntity<>(
              (new MessageResponse("Bạn chỉ có thể chọn tối đa 5 kỹ năng", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }
    if (request.getExperiences().size() > 5) {
      return new ResponseEntity<>(
              (new MessageResponse("Bạn chỉ có thể chọn tối đa 5 kinh nghiệm", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }
    if (request.getJobType().size() > 3) {
      return new ResponseEntity<>(
              (new MessageResponse("Bạn chỉ có thể chọn tối đa 5 loại công việc",
                      HttpStatus.BAD_REQUEST)), HttpStatus.BAD_REQUEST);
    }
    if (request.getCompanyType().size() > 3) {
      return new ResponseEntity<>((new MessageResponse("Bạn chỉ có thể chọn tối đa 5 loại công ty",
              HttpStatus.BAD_REQUEST)), HttpStatus.BAD_REQUEST);
    }
    var saveJob = userService.saveFavouriteJobType(request);
    return new ResponseEntity<>(saveJob, HttpStatus.OK);
  }

  @PostMapping(value = "/{companyId}/writeCompanyReview")
  public ResponseEntity<MessageResponse> writeCompanyReview(@PathVariable Integer companyId,
                                                            @Valid @RequestBody WriteReviewRequest content) {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (userName == null) {
      return new ResponseEntity<>(
              new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var company = companyRepository.findById(companyId);
    if (company.isEmpty()) {
      return new ResponseEntity<>(
              (new MessageResponse("Công ty không tồn tại", HttpStatus.NOT_FOUND)),
              HttpStatus.NOT_FOUND);
    }
    if (content.getRating() < 1 || content.getRating() > 5) {
      return new ResponseEntity<>(
              (new MessageResponse("Đánh giá phải từ 1 đến 5 sao", HttpStatus.BAD_REQUEST)),
              HttpStatus.BAD_REQUEST);
    }
    if (content.getTitle().length() > 100) {
      return new ResponseEntity<>(
              (new MessageResponse("Tiêu đề đánh giá không được quá 100 ký tự",
                      HttpStatus.BAD_REQUEST)), HttpStatus.BAD_REQUEST);
    }

    if (content.getContent().length() > 500) {
      return new ResponseEntity<>(
              (new MessageResponse("Nội dung đánh giá không được quá 500 ký tự",
                      HttpStatus.BAD_REQUEST)), HttpStatus.BAD_REQUEST);
    }
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
                    .build())
            .toList();
    Page<CompanyReviewResponse> companyReviewResponsePage = new PageImpl<>(
            companyReviewResponses, pageable, companyReviewPage.getTotalElements());
    return ResponseEntity.ok(companyReviewResponsePage);
  }

  @PostMapping("/followCompany/{companyId}")
  public ResponseEntity<MessageResponse> followCompany(@PathVariable Integer companyId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    var user = userRepository.findByUsername(authentication.getName());
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new MessageResponse("Người dùng không tồn tại", HttpStatus.NOT_FOUND));
    }
    var company = companyRepository.findById(companyId);
    if (company.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(new MessageResponse("Công ty không tồn tại", HttpStatus.NOT_FOUND));
    }
    var existingFollow = companyFollowRepository.findByUserIdAndCompanyId(user.get().getId(),
            company.get().getId());
    if (existingFollow != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(
                      new MessageResponse("Bạn đã theo dõi công ty này trước đó", HttpStatus.BAD_REQUEST));
    }
    userService.followCompany(company.get().getId());
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

  private List<GetJobResponse> mapJobsToGetJobResponses(List<Job> jobs) {
    return jobs.stream()
            .map(this::mapToGetJobResponse)
            .collect(Collectors.toList());
  }

  private GetJobResponse mapToGetJobResponse(Job job) {

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
}


