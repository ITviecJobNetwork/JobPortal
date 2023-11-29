package vn.hcmute.springboot.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.TokenRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.request.RegisterRequest;
import vn.hcmute.springboot.response.AuthenticationResponse;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.AuthenticationService;
import vn.hcmute.springboot.service.EmailService;
import vn.hcmute.springboot.service.OtpService;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailService emailService;
  private final OtpService otpService;


  @Override
  public MessageResponse register(RegisterRequest request) {
    var nickName = repository.existsByNickname(request.getNickname());
    if (nickName) {
      return MessageResponse.builder()
              .message("Nickname đã có người sử dụng vui lòng chọn nickname khác")
              .status(HttpStatus.BAD_REQUEST)
              .build();

    }
    var userName = repository.existsByUsername(request.getUsername());
    if (userName) {
      return MessageResponse.builder()
              .message("Email đã có người sử dụng vui lòng chọn nickname khác")
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    var otp = String.valueOf(otpService.generateOtp());
    var nickname = request.getNickname();
    var username = request.getUsername();
    try {
      emailService.sendOtpToEmail(nickname, username, otp);
    } catch (MessagingException e) {
      return MessageResponse.builder()
              .message("Không thể gửi OTP, vui lòng thử lại")
              .status(HttpStatus.BAD_REQUEST)
              .build();
    }
    var user = User.builder()
            .nickname(request.getNickname())
            .password(passwordEncoder.encode(request.getPassword()))
            .username(request.getUsername())
            .role(Role.USER)
            .otp(otp)
            .otpGeneratedTime(LocalDateTime.now())
            .status(UserStatus.INACTIVE)
            .build();
    repository.save(user);
    var message = "Chúc mừng bạn đã đăng ký tài khoản thành công. Vui lòng kiểm tra email để xác thực tài khoản";
    return MessageResponse.builder()
            .message(message)
            .status(HttpStatus.CREATED)
            .build();

  }


  @Override
  public JwtResponse authenticate(LoginRequest request) {

    var user = repository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    var password = user.getPassword();
    var enteredPassword = request.getPassword();
    if (!passwordEncoder.matches(enteredPassword, password)) {
      throw new BadRequestException("Mật khẩu không chính xác");
    }
    if (user.getStatus().equals(UserStatus.INACTIVE)) {
      throw new BadRequestException("Tài khoản chưa được kích hoạt");
    }
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    user.setLastSignInTime(LocalDateTime.now());
    repository.save(user);
    return JwtResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .firstName(user.getFirstname())
            .lastName(user.getLastname())
            .id(user.getId())
            .password(user.getPassword())
            .role(user.getRole())
            .username(user.getUsername())
            .lastSignInTime(LocalDateTime.now())
            .status(HttpStatus.OK)
            .authorities(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
            .build();

  }

  @Override
  public void saveUserToken(User user, String token) {
    var jwtToken = Token.builder()
            .user(user)
            .token(token)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(jwtToken);
  }


  @Override
  public void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
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
      var user = this.repository.findByUsername(userEmail)
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
  public MessageResponse verifyAccount(String email, String otp) {
    var user = repository.findByUsername(email)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
    if (!user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
            LocalDateTime.now()).getSeconds() < (120)) {
      var messageError = "Mã OTP đã hết hạn hoặc không chính xác. Vui lòng tạo mã OTP mới và thử lại";
      throw new BadRequestException(messageError);
    }
    user.setStatus(UserStatus.ACTIVE);
    var message = "Xác thực tài khoản thành công";
    repository.save(user);
    return MessageResponse.builder()
            .message(message)
            .status(HttpStatus.ACCEPTED)
            .build();

  }

  @Override
  public MessageResponse regenerateOtp(String email) {
    var user = repository.findByUsername(email)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
    var otp = String.valueOf(otpService.generateOtp());
    var nickname = user.getNickname();
    try {
      emailService.sendOtpToEmail(nickname, email, otp);
    } catch (MessagingException e) {
      throw new BadRequestException("Không thể gửi mã OTP, vui lòng thử lại");
    }
    user.setOtp(otp);
    user.setOtpGeneratedTime(LocalDateTime.now());
    repository.save(user);
    var message = "Mã OTP đã được gửi... vui lòng xác thực tài khoản trong vòng 2 phút";
    return MessageResponse.builder()
            .message(message)
            .build();
  }
}
