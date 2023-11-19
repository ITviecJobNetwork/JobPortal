package vn.hcmute.springboot.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.YearMonth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddEducationRequest {
  @NotBlank(message = "School is mandatory")
  private String school;
  @NotBlank(message = "Major is mandatory")
  private String major;
  @NotBlank(message = "Description is mandatory")
  private LocalDate startDate;
  @NotBlank(message = "Description is mandatory")
  private LocalDate endDate;

}
