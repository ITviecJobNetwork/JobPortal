package vn.hcmute.springboot.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.authentication.PasswordEncoderParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final OtpServiceImpl otpService;
  private final PasswordEncoder encoder;
  private final EmailServiceImpl emailService;
  private final PasswordEncoder passwordEncoder;
  @Override
  public String sendNewPasswordToEmail(String email) {
    var user = userRepository.findByUsername(email)
        .orElseThrow(() -> new UsernameNotFoundException("user-not-found"));
    if (user.getStatus().equals(UserStatus.INACTIVE)) {
      throw new BadRequestException("user-is-unverified");
    }
    try {
      String newPassword = String.valueOf(otpService.generateNewPassword());
      user.setPassword(encoder.encode(newPassword));
      emailService.sendNewPasswordToEmail(email, newPassword);
    } catch (MessagingException e) {
      throw new RuntimeException("Unable to send new password please try again");
    }
    userRepository.save(user);
    return "new password has been sent to your email";
  }

  @Override
  public String changePassword( String currentPassword, String newPassword,
      String confirmPassword) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return "Unauthorized access";
    }
    var user = userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new UsernameNotFoundException("user-not-found"));
    String initialPassword = user.getPassword();
    if (!passwordEncoder.matches(currentPassword, initialPassword)) {
      return "Current password is incorrect";
    }

    if(!newPassword.equals(confirmPassword))
      return "new password and confirm password are not the same";
    String hashedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(encoder.encode(hashedPassword));
    userRepository.save(user);
    return "password changed successfully";
  }

}