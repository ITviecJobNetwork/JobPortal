package vn.hcmute.springboot.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException e) throws IOException {
    log.error("Unauthorized error: {}", e.getMessage());
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
  }

}