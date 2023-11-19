package vn.hcmute.springboot.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import java.time.YearMonth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.config.CustomYearMonthDeserializer;

@Getter
@Setter
@Data
public class AddEducationRequest {
  @NotBlank(message = "School is mandatory")
  private String school;
  @NotBlank(message = "Major is mandatory")
  private String major;
  @NotBlank(message = "Description is mandatory")
  @JsonDeserialize(using = CustomYearMonthDeserializer.class)
  private String startDate;
  @NotBlank(message = "Description is mandatory")
  @JsonDeserialize(using = CustomYearMonthDeserializer.class)
  private String endDate;

}
