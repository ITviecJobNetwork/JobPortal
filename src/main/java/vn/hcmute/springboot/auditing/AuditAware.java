
package vn.hcmute.springboot.auditing;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.hcmute.springboot.model.User;
public class AuditAware implements AuditorAware<Integer> {


  @Override
  @NonNull
  public Optional<Integer> getCurrentAuditor() {
    Authentication authentication =
        SecurityContextHolder
            .getContext()
            .getAuthentication();
    if (authentication == null ||
        !authentication.isAuthenticated() ||
        authentication instanceof AnonymousAuthenticationToken
    ) {
      return Optional.empty();
    }

    User userPrincipal = (User) authentication.getPrincipal();
    return Optional.ofNullable(userPrincipal.getId());
  }
}
