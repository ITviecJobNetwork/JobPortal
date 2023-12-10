package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.springboot.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer>
{
  Optional<Location> findByCityName(String cityName);

  Optional<Location> findFirstByCityName(String cityName);
}
