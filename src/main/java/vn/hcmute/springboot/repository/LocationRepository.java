package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Location;

public interface LocationRepository extends JpaRepository<Location, Integer>
{
  Location findByCityName(String cityName);
}
