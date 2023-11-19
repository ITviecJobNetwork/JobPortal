package vn.hcmute.springboot.response;

import lombok.*;
import vn.hcmute.springboot.model.CompanyReviewStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyReviewResponse {
  private Integer id;
  private String content;
  private Integer rating;
  private String title;
  private LocalDate localDate;
  private String username;
  private CompanyReviewStatus status;
}
