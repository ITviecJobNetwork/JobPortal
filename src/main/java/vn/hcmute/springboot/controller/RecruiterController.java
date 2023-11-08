package vn.hcmute.springboot.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.springboot.model.RecruiterStatus;
import vn.hcmute.springboot.model.Recruiters;
import vn.hcmute.springboot.repository.CompanyRepository;
import vn.hcmute.springboot.repository.RecruiterRepository;
import vn.hcmute.springboot.request.*;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.RecruiterService;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {
  private final RecruiterRepository recruiterRepository;
  private final RecruiterService recruiterService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final CompanyRepository companyRepository;
  @PostMapping("/register")
  public ResponseEntity<MessageResponse> register(@RequestBody RecruiterRegisterRequest request) {
    var username = recruiterRepository.existsByUsername(request.getUsername());
    if (username) {
      return ResponseEntity.badRequest().body(MessageResponse.builder()
          .message("Email đã có người sử dụng vui lòng chọn nickname khác")
          .build());
    }
    if(request.getUsername().isEmpty()){
      return ResponseEntity.badRequest().body(MessageResponse.builder()
          .message("Không thể gửi tài khoản và mật khẩu, vui lòng thử lại")
          .build());
    }
    var recruiterRegister = recruiterService.registerRecruiter(request);

    return new ResponseEntity<>(recruiterRegister, HttpStatus.OK);
  }
  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
    var user = recruiterRepository.findByUsername(loginRequest.getUsername());
    if (user.isEmpty()) {
      return new ResponseEntity<>(
          new JwtResponse("Không tìm thấy người dùng", HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);
    }
    else{
      var userStatus = user.get().getStatus();
      if (userStatus.equals(RecruiterStatus.INACTIVE)) {
        return new ResponseEntity<>(
            new JwtResponse("Tài khoản chưa được kích hoạt", HttpStatus.BAD_REQUEST),
            HttpStatus.BAD_REQUEST);
      }
      if(!passwordEncoder.matches(loginRequest.getPassword(),user.get().getPassword())){
        return new ResponseEntity<>(
            new JwtResponse("Mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
            HttpStatus.BAD_REQUEST);
      }
    }

    var userLogin = recruiterService.loginRecruiter(loginRequest);
    if (jwtService.isTokenExpired(userLogin.getAccessToken())) {
      return new ResponseEntity<>(
          new JwtResponse("Token đã hết hạn vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
          HttpStatus.UNAUTHORIZED
      );
    }
    return new ResponseEntity<>(userLogin, HttpStatus.OK);
  }
  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    recruiterService.refreshToken(request, response);
  }

  @PostMapping("/change-password")
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ChangePasswordRequest request) {

    var authentication = SecurityContextHolder.getContext().getAuthentication();

    var userName = authentication.getName();

    var recruiter = recruiterRepository.findByUsername(userName);
    if (recruiter.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new MessageResponse("Người dùng chưa đăng nhâp", HttpStatus.NOT_FOUND));
    }

    var initialPassword = recruiter.get().getPassword();
    if (request.getCurrentPassword().equals(request.getNewPassword())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          new MessageResponse("Mật khẩu mới và hiện tại không được giống nhau",
              HttpStatus.BAD_REQUEST));
    }
    if (!passwordEncoder.matches(request.getCurrentPassword(), initialPassword)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponse("Mật khẩu hiện tại không chính xác", HttpStatus.BAD_REQUEST));
    }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          new MessageResponse("Mật khẩu mới và mật khẩu xác nhận không khớp",
              HttpStatus.BAD_REQUEST));
    }

     recruiterService.changePassword(request.getCurrentPassword(),
        request.getNewPassword(), request.getConfirmPassword());

    return ResponseEntity.ok(new MessageResponse("Đổi mật khẩu thành công", HttpStatus.OK));

  }
  @PostMapping("/change-nickname")
  public ResponseEntity<MessageResponse> changeNickname(
      @RequestBody ChangeNickNameRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    var userName = authentication.getName();
    if (userName == null) {
      return new ResponseEntity<>(
          new MessageResponse("Người dùng chưa đăng nhâp", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }
    var existNickName = recruiterRepository.existsByNickname(request.getNewNickName());
    if (existNickName) {
      return new ResponseEntity<>(
          (new MessageResponse("Biệt danh đã tồn tại", HttpStatus.BAD_REQUEST)),
          HttpStatus.BAD_REQUEST);
    }
    recruiterService.changeNickName(request.getNewNickName());
    return new ResponseEntity<>(new MessageResponse("Thay đổi biệt danh thành công", HttpStatus.OK),
        HttpStatus.OK);
  }

  @PostMapping("/reset-password")
  @Valid
  public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
    Optional<Recruiters> recruiter = recruiterRepository.findByUsername(request.getEmail());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(
          (new MessageResponse("Nhà tuyển dụng không tồn tại và không thể cập nhật mật khẩu mới", HttpStatus.NOT_FOUND)),
          HttpStatus.NOT_FOUND);
    }

    if (!passwordEncoder.matches(request.getCurrentPassword(), recruiter.get().getPassword())
        && !Objects.equals(request.getCurrentPassword(), request.getNewPassword())) {
      String message = "Mật khẩu do admin cấp bạn nhập không đúng";
      return new ResponseEntity<>(new MessageResponse(message, HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);
    }
    if (Objects.equals(request.getCurrentPassword(), request.getNewPassword())) {
      String message = "Mật khẩu mới và hiện tại không được giống nhau";
      return new ResponseEntity<>(new MessageResponse(message, HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);
    }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      String message = "Mật khẩu mới và mật khẩu xác nhận không khớp";
      return new ResponseEntity<>(new MessageResponse(message, HttpStatus.BAD_REQUEST),
          HttpStatus.BAD_REQUEST);
    }

    recruiterService.resetPassword(request.getEmail(), request.getCurrentPassword(),
        request.getNewPassword(), request.getConfirmPassword());
    return new ResponseEntity<>(new MessageResponse("Thay đổi mật khẩu thành công", HttpStatus.OK),HttpStatus.OK);
  }


  @PostMapping("/forgot-password")
  public ResponseEntity<MessageResponse> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest request) {

    var recruiter = recruiterRepository.findByUsername(request.getEmail());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(
          (new MessageResponse("Nhà tuyển dụng không tồn tại", HttpStatus.NOT_FOUND)),
          HttpStatus.NOT_FOUND);
    }
    if (request.getEmail() == null) {
      return new ResponseEntity<>(
          (new MessageResponse("Không thể gửi mật khẩu mới tới email của bạn",
              HttpStatus.BAD_REQUEST)), HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(recruiterService.sendNewPasswordToEmail(request.getEmail()),
        HttpStatus.OK);
  }
  @PostMapping("/update-profile")
  public ResponseEntity<MessageResponse> updateRecruiterProfile(@Valid @RequestBody
      UpdateProfileRecruiterRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(authentication instanceof AnonymousAuthenticationToken){
      return new ResponseEntity<>(
          new MessageResponse("Nhà tuyển dụng chưa đăng nhâp", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(
          new MessageResponse("Nhà tuyển dụng không tồn tại", HttpStatus.NOT_FOUND),
          HttpStatus.NOT_FOUND);
    }
    recruiterService.updateProfile(request);
    return new ResponseEntity<>(new MessageResponse("Cập nhật thông tin thành công", HttpStatus.OK),
        HttpStatus.OK);

  }


  @PostMapping(value="/create-company", consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> createCompany(@Valid @ModelAttribute PostInfoCompanyRequest request) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(authentication instanceof AnonymousAuthenticationToken){
      return new ResponseEntity<>(
              new MessageResponse("Nhà tuyển dụng chưa đăng nhâp", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(
              new MessageResponse("Nhà tuyển dụng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    recruiterService.createCompany(request);
    return new ResponseEntity<>(new MessageResponse("Tạo thông tin công ty thành công", HttpStatus.OK),
            HttpStatus.OK);
  }
  @PostMapping(value="/update-company",consumes = {"multipart/form-data"})
  public ResponseEntity<MessageResponse> updateCompany(@Valid @ModelAttribute UpdateInfoCompanyRequest request) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(authentication instanceof AnonymousAuthenticationToken){
      return new ResponseEntity<>(
              new MessageResponse("Nhà tuyển dụng chưa đăng nhâp", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(
              new MessageResponse("Nhà tuyển dụng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    recruiterService.updateCompany(request);
    return new ResponseEntity<>(new MessageResponse("Tạo thông tin công ty thành công", HttpStatus.OK),
            HttpStatus.OK);
  }
  @DeleteMapping("/delete-company")
  public ResponseEntity<MessageResponse> deleteCompany() throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(authentication instanceof AnonymousAuthenticationToken){
      return new ResponseEntity<>(
              new MessageResponse("Nhà tuyển dụng chưa đăng nhâp", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var recruiter = recruiterRepository.findByUsername(authentication.getName());
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(
              new MessageResponse("Nhà tuyển dụng không tồn tại", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }
    var company = companyRepository.finCompanyByRecruiter(recruiter.get());
    if(!company.get().getRecruiter().getId().equals(recruiter.get().getId())){
      return new ResponseEntity<>(
              new MessageResponse("Bạn không có quyền xóa công ty này", HttpStatus.NOT_FOUND),
              HttpStatus.NOT_FOUND);
    }

    recruiterService.deleteCompany();
    return new ResponseEntity<>(new MessageResponse("Tạo thông tin công ty thành công", HttpStatus.OK),
            HttpStatus.OK);
  }
}
