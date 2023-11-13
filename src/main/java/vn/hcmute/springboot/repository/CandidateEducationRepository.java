package vn.hcmute.springboot.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.CandidateEducation;
import vn.hcmute.springboot.model.Skill;

import java.util.List;


public interface CandidateEducationRepository extends JpaRepository<CandidateEducation,Integer> {


  @Modifying
  @Query("DELETE FROM CandidateEducation ce WHERE ce.id = :educationId")
  void deleteByEducationId(@Param("educationId") Integer educationId);

  @Query("SELECT s FROM CandidateEducation s JOIN s.users u WHERE u.id = :userId")
  List<CandidateEducation> findByUserId(@Param("userId") Integer userId);


}
