package vn.hcmute.springboot.serviceImpl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.exception.UnauthorizedException;
import vn.hcmute.springboot.model.*;
import vn.hcmute.springboot.repository.*;
import vn.hcmute.springboot.response.CompanyKeySkillResponse;
import vn.hcmute.springboot.response.CompanyResponse;
import vn.hcmute.springboot.response.CompanyWithJobsResponse;
import vn.hcmute.springboot.response.JobOpeningResponse;
import vn.hcmute.springboot.service.CompanyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

  private final CompanyRepository companyRepository;
  private final JobRepository jobRepository;
  private final CompanyKeySkillRepository companyKeySkillRepository;

  @Override
  public Page<CompanyResponse> listAllCompany(int page,int size) {
    Pageable pageable = PageRequest.of(page, size);
    var company = companyRepository.findAllCompanies(pageable);
    if (company.isEmpty()) {
      throw new NotFoundException("Không tìm thấy công ty");
    }
    return company.map(this::mapToCompanyResponse);
  }



  @Override
  public CompanyWithJobsResponse findCompanyById(Integer id) {
    var company = companyRepository.findById(id);
    if (company.isEmpty()) {
      throw new NotFoundException("Không tìm thấy công ty");
    }
    List<Job> jobOpenings = jobRepository.findJobByCompanyId(company.get().getId());
    List<JobOpeningResponse> jobOpeningResponses = new ArrayList<>();
    if (jobOpenings.isEmpty()) {
      company.get().setCountJobOpening(0);
      throw new NotFoundException("Không tìm thấy công việc");
    } else {
      company.get().setCountJobOpening(jobOpenings.size());
    }
    for (Job job : jobOpenings) {
      var jobResponse = mapToCreateJobOpening(job, company.get());
      jobOpeningResponses.add(jobResponse);
    }
    CompanyWithJobsResponse response = mapToCompanyWithJobsResponse(company.get(), jobOpeningResponses);
    companyRepository.save(company.get());
    return response;
  }

  @Override
  public List<CompanyKeySkillResponse> listCompanyKeySkill(Integer companyId) {
    var company = companyRepository.findById(companyId);
    if (company.isEmpty()) {
      throw new NotFoundException("Không tìm thấy công ty");
    }
    List<CompanyKeySkill> companyKeySkills = companyKeySkillRepository.findByCompanyId(companyId);
    if (companyKeySkills == null) {
      throw new NotFoundException("Không tìm thấy kỹ năng");
    }
    for (CompanyKeySkill companyKeySkill : companyKeySkills) {
      companyKeySkill.setCompany(company.get());

    }
    companyKeySkillRepository.saveAll(companyKeySkills);
    return companyKeySkills.stream().map(this::mapToCompanyKeySkillResponse).collect(Collectors.toList());

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
            .createdDate(job.getCreatedAt())
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
  private CompanyKeySkillResponse mapToCompanyKeySkillResponse(CompanyKeySkill companyKeySkill) {

    return CompanyKeySkillResponse.builder()
            .id(companyKeySkill.getId())
            .title(companyKeySkill.getCompanyKeySkill().stream().map(Skill::getTitle).toList().toString())
            .companyId(companyKeySkill.getCompany().getId())
            .build();
  }
  


}
