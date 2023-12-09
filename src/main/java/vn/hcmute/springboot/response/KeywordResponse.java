package vn.hcmute.springboot.response;

import lombok.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class KeywordResponse {
  private List<String> keyword;
}
