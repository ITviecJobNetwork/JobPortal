package vn.hcmute.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "title")
  private String title;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "company_key_skill_id")
  @JsonIgnore
  private CompanyKeySkill companyKeySkill;

  @ManyToMany(mappedBy = "skills", fetch = FetchType.EAGER)
  @JsonIgnore
  private Set<Job> jobs = new HashSet<>();
  @ManyToMany(mappedBy = "skills", fetch = FetchType.EAGER)
  private Set<User> users = new HashSet<>();
}