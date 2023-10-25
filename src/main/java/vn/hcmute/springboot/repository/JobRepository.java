package vn.hcmute.springboot.repository;


import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.Job;
import vn.hcmute.springboot.model.Skill;

public interface JobRepository extends JpaRepository<Job,Integer>, JpaSpecificationExecutor<Job> {
  @Query("SELECT j FROM Job j JOIN j.skills s WHERE s = :skill")
  List<Job> findJobsBySkills(@Param("skill") Skill skill);

  @Query("SELECT j FROM Job j JOIN j.candidateLevel s WHERE s.candidateLevel = :candidateLevel")
  List<Job> findJobsByCandidateLevel(@Param("candidateLevel") String candidateLevel);

  @Query("SELECT j FROM Job j JOIN j.company s WHERE s.name = :companyName")
  List<Job> findJobsByCompanyName(@Param("companyName") String companyName);

  @Query("SELECT j FROM Job j JOIN j.location s WHERE s.cityName = :location")
  List<Job> findJobByLocation(@Param("location") String location);
  @Query("SELECT j FROM Job j " +
      "LEFT JOIN j.company c " +
      "LEFT JOIN j.location l " +
      "LEFT JOIN j.candidateLevel cl " +
      "LEFT JOIN j.skills s " +
      "WHERE " +
      "j.title LIKE %:keyword% OR " +
      "c.name LIKE %:keyword% OR " +
      "l.cityName LIKE %:keyword% OR " +
      "cl.candidateLevel LIKE %:keyword% OR " +
      "s.title LIKE %:keyword%")
  List<Job> findByKeywordAllIgnoreCase(@Param("keyword")String keyword);
//  default List<Job> findByKeyword(String keyword) {
//    return findAll((Specification<Job>) (root, query, builder) -> {
//      List<Predicate> predicates = new ArrayList<>();
//
//      if (keyword != null) {
//        String keywordLike = "%" + keyword.toLowerCase() + "%";
//        predicates.add(builder.like(builder.lower(root.get("skills").get("title").as(String.class)), keywordLike));
//        predicates.add(builder.like(builder.lower(root.get("candidateLevel").get("candidateLevel").as(String.class)), keywordLike));
//        predicates.add(builder.like(builder.lower(root.get("company").get("name").as(String.class)), keywordLike));
//        predicates.add(builder.like(builder.lower(root.get("location").get("cityName").as(String.class)), keywordLike));
//      }
//
//      return builder.or(predicates.toArray(new Predicate[0]));
//    });
//  }

}


