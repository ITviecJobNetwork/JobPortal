package vn.hcmute.springboot.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "address")
  private String address;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "website")
  private String website;

  @Column(name = "industry")
  private String industry;

  @Column(name = "founded_date")
  private Date foundedDate;

  @Column(name = "description")
  private String description;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_date")
  private Date createdDate;

  @Column(name = "last_modified_by")
  private String lastModifiedBy;

  @Column(name = "last_modified_date")
  private Date lastModifiedDate;


}

