package vn.hcmute.springboot.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>
{
  Optional<Role> findByName(String name);
}
