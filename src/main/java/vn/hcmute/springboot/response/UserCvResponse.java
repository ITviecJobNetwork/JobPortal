package vn.hcmute.springboot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCvResponse {
  private String message;
  private HttpStatus status;
  private String linkCv;

  public UserCvResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
  public UserCvResponse(String linkCv) {
    this.linkCv = linkCv;
  }

}