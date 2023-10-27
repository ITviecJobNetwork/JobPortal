package vn.hcmute.springboot.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.Skill;

public interface JobRepository extends JpaRepository<Job,Integer>, JpaSpecificationExecutor<Job> {
  @Query("SELECT j FROM Job j JOIN j.skills s WHERE s = :skill")
  Page<Job> findJobsBySkills(@Param("skill") Skill skill,Pageable pageable);

  @Query("SELECT j FROM Job j")
  Page<Job> findAllJobs(Pageable pageable);

  @Query("SELECT j FROM Job j JOIN j.candidateLevels s WHERE s.candidateLevel = :candidateLevel")
  Page<Job> findJobsByCandidateLevel(@Param("candidateLevel") String candidateLevel,Pageable pageable);

  @Query("SELECT j FROM Job j JOIN j.company s WHERE s.name = :companyName")
  Page<Job> findJobsByCompanyName(@Param("companyName") String companyName,Pageable pageable);

  @Query("SELECT j FROM Job j JOIN j.location s WHERE s.cityName = :location")
  Page<Job> findJobByLocation(@Param("location") String location,Pageable pageable);
  @Query("SELECT DISTINCT j FROM Job j " +
      "LEFT JOIN j.company c " +
      "LEFT JOIN j.location l " +
      "LEFT JOIN j.candidateLevels cl " +
      "LEFT JOIN j.skills s " +
      "WHERE " +
      "j.title LIKE %:keyword% OR " +
      "c.name LIKE %:keyword% OR " +
      "l.cityName LIKE %:keyword% OR " +
      "cl.candidateLevel LIKE %:keyword% OR " +
      "s.title LIKE %:keyword%")
  Page<Job> findByKeywordAllIgnoreCase(@Param("keyword") String keyword, Pageable pageable);



}


