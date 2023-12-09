package vn.hcmute.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JoinColumn(name = "company_id")
  private Company company;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "company_key_skill_id")
  private List<Skill> companyKeySkill;
}
