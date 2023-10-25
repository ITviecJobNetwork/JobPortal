package vn.hcmute.springboot.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.Gender;
import vn.hcmute.springboot.model.Skill;

@Getter
@Setter
@Data
public class ProfileUpdateRequest {
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
  private MultipartFile avatar;

}
