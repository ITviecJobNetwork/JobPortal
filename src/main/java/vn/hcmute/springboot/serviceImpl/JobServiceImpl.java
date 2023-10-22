package vn.hcmute.springboot.serviceImpl;




import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.exception.NotFoundException;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.Skill;
import vn.hcmute.springboot.repository.CandidateLevelRepository;
import vn.hcmute.springboot.repository.CompanyRepository;
import vn.hcmute.springboot.repository.JobRepository;
import vn.hcmute.springboot.repository.LocationRepository;
import vn.hcmute.springboot.repository.SkillRepository;
import vn.hcmute.springboot.service.JobService;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final JobRepository jobRepository;
  private final SkillRepository skillRepository;
  private final CandidateLevelRepository candidateLevelRepository;
  private final CompanyRepository companyRepository;
  private final LocationRepository locationRepository;

  @Override
  public List<Job> findAllJob() {
    var allJobs = jobRepository.findAll();
    if (allJobs.isEmpty()) {
      throw new NotFoundException("No job found");
    }
    return allJobs;
  }

  @Override
  public List<Job> findJobByJobSkill(String skill) {
    Skill skillName = skillRepository.findByName(skill);

    if (skill != null) {
      return jobRepository.findJobsBySkills(skillName);
    } else {
      throw new NotFoundException("No job found with this skill" + null);
    }
  }

  @Override
  public List<Job> findJobByCandidateLevel(String level) {
    var candidateLevel = candidateLevelRepository.findByCandidateLevel(level);
    if (candidateLevel != null) {
      return jobRepository.findJobsByCandidateLevel(candidateLevel.getCandidateLevel());
    }
    throw new NotFoundException("No job found with this candidate level" + null);
  }

  @Override
  public List<Job> findJobByCompanyName(String companyName) {
    var company = companyRepository.findByName(companyName);
    if (company!=null) {
      return jobRepository.findJobsByCompanyName(companyName);
    }
    throw new NotFoundException("No job found with this company name" + null);
  }

  @Override
  public List<Job> findByLocation(String location) {
    var locationName = locationRepository.findByCityName(location);
    if (locationName != null) {
      return jobRepository.findJobByLocation(location);
    }
    throw new NotFoundException("No job found with this location" + null);
  }

  @Override
  public List<Job> findJobByKeyWord(String keyword) {
    return jobRepository.findByKeyword(keyword);
  }

}
