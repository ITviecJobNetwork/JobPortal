package vn.hcmute.springboot.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import vn.hcmute.springboot.model.Gender;

@Getter
@AllArgsConstructor
@Setter
@Builder
public class UserProfileResponse {
  private String aboutMe;
  private String fullName;
  private String email;
  private String location;
  private String address;
  private String position;
  private String phoneNumber;
  private LocalDate birthdate;
  private String linkWebsiteProfile;
  private List<String> skills;
  private String city;
  private Gender gender;
  private List<String> school;
  private List<String> major;
  private String coverLetter;
  private String avatar;
  private String message;
  private HttpStatus status;

    public UserProfileResponse(String message, HttpStatus httpStatus) {
      this.message = message;
      this.status = httpStatus;
    }
}
