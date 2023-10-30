package vn.hcmute.springboot.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddEducationRequest {
  private Integer id;
  @NotNull
  private String school;
  @NotNull
  private String major;
  @NotNull
  private LocalDate startDate;
  @NotNull
  private LocalDate endDate;

}
