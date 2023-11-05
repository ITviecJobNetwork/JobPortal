package vn.hcmute.springboot.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.CompanyKeySkill;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.repository.CompanyKeySkillRepository;
import vn.hcmute.springboot.repository.CompanyRepository;
import vn.hcmute.springboot.repository.JobRepository;
import vn.hcmute.springboot.repository.RecruiterRepository;
import vn.hcmute.springboot.repository.SkillRepository;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.response.CompanyResponse;
import vn.hcmute.springboot.response.JobResponse;
import vn.hcmute.springboot.service.CompanyService;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
  private final CompanyService companyService;
  private final UserRepository userRepository;
  private final JobRepository jobRepository;
  private final CompanyRepository companyRepository;
  private final CompanyKeySkillRepository companyKeySkillRepository;
  private final SkillRepository skillRepository;
  private final RecruiterRepository recruiterRepository;

  @GetMapping()
  public ResponseEntity<CompanyResponse> getAllCompanies(@RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Company> allCompany =  companyService.listAllCompany(page,size);
    if(allCompany.isEmpty()) {
      return new ResponseEntity<>(new CompanyResponse("Không tìm thấy công ty",HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(new CompanyResponse(allCompany), HttpStatus.OK);
  }

  @GetMapping("/findByName")
  public ResponseEntity<CompanyResponse> findCompanyByName(@RequestParam("name") String name,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Company> company = companyService.findCompanyByName(name,page,size);
    if(company == null) {
      return new ResponseEntity<>(new CompanyResponse("Không tìm thấy công ty",HttpStatus.NOT_FOUND),HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(new CompanyResponse(company), HttpStatus.OK);
  }

  @GetMapping("{id}")
  public ResponseEntity<Company> findCompanyById(@PathVariable Integer id){
    var company = companyService.findCompanyById(id);
    if(company == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(company,HttpStatus.OK);
  }
  @GetMapping("/{id}/jobOpenings")
  public ResponseEntity<List<Job>> getJobOpenings(@PathVariable Integer id) {
    var company = companyRepository.findById(id);
    if(company.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<Job> jobOpenings = jobRepository.findJobByCompanyId(company.get().getId());
    if (jobOpenings.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(jobOpenings, HttpStatus.OK);
  }
  @GetMapping("/companyKeySkill/{recruiterId}")
  public ResponseEntity<List<CompanyKeySkill>> listCompanyKeySkill(@PathVariable Integer recruiterId){
    var recruiter = recruiterRepository.findById(recruiterId);
    if(recruiter.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    var company = recruiter.get().getCompany();
    if(company== null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<CompanyKeySkill> companyKeySkills = companyKeySkillRepository.findByRecruiterId(recruiterId);
    if(companyKeySkills==null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(companyKeySkills,HttpStatus.OK);
  }


}
