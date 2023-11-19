package vn.hcmute.springboot.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Data
public class CountDateRequest {
  private Date startDate;
  private Date endDate;
}
