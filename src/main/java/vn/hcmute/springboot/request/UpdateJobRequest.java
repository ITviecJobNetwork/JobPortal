package vn.hcmute.springboot.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateJobRequest {
  private String description;
  private Double minSalary;
  private Double maxSalary;
  private String jobTitle;
  private String requirements;
  private String location;
  private String jobType;
  private List<String> skills;
}
