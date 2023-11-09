package vn.hcmute.springboot.serviceImpl;


import jakarta.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.request.ApplyJobRequest;
import vn.hcmute.springboot.request.FavouriteJobRequest;
import vn.hcmute.springboot.request.WriteReviewRequest;
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
  public MessageResponse changePassword(String currentPassword, String newPassword,
      String confirmPassword) {
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
    if (Objects.equals(currentPassword, newPassword)) {
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
  public void applyJob(ApplyJobRequest request) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Bạn chưa đăng nhập vui lòng đăng nhập"));
    var job = jobRepository.findById(request.getJobId()).orElse(null);
    if (job == null) {
      ApplyJobResponse.builder()
          .message("Không tìm thấy công việc")
          .status(HttpStatus.BAD_REQUEST)
          .build();
      return;
    }
    if (job.getExpireAt().isBefore(java.time.LocalDate.now())) {
      ApplyJobResponse.builder()
          .message("Công việc đã hết hạn")
          .status(HttpStatus.BAD_REQUEST)
          .build();
      return;
    }
    if (hasAlreadyApplied(user, job)) {
      ApplyJobResponse.builder()
          .message("Bạn đã nộp đơn cho công việc này")
          .status(HttpStatus.BAD_REQUEST)
          .build();
      return;
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
    //condition when upload CV
    if (user.getLinkCV() != null) {
      if (request.getLinkCv() == null && request.getLinkNewCv() != null) {
        String linkNewCv = fileService.uploadCv(request.getLinkNewCv());
        applicationForm.setLinkCV(linkNewCv);
      } else if (request.getLinkCv() != null && request.getLinkNewCv() == null) {
        String linkCv = user.getLinkCV();
        applicationForm.setLinkCV(linkCv);
      } else {
        ApplyJobResponse.builder()
            .message("Bạn chỉ được upload 1 file")
            .status(HttpStatus.BAD_REQUEST)
            .build();
        return;
      }
    } else {
      if (request.getLinkNewCv() != null) {
        String urlCv = fileService.uploadCv(request.getLinkNewCv());
        applicationForm.setLinkCV(urlCv);
        user.setLinkCV(urlCv);
      } else {
        ApplyJobResponse.builder()
            .message("Bạn cần tải lên một liên kết CV mới")
            .status(HttpStatus.BAD_REQUEST)
            .build();
        return;
      }
    }


    applicationForm.setSubmittedAt(LocalDate.from(LocalDateTime.now()));
    List<Job> relatedJobs = jobRepository.findSimilarJobsByTitleAndLocation(request.getJobId(),job.getTitle(),job.getLocation().getCityName());
    applicationFormRepository.save(applicationForm);
    ApplyJobResponse.builder()
        .message("Nộp đơn thành công")
        .relatedJobs(relatedJobs)
        .status(HttpStatus.OK)
        .relatedJobs(relatedJobs)
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

  @Override
  public MessageResponse resetPassword(String email, String currentPassword, String newPassword,
      String confirmPassword) {
    var user = userRepository.findByUsername(email)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

    if (!passwordEncoder.matches(currentPassword, user.getPassword())
        && !Objects.equals(currentPassword, newPassword)) {
      String message = "Mật khẩu hiện tại không đúng";
      return MessageResponse.builder()
          .message(message)
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    if (Objects.equals(currentPassword, newPassword)) {
      String message = "Mật khẩu mới và mật khẩu hiện tại không được giống nhau";
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
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    return MessageResponse.builder()
        .message("Thay đổi mật khẩu thành công")
        .status(HttpStatus.OK)
        .build();
  }

  @Override
  public void saveJob(Integer jobId) throws IOException {
    var job = jobRepository.findById(jobId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy công việc"));
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    if (job != null) {
      SaveJobs saveJobs = new SaveJobs();
      saveJobs.setJob(job);
      saveJobs.setCandidate(user);
      saveJobs.setIsSaved(true);
      saveJobs.setSaveAt(LocalDateTime.now());
      saveJobRepository.save(saveJobs);

    }
    MessageResponse.builder()
        .message("Lưu công việc thành công")
        .status(HttpStatus.OK)
        .build();
  }


  @Override
  public List<Job> getSavedJobs(User user) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    if (user != null) {
      List<SaveJobs> savedJobs = saveJobRepository.findByCandidate(user);
      return savedJobs.stream()
          .map(SaveJobs::getJob)
          .toList();
    }

    return Collections.emptyList();
  }

  @Override
  public Page<Job>  getAppliedJobs(User user,Pageable pageRequest) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    if (user != null) {
      List<ApplicationForm> applicationForms = applicationFormRepository.findByCandidate(user);
      List<Job> appliedJobs = applicationForms.stream()
          .map(ApplicationForm::getJob)
          .toList();
      int pageSize = pageRequest.getPageSize();
      int start = (int) pageRequest.getOffset();
      int end = Math.min((start + pageSize), appliedJobs.size());
      List<Job> pageContent = appliedJobs.subList(start, end);
      return new PageImpl<>(pageContent, pageRequest, appliedJobs.size());
    }
    return new PageImpl<>(Collections.emptyList());
  }

  @Override
  public MessageResponse deleteSaveJobs(Integer id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    if (user != null) {
      saveJobRepository.deleteById(id);
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
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    List<Skill> skills = updateSkills(request.getSkills());
    if(user==null){
      return MessageResponse.builder()
          .message("Người dùng chưa đăng nhập")
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    if(request.getId()!=null){
      FavouriteJobType existingFavouriteJobType = favouriteJobTypeRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy FavouriteJobType"));
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

    }
    else{
      var favouriteJobType =new FavouriteJobType();
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
      return MessageResponse.builder()
          .message("Lưu công việc ưa thích thành công")
          .status(HttpStatus.OK)
          .build();
    }



  }

  @Override
  public MessageResponse writeCompanyReview(Integer companyId,WriteReviewRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    var company = companyRepository.findById(companyId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));
    if (user == null || company == null) {
      return MessageResponse.builder()
          .message("User or company not found")
          .status(HttpStatus.NOT_FOUND)
          .build();
    }
    else {
      CompanyReview companyReview = new CompanyReview();
      companyReview.setCandidate(user);
      companyReview.setCompany(company);
      companyReview.setRating(request.getRating());
      companyReview.setTitle(request.getTitle());
      companyReview.setContent(request.getContent());
      companyReview.setCreatedDate(LocalDate.now());
      companyReviewRepository.save(companyReview);

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
        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
    var company = companyRepository.findById(companyId);
    if(company.isEmpty()){
      MessageResponse.builder()
              .message("Không tồn tại công ty này")
              .status(HttpStatus.BAD_REQUEST)
              .build();
      return;
    }
    if(company.equals(user.getId())){
      MessageResponse.builder()
              .message("Bạn đã theo dõi công ty này trước đó")
              .status(HttpStatus.BAD_REQUEST)
              .build();
      return;
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

    List<ApplicationForm> applicationForms = applicationFormRepository.findByCandidateAndJob(
        candidate, job);
    return !applicationForms.isEmpty();
  }


}