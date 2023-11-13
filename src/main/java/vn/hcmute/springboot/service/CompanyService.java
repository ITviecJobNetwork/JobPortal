package vn.hcmute.springboot.service;

import org.springframework.data.domain.Page;
import vn.hcmute.springboot.response.CompanyResponse;
import vn.hcmute.springboot.response.CompanyWithJobsResponse;

public interface CompanyService {
  Page<CompanyResponse> listAllCompany(int page, int size);

  CompanyWithJobsResponse findCompanyById (Integer id);



}
