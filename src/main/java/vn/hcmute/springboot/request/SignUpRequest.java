package vn.hcmute.springboot.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SignUpRequest {
  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotBlank
  @Email

  private String email;



  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
