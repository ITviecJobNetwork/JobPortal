package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

  Optional<Admin> findByEmail(String email);

  Boolean existsByEmail(String email);

}
