package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.CandidateLevel;

public interface CandidateLevelRepository extends JpaRepository<CandidateLevel, Integer>
{
  CandidateLevel findByCandidateLevel(String candidateLevel);
}
