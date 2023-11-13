package vn.hcmute.springboot.serviceImpl;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.BadRequestException;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.exception.UnauthorizedException;
import vn.hcmute.springboot.model.Admin;
import vn.hcmute.springboot.model.Role;
import vn.hcmute.springboot.model.Token;
import vn.hcmute.springboot.model.TokenType;
import vn.hcmute.springboot.repository.AdminRepository;
import vn.hcmute.springboot.repository.TokenRepository;
import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.response.AuthenticationResponse;
import vn.hcmute.springboot.response.JwtResponse;
import vn.hcmute.springboot.response.MessageResponse;
import vn.hcmute.springboot.security.JwtService;
import vn.hcmute.springboot.service.AdminService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor

public class AdminServiceImpl implements AdminService {

  private final AdminRepository adminRepository;
  private final JwtService jwtService;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder encoder;
  @Override
  public JwtResponse loginAdmin(LoginRequest request) {
    var admin = adminRepository.findByEmail(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản admin"));
    var jwtToken = jwtService.generateToken(admin);
    var refreshToken = jwtService.generateRefreshToken(admin);
    if(!encoder.matches(request.getPassword(), admin.getPassword())){
      throw new BadRequestException("Mật khẩu không đúng");
    }
    revokeAllAdminTokens(admin);
    saveAdminToken(admin, jwtToken);
    return JwtResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .id(admin.getId())
            .password(admin.getPassword())
            .role(Role.ADMIN)
            .username(admin.getUsername())
            .lastSignInTime(LocalDateTime.now())
            .status(HttpStatus.OK)
            .authorities(
                    admin.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
            .build();
  }

  @Override
  public void saveAdminToken(Admin admin, String token) {
    var jwtToken = Token.builder()
            .admin(admin)
            .token(token)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(jwtToken);
  }

  @Override
  public void revokeAllAdminTokens(Admin admin) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(admin.getId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  @Override
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String adminEmail;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    adminEmail = jwtService.extractUsername(refreshToken);
    if (adminEmail != null) {
      var admin = this.adminRepository.findByEmail(adminEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, admin)) {
        var accessToken = jwtService.generateToken(admin);
        revokeAllAdminTokens(admin);
        saveAdminToken(admin, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  @Override
  public void changePassword(String currentPassword, String newPassword, String confirmPassword) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new UnauthorizedException("Người dùng chưa đăng nhập");
    }
    var admin = adminRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy admin"));

    var initialPassword = admin.getPassword();
    if (!encoder.matches(currentPassword, initialPassword)) {
      var message = "Mật khẩu hiện tại không đúng";
      throw new BadRequestException(message);
    }
    if (Objects.equals(currentPassword, newPassword)) {
      String message = "Mật khẩu mới và mật khẩu hiện tại không được giống nhau";
      throw new BadRequestException(message);
    }

    if (!newPassword.equals(confirmPassword)) {
      var message = "Mật khẩu mới và xác nhận mật khẩu không khớp";
      throw new BadRequestException(message);
    }
    admin.setPassword(encoder.encode(newPassword));
    adminRepository.save(admin);
    MessageResponse.builder()
            .message("Thay đổi mật khẩu thành công")
            .status(HttpStatus.OK)
            .build();
  }

  @Override
  public void changeFullName(String fullName) {
    var authentication = SecurityContextHolder.getContext().getAuthentication().getName();
    var admin = adminRepository.findByEmail(authentication)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhà tuyển dụng"));
    admin.setFullName(fullName);
    adminRepository.save(admin);
    MessageResponse.builder()
            .message("Thay đổi tên thành công")
            .status(HttpStatus.OK)
            .build();
  }
}
