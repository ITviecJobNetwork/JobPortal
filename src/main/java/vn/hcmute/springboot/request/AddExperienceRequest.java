package vn.hcmute.springboot.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddExperienceRequest {
  private Integer id;
  @NotNull
  private String companyName;
  @NotNull
  private String jobTitle;
  @NotNull
  private LocalDate startDate;
  @NotNull
  private LocalDate endDate;

}
