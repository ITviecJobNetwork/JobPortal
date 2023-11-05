package vn.hcmute.springboot.repository;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.Skill;

public interface JobRepository extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {


  @Query("SELECT j FROM Job j JOIN j.skills s WHERE s = :skill")
  Page<Job> findJobsBySkills(@Param("skill") Skill skill, Pageable pageable);

  @Query("SELECT j FROM Job j")
  Page<Job> findAllJobs(Pageable pageable);

  Page<Job> findJobByIsReadAtTrue(Pageable pageable);


  @Query("SELECT j FROM Job j JOIN j.candidateLevels s WHERE s.candidateLevel = :candidateLevel")
  Page<Job> findJobsByCandidateLevel(@Param("candidateLevel") String candidateLevel,
      Pageable pageable);

  @Query("SELECT j FROM Job j JOIN j.company s WHERE s.name = :companyName")
  Page<Job> findJobsByCompanyName(@Param("companyName") String companyName, Pageable pageable);

  @Query("SELECT j FROM Job j JOIN j.location s WHERE s.cityName = :location")
  Page<Job> findJobByLocation(@Param("location") String location, Pageable pageable);

  @Query("SELECT DISTINCT j FROM Job j " +
      "LEFT JOIN j.company c " +
      "LEFT JOIN j.location l " +
      "LEFT JOIN j.candidateLevels cl " +
      "LEFT JOIN j.skills s " +
      "WHERE " +
      "(:keyword is null OR :keyword = '' OR REPLACE(j.title, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%') "
      +
      "OR REPLACE(c.name, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%') " +
      "OR REPLACE(l.cityName, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%') " +
      "OR REPLACE(cl.candidateLevel, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%') " +
      "OR REPLACE(s.title, ' ', '') LIKE CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
      "AND (:salaryMin is null OR j.minSalary >= :salaryMin) " +
      "AND (:salaryMax is null OR j.maxSalary <= :salaryMax) " +
      "AND (:companyType is null OR j.companyType.type IN :companyType) " +
      "AND (:jobType is null OR j.jobType.jobType IN :jobType) " +
      "AND (:candidateLevel is null OR REPLACE(cl.candidateLevel, ' ', '') IN :candidateLevel)")
  Page<Job> findByKeywordAndFilters(
      @Param("keyword") String keyword,
      @Param("salaryMin") Double salaryMin,
      @Param("salaryMax") Double salaryMax,
      @Param("companyType") List<String> companyType,
      @Param("jobType") List<String> jobType,
      @Param("candidateLevel") List<String> candidateLevel,
      Pageable pageable);

  @Query("SELECT j FROM Job j WHERE j.id != :appliedJobId "
      + "AND j.title LIKE %:title% " + "OR j.location.cityName LIKE %:location%")
  List<Job> findSimilarJobsByTitleAndLocation(@Param("appliedJobId") Integer appliedJobId,
      @Param("title") String title, @Param("location") String location);


  List<Job> findJobByCompanyId(Integer id);

}


