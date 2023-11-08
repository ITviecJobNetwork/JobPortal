package vn.hcmute.springboot.security;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.hcmute.springboot.auditing.AuditAware;
import vn.hcmute.springboot.model.Recruiters;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.repository.RecruiterRepository;
import vn.hcmute.springboot.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final UserRepository repository;
  private final RecruiterRepository recruiterRepository;


  @Bean
  public UserDetailsService userDetailsService() {
    return email -> {
      Optional<User> user = repository.findByUsername(email);
      if (user.isPresent()) {
        return user.get();
      }

      Optional<Recruiters> recruiter = recruiterRepository.findByUsername(email);
      if (recruiter.isPresent()) {
        return recruiter.get();
      }

      throw new UsernameNotFoundException("user-not-found");
    };
  }


  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuditorAware<Integer> auditorAware() {
    return new AuditAware();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
