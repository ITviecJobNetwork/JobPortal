package vn.hcmute.springboot.response;



import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ErrorResponse {

  private LocalDateTime timestamp;
  private int status;
  private String errorCode;

}
