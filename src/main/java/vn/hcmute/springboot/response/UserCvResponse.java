package vn.hcmute.springboot.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCvResponse {
  private String message;
  private HttpStatus status;
  private String linkCv;
  private String coverLetter;
  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
  private LocalDateTime updatedAt;

  public UserCvResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
  public UserCvResponse(String linkCv) {
    this.linkCv = linkCv;
  }

  public UserCvResponse(String linkCv, String coverLetter, LocalDateTime updatedAt) {
    this.linkCv = linkCv;
    this.coverLetter = coverLetter;
    this.updatedAt = updatedAt;
  }
}
