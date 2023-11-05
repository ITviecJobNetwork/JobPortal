package vn.hcmute.springboot.service;

import org.springframework.data.domain.Page;
import vn.hcmute.springboot.model.Company;

public interface CompanyService {
  Page<Company> listAllCompany(int page, int size);
  Page<Company> findCompanyByName(String name,int page,int size);

  Company findCompanyById (Integer id);



}
