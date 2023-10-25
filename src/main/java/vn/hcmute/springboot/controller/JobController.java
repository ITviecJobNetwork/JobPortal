package vn.hcmute.springboot.controller;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.CandidateLevel;
import vn.hcmute.springboot.model.Company;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.Location;
import vn.hcmute.springboot.model.Skill;
import vn.hcmute.springboot.repository.CandidateLevelRepository;
import vn.hcmute.springboot.repository.CompanyRepository;
import vn.hcmute.springboot.repository.LocationRepository;
import vn.hcmute.springboot.repository.SkillRepository;
import vn.hcmute.springboot.serviceImpl.JobServiceImpl;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

  private final JobServiceImpl jobService;
  private final SkillRepository skillRepository;
  private final CandidateLevelRepository candidateLevelRepository;
  private final CompanyRepository companyRepository;
  private final LocationRepository locationRepository;

  @GetMapping()
  public ResponseEntity<List<Job>> getAllJobs() {

    return ResponseEntity.ok(jobService.findAllJob());
  }

  @GetMapping("/searchBySkill")
  public List<Job> findJobsBySkill(@RequestParam("skill") String skillName) {
    Skill skill = skillRepository.findByName(skillName);
    if (skill != null) {
      return jobService.findJobByJobSkill(skillName);
    } else {
      return Collections.emptyList();
    }
  }

  @GetMapping("/searchByCandidateLevel")
  public List<Job> findJobByLevel(@RequestParam("candidateLevel") String candidateLevelName) {
    CandidateLevel candidateLevel = candidateLevelRepository.findByCandidateLevel(
        candidateLevelName);
    if (candidateLevel != null) {
      return jobService.findJobByCandidateLevel(candidateLevelName);
    } else {
      return Collections.emptyList();
    }
  }

  @GetMapping("/searchByCompany")
  public List<Job> findJobByCompany(@RequestParam("company") String companyName) {
    Company company = companyRepository.findByName(companyName);
    if (company != null) {
      return jobService.findJobByCompanyName(companyName);
    } else {
      return Collections.emptyList();
    }
  }

  @GetMapping("/searchByLocation")
  public List<Job> findJobByLocation(@RequestParam("location") String locationName) {
    Location location = locationRepository.findByCityName(locationName);
    if (location != null) {
      return jobService.findByLocation(locationName);
    } else {
      return Collections.emptyList();
    }
  }

  @GetMapping("/searchJob")
  public List<Job> searchJob(@RequestParam(required = false) String keyword) {
    List<Job> jobs;
    if (keyword != null && !keyword.isEmpty()) {
      jobs = jobService.findJobByKeyWord(keyword);
      if (jobs==null) {
        return Collections.emptyList();
      }
    } else {
      jobs = jobService.findAllJob();
    }

    return jobs;
  }


}
