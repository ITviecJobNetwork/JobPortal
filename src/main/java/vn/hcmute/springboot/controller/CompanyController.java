package vn.hcmute.springboot.controller;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.service.CompanyService;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
  private final CompanyService companyService;

  @GetMapping()
  public ResponseEntity<List<Company>> getAllCompanies() {
    return ResponseEntity.ok(companyService.listAllCompany());
  }

  @GetMapping("/findByName")
  public ResponseEntity<Company> findCompanyByName(@RequestParam("name") String name) {
    return ResponseEntity.ok(companyService.findCompanyByName(name));
  }
}
