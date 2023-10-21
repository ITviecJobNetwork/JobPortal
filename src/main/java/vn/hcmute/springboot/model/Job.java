package vn.hcmute.springboot.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jobs")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Job {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id",nullable = false,unique = true)
  private Integer id;

  @Column(name = "title")
  private String title;

  @Column(name = "company_id")
  private Integer companyId;

  @Column(name = "job_type_id")
  private Integer jobTypeId;

  @Column(name = "location_id")
  private Integer locationId;

  @Column(name = "min_salary")
  private Double minSalary;

  @Column(name = "max_salary")
  private Double maxSalary;

  @Column(name = "candidate_level_id")
  private Integer candidateLevelId;

  @Column(name = "description")
  private String description;

  @Column(name = "requirements")
  private String requirements;

  @Column(name = "expire_at")
  private Date expireAt;

  @Column(name = "created_date")
  private Date createdDate;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "last_modified_date")
  private Date lastModifiedDate;

  @Column(name = "last_modified_by")
  private String lastModifiedBy;

}

