package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.CandidateExperience;

public interface CandidateExperienceRepository extends JpaRepository<CandidateExperience, Integer> {

}
