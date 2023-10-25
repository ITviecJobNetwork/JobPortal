package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.CandidateLevel;

public interface CandidateLevelRepository extends JpaRepository<CandidateLevel, Long>
{
  CandidateLevel findByCandidateLevel(String candidateLevel);
}
