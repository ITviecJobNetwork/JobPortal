package vn.hcmute.springboot.request;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostJobRequest {
  private String description;
  private LocalDate expireAt;
  private Double minSalary;
  private Double maxSalary;
  private String jobTitle;
  private String requirements;
  private String location;
  private String jobType;
}
