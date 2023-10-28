package vn.hcmute.springboot.request;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.Gender;
import vn.hcmute.springboot.model.Skill;

@Getter
@Setter
@Data
public class ProfileUpdateRequest {
//  @Nullable
//  private MultipartFile avatar;
  private String aboutMe;
  private String fullName;
  @NotNull
  @Email
  private String email;
  private String location;
  private String address;
  private String position;
  @NotNull
  private String phoneNumber;
  private LocalDate birthdate;
  private String linkWebsiteProfile;
  private List<String> skills;
  private String city;
  private Gender gender;
  private List<String> school;
  private List<String> major;
  private String coverLetter;
  private List<String> companyName;
  private List<String> positionName;
  private LocalDate startDate;
  private LocalDate endDate;


}
