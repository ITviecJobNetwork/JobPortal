package vn.hcmute.springboot.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.ApplicationForm;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.SaveJobs;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.repository.ApplicationFormRepository;
import vn.hcmute.springboot.repository.JobRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.ChangeNickNameRequest;
import vn.hcmute.springboot.request.ChangePasswordRequest;
import vn.hcmute.springboot.request.ForgotPasswordRequest;
import vn.hcmute.springboot.request.ResetPasswordRequest;
import vn.hcmute.springboot.response.ApplyJobResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.response.SaveJobResponse;
import vn.hcmute.springboot.response.UserCvResponse;
import vn.hcmute.springboot.serviceImpl.JobServiceImpl;
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
  private final JobServiceImpl jobService;


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



    if(job.get().getExpireAt().isBefore(java.time.LocalDate.now())){
      return new ResponseEntity<>((new ApplyJobResponse("Công việc đã hết hạn",HttpStatus.BAD_REQUEST)),HttpStatus.BAD_REQUEST);
    }
    if(hasAlreadyApplied(user.get(), job.get())){
      return new ResponseEntity<>((new ApplyJobResponse("Bạn đã ứng tuyển công việc này trước đó",HttpStatus.BAD_REQUEST)),HttpStatus.BAD_REQUEST);
    }
    if(request.getCoverLetter().length()>500){
      return new ResponseEntity<>((new ApplyJobResponse("Thư xin việc không được quá 500 ký tự",HttpStatus.BAD_REQUEST)),HttpStatus.BAD_REQUEST);
    }
    if(request.getLinkCv()==null && request.getLinkNewCv()==null){
      return new ResponseEntity<>((new ApplyJobResponse("Bạn chưa đính kèm CV",HttpStatus.BAD_REQUEST)),HttpStatus.BAD_REQUEST);
    }
    if(user.get().getLinkCV()==null && request.getLinkCv()==null){
      return new ResponseEntity<>((new ApplyJobResponse("Bạn chưa tải CV lên hệ thống",HttpStatus.BAD_REQUEST)),HttpStatus.BAD_REQUEST);
    }
    userService.applyJob(request);
    var relatedJobs =jobRepository.findSimilarJobsByTitleAndLocation(request.getJobId(),job.get().getTitle(),job.get().getLocation().getCityName());
    List<Job> top3RelatedJobs = relatedJobs.stream().limit(3).toList();
    String position = job.get().getTitle();
    String company = job.get().getCompany().getName();
    String message = String.format("Chúng tôi đã nhận được CV của bạn cho:%n\nVị trí: %s%nCông ty: %s%nCV của bạn sẽ được gửi tới nhà tuyển dụng sau khi được JobPortal xét duyệt. Vui lòng theo dõi email %s để cập nhật thông tin về tình trạng CV.", position, company, userName);
    return new ResponseEntity<>(new ApplyJobResponse(message,HttpStatus.OK,top3RelatedJobs),HttpStatus.OK);
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

  @PostMapping("/reset-password/{email}")
  @Valid
  public ResponseEntity<MessageResponse> resetPassword(@PathVariable String email,@RequestBody ResetPasswordRequest request) {
    Optional<User> user = userRepository.findByUsername(email);
    if(user.isEmpty()){
      return new ResponseEntity<>((new MessageResponse("Người dùng không tồn tại",HttpStatus.NOT_FOUND)),HttpStatus.NOT_FOUND);
    }

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.get().getPassword()) && !Objects.equals(request.getCurrentPassword(), request.getNewPassword()))
    {
      String message = "Mật khẩu hiện tại không đúng";
      return new ResponseEntity<>(new MessageResponse(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    if(Objects.equals(request.getCurrentPassword(), request.getNewPassword())){
      String message = "Mật khẩu mới và mật khẩu hiện tại không được giống nhau";
      return new ResponseEntity<>(new MessageResponse(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      String message = "Mật khẩu mới và xác nhận mật khẩu không khớp";
      return new ResponseEntity<>(new MessageResponse(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    var resetPassword = userService.resetPassword(email,request.getCurrentPassword(), request.getNewPassword(), request.getConfirmPassword());
    return new ResponseEntity<>(resetPassword,HttpStatus.OK);
  }

  @PostMapping("/saveJob/{jobId}")
  public ResponseEntity<MessageResponse> saveJob(@PathVariable Integer jobId) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Người dùng chưa đăng nhập", HttpStatus.UNAUTHORIZED));
    }
    var userName = authentication.getName();
    var user = userRepository.findByUsername(userName);
    var job = jobRepository.findById(jobId);
    if(hasAlreadyApplied(user.get(),job.get())){
      return new ResponseEntity<>((new MessageResponse("Bạn đã ứng tuyển công việc này trước đó",HttpStatus.BAD_REQUEST)),HttpStatus.BAD_REQUEST);
    }
    userService.saveJob(jobId);
    return new ResponseEntity<>(new MessageResponse("Lưu công việc thành công",HttpStatus.OK),HttpStatus.OK);
  }
  @GetMapping("/getUserCv")
  public ResponseEntity<UserCvResponse> getUserCv() {
    var userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if(userName == null) {
      return new ResponseEntity<>(new UserCvResponse("Người dùng chưa đăng nhập", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    var user = userRepository.findByUsername(userName);
    if(user.isEmpty()){
      return new ResponseEntity<>((new UserCvResponse("Người dùng không tồn tại",HttpStatus.NOT_FOUND)),HttpStatus.NOT_FOUND);
    }
    var linkCv= user.get().getLinkCV();
    if(linkCv==null){
      return new ResponseEntity<>((new UserCvResponse("Bạn chưa tải CV lên hệ thống",HttpStatus.NOT_FOUND)),HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>((new UserCvResponse(linkCv)),HttpStatus.OK);
  }


  @GetMapping("/getSavedJobs")
  public  ResponseEntity<SaveJobResponse> getSavedJobs(@RequestParam(value="sort",defaultValue = "recentExpiredAt") String sort) {
    List<Job> saveJob=userService.getSavedJobs();
    if(saveJob.isEmpty()){
      return new ResponseEntity<>(new SaveJobResponse("Bạn có 0 Việc làm đã lưu",HttpStatus.NOT_FOUND),HttpStatus.NOT_FOUND);
    }
    if("recentExpiredAt".equals(sort)) {

      LocalDateTime now =LocalDateTime.now();
      Job nearestJob = saveJob.stream()
          .min(Comparator.comparing(job -> Duration.between(now, job.getExpireAt().atStartOfDay()).abs()))
          .orElse(null);

      if (nearestJob != null) {
        saveJob = saveJob.stream()
            .sorted(Comparator.comparing(Job::getExpireAt))
            .toList();
      }

    }
    else{
      saveJob = saveJob.stream().sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).toList();
    }


    if(saveJob.size()>20){
      return new ResponseEntity<>(new SaveJobResponse("Bạn chỉ có thể lưu tối đa 20 công việc",HttpStatus.BAD_REQUEST),HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(new SaveJobResponse(saveJob),HttpStatus.OK);
  }

  @GetMapping("/getAppliedJobs")
  public  ResponseEntity<SaveJobResponse> getAppliedJobs(@RequestParam(value="sort",defaultValue = "recentApplied") String sort) {
    List<Job> appliedJob=userService.getAppliedJobs();
    if("recentApplied".equals(sort)) {
      appliedJob = appliedJob.stream().sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())).toList();
    }
    else{
      appliedJob = appliedJob.stream().sorted((o1, o2) -> o2.getExpireAt().compareTo(o1.getExpireAt())).toList();
    }
    if(appliedJob.isEmpty()){
      return new ResponseEntity<>(new SaveJobResponse("Bạn có 0 việc làm ứng tuyển",HttpStatus.NOT_FOUND),HttpStatus.NOT_FOUND);
    }
    if(appliedJob.size()>20){
      return new ResponseEntity<>(new SaveJobResponse("Bạn chỉ có thể ứng tuyển tối đa 20 công việc",HttpStatus.BAD_REQUEST),HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(new SaveJobResponse(appliedJob),HttpStatus.OK);
  }
  private boolean hasAlreadyApplied(User candidate, Job job) {

    List<ApplicationForm> applicationForms = applicationFormRepository.findByCandidateAndJob(candidate, job);
    return !applicationForms.isEmpty();
  }

  @DeleteMapping("/saveJobs/{id}")
  public ResponseEntity<MessageResponse> deleteSaveJobs(@PathVariable Integer id){
    var saveJobs = userService.deleteSaveJobs(id);
    if(saveJobs==null){
      return new ResponseEntity<>((new MessageResponse("Bạn chưa lưu công việc này",HttpStatus.NOT_FOUND)),HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(saveJobs,HttpStatus.OK);

  }


}


