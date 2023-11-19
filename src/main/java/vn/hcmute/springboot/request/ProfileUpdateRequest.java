package vn.hcmute.springboot.request;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.CandidateExperience;
import vn.hcmute.springboot.model.Gender;
import vn.hcmute.springboot.model.Skill;

@Getter
@Setter
@Data
public class ProfileUpdateRequest {
  private String avatar;
  private String aboutMe;
  private String fullName;
  @NotBlank
  @Email
  private String email;
  private String location;
  private String address;
  private String position;
  @NotNull
  private String phoneNumber;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  private LocalDate birthdate;
  private String linkWebsiteProfile;
  private List<String> skills;
  private String city;
  private Gender gender;
  private String coverLetter;

}
