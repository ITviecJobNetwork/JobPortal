package vn.hcmute.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "candidate_education")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor




public class CandidateEducation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToMany(mappedBy = "educations",fetch = FetchType.EAGER)
  private Set<User> users;
  @Column(name = "major")
  private String major;

  @Column(name = "school")
  private String school;

  @Column(name = "start_time")
  private LocalDate startTime;

  @Column(name = "end_time")
  private LocalDate endTime;

  @Column(name = "description")
  private String description;




}
