package vn.hcmute.springboot.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.exception.UnauthorizedException;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.AuthenticationResponse;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.EmailService;
import vn.hcmute.springboot.service.FileUploadService;
import vn.hcmute.springboot.service.OtpService;
import vn.hcmute.springboot.service.RecruiterService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

  private final RecruiterRepository recruiterRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailService emailService;
  private final OtpService otpService;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder encoder;
  private final FileUploadService fileUploadService;
  private final CompanyRepository companyRepository;
  private final CompanyTypeRepository companyTypeRepository;
  private final JobTypeRepository jobTypeRepository;
  private final LocationRepository locationRepository;
  private final JobRepository jobRepository;
  private final ApplicationFormRepository applicationFormRepository;

  @Override
  public MessageResponse registerRecruiter(RecruiterRegisterRequest recruiterRegisterRequest) {
    var userName = recruiterRepository.existsByUsername(recruiterRegisterRequest.getUsername());
    if (userName) {
      return MessageResponse.builder()
              .message("Email đã có người sử dụng vui lòng chọn nickname khác")
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }

    String password = String.valueOf(otpService.generateNewPassword());
    try {
      emailService.sendConfirmRegistrationToRecruiter(recruiterRegisterRequest.getUsername(),
              password);
    } catch (Exception e) {
      return MessageResponse.builder()
              .message("Email không tồn tại")
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    var recruiter = Recruiters.builder()
            .username(recruiterRegisterRequest.getUsername())
            .password(passwordEncoder.encode(password))
            .companyName(recruiterRegisterRequest.getCompanyName())
            .phoneNumber(recruiterRegisterRequest.getPhoneNumber())
            .locations(recruiterRegisterRequest.getCompanyLocation())
            .status(RecruiterStatus.ACTIVE)
            .build();
    recruiterRepository.save(recruiter);

    String message = "Chúc mừng bạn đã đăng ký tài khoản thành công. Vui lòng kiểm tra email để lấy tài khoản đăng nhập";
    return MessageResponse.builder()
            .message(message)
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public JwtResponse loginRecruiter(LoginRequest request) {
    var recruiter = recruiterRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
    recruiter.setLastSignInTime(LocalDateTime.now());
    recruiterRepository.save(recruiter);
    var jwtToken = jwtService.generateToken(recruiter);
    var refreshToken = jwtService.generateRefreshToken(recruiter);
    revokeAllUserTokens(recruiter);
    saveUserToken(recruiter, jwtToken);
    return JwtResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .id(recruiter.getId())
            .password(recruiter.getPassword())
            .role(Role.RECRUITER)
            .username(recruiter.getUsername())
            .lastSignInTime(LocalDateTime.now())
            .status(HttpStatus.OK)
            .authorities(
                    recruiter.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
            .build();
  }

  @Override
  public void saveUserToken(Recruiters recruiter, String token) {
    var jwtToken = Token.builder()
            .recruiters(recruiter)
            .token(token)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(jwtToken);
  }

  @Override
  public void revokeAllUserTokens(Recruiters recruiter) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(recruiter.getId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.recruiterRepository.findByUsername(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  @Override
  public void changePassword(String currentPassword, String newPassword, String confirmPassword) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      MessageResponse.builder()
              .message("Người dùng chưa đăng nhập")
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));

    var initialPassword = recruiter.getPassword();
    if (!passwordEncoder.matches(currentPassword, initialPassword)) {
      var message = "Mật khẩu hiện tại không đúng";
      MessageResponse.builder()
              .message(message)
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    if (Objects.equals(currentPassword, newPassword)) {
      String message = "Mật khẩu mới và mật khẩu hiện tại không được giống nhau";
      MessageResponse.builder()
              .message(message)
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }

    if (!newPassword.equals(confirmPassword)) {
      var message = "Mật khẩu mới và xác nhận mật khẩu không khớp";
      MessageResponse.builder()
              .message(message)
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    recruiter.setPassword(encoder.encode(newPassword));
    recruiterRepository.save(recruiter);
    MessageResponse.builder()
            .message("Thay đổi mật khẩu thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public void changeNickName(String newNickName) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));
    boolean existRecruiter = recruiterRepository.existsByNickname(newNickName);
    if (existRecruiter) {
      MessageResponse.builder()
              .message("Biệt danh đã tồn tại")
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    recruiter.setNickname(newNickName);
    recruiterRepository.save(recruiter);
    MessageResponse.builder()
            .message("Thay đổi biệt danh thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public void resetPassword(String email, String currentPassword, String newPassword,
                            String confirmPassword) {
    var recruiter = recruiterRepository.findByUsername(email)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));
    var initialPassword = recruiter.getPassword();
    if (!passwordEncoder.matches(currentPassword, initialPassword)) {
      var message = "Mật khẩu được cấp bạn nhập không đúng";
      MessageResponse.builder()
              .message(message)
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    if (Objects.equals(currentPassword, newPassword)) {
      String message = "Mật khẩu mới và mật khẩu hiện tại không được trùng nhau";
      MessageResponse.builder()
              .message(message)
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    if (!newPassword.equals(confirmPassword)) {
      var message = "Mật khẩu mới và mật khẩu xác nhận không khớp";
      MessageResponse.builder()
              .message(message)
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    recruiter.setPassword(encoder.encode(newPassword));
    recruiterRepository.save(recruiter);
    MessageResponse.builder()
            .message("Thay đổi mật khẩu thành công")
            .status(HttpStatus.OK)
            .build();

  }

  @Override
  public MessageResponse sendNewPasswordToEmail(String email) {
    var recruiter = recruiterRepository.findByUsername(email)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));
    if (recruiter.getStatus().equals(RecruiterStatus.INACTIVE)) {
      var messageError = "Nhà tuyển dụng chưa xác thực tài khoản";
      return MessageResponse.builder()
              .status(HttpStatus.UNAUTHORIZED)
              .message(messageError)
              .build();
    }
    try {
      var newPassword = String.valueOf(otpService.generateNewPassword());
      recruiter.setPassword(encoder.encode(newPassword));
      emailService.sendNewPasswordToEmail(email, newPassword);
    } catch (MessagingException e) {
      var messageError = "Không thể gửi mật khẩu mới, vui lòng thử lại";
      return MessageResponse.builder()
              .status(HttpStatus.BAD_REQUEST)
              .message(messageError)
              .build();
    }
    recruiterRepository.save(recruiter);
    return MessageResponse.builder()
            .status(HttpStatus.OK)
            .message("Mật khẩu mới đã được gửi đến email của bạn")
            .build();
  }

  @Override
  public void updateProfile(UpdateProfileRecruiterRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));
    recruiter.setUsername(request.getUsername());
    recruiter.setNickname(request.getNickname());
    recruiter.setFbUrl(request.getFbUrl());
    recruiter.setPhoneNumber(request.getPhoneNumber());
    recruiter.setBirthDate(request.getBirthDate());
    recruiterRepository.save(recruiter);
    MessageResponse.builder()
            .status(HttpStatus.OK)
            .message("Cập nhật thông tin thành công")
            .build();
  }

  @Override
  public void createCompany(PostInfoCompanyRequest request) throws IOException {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));

    MultipartFile companyLogo = request.getCompanyLogo();

    if(request.getCompanyLogo()!=null) {
      if (!isImageFile(companyLogo.getOriginalFilename())) {
       MessageResponse.builder()
                .message("không-phải-file-ảnh")
                .status(HttpStatus.BAD_REQUEST)
                .build();
      }

    }
    String logo = fileUploadService.uploadFile(companyLogo);
    String companyTypeName = request.getCompanyType();
    CompanyType companyType = companyTypeRepository.findByType(companyTypeName);

    if(companyType==null){
      CompanyType newCompanyType = CompanyType.builder()
              .type(companyTypeName)
              .build();
      companyTypeRepository.save(newCompanyType);

    }
    var company = Company.builder()
            .address(request.getAddress())
            .description(request.getDescription())
            .foundedDate(request.getFoundedDate())
            .industry(request.getIndustry())
            .name(request.getCompanyName())
            .phoneNumber(request.getPhoneNumber())
            .website(request.getWebsite())
            .recruiter(recruiter)
            .logo(logo)
            .companyType(companyType)
            .companySize(request.getCompanySize())
            .country(request.getCountry())
            .build();
    companyRepository.save(company);
  }

  @Override
  public void updateCompany(UpdateInfoCompanyRequest request) throws IOException {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));
    var company = companyRepository.findById(recruiter.getCompany().getId())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));
    var findCompany = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));
    MultipartFile companyLogo = request.getCompanyLogo();

    if(request.getCompanyLogo()!=null) {
      if (!isImageFile(companyLogo.getOriginalFilename())) {
        MessageResponse.builder()
                .message("không-phải-file-ảnh")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }
      String logo = fileUploadService.uploadFile(companyLogo);
      findCompany.setLogo(logo);
    }


      var companyType = companyTypeRepository.findByType(company.getCompanyType().getType());

      if(companyType!=null){
        companyType.setType(request.getCompanyType());
        companyTypeRepository.save(companyType);

      }
      findCompany.setAddress(request.getAddress());
      findCompany.setDescription(request.getDescription());
      findCompany.setFoundedDate(request.getFoundedDate());
      findCompany.setIndustry(request.getIndustry());
      findCompany.setName(request.getCompanyName());
      findCompany.setPhoneNumber(request.getPhoneNumber());
      findCompany.setWebsite(request.getWebsite());
      findCompany.setCompanySize(request.getCompanySize());
      findCompany.setCountry(request.getCountry());
      companyRepository.save(findCompany);




  }

  @Override
  public void deleteCompany() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));

    var company = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));

    companyRepository.delete(company);
  }

  @Override
  public void postJob(PostJobRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));

    var company = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));

    // Check if the JobType already exists, and if not, create and save a new one
    JobType jobType = jobTypeRepository.findByJobType(request.getJobType());
    if (jobType == null) {
      jobType = JobType.builder()
              .jobType(request.getJobType())
              .build();
      jobType = jobTypeRepository.save(jobType);
    }

    Location location = locationRepository.findByCityName(request.getLocation());
    if (location == null) {
      location = Location.builder()
              .cityName(request.getLocation())
              .build();
      location = locationRepository.save(location);
    }

    var job = Job.builder()
            .description(request.getDescription())
            .expireAt(request.getExpireAt())
            .minSalary(request.getMinSalary())
            .maxSalary(request.getMaxSalary())
            .title(request.getJobTitle())
            .createdBy(recruiter.getUsername())
            .createdAt(LocalDateTime.now())
            .jobType(jobType) // Set the JobType
            .location(location) // Set the Location
            .requirements(request.getRequirements())
            .company(company)
            .build();
    var jobOpening = company.getCountJobOpening();
    if(jobOpening==null){
      company.setCountJobOpening(1);
    }
    else{
      company.setCountJobOpening(company.getCountJobOpening() + 1);
    }
    companyRepository.save(company);
    jobRepository.save(job);
  }


  @Override
  public void updateJob(Integer jobId,UpdateJobRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));

    var company = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Bạn chưa có thông tin công ty"));

    // Query the job based on the job ID or some other unique identifier
    var findJob = jobRepository.findByIdAndRecruiter(jobId, recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công việc"));

    var jobType = jobTypeRepository.findByJobType(findJob.getJobType().getJobType());
    if (jobType != null) {
      jobType.setJobType(request.getJobType());
      jobTypeRepository.save(jobType);
    }

    var location = locationRepository.findByCityName(findJob.getLocation().getCityName());
    if (location != null) {
      location.setCityName(request.getLocation());
      locationRepository.save(location);
    }

    findJob.setDescription(request.getDescription());
    findJob.setExpireAt(request.getExpireAt());
    findJob.setMinSalary(request.getMinSalary());
    findJob.setMaxSalary(request.getMaxSalary());
    findJob.setTitle(request.getJobTitle());
    findJob.setRequirements(request.getRequirements());
    findJob.setLocation(location);
    findJob.setJobType(jobType);
    findJob.setCompany(company);
    jobRepository.save(findJob);
  }



  @Override
  public void deleteJob(Integer jobId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));

    var existingJob = jobRepository.findByIdAndRecruiter(jobId, recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công việc "));

    if (!existingJob.getCreatedBy().equals(recruiter.getUsername())) {
      throw new UnauthorizedException("Bạn không có quyền xóa công việc này");
    }
    jobRepository.delete(existingJob);
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


}
