package vn.hcmute.springboot.response;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hcmute.springboot.model.Role;
import java.util.Date;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class JwtResponse {

  private String accessToken;
  private String refreshToken;
  private Integer id;
  private String username;
  private String firstName;
  private String lastName;
  private Role role;
  private List<String> authorities;
  private String password;
  private LocalDateTime lastSignInTime;


  public JwtResponse(String token) {
    this.accessToken = token;
  }
}
