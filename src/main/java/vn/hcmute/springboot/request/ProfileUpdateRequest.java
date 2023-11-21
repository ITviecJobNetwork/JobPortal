package vn.hcmute.springboot.request;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.Gender;

@Getter
@Setter
@Data
public class ProfileUpdateRequest {
  private String avatar;
  private String fullName;
  @NotBlank
  @Email
  private String email;
  private String address;
  private String position;
  @NotNull
  private String phoneNumber;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  private LocalDate birthdate;
  private String linkWebsiteProfile;
  private String city;
  private Gender gender;

}
