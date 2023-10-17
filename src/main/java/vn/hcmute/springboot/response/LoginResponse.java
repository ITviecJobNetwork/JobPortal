package vn.hcmute.springboot.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
  private String email;
  private String password;
  private String status;
  private Integer id;

  public LoginResponse() {
    this.setStatus("");
    this.setPassword("");
    this.setEmail("");
    this.setId(0);
  }

}
