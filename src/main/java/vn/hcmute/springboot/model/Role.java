package vn.hcmute.springboot.model;

import static vn.hcmute.springboot.model.Permission.ADMIN_CREATE;
import static vn.hcmute.springboot.model.Permission.ADMIN_DELETE;
import static vn.hcmute.springboot.model.Permission.ADMIN_READ;
import static vn.hcmute.springboot.model.Permission.ADMIN_UPDATE;
import static vn.hcmute.springboot.model.Permission.CANDIDATE_CREATE;
import static vn.hcmute.springboot.model.Permission.CANDIDATE_DELETE;
import static vn.hcmute.springboot.model.Permission.CANDIDATE_READ;
import static vn.hcmute.springboot.model.Permission.CANDIDATE_UPDATE;
import static vn.hcmute.springboot.model.Permission.RECRUITER_CREATE;
import static vn.hcmute.springboot.model.Permission.RECRUITER_DELETE;
import static vn.hcmute.springboot.model.Permission.RECRUITER_READ;
import static vn.hcmute.springboot.model.Permission.RECRUITER_UPDATE;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@RequiredArgsConstructor

public enum Role{
  USER(
      Set.of(
          CANDIDATE_READ,
          CANDIDATE_UPDATE,
          CANDIDATE_CREATE,
          CANDIDATE_DELETE
      )
  ),

  ADMIN(
      Set.of(
          ADMIN_READ,
          ADMIN_UPDATE,
          ADMIN_DELETE,
          ADMIN_CREATE,
          RECRUITER_READ,
          RECRUITER_UPDATE,
          RECRUITER_CREATE,
          RECRUITER_DELETE,
          CANDIDATE_READ,
          CANDIDATE_UPDATE,
          CANDIDATE_CREATE,
          CANDIDATE_DELETE
      )
  ),
  RECRUITER(
      Set.of(
          RECRUITER_READ,
          RECRUITER_UPDATE,
          RECRUITER_CREATE,
          RECRUITER_DELETE
      )
  );





  @Getter(AccessLevel.PUBLIC)
  private final Set<Permission> permissions;

  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorities = getPermissions()
        .stream()
        .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
        .collect(Collectors.toList());
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return authorities;
  }

}