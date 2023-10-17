package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Recruiters;

public interface RecruiterRepository extends JpaRepository<Recruiters,Integer> {

}
