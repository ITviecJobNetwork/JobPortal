package vn.hcmute.springboot.controller;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    var allCompany =  companyService.listAllCompany();
    if(allCompany.isEmpty()) {
      return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(allCompany, HttpStatus.OK);
  }

  @GetMapping("/findByName")
  public ResponseEntity<Company> findCompanyByName(@RequestParam("name") String name) {
    var company = companyService.findCompanyByName(name);
    if(company == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(company, HttpStatus.OK);
  }
}
