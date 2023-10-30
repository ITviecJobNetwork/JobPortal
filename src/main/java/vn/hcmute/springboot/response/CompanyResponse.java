package vn.hcmute.springboot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.Job;

@Getter
@AllArgsConstructor
@Setter
@Builder
public class CompanyResponse {
  private Page<Company> company;
  private String message;
  private HttpStatus status;
  public CompanyResponse(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }
  public  CompanyResponse(Page<Company> company) {
    this.company = company;
  }

  public Page<Company> getCompany() {
    return company;
  }

  public void setCompany(Page<Company> company) {
    this.company = company;
  }
}
