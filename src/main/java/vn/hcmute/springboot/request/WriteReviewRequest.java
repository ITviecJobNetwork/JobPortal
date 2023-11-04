package vn.hcmute.springboot.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriteReviewRequest {
  private Integer companyId;
  private Integer rating;
  private String title;
  private String content;


}
