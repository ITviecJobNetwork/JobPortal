package vn.hcmute.springboot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@Data
@Builder
public class MessageResponse {
  private String message;
  private HttpStatus status;
  private LocalDate updatedAt;

  public MessageResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }

}
