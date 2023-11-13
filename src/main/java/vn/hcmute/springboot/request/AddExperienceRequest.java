package vn.hcmute.springboot.request;

import jakarta.validation.constraints.NotBlank;
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
  @NotBlank(message = "Company name is mandatory")
  private String companyName;
  @NotBlank(message = "Job title is mandatory")
  private String jobTitle;
  @NotBlank(message = "Description is mandatory")
  private LocalDate startDate;
  @NotBlank(message = "Description is mandatory")
  private LocalDate endDate;

}
