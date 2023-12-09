package vn.hcmute.springboot.service;

import org.springframework.data.domain.Page;
import vn.hcmute.springboot.response.CompanyKeySkillResponse;
import vn.hcmute.springboot.response.CompanyResponse;
import vn.hcmute.springboot.response.CompanyWithJobsResponse;

import java.util.List;

public interface CompanyService {
  Page<CompanyResponse> listAllCompany(int page, int size);

  CompanyWithJobsResponse findCompanyById (Integer id);

  List<CompanyKeySkillResponse> listCompanyKeySkill(Integer companyId);



}
