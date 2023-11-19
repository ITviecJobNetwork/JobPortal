package vn.hcmute.springboot.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.springboot.model.Location;


import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostInfoCompanyRequest {
  private String companyLogo;
  private String address;
  private String description;
  private LocalDate foundedDate;
  private String industry;
  private String companyName;
  private String phoneNumber;
  private String website;
  private Integer companySize;
  private String country;
  private String companyType;
  private String location;
}
