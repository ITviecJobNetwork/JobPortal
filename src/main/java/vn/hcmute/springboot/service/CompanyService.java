package vn.hcmute.springboot.service;

import java.util.List;
import org.springframework.data.domain.Page;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.Job;

public interface CompanyService {
  Page<Company> listAllCompany(int page, int size);
  Page<Company> findCompanyByName(String name,int page,int size);

  Company findCompanyById (Integer id);



}
