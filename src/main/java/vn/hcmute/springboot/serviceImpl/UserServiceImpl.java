package vn.hcmute.springboot.serviceImpl;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.exception.UnauthorizedException;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.request.ApplyJobRequest;
import vn.hcmute.springboot.request.FavouriteJobRequest;
import vn.hcmute.springboot.request.WriteReviewRequest;
import vn.hcmute.springboot.response.*;
import vn.hcmute.springboot.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


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
  private final SaveJobRepository saveJobRepository;
  private final FavouriteJobTypeRepository favouriteJobTypeRepository;
  private final JobTypeRepository jobTypeRepository;
  private final SkillRepository skillRepository;
  private final CompanyTypeRepository companyTypeRepository;
  private final CompanyRepository companyRepository;
  private final CompanyReviewRepository companyReviewRepository;
  private final CompanyFollowRepository companyFollowRepository;


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
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    if (user.getStatus().equals(UserStatus.INACTIVE)) {
      String messageError = "Người dùng chưa xác thực";
      throw new BadRequestException(messageError);
    }
    try {
      String newPassword = String.valueOf(otpService.generateNewPassword());
      user.setPassword(encoder.encode(newPassword));
      emailService.sendNewPasswordToEmail(user.getFullName(),email, newPassword);
    } catch (MessagingException e) {
      String messageError = "Không thể gửi mật khẩu mới, vui lòng thử lại";
      throw new BadRequestException(messageError);
    }
    userRepository.save(user);
    return MessageResponse.builder()
            .status(HttpStatus.OK)
            .message("Mật khẩu mới đã được gửi đến email của bạn")
            .build();
  }

  @Override
  public MessageResponse changePassword(String currentPassword, String newPassword,
                                        String confirmPassword) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      String message = "Người dùng chưa đăng nhập";
      throw new UnauthorizedException(message);
    }
    var user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    String initialPassword = user.getPassword();
    if (Objects.equals(currentPassword, newPassword)) {
      String message = "Mật khẩu mới và mật khẩu hiện tại không được giống nhau";
      throw new BadRequestException(message);
    }
    if (!passwordEncoder.matches(currentPassword, initialPassword)) {
      String message = "Mật khẩu hiện tại không đúng";
      throw new BadRequestException(message);
    }
    if (!newPassword.equals(confirmPassword)) {
      String message = "Mật khẩu mới và xác nhận mật khẩu không khớp";
      throw new BadRequestException(message);
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
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    boolean existUser = userRepository.existsByNickname(newNickName);
    if (existUser) {
      throw new BadRequestException("Nickname đã có người sử dụng vui lòng chọn nickname khác");
    }
    user.setNickname(newNickName);
    userRepository.save(user);
    return MessageResponse.builder()
            .message("Thay đổi biệt danh thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public ApplyJobResponse applyJob(Integer jobId,ApplyJobRequest request) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Bạn chưa đăng nhập vui lòng đăng nhập"));
    var username = user.getUsername();
    var job = jobRepository.findById(jobId).orElse(null);
    if (job == null) {
      throw new NotFoundException("Không tìm thấy công việc");
    }
    if (job.getExpireAt().isBefore(java.time.LocalDate.now())) {
      throw new BadRequestException("Hết hạn nộp đơn");
    }
    if (hasAlreadyApplied(user, job)) {
      throw new BadRequestException("Bạn đã nộp đơn cho công việc này");
    }
    if (request.getCoverLetter().length() > 500) {
      throw new BadRequestException("Cover letter không được quá 500 ký tự");
    }
    if (request.getLinkCv() == null) {
      throw new BadRequestException("Vui lòng upload CV");
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
    applicationForm.setIsApplied(true);
    applicationForm.setLinkCV(request.getLinkCv());
    applicationForm.setSubmittedAt(LocalDate.from(LocalDateTime.now()));
    applicationForm.setJob(job);

    List<Job> relatedJobs = jobRepository.findSimilarJobsByTitleAndLocation(jobId, job.getTitle(), job.getLocation().getCityName());
    List<Job> top5RelatedJobs = relatedJobs.stream().limit(5).toList();
    String position = job.getTitle();
    String company = job.getCompany().getName();
    String message = String.format(
            "Chúng tôi đã nhận được CV của bạn cho:%n\nVị trí: %s%nCông ty: %s%nCV của bạn sẽ được gửi tới nhà tuyển dụng sau khi được JobPortal xét duyệt. Vui lòng theo dõi email %s để cập nhật thông tin về tình trạng CV.",
            position, company, username);
    if (job.getApplyCounts() == null) {
      job.setApplyCounts(0);
    }
    job.setApplyCounts(job.getApplyCounts() + 1);
    jobRepository.save(job);
    SaveJobs saveJobs = saveJobRepository.findByCandidateAndJob(user, job);
    if (saveJobs != null) {
      saveJobRepository.delete(saveJobs);
    }
    applicationFormRepository.save(applicationForm);

    return ApplyJobResponse.builder()
            .message(message)
            .status(HttpStatus.OK)
            .relatedJobs(mapJobsToGetJobResponses(top5RelatedJobs))
            .build();
  }


  @Override
  public MessageResponse uploadUserCv(String fileCv) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    user.setLinkCV(fileCv);
    userRepository.save(user);
    if(fileCv!=null){
      user.setLinkCV(fileCv);
      user.setUpdatedCvAt(LocalDateTime.now());
      userRepository.save(user);
    }
    return MessageResponse.builder()
            .message("Đăng tải CV thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse writeCoverLetter(String coverLetter) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    if(coverLetter!=null && user.getCoverLetter()!=null){
      user.setCoverLetter(coverLetter);
      userRepository.save(user);
      return MessageResponse.builder()
              .message("Cập nhật thư xin việc thành công")
              .status(HttpStatus.OK)
              .build();
    }
    if(coverLetter!=null && coverLetter.length()>500){
      throw new BadRequestException("Cover letter không được quá 500 ký tự");
    }
    user.setCoverLetter(coverLetter);
    userRepository.save(user);
    return MessageResponse.builder()
            .message("Viết thư xin việc thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse resetPassword(String email, String currentPassword, String newPassword,
                                       String confirmPassword) {
    var user = userRepository.findByUsername(email)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
      String message = "Mật khẩu hiện tại không đúng";
      throw new BadRequestException(message);
    }
    if (Objects.equals(currentPassword, newPassword)) {
      String message = "Mật khẩu mới và mật khẩu hiện tại không được giống nhau";
      throw new BadRequestException(message);
    }

    if (!newPassword.equals(confirmPassword)) {
      String message = "Mật khẩu mới và xác nhận mật khẩu không khớp";
      throw new BadRequestException(message);
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    return MessageResponse.builder()
            .message("Thay đổi mật khẩu thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public void saveJob(Integer jobId) throws IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));
    var job = jobRepository.findById(jobId)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công việc"));
    var jobAlreadySaved = saveJobRepository.existsByCandidateAndJob(user, job);
    if (jobAlreadySaved) {
      throw new BadRequestException("Bạn đã lưu công việc này trước đó");
    }
    if (hasAlreadyApplied(user, job)) {
      throw new BadRequestException("Bạn đã nộp đơn cho công việc này");
    }
    SaveJobs saveJobs = new SaveJobs();
    saveJobs.setJob(job);
    saveJobs.setCandidate(user);
    saveJobs.setIsSaved(true);
    saveJobs.setSaveAt(LocalDateTime.now());
    saveJobRepository.save(saveJobs);
    SaveJobResponse.builder()
            .message("Lưu công việc thành công")
            .status(HttpStatus.OK)
            .build();
  }


  @Override
  public List<SaveJobResponse> getSavedJobs(User user) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    if (user != null) {

      List<SaveJobs> savedJobs = saveJobRepository.findByCandidate(user);
      List<Job> jobs = savedJobs.stream()
              .map(SaveJobs::getJob)
              .toList();
      return jobs.stream()
              .map(this::mapToSaveJobResponse)
              .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  @Override
  public Page<JobApplyResponse> getAppliedJobs(User user, Pageable pageRequest) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    if (user != null) {
      List<ApplicationForm> applicationForms = applicationFormRepository.findByCandidate(user);
      List<Job> appliedJobs = applicationForms.stream()
              .map(ApplicationForm::getJob)
              .toList();
      int pageSize = pageRequest.getPageSize();
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageSize), appliedJobs.size());
      List<Job> pageContent = appliedJobs.subList(start, end);
      List<JobApplyResponse> jobApplyResponses = pageContent.stream()
              .map(this::mapToApplyJobResponse)
              .toList();
      return new PageImpl<>(jobApplyResponses, pageRequest, appliedJobs.size());
    }
    return new PageImpl<>(Collections.emptyList());
  }

  @Override
  public MessageResponse deleteSaveJobs(Integer id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    var job = jobRepository.findById(id).orElse(null);
    var saveJob = saveJobRepository.findByCandidateAndJob(user, job);
    if (user != null) {
      saveJobRepository.delete(saveJob);
    }

    return MessageResponse.builder()
            .message("Xóa công việc đã lưu thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse saveFavouriteJobType(FavouriteJobRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    List<Skill> skills = updateSkills(request.getSkills());
    if (user == null) {
      throw new UnauthorizedException("Không tìm thấy người dùng");
    }
    if (request.getId() != null) {
      FavouriteJobType existingFavouriteJobType = favouriteJobTypeRepository.findById(request.getId())
              .orElseThrow(() -> new NotFoundException("Không tìm thấy loại công việc yêu thích"));
      existingFavouriteJobType.setMinSalary(request.getMinSalary());
      existingFavouriteJobType.setMaxSalary(request.getMaxSalary());
      existingFavouriteJobType.setCurrentSalary(request.getCurrentSalary());
      existingFavouriteJobType.setLocations(request.getJobLocation().toString());
      existingFavouriteJobType.setExperience(request.getExperiences().toString());
      existingFavouriteJobType.setCompanySize(request.getCompanySize().toString());
      existingFavouriteJobType.setJobTypeSkills(skills);
      List<CompanyType> companyTypes = updateCompanyTypes(request.getCompanyType());
      existingFavouriteJobType.setCompanyTypes(companyTypes);
      List<JobType> jobTypes = updateJobTypes(request.getJobType());
      existingFavouriteJobType.setJobTypes(jobTypes);
      favouriteJobTypeRepository.save(existingFavouriteJobType);
      return MessageResponse.builder()
              .message("Cập nhật công việc ưa thích thành công")
              .status(HttpStatus.OK)
              .build();

    } else {
      var favouriteJobType = new FavouriteJobType();
      List<Skill> favouriteSkill = updateSkills(request.getSkills());
      favouriteJobType.setJobTypeSkills(favouriteSkill);

      List<CompanyType> companyTypes = updateCompanyTypes(request.getCompanyType());
      favouriteJobType.setCompanyTypes(companyTypes);

      List<JobType> jobTypes = updateJobTypes(request.getJobType());
      favouriteJobType.setJobTypes(jobTypes);
      var companySizeList = request.getCompanySize();
      favouriteJobType.setCandidate(user);
      favouriteJobType.setMinSalary(request.getMinSalary());
      favouriteJobType.setMaxSalary(request.getMaxSalary());
      favouriteJobType.setCurrentSalary(request.getCurrentSalary());
      favouriteJobType.setLocations(request.getJobLocation().toString());
      favouriteJobType.setExperience(request.getExperiences().toString());
      favouriteJobType.setCompanySize(companySizeList.toString());
      favouriteJobTypeRepository.save(favouriteJobType);
      if (request.getSkills().size() > 5) {
        throw new BadRequestException("Bạn chỉ có thể chọn tối đa 5 kỹ năng");
      }
      if (request.getExperiences().size() > 5) {
        throw new BadRequestException("Bạn chỉ có thể chọn tối đa 5 kinh nghiệm và trình độ");
      }
      if (request.getJobType().size() > 3) {
        throw new BadRequestException("Bạn chỉ có thể chọn tối đa 5 loại công việc");
      }
      if (request.getCompanyType().size() > 3) {
        throw new BadRequestException("Bạn chỉ có thể chọn tối đa 5 loại công ty");
      }
      return MessageResponse.builder()
              .message("Lưu công việc ưa thích thành công")
              .status(HttpStatus.OK)
              .build();
    }


  }

  @Override
  public MessageResponse writeCompanyReview(Integer companyId, WriteReviewRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    var company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));
    if (user == null || company == null) {
      return MessageResponse.builder()
              .message("Không tìm thấy nhà tuyển dụng")
              .status(HttpStatus.NOT_FOUND)
              .build();
    } else {
      CompanyReview companyReview = new CompanyReview();
      companyReview.setCandidate(user);
      companyReview.setCompany(company);
      companyReview.setRating(request.getRating());
      companyReview.setTitle(request.getTitle());
      companyReview.setContent(request.getContent());
      companyReview.setCreatedDate(LocalDate.now());
      companyReview.setStatus(CompanyReviewStatus.PENDING);
      companyReviewRepository.save(companyReview);
      if (request.getRating() < 1 || request.getRating() > 5) {
        throw new BadRequestException("Đánh giá phải từ 1 đến 5 sao");
      }
      if (request.getTitle().length() > 100) {
        throw new BadRequestException("Tiêu đề đánh giá không được quá 100 ký tự");
      }

      if (request.getContent().length() > 500) {
        throw new BadRequestException("Nội dung đánh giá không được quá 500 ký tự");
      }

    }
    return MessageResponse.builder()
            .message("Viết đánh giá thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public void followCompany(Integer companyId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    var company = companyRepository.findById(companyId);
    if (company.isEmpty()) {
      throw new NotFoundException("Không tìm thấy nhà tuyển dụng");
    }
    var existingFollow = companyFollowRepository.findByUserIdAndCompanyId(user.getId(),
            company.get().getId());
    if (existingFollow != null) {
      throw new BadRequestException("Bạn đã theo dõi công ty này trước đó");
    }
    CompanyFollow companyFollow = new CompanyFollow();
    companyFollow.setFollowedAt(LocalDate.now().atStartOfDay());
    companyFollow.setUser(user);
    companyFollow.setCompany(company.get());
    companyFollowRepository.save(companyFollow);
    MessageResponse.builder()
            .message("Theo dõi công ty thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse activeAccount(String userName, String adminEmail) throws MessagingException {
    try {
      emailService.sendReasonToActiveFromUser(userName, adminEmail);
      return MessageResponse.builder()
              .message("Gửi yêu cầu kích hoạt tài khoản thành công")
              .status(HttpStatus.OK)
              .build();
    } catch (MessagingException e) {
      throw new BadRequestException("Không thể gửi yêu cầu kích hoạt tài khoản");
    }

  }

  private List<Skill> updateSkills(List<String> skillNames) {
    List<Skill> skills = new ArrayList<>();
    for (String skillName : skillNames) {
      Skill skill = skillRepository.findByName(skillName);
      if (skill != null) {
        skills.add(skill);
      }
    }
    return skills;
  }

  private List<CompanyType> updateCompanyTypes(List<String> companyTypeNames) {
    List<CompanyType> companyTypes = new ArrayList<>();
    for (String companyTypeStr : companyTypeNames) {
      CompanyType companyType = companyTypeRepository.findByType(companyTypeStr);
      if (companyType != null) {
        companyTypes.add(companyType);
      }
    }
    return companyTypes;
  }

  private List<JobType> updateJobTypes(List<String> jobTypeNames) {
    List<JobType> jobTypes = new ArrayList<>();
    for (String jobTypeStr : jobTypeNames) {
      JobType jobType = jobTypeRepository.findByJobType(jobTypeStr);
      if (jobType != null) {
        jobTypes.add(jobType);
      }
    }
    return jobTypes;
  }



  private boolean hasAlreadyApplied(User candidate, Job job) {

    ApplicationForm applicationForms = applicationFormRepository.findByCandidateAndJob(
            candidate, job);
    return applicationForms != null;
  }

  private List<GetJobResponse> mapJobsToGetJobResponses(List<Job> jobs) {
    return jobs.stream()
            .map(this::mapToGetJobResponse)
            .collect(Collectors.toList());
  }

  private GetJobResponse mapToGetJobResponse(Job job) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    Optional<User> userOpt = userRepository.findByUsername(userName);

    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle)
            .toList();

    boolean isSaved = false;
    boolean isApplied = false;

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      var savedJob = saveJobRepository.findByCandidateAndJob(user, job);
      if (savedJob != null) {
        isSaved = savedJob.getIsSaved();
      }

    }

    return GetJobResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .address(job.getCompany().getAddress())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .minSalary(job.getMinSalary())
            .maxSalary(job.getMaxSalary())
            .isSaved(isSaved)
            .isApplied(isApplied)
            .appliedAt(null)
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .build();
  }


  private SaveJobResponse mapToSaveJobResponse(Job job) {
    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle) // Assuming 'name' is an attribute in Skill
            .toList();
    return SaveJobResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .address(job.getCompany().getAddress())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .isSaved(true)
            .build();

  }

  private JobApplyResponse mapToApplyJobResponse(Job job) {
    var skills = skillRepository.findSkillByJob(job);
    List<String> skillNames = skills.stream()
            .map(Skill::getTitle)
            .toList();
    return JobApplyResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(job.getCompany().getName())
            .address(job.getCompany().getAddress())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .isApplied(true)
            .build();

  }


}