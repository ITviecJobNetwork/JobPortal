package vn.hcmute.springboot.service;

import java.util.List;
import vn.hcmute.springboot.model.Company;

public interface CompanyService {
  List<Company> listAllCompany();
  Company findCompanyByName(String name);

}
