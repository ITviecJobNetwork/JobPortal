package vn.hcmute.springboot.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplyResponse {
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
  private Boolean isApplied;
  private String message;
  private HttpStatus status;
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate appliedAt;

}
