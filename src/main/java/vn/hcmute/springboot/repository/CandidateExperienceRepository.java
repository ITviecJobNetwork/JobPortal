package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.CandidateEducation;
import vn.hcmute.springboot.model.CandidateExperience;

import java.util.List;

public interface CandidateExperienceRepository extends JpaRepository<CandidateExperience, Integer> {
  @Query("SELECT s FROM CandidateExperience s JOIN s.users u WHERE u.id = :userId")
  List<CandidateExperience> findByUserId(@Param("userId") Integer userId);

}
