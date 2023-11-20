package vn.hcmute.springboot.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import vn.hcmute.springboot.config.CustomLocalDateDeserializer;

import java.time.LocalDate;


@Getter
@Setter
@Data
public class AddExperienceRequest {
    @NotBlank(message = "Company name is mandatory")
    private String companyName;
    @NotBlank(message = "Job title is mandatory")
    private String jobTitle;
    @NotBlank(message = "Description is mandatory")
    private String description;
    @NotBlank(message = "Description is mandatory")
    private String startDate;
    @NotBlank(message = "Description is mandatory")
    private String endDate;

}
