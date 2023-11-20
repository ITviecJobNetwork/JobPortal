package vn.hcmute.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.CandidateSkill;
import vn.hcmute.springboot.model.Skill;

import java.util.List;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, Integer> {
    @Query("SELECT s FROM CandidateSkill s JOIN s.users u WHERE u.id = :userId")
    List<CandidateSkill> findByUserId(@Param("userId") Integer userId);
}
