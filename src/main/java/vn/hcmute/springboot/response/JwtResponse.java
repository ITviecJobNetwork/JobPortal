package vn.hcmute.springboot.response;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.RoleName;


@Getter
@Setter
public class JwtResponse {

  private String accessToken;
  private String refreshToken;
  private String type = "Bearer";
  private Integer id;
  private String firstName;
  private String lastName;
  private String username;
  private RoleName role;
  private List<String> authorities;

  public JwtResponse(String accessToken, String refreshToken, Integer id, String firstName,
      String lastName, String username, RoleName role,
      List<String> authorities) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.id = id;
    this.role = role;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.authorities = authorities;

  }

  public JwtResponse(String token) {
    this.accessToken = token;
  }
}
