package vn.hcmute.springboot.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
import vn.hcmute.springboot.service.OtpService;
import vn.hcmute.springboot.service.RecruiterService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
  private final ApplicationFormRepository applicationFormRepository;
  private final SkillRepository skillRepository;
  private final CompanyKeySkillRepository companyKeySkillRepository;
  private final ViewJobRepository viewJobRepository;
  private final SaveJobRepository saveJobRepository;

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
            .fullname(recruiterRegisterRequest.getFullName())
            .workTitle(recruiterRegisterRequest.getWorkTitle())
            .websiteUrl(recruiterRegisterRequest.getWebsiteUrl())
            .companyName(recruiterRegisterRequest.getCompanyName())
            .password(encoder.encode(password))
            .createdDate(LocalDateTime.now())
            .phoneNumber(recruiterRegisterRequest.getPhoneNumber())
            .location(recruiterRegisterRequest.getCompanyLocation())
            .status(RecruiterStatus.ACTIVE)
            .build();
    var company = Company.builder()
            .name(recruiterRegisterRequest.getCompanyName())
            .address(recruiterRegisterRequest.getCompanyLocation())
            .recruiter(recruiter)
            .website(recruiterRegisterRequest.getWebsiteUrl())
            .countJobOpening(0)
            .countReview(0)
            .build();
    recruiterRepository.save(recruiter);
    companyRepository.save(company);

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
      throw new BadRequestException("Tài khoản chưa được xác thực");
    }
    var password = recruiter.getPassword();
    var enteredPassword = request.getPassword();
    if (!passwordEncoder.matches(enteredPassword, password)) {
      throw new BadRequestException("Mật khẩu không chính xác");
    }

    var jwtToken = jwtService.generateToken(recruiter);
    var refreshToken = jwtService.generateRefreshToken(recruiter);
    revokeAllUserTokens(recruiter);
    saveUserToken(recruiter, jwtToken);
    recruiter.setLastSignInTime(LocalDateTime.now());
    recruiterRepository.save(recruiter);
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
  public MessageResponse changePassword(String currentPassword, String newPassword, String confirmPassword) {
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
    return MessageResponse.builder()
            .message("Thay đổi mật khẩu thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse changeNickName(String newNickName) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsernameIgnoreCase(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà tuyển dụng"));
    boolean existRecruiter = recruiterRepository.existsByNickname(newNickName);
    if (existRecruiter) {
      throw new BadRequestException("Biệt danh đã tồn tại");
    }
    recruiter.setNickname(newNickName);
    recruiterRepository.save(recruiter);
    return MessageResponse.builder()
            .message("Thay đổi biệt danh thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse resetPassword(String email, String currentPassword, String newPassword,
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
    return MessageResponse.builder()
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
      emailService.sendNewPasswordToEmail(recruiter.getFullname(),email, newPassword);
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
  public MessageResponse updateProfile(UpdateProfileRecruiterRequest request) {
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
    recruiter.setWorkingFrom(request.getWorkingFrom());
    recruiter.setWorkingTo(request.getWorkingTo());
    recruiterRepository.save(recruiter);
    return MessageResponse.builder()
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
  public MessageResponse createCompany(PostInfoCompanyRequest request) throws IOException {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));

    String logo = request.getCompanyLogo();
    String companyTypeName = request.getCompanyType();
    CompanyType companyType = companyTypeRepository.findByType(companyTypeName);
    String locationName = request.getLocation();
    var location = locationRepository.findByCityName(locationName);
    if (location.isEmpty()) {
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
    var company = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));
    var companyKeySkills = companyKeySkillRepository.findByCompanyId(company.getId());
    for (String skillTitle : request.getCompanyKeySkills()) {
      Skill skill = skillRepository.findFirstByTitle(skillTitle)
              .orElseGet(() -> {
                Skill newSkill = new Skill();
                newSkill.setTitle(skillTitle);
                return skillRepository.save(newSkill);
              });

      CompanyKeySkill companyKeySkillOfCompany = CompanyKeySkill.builder()
              .company(recruiter.getCompany())
              .companyKeySkill(List.of(skill))
              .build();

      companyKeySkillRepository.save(companyKeySkillOfCompany);
    }
    var createCompany = Company.builder()
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
            .minCompanySize(request.getMinCompanySize())
            .maxCompanySize(request.getMaxCompanySize())
            .phoneNumber(request.getPhoneNumber())
            .companyKeySkill(companyKeySkills)
            .country(request.getCountry())
            .build();
    var recruiterCompany = Recruiters.builder()
            .company(company)
            .workingFrom(request.getWorkingFrom())
            .workingTo(request.getWorkingTo())
            .overtimePolicy(request.getOvertimePolicy())
            .build();
    companyRepository.save(createCompany);
    recruiterRepository.save(recruiterCompany);
    return MessageResponse.builder()
            .message("Tạo công ty thành công")
            .status(HttpStatus.OK)
            .build();
  }

    @Override
    @Transactional
    public MessageResponse updateCompany(UpdateInfoCompanyRequest request) throws IOException {
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

      var companyType = companyTypeRepository.findByType(company.getCompanyType().getType());
      if (companyType != null) {
        companyType.setType(request.getCompanyType());
        companyTypeRepository.save(companyType);

      }

      var companyKeySkill = company.getCompanyKeySkill();
      companyKeySkillRepository.deleteAll(companyKeySkill);
      company.setCompanyKeySkill(new ArrayList<>());
      for (String skillTitle : request.getCompanyKeySkill()) {
        Skill skill = skillRepository.findFirstByTitle(skillTitle)
                .orElseGet(() -> {
                  Skill newSkill = new Skill();
                  newSkill.setTitle(skillTitle);
                  return skillRepository.save(newSkill);
                });

        CompanyKeySkill companyKeySkillOfCompany = CompanyKeySkill.builder()
                .company(company)
                .companyKeySkill(List.of(skill))
                .build();

        companyKeySkillRepository.save(companyKeySkillOfCompany);
      }
      findCompany.setAddress(request.getAddress());
      findCompany.setDescription(request.getDescription());
      findCompany.setFoundedDate(request.getFoundedDate());
      findCompany.setIndustry(request.getIndustry());
      findCompany.setName(request.getCompanyName());
      findCompany.setPhoneNumber(request.getPhoneNumber());
      findCompany.setWebsite(request.getWebsite());
      findCompany.setMinCompanySize(request.getMinCompanySize());
      findCompany.setMaxCompanySize(request.getMaxCompanySize());
      findCompany.setCountry(request.getCountry());
      findCompany.setCompanyType(companyType);
      recruiter.setOvertimePolicy(request.getOvertimePolicy());
      recruiter.setWorkingFrom(request.getWorkingFrom());
      recruiter.setWorkingTo(request.getWorkingTo());
      recruiterRepository.save(recruiter);
      companyRepository.save(findCompany);
      return MessageResponse.builder()
              .message("Cập nhật thông tin công ty thành công")
              .status(HttpStatus.OK)
              .build();

    }



  @Override
  public MessageResponse deleteCompany() {
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
    return MessageResponse.builder()
            .message("Xóa công ty thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse postJob(PostJobRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));

    var company = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));

    JobType jobType = jobTypeRepository.findFirstByJobType(request.getJobType())
            .orElseGet(() -> jobTypeRepository.save(new JobType(request.getJobType())));

    Location location = locationRepository.findFirstByCityName(request.getLocation())
            .orElseGet(() -> locationRepository.save(new Location(request.getLocation())));
    List<Skill> skillsList = new ArrayList<>();
    for (String skillName : request.getSkills()) {
      Skill skill = skillRepository.findByName(skillName);
      if (skill == null) {
        skill = Skill.builder()
                .title(skillName)
                .build();
        skill = skillRepository.save(skill);

      }
      skillsList.add(skill);
    }

    var job = Job.builder()
            .description(request.getDescription())
            .expireAt(request.getExpireAt())
            .minSalary(request.getMinSalary())
            .maxSalary(request.getMaxSalary())
            .title(request.getJobTitle())
            .createdBy(recruiter.getUsername())
            .createdAt(LocalDate.now())
            .expireAt(LocalDate.now().plusDays(30))
            .jobType(jobType)
            .location(location)
            .skills(skillsList)
            .requirements(request.getRequirements())
            .company(company)
            .build();


    var jobOpening = company.getCountJobOpening();
    company.setCountJobOpening(jobOpening != null ? jobOpening + 1 : 1);

    companyRepository.save(company);
    jobRepository.save(job);

    return MessageResponse.builder()
            .message("Đăng bài tuyển dụng thành công")
            .status(HttpStatus.OK)
            .build();
  }


  @Override
  public MessageResponse updateJob(Integer jobId, UpdateJobRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));

    var company = companyRepository.finCompanyByRecruiter(recruiter)
            .orElseThrow(() -> new NotFoundException("Bạn chưa có thông tin công ty"));

    var findJob = jobRepository.findByIdAndRecruiter(jobId, recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công việc"));


    JobType jobType = jobTypeRepository.findFirstByJobType(request.getJobType())
            .orElseGet(() -> jobTypeRepository.save(new JobType(request.getJobType())));

    Location location = locationRepository.findFirstByCityName(request.getLocation())
            .orElseGet(() -> locationRepository.save(new Location(request.getLocation())));

    List<Skill> skillsList = new ArrayList<>();
    for (String skillName : request.getSkills()) {
      Skill skill = skillRepository.findByName(skillName);
      if (skill == null) {
        skill = Skill.builder()
                .title(skillName)
                .build();
        skill = skillRepository.save(skill);

      }
      skillsList.add(skill);
    }
    LocalDate createdDate = LocalDate.now();
    LocalDate expiredAt = createdDate.plusDays(30);

    findJob.setDescription(request.getDescription());
    findJob.setExpireAt(expiredAt);
    findJob.setMinSalary(request.getMinSalary());
    findJob.setMaxSalary(request.getMaxSalary());
    findJob.setTitle(request.getJobTitle());
    findJob.setRequirements(request.getRequirements());
    findJob.setLocation(location);
    findJob.setJobType(jobType);
    findJob.setCompany(company);
    findJob.setSkills(skillsList);
    jobRepository.save(findJob);

    return MessageResponse.builder()
            .message("Cập nhật bài tuyển dụng thành công")
            .status(HttpStatus.OK)
            .build();
  }


  @Override
  @Transactional
  public MessageResponse deleteJob(Integer jobId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var recruiter = recruiterRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));

    var existingJob = jobRepository.findByIdAndRecruiter(jobId, recruiter)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công việc "));
    var viewJob = viewJobRepository.existsByJob(existingJob);
    if(viewJob){
      viewJobRepository.deleteByJob(existingJob);
    }
    var applicationForms = applicationFormRepository.existsByJob(existingJob);
    if(applicationForms){
      applicationFormRepository.deleteByJob(existingJob);
    }
    var saveJob = saveJobRepository.existsByJob(existingJob);
    if(saveJob){
      saveJobRepository.deleteByJob(existingJob);
    }
    jobRepository.delete(existingJob);
    return MessageResponse.builder()
            .message("Xóa công việc thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public MessageResponse updateStatusJob(Integer applicationId, UpdateApplicationRequest request) throws MessagingException {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("Bạn chưa đăng nhập");
    }

    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      throw new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng");
    }

    if (recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)) {
      throw new UnauthorizedException("Tài khoản chưa được xác thực");
    }

    Optional<ApplicationForm> optionalApplicationForm = Optional.ofNullable(applicationFormRepository.findByIdAndCompanyRecruiter(applicationId, recruiter.get()));
    if (optionalApplicationForm.isEmpty()) {
      throw new NotFoundException("Không tìm thấy đơn ứng tuyển");
    }

    ApplicationForm applicationForm = optionalApplicationForm.get();
    applicationForm.setStatus(request.getStatus());
    applicationFormRepository.save(applicationForm);
    emailService.sendApplicationUpdateEmail(applicationForm);

    return MessageResponse.builder()
            .message("Cập nhật trạng thái thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public GetJobResponse getJobById(Integer jobId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("Bạn chưa đăng nhập");
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      throw new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng");
    }
    if(recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)){
      throw new UnauthorizedException("Tài khoản chưa được xác thực");
    }
    var job = jobRepository.findByIdAndRecruiterId(jobId, recruiter.get().getId());
    return createGetJobResponse(job);
  }

  @Override
  public Page<ApplicationFormResponse> getAppliedJob(Pageable pageable,String type) {

    final String APPROVED = "APPROVED";
    final String DELIVERED = "DELIVERED";
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("Bạn chưa đăng nhập");
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      throw new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng");
    }

    if (recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)) {
      throw new UnauthorizedException("Tài khoản chưa được xác thực");
    }
    if(Objects.equals(type, APPROVED)){
      var applicationForms = applicationFormRepository.findByJobCompanyRecruiterAndStatus(recruiter.get(),ApplicationStatus.APPROVED,pageable);
      return applicationForms.map(this::mapToApplicationFormResponse);

    }
    if(Objects.equals(type, DELIVERED)){
      var applicationForms = applicationFormRepository.findByJobCompanyRecruiterAndStatus(recruiter.get(),ApplicationStatus.DELIVERED,pageable);
      return applicationForms.map(this::mapToApplicationFormResponse);
    }
    var applicationForms = applicationFormRepository.findByJobCompanyRecruiter(recruiter.get(),pageable);

    return applicationForms.map(this::mapToApplicationFormResponse);
  }

  @Override
  public ApplicationFormResponse getApplicationById(Integer applicationId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("Bạn chưa đăng nhập");
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      throw new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng");
    }

    if (recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)) {
      throw new UnauthorizedException("Tài khoản chưa được xác thực");
    }

    Optional<ApplicationForm> optionalApplicationForm = Optional.ofNullable(applicationFormRepository.findByIdAndCompanyRecruiter(applicationId, recruiter.get()));
    if (optionalApplicationForm.isEmpty()) {
      throw new NotFoundException("Không tìm đơn ứng tuyển");
    }
    ApplicationForm applicationForm = optionalApplicationForm.get();

    return mapToApplicationFormResponse(applicationForm);
  }

  @Override
  public MessageResponse addCompanyKeySkill(AddCompanyKeySkillRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("Bạn chưa đăng nhập");
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      throw new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng");
    }
    var company = companyRepository.finCompanyByRecruiter(recruiter.get())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));
    var companyKeySkills = companyKeySkillRepository.findByCompanyId(company.getId());
    for (String skillTitle : request.getCompanyKeySkill()) {
      Skill skill = skillRepository.findFirstByTitle(skillTitle)
              .orElseGet(() -> {
                Skill newSkill = new Skill();
                newSkill.setTitle(skillTitle);
                return skillRepository.save(newSkill);
              });

      CompanyKeySkill companyKeySkillOfCompany = CompanyKeySkill.builder()
              .company(company)
              .companyKeySkill(List.of(skill))
              .build();

      companyKeySkillRepository.save(companyKeySkillOfCompany);
    }
    companyRepository.save(company);
    company.setCompanyKeySkill(companyKeySkills);
    return MessageResponse.builder()
            .message("Thêm kỹ năng thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public Page<GetJobResponse> listAllJobResponse(int page,int size) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("Bạn chưa đăng nhập");
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      throw new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng");
    }
    if (recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)) {
      throw new UnauthorizedException("Tài khoản chưa được xác thực");
    }
    Pageable pageable = PageRequest.of(page, size);
    var jobs = jobRepository.findByRecruiter(recruiter.get(),pageable) ;
    return new PageImpl<>(jobs.stream().map(this::createGetJobResponse).toList(), pageable, jobs.getTotalElements());
  }

  @Override
  public CompanyResponse getCompanyByRecruiter() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("Bạn chưa đăng nhập");
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      throw new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng");
    }
    if (recruiter.get().getStatus().equals(RecruiterStatus.INACTIVE)) {
      throw new UnauthorizedException("Tài khoản chưa được xác thực");
    }
    var company = companyRepository.finCompanyByRecruiter(recruiter.get())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy công ty"));
    return CompanyResponse.builder()
            .companyId(company.getId())
            .companyName(company.getName())
            .companyLogo(company.getLogo())
            .companyType(company.getCompanyType().getType())
            .address(company.getAddress())
            .description(company.getDescription())
            .website(company.getWebsite())
            .phoneNumber(company.getPhoneNumber())
            .industry(company.getIndustry())
            .createdDate(company.getCreatedDate())
            .countJobOpenings(company.getCountJobOpening())
            .minCompanySize(company.getMinCompanySize())
            .maxCompanySize(company.getMaxCompanySize())
            .overtimePolicy(recruiter.get().getOvertimePolicy())
            .workingFrom(recruiter.get().getWorkingFrom())
            .workingTo(recruiter.get().getWorkingTo())
            .country(company.getCountry())
            .foundedDate(company.getFoundedDate())
            .companyKeySkill(company.getCompanyKeySkill().stream().map(this::mapToCompanyKeySkillResponse).toList())
            .build();
  }


  private GetJobResponse createGetJobResponse(Job job) {
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
            .minCompanySize(job.getCompany().getMinCompanySize())
            .maxCompanySize(job.getCompany().getMaxCompanySize())
            .companyLogo(job.getCompany().getLogo())
            .companyType(job.getCompany().getCompanyType().getType())
            .country(job.getCompany().getCountry())
            .workingFrom(job.getCompany().getRecruiter().getWorkingFrom())
            .workingTo(job.getCompany().getRecruiter().getWorkingTo())
            .overtimePolicy(job.getCompany().getRecruiter().getOvertimePolicy())
            .skills(skillNames)
            .description(job.getDescription())
            .createdDate(job.getCreatedAt())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .minSalary(job.getMinSalary())
            .maxSalary(job.getMaxSalary())
            .companyKeySkill(job.getCompany().getCompanyKeySkill().stream()
                    .map(this::mapToCompanyKeySkillResponse)
                    .toList())
            .level(determineJobLevel(job))
            .build();
  }
  private GetJobResponse createGetJobResponseWithLevel(Job job, String level) {
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
            .createdDate(job.getCreatedAt())
            .expiredDate(job.getExpireAt())
            .requirements(job.getRequirements())
            .jobType(job.getJobType().getJobType())
            .location(job.getLocation().getCityName())
            .minSalary(job.getMinSalary())
            .maxSalary(job.getMaxSalary())
            .companyKeySkill(job.getCompany().getCompanyKeySkill().stream().map(this::mapToCompanyKeySkillResponse).toList())
            .level(level)
            .isSaved(false)
            .isApplied(false)
            .appliedAt(null)
            .level(level)
            .build();
  }
  private String determineJobLevel(Job job) {
    final int SUPER_HOT_VIEW_THRESHOLD = 1000;
    final int SUPER_HOT_APPLICATION_THRESHOLD = 1000;
    final int HOT_VIEW_THRESHOLD = 500;
    if (job.getViewCounts() == null) {
      job.setViewCounts(0);

    }
    if (job.getApplyCounts() == null) {
      job.setApplyCounts(0);
    }
    if (job.getViewCounts() >= SUPER_HOT_VIEW_THRESHOLD && job.getApplyCounts() >= SUPER_HOT_APPLICATION_THRESHOLD) {
      return JobLevel.SUPER_HOT.toString();

    }

    if (job.getViewCounts() >= HOT_VIEW_THRESHOLD) {
      return JobLevel.HOT.toString();
    }
    jobRepository.save(job);
    return null;
  }
  private CompanyKeySkillResponse mapToCompanyKeySkillResponse(CompanyKeySkill companyKeySkill) {

    return CompanyKeySkillResponse.builder()
            .id(companyKeySkill.getId())
            .title(companyKeySkill.getCompanyKeySkill().stream().map(Skill::getTitle).toList().toString())
            .build();
  }

  private ApplicationFormResponse mapToApplicationFormResponse(ApplicationForm applicationForm) {
    return ApplicationFormResponse.builder()
            .id(applicationForm.getId())
            .email(applicationForm.getCandidate().getUsername())
            .phoneNumber(applicationForm.getCandidate().getPhoneNumber())
            .birthdate(applicationForm.getCandidate().getBirthDate())
            .address(applicationForm.getCandidate().getAddress())
            .linkCV(applicationForm.getLinkCV())
            .jobId(applicationForm.getJob().getId())
            .jobTitle(applicationForm.getJob().getTitle())
            .candidateName(applicationForm.getCandidateName())
            .submittedAt(applicationForm.getSubmittedAt())
            .coverLetter(applicationForm.getCoverLetter())
            .status(applicationForm.getStatus())
            .candidateId(applicationForm.getCandidate().getId())
            .build();
  }


}
