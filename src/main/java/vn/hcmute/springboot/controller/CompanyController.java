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
import vn.hcmute.springboot.response.CompanyKeySkillResponse;
import vn.hcmute.springboot.response.CompanyResponse;
import vn.hcmute.springboot.response.CompanyWithJobsResponse;
import vn.hcmute.springboot.service.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
  private final CompanyService companyService;

  @GetMapping
  public ResponseEntity<?> getAllCompanies(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<CompanyResponse> allCompany = companyService.listAllCompany(page, size);
    return ResponseEntity.ok().body(allCompany);

  }



  @GetMapping("{id}")
  public ResponseEntity<CompanyWithJobsResponse> findCompanyById(@PathVariable Integer id) {
    var company = companyService.findCompanyById(id);
    return ResponseEntity.ok().body(company);
  }


  @GetMapping("/{id}/company-key-skill")
  public ResponseEntity<List<CompanyKeySkillResponse>> listCompanyKeySkill(@PathVariable Integer id) {
    var companyKeySkill = companyService.listCompanyKeySkill(id);
    return ResponseEntity.ok().body(companyKeySkill);
  }
}
