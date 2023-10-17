package vn.hcmute.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hcmute.springboot.model.Role;
import vn.hcmute.springboot.model.RoleName;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.model.UserStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends User {

  private Integer userid;
  private String email;
  private String password;
  private Role role;
  private UserStatus status;

  public UserDTO(User user) {
    this.setId(user.getId());
    this.setEmail(user.getEmail());
    this.setPassword(user.getPassword());
    this.setRole(user.getRole());
    if (UserStatus.ACTIVE == user.getStatus()) {
      this.setStatus(UserStatus.valueOf("Tài khoản đã kích hoạt"));
    } else {
      this.setStatus(UserStatus.valueOf("Tài khoản chưa kích hoạt"));
    }

  }
}