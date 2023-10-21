package vn.hcmute.springboot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.request.ChangePasswordRequest;
import vn.hcmute.springboot.request.ForgotPasswordRequest;
import vn.hcmute.springboot.serviceImpl.UserServiceImpl;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserServiceImpl userService;
  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){

    return ResponseEntity.ok(userService.sendNewPasswordToEmail(request.getEmail()));
  }

  @PostMapping("/change-password")
  public ResponseEntity<String> resetPassword( @RequestBody ChangePasswordRequest request) {

    return ResponseEntity.ok(userService.changePassword(request.getCurrentPassword(),
        request.getNewPassword(), request.getConfirmPassword()));
  }
}

