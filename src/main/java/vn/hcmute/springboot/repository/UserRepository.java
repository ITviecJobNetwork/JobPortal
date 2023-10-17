package vn.hcmute.springboot.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.model.UserStatus;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);
  boolean existsByUsername(String username);
  Optional<User>findByUsernameIgnoreCase(String username);
  boolean existsByEmail(String email);
}


