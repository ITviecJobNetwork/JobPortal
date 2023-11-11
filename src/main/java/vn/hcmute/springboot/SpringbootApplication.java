package vn.hcmute.springboot;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@RequiredArgsConstructor
// Adjust the package path accordingly

public class SpringbootApplication {


  public static void main(String[] args) {
    SpringApplication.run(SpringbootApplication.class, args);
  }


}
