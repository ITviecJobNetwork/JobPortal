package vn.hcmute.springboot.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.request.SignUpRequest;
import vn.hcmute.springboot.response.JwtResponse;
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
  public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest userLogin) throws Exception {
    JwtResponse response = userService.login(userLogin);
    return ResponseEntity.ok(response);
  }
  @CrossOrigin
  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody SignUpRequest request){
    return new ResponseEntity<>(userService.registerUser(request), HttpStatus.OK);
  }
}