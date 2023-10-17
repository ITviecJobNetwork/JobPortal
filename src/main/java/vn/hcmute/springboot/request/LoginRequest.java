package vn.hcmute.springboot.request;

import jakarta.validation.constraints.NotBlank;
import vn.hcmute.springboot.valid.ValidEmail;

public class LoginRequest {
  @NotBlank
  @ValidEmail
  private String email;

  @NotBlank
  private String password;


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}