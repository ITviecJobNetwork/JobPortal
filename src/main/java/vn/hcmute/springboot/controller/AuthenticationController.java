package vn.hcmute.springboot.controller;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.request.RegisterRequest;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.service.AuthenticationService;
import vn.hcmute.springboot.serviceImpl.AuthenticationServiceImpl;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationServiceImpl service;
  @PostMapping("/register")
  public ResponseEntity<MessageResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/sign-in")
  public ResponseEntity<JwtResponse> authenticate(
      @RequestBody LoginRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
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
    return ResponseEntity.ok(service.verifyAccount(email, otp));
  }

  @PutMapping("/regenerate-otp")
  public ResponseEntity<MessageResponse> regenerateOtp(@RequestParam String email) {
    return ResponseEntity.ok(service.regenerateOtp(email));
  }



}