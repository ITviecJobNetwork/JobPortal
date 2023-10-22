package vn.hcmute.springboot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "company_key_skill")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompanyKeySkill {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "recruiter_id")
  private Recruiters recruiter;

  @ManyToOne
  @JoinColumn(name = "skill_id",nullable = false,unique = true)
  private Skill skill;


}
