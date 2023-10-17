package vn.hcmute.springboot.controller;



import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.repository.AdminRepository;
import vn.hcmute.springboot.repository.CandidateRepository;
import vn.hcmute.springboot.repository.RecruiterRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.request.RegisterRequest;
import vn.hcmute.springboot.response.AuthenticationResponse;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.service.AuthenticationService;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final CandidateRepository candidateRepository;
  private final RecruiterRepository recruiterRepository;
  @PostMapping("/register")
  public ResponseEntity<User> register(
      @RequestBody RegisterRequest request
  ) {
    var user= userRepository.existsByEmail(request.getEmail());
    if(user){
      throw new BadRequestException("Email is already in use");
    }

    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<JwtResponse> authenticate(
      @RequestBody LoginRequest request
  ) {
    var user = userRepository.existsByEmail(request.getEmail());
    if(!user){
      throw new UsernameNotFoundException("User-not-found");
    }
    if(userRepository.findByEmail(request.getEmail()).get().getStatus() == UserStatus.INACTIVE)
      throw new BadRequestException("User-not-active");
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}