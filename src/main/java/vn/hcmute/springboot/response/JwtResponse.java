package vn.hcmute.springboot.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hcmute.springboot.model.Role;


@Data
@Builder
@NoArgsConstructor
@Getter
@Setter
public class JwtResponse {

  private String accessToken;
  private String refreshToken;
  private Integer id;
  private String username;
  private Role role;
  private List<String> authorities;
  private String password;
  public JwtResponse(String accessToken, String refreshToken, Integer id, String username, Role role,
      List<String> authorities,String password) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.id = id;
    this.role = role;
    this.username = username;
    this.authorities = authorities;
    this.password = password;

  }

  public JwtResponse(String token) {
    this.accessToken = token;
  }
}
