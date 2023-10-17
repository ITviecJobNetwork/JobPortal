package vn.hcmute.springboot.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "web", ignoreUnknownFields = false)
public class WebConfigProperties {

  private Cors cors;

  @Getter
  @Setter
  public static class Cors {

    private String[] allowedOrigins;
    private String[] allowedMethods;
    private Long maxAge;
    private String[] allowedHeaders;
    private String[] exposedHeaders;

  }

}