package vn.hcmute.springboot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "search_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToMany
  @JoinColumn(name = "user_id")
  private Set<User> user;

  @Column(name = "search_key")
  private String searchKeyWord;

  @Column(name = "search_count")
  private Integer searchCount;
}
