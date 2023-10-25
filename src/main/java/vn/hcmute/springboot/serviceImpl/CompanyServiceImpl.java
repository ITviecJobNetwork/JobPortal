package vn.hcmute.springboot.serviceImpl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.repository.CompanyRepository;
import vn.hcmute.springboot.service.CompanyService;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final CompanyRepository companyRepository;

  @Override
  public List<Company> listAllCompany() {
    var company = companyRepository.findAll();
    if (company.isEmpty()) {
      throw new NotFoundException("không-tìm-thấy-công-ty");
    }
    return company;
  }

  @Override
  public Company findCompanyByName(String name) {
    return companyRepository.findByNameIgnoreCase(name)
        .orElseThrow(() -> new NotFoundException("không-tìm-thấy-công-ty " + name));
  }
}
