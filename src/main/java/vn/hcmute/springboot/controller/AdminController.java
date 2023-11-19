package vn.hcmute.springboot.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.springboot.model.CompanyReviewStatus;
import vn.hcmute.springboot.repository.AdminRepository;
import vn.hcmute.springboot.request.ChangePasswordRequest;
import vn.hcmute.springboot.request.DeActiveRequest;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.response.*;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.AdminService;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

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

  @GetMapping("/get-all-user")
  public ResponseEntity<Page<UserResponse>> getAllUser(@RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "20") Integer size){
    return ResponseEntity.ok(adminService.getAllUser(page, size));

  }

  @GetMapping("/get-user-by-id/{id}")
  public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Integer id){
    return ResponseEntity.ok(adminService.getUserById(id));
  }

  @PostMapping("/deActive-user/{id}")
  public ResponseEntity<MessageResponse> deActiveUser(@PathVariable Integer id, @RequestBody DeActiveRequest request) throws MessagingException {
    adminService.deActiveUser(id,request.getReason(), request.getStatus());
    return ResponseEntity.ok(new MessageResponse("Deactive user thành công", HttpStatus.OK));
  }

  @GetMapping("/get-all-recruiter")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Page<RecruiterResponse>> getAllRecruiter(@RequestParam(defaultValue = "0") Integer page,
                                                                 @RequestParam(defaultValue = "20") Integer size){
    return ResponseEntity.ok(adminService.getAllRecruiter(page, size));

  }

  @GetMapping("/get-recruiter-by-id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<RecruiterProfileResponse> getRecruiterById(@PathVariable Integer id){
    return ResponseEntity.ok(adminService.getRecruiterById(id));
  }

  @PostMapping("/active-user")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MessageResponse> activeUser(@RequestBody String email) throws MessagingException {
    return ResponseEntity.ok(adminService.activeUser(email));
  }

  @PostMapping("/update-status-company-review/{companyReviewId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MessageResponse> updateStatusCompanyReview(@PathVariable Integer companyReviewId, @RequestBody CompanyReviewStatus status){
    adminService.updateStatusCompanyReview(companyReviewId, status);
    return ResponseEntity.ok(new MessageResponse("Cập nhật thành công", HttpStatus.OK));
  }

}