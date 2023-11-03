package vn.hcmute.springboot.request;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

  @Getter
  @Setter
  @Data
  public class FavouriteJobRequest {
    private Integer id;
    List<String> skills;
    List<String> experiences;
    Double currentSalary;
    Double minSalary;
    Double maxSalary;
    List<String> jobType;
    List<String> companyType;
    List<String> companySize;
    List<String> jobLocation;
  }
