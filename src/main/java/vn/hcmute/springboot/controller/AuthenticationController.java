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
    var userLogin = service.authenticate(request);
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
    var verifyAccount = service.verifyAccount(email, otp);
    return new ResponseEntity<>(verifyAccount, HttpStatus.OK);

  }

  @PutMapping("/regenerate-otp")
  public ResponseEntity<MessageResponse> regenerateOtp(@RequestParam String email) {
    var regenerateOtp = service.regenerateOtp(email);
    return new ResponseEntity<>(regenerateOtp, HttpStatus.OK);
  }


}