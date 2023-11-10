package vn.hcmute.springboot.response;

import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.model.Company;

import java.util.List;

@Getter
@Setter
public class CompanyWithJobsResponse {
  private List<JobOpeningResponse> jobOpenings;
  private Company company;
}
