package vn.hcmute.springboot.serviceImpl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.exception.UnauthorizedException;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.Token;
import vn.hcmute.springboot.repository.CompanyKeySkillRepository;
import vn.hcmute.springboot.repository.CompanyRepository;
import vn.hcmute.springboot.repository.TokenRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.service.CompanyService;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final CompanyKeySkillRepository companyKeySkillRepository;
  private final TokenRepository tokenRepository;
  @Override
  public Page<Company> listAllCompany(int page,int size) {
    Pageable pageable = PageRequest.of(page, size);
    var company = companyRepository.findAllCompanies(pageable);
    if (company.isEmpty()) {
      throw new NotFoundException("không-tìm-thấy-công-ty");
    }
    return company;
  }

  @Override
  public Page<Company> findCompanyByName(String name,int page,int size) {
    Pageable pageable = PageRequest.of(page, size);
    var company = companyRepository.findCompanyByName(name,pageable);
    if (company.isEmpty()) {
      throw new NotFoundException("không-tìm-thấy-công-ty " + name);
    }
    return company;
  }

  @Override
  public Company findCompanyById(Integer id) {
    return companyRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("không-tìm-thấy-công-ty " + id));
  }

}
