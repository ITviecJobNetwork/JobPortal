package vn.hcmute.springboot.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.response.LoginResponse;
import vn.hcmute.springboot.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserService userService;


  public AuthController(UserService userService) {
    this.userService = userService;
  }
  @CrossOrigin
  @PostMapping("/sign-in")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginResponse userLogin){
    LoginResponse response = userService.login(userLogin);
    return ResponseEntity.ok(response);
  }
}