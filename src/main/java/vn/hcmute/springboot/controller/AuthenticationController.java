package vn.hcmute.springboot.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.request.RegisterRequest;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.serviceImpl.AuthenticationServiceImpl;
import vn.hcmute.springboot.serviceImpl.EmailServiceImpl;
import vn.hcmute.springboot.serviceImpl.OtpServiceImpl;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final UserRepository userRepository;

  private final AuthenticationServiceImpl service;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<MessageResponse> register(
      @RequestBody RegisterRequest request
  ) {
    var nickName = userRepository.existsByNickname(request.getNickname());
    if (nickName) {
      return new ResponseEntity<>(
          new MessageResponse("Nickname đã có người sử dụng vui lòng chọn nickname khác",
              HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    var email = userRepository.existsByUsername(request.getUsername());
    if (email) {
      return new ResponseEntity<>(
          new MessageResponse("Email đã được sử dụng", HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);

    }
    if (request.getUsername().isEmpty()) {
      return new ResponseEntity<>(
          new MessageResponse("Không thể gửi mã OTP, vui lòng thử lại", HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);
    }

    var userRegister = service.register(request);
    return new ResponseEntity<>(userRegister, HttpStatus.OK);
  }

  @PostMapping("/sign-in")
  public ResponseEntity<JwtResponse> authenticate(
      @RequestBody LoginRequest request
  ) {
    var user = userRepository.findByUsername(request.getUsername());
    if (user.isEmpty()) {
      return new ResponseEntity<>(
          new JwtResponse("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);
    } else {
      var status = user.get().getStatus();
      if (status.equals(UserStatus.INACTIVE)) {
        return new ResponseEntity<>(
            new JwtResponse("Tài khoản chưa được kích hoạt", HttpStatus.BAD_REQUEST),
            HttpStatus.BAD_REQUEST);
      } else {
        try {
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  request.getUsername(),
                  request.getPassword()
              )
          );
        } catch (AuthenticationException ex) {
          return new ResponseEntity<>(
              new JwtResponse("Username hoặc mật khẩu không đúng", HttpStatus.BAD_REQUEST),
              HttpStatus.BAD_REQUEST);
        }
      }

    }

    var userLogin = service.authenticate(request);
    if (jwtService.isTokenExpired(userLogin.getAccessToken())) {
      return new ResponseEntity<>(
          new JwtResponse("Phiên làm việc đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
          HttpStatus.UNAUTHORIZED
      );
    }
    return new ResponseEntity<>(userLogin, HttpStatus.OK);
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  @PutMapping("/verify-account")
  public ResponseEntity<MessageResponse> verifyAccount(@RequestParam String email,
      @RequestParam String otp) {
    var user = userRepository.findByUsername(email);
    if (user.isEmpty()) {
      return new ResponseEntity<>(
          (new MessageResponse("Email không tồn tại", HttpStatus.BAD_REQUEST)),
          HttpStatus.BAD_REQUEST);

    }
    if (user.get().getOtp().equals(otp) && Duration.between(user.get().getOtpGeneratedTime(),
        LocalDateTime.now()).getSeconds() < (120)) {
      return new ResponseEntity<>(service.verifyAccount(email, otp), HttpStatus.OK);
    }
    return new ResponseEntity<>(
        (new MessageResponse("Mã OTP không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST)),
        HttpStatus.BAD_REQUEST);


  }

  @PutMapping("/regenerate-otp")
  public ResponseEntity<MessageResponse> regenerateOtp(@RequestParam String email) {
    var user = userRepository.findByUsername(email);
    if (user.isEmpty()) {
      return new ResponseEntity<>(
          (new MessageResponse("Email không tồn tại", HttpStatus.BAD_REQUEST)),
          HttpStatus.BAD_REQUEST);

    }
    if (email.isEmpty()) {
      return new ResponseEntity<>(
          new MessageResponse("Không thể gửi mã OTP, vui lòng thử lại", HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(service.regenerateOtp(email), HttpStatus.OK);
  }


}