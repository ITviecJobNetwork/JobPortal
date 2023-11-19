package vn.hcmute.springboot.response;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class CountResponse {
  private Long count;
}
