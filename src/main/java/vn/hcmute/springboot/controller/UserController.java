package vn.hcmute.springboot.controller;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.request.ChangeNickNameRequest;
import vn.hcmute.springboot.request.ChangePasswordRequest;
import vn.hcmute.springboot.request.ForgotPasswordRequest;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.serviceImpl.UserServiceImpl;
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
  private final UserServiceImpl userService;

  @PostMapping("/forgot-password")
  public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){

    return ResponseEntity.ok(userService.sendNewPasswordToEmail(request.getEmail()));
  }

  @PostMapping("/change-password")
  public ResponseEntity<MessageResponse> resetPassword( @RequestBody ChangePasswordRequest request) {

    return ResponseEntity.ok(userService.changePassword(request.getCurrentPassword(),
        request.getNewPassword(), request.getConfirmPassword()));
  }


  @PostMapping("/change-nickname")
  public ResponseEntity<MessageResponse> changeNickname( @RequestBody ChangeNickNameRequest request) {

    return new ResponseEntity<>(userService.changeNickName(request.getNewNickName()),
         HttpStatus.OK);
  }







}

