package vn.hcmute.springboot.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);
  boolean existsByNickname(String nickname);
  Optional<User> findByUsernameIgnoreCase(String username);
}


