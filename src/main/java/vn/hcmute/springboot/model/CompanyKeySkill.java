package vn.hcmute.springboot.model;

import jakarta.persistence.*;
import java.util.List;
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

  @OneToMany(mappedBy = "companyKeySkill", fetch=FetchType.EAGER,cascade = CascadeType.ALL)
  private List<Skill> skills;




}
