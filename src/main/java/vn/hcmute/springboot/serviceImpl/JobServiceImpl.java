package vn.hcmute.springboot.serviceImpl;




import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.data.domain.Page;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final JobRepository jobRepository;
  private final SkillRepository skillRepository;
  private final CandidateLevelRepository candidateLevelRepository;
  private final CompanyRepository companyRepository;
  private final LocationRepository locationRepository;

  @Override
  public Page<Job> findAllJob(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    var allJobs = jobRepository.findAllJobs(pageable);
    if (allJobs.isEmpty()) {
      throw new NotFoundException("Hiện tại không có công việc nào");
    }
    return allJobs;
  }

  @Override
  public Page<Job> findJobByJobSkill(String skill,int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Skill skillName = skillRepository.findByName(skill);

    if (skill != null) {
      return jobRepository.findJobsBySkills(skillName,pageable);
    } else {
      throw new NotFoundException("Không có công việc với kỹ năng " + skillName);
    }
  }

  @Override
  public Page<Job> findJobByCandidateLevel(String level,int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    var candidateLevel = candidateLevelRepository.findByCandidateLevel(level);
    if (candidateLevel != null) {
      return jobRepository.findJobsByCandidateLevel(candidateLevel.getCandidateLevel(),pageable);
    }
    throw new NotFoundException("Không có công việc với tên level " + level);
  }

  @Override
  public Page<Job> findJobByCompanyName(String companyName,int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    var company = companyRepository.findCompanyByName(companyName,pageable);
    if (company!=null) {
      return jobRepository.findJobsByCompanyName(companyName,pageable);
    }
    throw new NotFoundException("Không có công việc với tên công ty là " + companyName);
  }

  @Override
  public Page<Job> findByLocation(String location,int page, int size) {
    var locationName = locationRepository.findByCityName(location);
    Pageable pageable = PageRequest.of(page, size);
    if (locationName != null) {
      return jobRepository.findJobByLocation(location,pageable);
    }
    throw new NotFoundException("Không có công việc với địa điểm đó " + location);
  }

  @Override
  public Page<Job> findJobsWithFilters(
      String keyword,
      Double salaryMin,
      Double salaryMax,
      List<String> companyType,
      List<String> jobType,
      List<String> candidateLevel,
      int page,
      int size
  ) {
    Pageable pageable = PageRequest.of(page, size);

    return jobRepository.findByKeywordAndFilters(
        keyword,
        salaryMin,
        salaryMax,
        companyType,
        jobType,
        candidateLevel,
        pageable
    );
  }

}
