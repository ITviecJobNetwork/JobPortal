package vn.hcmute.springboot.serviceImpl;


import jakarta.mail.MessagingException;
import java.util.Optional;
import javax.management.relation.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.repository.SkillRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final OtpServiceImpl otpService;
  private final PasswordEncoder encoder;
  private final EmailServiceImpl emailService;
  private final PasswordEncoder passwordEncoder;
  private final SkillRepository skillRepository;
  public void handleUserStatus() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      MessageResponse.builder()
          .message("Unauthorized access")
          .status(HttpStatus.UNAUTHORIZED)
          .build();
    }
  }

      @Override
  public MessageResponse sendNewPasswordToEmail(String email) {
    var user = userRepository.findByUsername(email)
        .orElseThrow(() -> new UsernameNotFoundException("user-not-found"));
    if (user.getStatus().equals(UserStatus.INACTIVE)) {
      String messageError = "user-is-unverified";
      return MessageResponse.builder()
          .status(HttpStatus.UNAUTHORIZED)
          .message(messageError)
          .build();
    }
    try {
      String newPassword = String.valueOf(otpService.generateNewPassword());
      user.setPassword(encoder.encode(newPassword));
      emailService.sendNewPasswordToEmail(email, newPassword);
    } catch (MessagingException e) {
      String messageError = "Unable to send new password please try again";
      return MessageResponse.builder()
          .status(HttpStatus.BAD_REQUEST)
          .message(messageError)
          .build();
    }
    userRepository.save(user);
    return MessageResponse.builder()
        .status(HttpStatus.OK)
        .message("new password sent to your email")
        .build();
  }

  @Override
  public MessageResponse changePassword(String currentPassword, String newPassword,
      String confirmPassword) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      String message = "user-is-unauthorized";
      return MessageResponse.builder()
          .message(message)
          .status(HttpStatus.UNAUTHORIZED)
          .build();
    }
    var user = userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new UsernameNotFoundException("user-not-found"));
    String initialPassword = user.getPassword();
    if (!passwordEncoder.matches(currentPassword, initialPassword)) {
      String message = "Current password is incorrect";
      return MessageResponse.builder()
          .message(message)
          .status(HttpStatus.BAD_REQUEST)
          .build();

    }

    if (!newPassword.equals(confirmPassword)) {
      String message = "New password and confirm password does not match";
      return MessageResponse.builder()
          .message(message)
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    String hashedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(encoder.encode(hashedPassword));
    userRepository.save(user);
    return MessageResponse.builder()
        .message("Password changed successfully")
        .status(HttpStatus.OK)
        .build();
  }

  @Override
  public MessageResponse changeNickName(String newNickName) {
    handleUserStatus();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var user = userRepository.findByUsernameIgnoreCase(authentication.getName())
        .orElseThrow(() -> new UsernameNotFoundException("user-not-found"));
    boolean existUser = userRepository.existsByNickname(newNickName);
    if (existUser) {
      return MessageResponse.builder()
          .message("Nickname already exists")
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }

    user.setNickname(newNickName);
    userRepository.save(user);
    return MessageResponse.builder()
        .message("Nickname changed successfully")
        .status(HttpStatus.OK)
        .build();
  }


}