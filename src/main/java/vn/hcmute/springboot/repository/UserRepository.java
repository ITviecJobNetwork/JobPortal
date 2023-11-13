package vn.hcmute.springboot.repository;


import jakarta.transaction.Transactional;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.response.GetJobResponse;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);
  boolean existsByNickname(String nickname);
  Optional<User> findByUsernameIgnoreCase(String username);


}


