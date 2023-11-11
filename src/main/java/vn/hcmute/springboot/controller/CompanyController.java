package vn.hcmute.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.CompanyKeySkill;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.Skill;
import vn.hcmute.springboot.repository.CompanyKeySkillRepository;
import vn.hcmute.springboot.repository.CompanyRepository;
import vn.hcmute.springboot.repository.JobRepository;
import vn.hcmute.springboot.repository.RecruiterRepository;
import vn.hcmute.springboot.response.CompanyResponse;
import vn.hcmute.springboot.response.CompanyWithJobsResponse;
import vn.hcmute.springboot.response.JobOpeningResponse;
import vn.hcmute.springboot.serviceImpl.CompanyServiceImpl;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {
  private final JobRepository jobRepository;
  private final CompanyRepository companyRepository;
  private final CompanyKeySkillRepository companyKeySkillRepository;
  private final RecruiterRepository recruiterRepository;
  private final CompanyServiceImpl companyServiceImpl;

  @GetMapping
  public ResponseEntity<Page<CompanyResponse>> getAllCompanies(@RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Company> allCompany = companyServiceImpl.listAllCompany(page, size);
    if (allCompany.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Page<CompanyResponse> companyResponses = allCompany.map(this::mapToCompanyResponse);
    return ResponseEntity.ok().body(companyResponses);
  }

  @GetMapping("/findByName")
  public ResponseEntity<Page<CompanyResponse>> findCompanyByName(@RequestParam("name") String name,
                                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                                 @RequestParam(value = "size", defaultValue = "20") int size) {
    Page<Company> company = companyServiceImpl.findCompanyByName(name, page, size);
    if (company == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Page<CompanyResponse> companyResponses = company.map(this::mapToCompanyResponse);
    return ResponseEntity.ok().body(companyResponses);
  }


  @GetMapping("{id}")
  public ResponseEntity<CompanyWithJobsResponse> findCompanyById(@PathVariable Integer id) {
    var company = companyServiceImpl.findCompanyById(id);
    if (company == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    List<Job> jobOpenings = jobRepository.findJobByCompanyId(company.getId());
    if (jobOpenings.isEmpty()) {
      company.setCountJobOpening(0);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      company.setCountJobOpening(jobOpenings.size());
    }
    List<JobOpeningResponse> jobOpeningResponses = new ArrayList<>();
    for (Job job : jobOpenings) {
      var jobResponse = mapToCreateJobOpening(job, company);
      jobOpeningResponses.add(jobResponse);
    }
    CompanyWithJobsResponse response = mapToCompanyWithJobsResponse(company, jobOpeningResponses);
    companyRepository.save(company);
    return ResponseEntity.ok().body(response);
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

  @GetMapping("/name/company")
  public ResponseEntity<CompanyResponse> findCompanyByName(@RequestParam("name") String name) {
    var company = companyRepository.findByName(name);
    if (company == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    CompanyResponse companyResponse = mapToCompanyResponse(company);
    return new ResponseEntity<>(companyResponse, HttpStatus.OK);
  }

  public CompanyResponse mapToCompanyResponse(Company company) {
    return CompanyResponse.builder()
            .companyId(company.getId())
            .companyName(company.getName())
            .companyLogo(company.getLogo())
            .companyType(company.getCompanyType().getType())
            .address(company.getAddress())
            .description(company.getDescription())
            .website(company.getWebsite())
            .phoneNumber(company.getPhoneNumber())
            .industry(company.getIndustry())
            .createdDate(company.getCreatedDate())
            .countJobOpenings(company.getCountJobOpening())
            .companySize(company.getCompanySize())
            .country(company.getCountry())
            .foundedDate(company.getFoundedDate())
            .build();
  }

  public JobOpeningResponse mapToCreateJobOpening(Job job, Company company) {
    return JobOpeningResponse.builder()
            .jobId(job.getId())
            .title(job.getTitle())
            .companyName(company.getName())
            .address(company.getAddress())
            .companyType(company.getCompanyType().getType())
            .skills(job.getSkills())
            .description(job.getDescription())
            .companyLogo(company.getLogo())
            .createdDate(job.getCreatedAt().toLocalDate())
            .build();
  }

  public CompanyWithJobsResponse mapToCompanyWithJobsResponse(Company company, List<JobOpeningResponse> jobOpeningResponses) {
    return CompanyWithJobsResponse.builder()
            .companyId(company.getId())
            .companyName(company.getName())
            .companyLogo(company.getLogo())
            .companyType(company.getCompanyType().getType())
            .address(company.getAddress())
            .description(company.getDescription())
            .website(company.getWebsite())
            .phoneNumber(company.getPhoneNumber())
            .industry(company.getIndustry())
            .createdDate(company.getCreatedDate())
            .countJobOpenings(company.getCountJobOpening())
            .companySize(company.getCompanySize())
            .country(company.getCountry())
            .foundedDate(company.getFoundedDate())
            .jobOpenings(jobOpeningResponses)
            .build();
  }


}
