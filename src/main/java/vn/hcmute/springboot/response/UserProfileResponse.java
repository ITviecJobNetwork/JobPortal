package vn.hcmute.springboot.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UserProfileResponse {
  private Integer id;
  private String aboutMe;
  private String fullName;
  private String email;
  private String location;
  private String address;
  private String position;
  private String phoneNumber;
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate birthdate;
  private String linkWebsiteProfile;
  private List<String> skills;
  private String city;
  private Gender gender;
  private CandidateEducationResponse education;
  private List<CandidateExperienceResponse> experience;
  private String avatar;
  private UserStatus userStatus;


}
