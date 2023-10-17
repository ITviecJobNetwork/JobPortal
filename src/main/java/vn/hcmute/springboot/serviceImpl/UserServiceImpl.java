package vn.hcmute.springboot.serviceImpl;

import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.request.SignUpRequest;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.security.JwtProvider;
import vn.hcmute.springboot.security.UserDetailsServiceImpl;
import vn.hcmute.springboot.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtUtil;
  private final UserDetailsServiceImpl userService;

  public JwtResponse login(LoginRequest request) throws Exception {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
      );
    } catch (BadCredentialsException e) {
      throw new Exception("Tên đăng nhập hoặc mật khẩu không đúng.", e);
    }

    final UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
    final String token = jwtUtil.generateAccessToken((Authentication) userDetails);

    return new JwtResponse(token);
  }

  @Override
  public User registerUser(SignUpRequest request) {
    User user = new User();
    Optional<User> existingUser = Optional.ofNullable(
        userRepository.findByEmail(request.getEmail()));
    if(existingUser.isPresent() && existingUser.get().getStatus().equals(UserStatus.ACTIVE)){
      throw new BadRequestException("Email này đã được sử dụng");
    }
    if(existingUser.isPresent() && existingUser.get().getStatus().equals(UserStatus.INACTIVE)){
      userRepository.delete(existingUser.get());
      user.setEmail(request.getEmail());
      user.setPassword(request.getPassword());
      user.setStatus(UserStatus.INACTIVE);
    }
    else{
      user.setEmail(request.getEmail());
      user.setPassword(request.getPassword());
      user.setStatus(UserStatus.ACTIVE);

    }
    return userRepository.save(user);
  }
}
