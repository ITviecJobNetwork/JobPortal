package vn.hcmute.springboot.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.request.RegisterRequest;
import vn.hcmute.springboot.response.AuthenticationResponse;
import vn.hcmute.springboot.response.JwtResponse;

public interface AuthenticationService {
  User register(RegisterRequest request);
  JwtResponse authenticate(LoginRequest request);
  void saveUserToken(User user, String token);

  void revokeAllUserTokens(User user);
  void refreshToken(HttpServletRequest request,
      HttpServletResponse response) throws IOException;

}
