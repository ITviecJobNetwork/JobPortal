package vn.hcmute.springboot.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Recruiters;
import vn.hcmute.springboot.model.User;

public interface RecruiterRepository extends JpaRepository<Recruiters,Integer> {
  Optional<Recruiters> findByUsername(String username);
  boolean existsByUsername(String username);

  boolean existsByNickname(String nickname);
  Optional<Recruiters> findByUsernameIgnoreCase(String username);
}
