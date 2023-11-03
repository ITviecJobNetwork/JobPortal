package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.FavouriteJobType;

public interface FavouriteJobTypeRepository extends JpaRepository<FavouriteJobType, Integer> {

}
