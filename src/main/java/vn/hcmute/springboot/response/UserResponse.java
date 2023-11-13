package vn.hcmute.springboot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.Gender;
import vn.hcmute.springboot.model.UserStatus;

@Getter
@AllArgsConstructor
@Setter
@Builder
public class UserResponse {
  private Integer id;
  private String aboutMe;
  private String fullName;
  private String email;
  private Gender gender;
  private String avatar;
  private UserStatus userStatus;

}
