package vn.hcmute.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.springboot.model.CompanyKeySkill;
import vn.hcmute.springboot.model.Skill;
import vn.hcmute.springboot.repository.CompanyKeySkillRepository;
import vn.hcmute.springboot.repository.RecruiterRepository;
import vn.hcmute.springboot.response.CompanyResponse;
import vn.hcmute.springboot.response.CompanyWithJobsResponse;
import vn.hcmute.springboot.service.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
  private final CompanyKeySkillRepository companyKeySkillRepository;
  private final RecruiterRepository recruiterRepository;
  private final CompanyService companyServiceImpl;

  @GetMapping
  public ResponseEntity<?> getAllCompanies(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<CompanyResponse> allCompany = companyServiceImpl.listAllCompany(page, size);
    return ResponseEntity.ok().body(allCompany);

  }



  @GetMapping("{id}")
  public ResponseEntity<CompanyWithJobsResponse> findCompanyById(@PathVariable Integer id) {
    var company = companyServiceImpl.findCompanyById(id);
    return ResponseEntity.ok().body(company);
  }


  @GetMapping("/companyKeySkill/{recruiterId}")
  public ResponseEntity<List<Skill>> listCompanyKeySkill(@PathVariable Integer recruiterId) {
    var recruiter = recruiterRepository.findById(recruiterId);
    if (recruiter.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    var company = recruiter.get().getCompany();
    if (company == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<CompanyKeySkill> companyKeySkills = companyKeySkillRepository.findByRecruiterId(recruiterId);
    if (companyKeySkills == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    for (CompanyKeySkill companyKeySkill : companyKeySkills) {
      List<Skill> skills = companyKeySkill.getSkills();
      return new ResponseEntity<>(skills, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);

  }
}
