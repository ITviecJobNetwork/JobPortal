package vn.hcmute.springboot.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidate_skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSkill {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "candidate_id",nullable = false,unique = true)
  private User candidate;

  @ManyToOne
  @JoinColumn(name = "skill_id",nullable = false,unique = true)
  private Skill skill;


}

