package vn.hcmute.springboot.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.hcmute.springboot.model.Token;
import vn.hcmute.springboot.model.TokenType;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.repository.TokenRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.request.RegisterRequest;
import vn.hcmute.springboot.response.AuthenticationResponse;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.AuthenticationService;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final EmailServiceImpl emailService;
  private final OtpServiceImpl otpService;
  @Override
  public String register(RegisterRequest request) {
    String otp = String.valueOf(otpService.generateOtp());
    try {
      emailService.sendOtpToEmail(request.getUsername(), otp);
    } catch (MessagingException e) {
      throw new RuntimeException("Unable to send otp please try again");
    }
    var user = User.builder()
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .username(request.getUsername())
        .firstname(request.getFirstName())
        .lastname(request.getLastName())
        .gender(request.getGender())
        .phoneNumber(request.getPhoneNumber())
        .birthDate(request.getBirthDate())
        .lastSignInTime(null)
        .role(request.getRole())
        .otp(otp)
        .otpGeneratedTime(LocalDateTime.now())
        .build();
    var savedUser = repository.save(user);
    savedUser.setStatus(UserStatus.INACTIVE);
    return "user-registered-successfully";

  }


  @Override
  public JwtResponse authenticate(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        )
    );
    var user = repository.findByUsername(request.getUsername())
        .orElseThrow();
    user.setLastSignInTime(LocalDateTime.now());
    repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
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
    if (validUserTokens.isEmpty())
      return;
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
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
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
  public String verifyAccount(String email, String otp) {
    User user=repository.findByUsernameIgnoreCase(email)
        .orElseThrow(()->new UsernameNotFoundException("User not found with email: "+email));
    if(user.getOtp().equals(otp)&& Duration.between(user.getOtpGeneratedTime(),
        LocalDateTime.now()).getSeconds() < (120)){
      user.setStatus(UserStatus.ACTIVE);
      repository.save(user);
      return "Account verified successfully";


    }
    return "Please regenerate otp and try again";

  }

  @Override
  public String regenerateOtp(String email) {
    User user = repository.findByUsername(email)
        .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
    String otp = String.valueOf(otpService.generateOtp());
    try {
      emailService.sendOtpToEmail(email, otp);
    } catch (MessagingException e) {
      throw new RuntimeException("Unable to send otp please try again");
    }
    user.setOtp(otp);
    user.setOtpGeneratedTime(LocalDateTime.now());
    repository.save(user);
    return "Email sent... please verify account within 2 minute";
  }
}
