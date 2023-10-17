package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Candidates;

public interface CandidateRepository extends JpaRepository<Candidates,Integer>{

}
