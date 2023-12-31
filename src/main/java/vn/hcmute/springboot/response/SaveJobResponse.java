package vn.hcmute.springboot.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveJobResponse {
  private Integer jobId;
  private String title;
  private Integer companyId;
  private String companyName;
  private String address;
  private List<String> skills;
  private String description;
  private LocalDate createdDate;
  private LocalDate expiredDate;
  private String requirements;
  private String jobType;
  private String location;
  private Double minSalary;
  private Double maxSalary;
  private Boolean isSaved;
  private String message;
  private HttpStatus status;

}
