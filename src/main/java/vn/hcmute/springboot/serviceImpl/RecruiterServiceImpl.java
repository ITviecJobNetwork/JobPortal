package vn.hcmute.springboot.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.exception.UnauthorizedException;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.*;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.EmailService;
import vn.hcmute.springboot.service.FileUploadService;
import vn.hcmute.springboot.service.OtpService;
import vn.hcmute.springboot.service.RecruiterService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
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
  private final CompanyRepository companyRepository;
  private final CompanyTypeRepository companyTypeRepository;
  private final JobTypeRepository jobTypeRepository;
  private final LocationRepository locationRepository;
  private final JobRepository jobRepository;

  @Override
  public MessageResponse registerRecruiter(RecruiterRegisterRequest recruiterRegisterRequest) {
    var userName = recruiterRepository.existsByUsername(recruiterRegisterRequest.getUsername());
    if (userName) {
      throw new BadRequestException("Tài khoản đã tồn tại");
    }

    String password = String.valueOf(otpService.generateNewPassword());
    try {
      emailService.sendConfirmRegistrationToRecruiter(recruiterRegisterRequest.getUsername(),
              password);
    } catch (Exception e) {
      throw new BadRequestException("Không thể gửi email xác thực, vui lòng thử lại");
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
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
    var userStatus = recruiter.getStatus();
    if (userStatus.equals(RecruiterStatus.INACTIVE)) {
      throw new UnauthorizedException("Tài khoản chưa được xác thực");
    }
    if (!passwordEncoder.matches(request.getPassword(), recruiter.getPassword())) {
      throw new UnauthorizedException("Mật khẩu không đúng");
    }
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
      throw new BadRequestException(message);
    }
    if (Objects.equals(currentPassword, newPassword)) {
      String message = "Mật khẩu mới và mật khẩu hiện tại không được giống nhau";
      throw new BadRequestException(message);
    }

    if (!newPassword.equals(confirmPassword)) {
      var message = "Mật khẩu mới và xác nhận mật khẩu không khớp";
      throw new BadRequestException(message);
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
      throw new BadRequestException("Biệt danh đã tồn tại");
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
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));
    var initialPassword = recruiter.getPassword();
    if (!passwordEncoder.matches(currentPassword, initialPassword)) {
      var message = "Mật khẩu được cấp bạn nhập không đúng";
      throw new BadRequestException(message);
    }
    if (Objects.equals(currentPassword, newPassword)) {
      String message = "Mật khẩu mới và mật khẩu hiện tại không được trùng nhau";
      throw new BadRequestException(message);
    }
    if (!newPassword.equals(confirmPassword)) {
      var message = "Mật khẩu mới và mật khẩu xác nhận không khớp";
      throw new BadRequestException(message);
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
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));
    if (recruiter.getStatus().equals(RecruiterStatus.INACTIVE)) {
      var messageError = "Nhà tuyển dụng chưa xác thực tài khoản";
      throw new BadRequestException(messageError);
    }
    try {
      var newPassword = String.valueOf(otpService.generateNewPassword());
      recruiter.setPassword(encoder.encode(newPassword));
      emailService.sendNewPasswordToEmail(email, newPassword);
    } catch (MessagingException e) {
      var messageError = "Không thể gửi mật khẩu mới, vui lòng thử lại";
      throw new BadRequestException(messageError);
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
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));
    recruiter.setUsername(request.getUsername());
    recruiter.setNickname(request.getNickname());
    recruiter.setFbUrl(request.getFbUrl());
    recruiter.setPhoneNumber(request.getPhoneNumber());
    recruiter.setBirthDate(request.getBirthDate());
    recruiter.setWebsiteUrl(request.getWebsiteUrl());
    recruiter.setBenefit(request.getBenefit());
    recruiter.setOvertimePolicy(request.getOverTimePolicy());
    recruiter.setRecruitmentProcedure(request.getRecruitmentProcedure());
    recruiter.setIntroduction(request.getIntroduction());
    recruiter.setWorkingDays(request.getWorkingDay());
    recruiterRepository.save(recruiter);
    MessageResponse.builder()
            .status(HttpStatus.OK)
            .message("Cập nhật thông tin thành công")
            .build();
  }

  @Override
  @Transactional
  public RecruiterProfileResponse getProfile() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));
    if (recruiter!=null) {
      return RecruiterProfileResponse.builder()
              .recruiterId(recruiter.getId())
              .fbUrl(recruiter.getFbUrl())
              .websiteUrl(recruiter.getWebsiteUrl())
              .linkedInUrl(recruiter.getLinkedInUrl())
              .username(recruiter.getUsername())
              .birthDate(recruiter.getBirthDate())
              .nickname(recruiter.getNickname())
              .phoneNumber(recruiter.getPhoneNumber())
              .build();
    }
    else{
      throw new NotFoundException("Không tìm thấy nhà tuyển dụng");
    }

  }

  @Override
  public void createCompany(PostInfoCompanyRequest request) throws IOException {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));

    String logo = request.getCompanyLogo();
    String companyTypeName = request.getCompanyType();
    CompanyType companyType = companyTypeRepository.findByType(companyTypeName);
    String locationName = request.getLocation();
    Location location = locationRepository.findByCityName(locationName);
    if (location == null) {
      Location newLocation = Location.builder()
              .cityName(locationName)
              .build();
      locationRepository.save(newLocation);
    }


    if (companyType == null) {
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
            .address(String.valueOf(location))
            .companySize(request.getCompanySize())
            .country(request.getCountry())
            .build();
    companyRepository.save(company);
  }

  @Override
  public void updateCompany(UpdateInfoCompanyRequest request) throws IOException {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));
    var company = companyRepository.findById(recruiter.getCompany().getId())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));
    if (!company.getRecruiter().getId().equals(recruiter.getId())) {
      throw new UnauthorizedException("Bạn không có quyền cập nhật thông tin công ty này");
    }
    var findCompany = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty của nhà tuyển dụng"));
    if (findCompany == null) {
      throw new NotFoundException("Bạn chưa có thông tin công ty");
    }
    var companyLogo = request.getCompanyLogo();

    var companyType = companyTypeRepository.findByType(company.getCompanyType().getType());
    var location =request.getLocations();
    var locationName = locationRepository.findByCityName(location);
    if (locationName != null) {
      locationName.setCityName(request.getLocations());
      locationRepository.save(locationName);
    }
    if (companyType != null) {
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
    findCompany.setLogo(companyLogo);
    companyRepository.save(findCompany);


  }

  @Override
  public void deleteCompany() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));

    var company = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));
    if (company == null) {
      throw new NotFoundException("Bạn chưa có thông tin công ty");
    }
    if (!company.getRecruiter().getId().equals(recruiter.getId())) {
      throw new UnauthorizedException("Bạn không có quyền xóa công ty này");
    }

    companyRepository.delete(company);
  }

  @Override
  public void postJob(PostJobRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));

    var company = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));

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
            .jobType(jobType)
            .location(location)
            .requirements(request.getRequirements())
            .company(company)
            .build();
    var jobOpening = company.getCountJobOpening();
    if (jobOpening == null) {
      company.setCountJobOpening(1);
    } else {
      company.setCountJobOpening(company.getCountJobOpening() + 1);
    }
    companyRepository.save(company);
    jobRepository.save(job);
  }


  @Override
  public void updateJob(Integer jobId, UpdateJobRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));

    var company = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Bạn chưa có thông tin công ty"));

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
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));

    var existingJob = jobRepository.findByIdAndRecruiter(jobId, recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công việc "));

    if (!existingJob.getCreatedBy().equals(recruiter.getUsername())) {
      throw new UnauthorizedException("Bạn không có quyền xóa công việc này");
    }
    jobRepository.delete(existingJob);
  }





}
