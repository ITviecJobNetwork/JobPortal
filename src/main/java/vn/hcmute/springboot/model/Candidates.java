  package vn.hcmute.springboot.model;

  import jakarta.persistence.Column;
  import jakarta.persistence.Entity;
  import jakarta.persistence.GeneratedValue;
  import jakarta.persistence.GenerationType;
  import jakarta.persistence.Id;
  import jakarta.persistence.Table;
  import jakarta.persistence.JoinColumn;
  import jakarta.persistence.ManyToOne;
  import java.sql.Timestamp;
  import java.util.Date;
  import lombok.Getter;
  import lombok.NoArgsConstructor;
  import lombok.Setter;

  @Entity
  @Table(name = "candidates")
  @Getter
  @Setter
  @NoArgsConstructor
  public class Candidates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String gender;
    private String phoneNumber;
    private Timestamp birthDate;
    private String email;
    private String aboutMe; // Use camelCase for Java property names

    @Column(name = "work_experience_id")
    private Integer workExperienceId;

    @Column(name = "education_id")
    private Integer educationId;

    private String status;

    @Column(name = "cover_letter")
    private String coverLetter;

    @Column(name = "link_cv")
    private String linkCV;


    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

  }
