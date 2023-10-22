package vn.hcmute.springboot.model;

import jakarta.persistence.Column;
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
@Table(name = "favourite_job_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavouriteJobType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "candidate_id",nullable = false,unique = true)
  private User candidate;

  @Column(name = "title", length = 255)
  private String title;

  @Column(name = "salary", length = 255)
  private String salary;

  @Column(name = "locations", length = 255)
  private String locations;

  @Column(name = "experience")
  private Integer experience;

  @ManyToOne
  @JoinColumn(name = "job_type")
  private JobType jobType;
}
