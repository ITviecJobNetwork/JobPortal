package vn.hcmute.springboot.request;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class AboutMeRequest {
    private String aboutMe;
}
