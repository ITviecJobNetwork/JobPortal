package vn.hcmute.springboot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.repository.AdminRepository;
import vn.hcmute.springboot.request.ChangePasswordRequest;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.AdminService;

import java.io.IOException;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

  private final PasswordEncoder passwordEncoder;
  private final AdminRepository adminRepository;
  private final AdminService adminService;
  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {

    var userLogin = adminService.loginAdmin(request);
    return new ResponseEntity<>(userLogin, HttpStatus.OK);

  }
  @PostMapping("/refresh-token")
  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    adminService.refreshToken(request, response);
  }
  @PostMapping("/change-password")
  public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordRequest request) {
    adminService.changePassword(request.getCurrentPassword(),
            request.getNewPassword(), request.getConfirmPassword());

    return ResponseEntity.ok(new MessageResponse("Đổi mật khẩu thành công", HttpStatus.OK));

  }

  @PostMapping("/change-fullname")
  public ResponseEntity<MessageResponse> changeFullName (@RequestBody String fullName){
    adminService.changeFullName(fullName);
    return ResponseEntity.ok(new MessageResponse("Đổi tên thành công", HttpStatus.OK));
  }

}
