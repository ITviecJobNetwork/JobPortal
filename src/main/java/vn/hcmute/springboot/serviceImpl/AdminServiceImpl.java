package vn.hcmute.springboot.serviceImpl;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.response.*;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.AdminService;
import vn.hcmute.springboot.service.EmailService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AdminServiceImpl implements AdminService {

  private final AdminRepository adminRepository;
  private final JwtService jwtService;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder encoder;
  private final UserRepository userRepository;
  private final SkillRepository skillRepository;
  private final CandidateEducationRepository candidateEducationRepository;
  private final CandidateExperienceRepository candidateExperienceRepository;
  private final EmailService emailService;
  @Override
  public JwtResponse loginAdmin(LoginRequest request) {
    var admin = adminRepository.findByEmail(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản admin"));
    var jwtToken = jwtService.generateToken(admin);
    var refreshToken = jwtService.generateRefreshToken(admin);
    if(!encoder.matches(request.getPassword(), admin.getPassword())){
      throw new BadRequestException("Mật khẩu không đúng");
    }
    revokeAllAdminTokens(admin);
    saveAdminToken(admin, jwtToken);
    return JwtResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .id(admin.getId())
            .password(admin.getPassword())
            .role(Role.ADMIN)
            .username(admin.getUsername())
            .lastSignInTime(LocalDateTime.now())
            .status(HttpStatus.OK)
            .authorities(
                    admin.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
            .build();
  }

  @Override
  public void saveAdminToken(Admin admin, String token) {
    var jwtToken = Token.builder()
            .admin(admin)
            .token(token)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(jwtToken);
  }

  @Override
  public void revokeAllAdminTokens(Admin admin) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(admin.getId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  @Override
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String adminEmail;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    adminEmail = jwtService.extractUsername(refreshToken);
    if (adminEmail != null) {
      var admin = this.adminRepository.findByEmail(adminEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, admin)) {
        var accessToken = jwtService.generateToken(admin);
        revokeAllAdminTokens(admin);
        saveAdminToken(admin, accessToken);
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
      throw new UnauthorizedException("Người dùng chưa đăng nhập");
    }
    var admin = adminRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy admin"));

    var initialPassword = admin.getPassword();
    if (!encoder.matches(currentPassword, initialPassword)) {
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
    admin.setPassword(encoder.encode(newPassword));
    adminRepository.save(admin);
    MessageResponse.builder()
            .message("Thay đổi mật khẩu thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public void changeFullName(String fullName) {
    var authentication = SecurityContextHolder.getContext().getAuthentication().getName();
    var admin = adminRepository.findByEmail(authentication)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));
    admin.setFullName(fullName);
    adminRepository.save(admin);
    MessageResponse.builder()
            .message("Thay đổi tên thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public Page<UserResponse> getAllUser(int page , int size) {
    var admin = SecurityContextHolder.getContext().getAuthentication().getName();
    adminRepository.findByEmail(admin)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy admin hoặc admin chưa đăng nhập"));
    var user = userRepository.findAll();
    if(user.isEmpty()){
      throw new NotFoundException("Không tìm thấy user");
    }
    Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
    List<UserResponse> userResponses = userPage.getContent().stream()
            .map(users -> UserResponse.builder()
                    .id(users.getId())
                    .email(users.getUsername())
                    .fullName(users.getFullName())
                    .avatar(users.getAvatar())
                    .aboutMe(users.getAboutMe())
                    .gender(users.getGender())
                    .userStatus(users.getStatus())
                    .build())
            .collect(Collectors.toList());

    return new PageImpl<>(userResponses, userPage.getPageable(), userPage.getTotalElements());
  }

  @Override
  public UserProfileResponse getUserById(Integer id) {
    var admin = SecurityContextHolder.getContext().getAuthentication().getName();
    adminRepository.findByEmail(admin)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy admin hoặc admin chưa đăng nhập"));
    var user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));
    var skills = skillRepository.findByUserId(id);
    var education = candidateEducationRepository.findByUserId(id);
    var experience = candidateExperienceRepository.findByUserId(id);
    return UserProfileResponse
            .builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getUsername())
            .aboutMe(user.getAboutMe())
            .location(user.getLocation())
            .address(user.getAddress())
            .position(user.getPosition())
            .phoneNumber(user.getPhoneNumber())
            .birthdate(user.getBirthDate())
            .linkWebsiteProfile(user.getLinkWebsiteProfile())
            .skills(skills.stream().map(Skill::getTitle).toList())
            .education(education.stream().map(CandidateEducation::getSchool).toList())
            .experience(experience.stream().map(CandidateExperience::getCompanyName).toList())
            .birthdate(user.getBirthDate())
            .city(user.getCity())
            .gender(user.getGender())
            .coverLetter(user.getCoverLetter())
            .avatar(user.getAvatar())
            .userStatus(user.getStatus())
            .build();
  }

  @Override
  public void deActiveUser(Integer id, String reason, UserStatus status) throws MessagingException {
    var admin = SecurityContextHolder.getContext().getAuthentication().getName();
    adminRepository.findByEmail(admin)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy admin hoặc admin chưa đăng nhập"));
    var user = userRepository.findById(id);
    if(user.isEmpty()){
      throw new NotFoundException("Không tìm thấy user");
    }
    if(status.equals(UserStatus.INACTIVE)){
      emailService.sendReasonDeActiveUser(user.get().getUsername(), reason, status);
    }
    else{
      emailService.sendReasonDeActiveUser(user.get().getUsername(), reason, status);
    }
    user.get().setStatus(status);
    userRepository.save(user.get());
  }
}
