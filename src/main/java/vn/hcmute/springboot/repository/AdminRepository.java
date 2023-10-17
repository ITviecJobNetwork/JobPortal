package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Admin;

public interface AdminRepository extends JpaRepository<Admin,Integer>{

}
