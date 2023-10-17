package vn.hcmute.springboot.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder()
        .group("spring-boot-3-starter")
        .pathsToMatch("/api/**")
        .build();
  }

  @Bean
  public OpenAPI springShopOpenAPI() {
    var license = new License().name("License of API").url("API license URL");
    var info = new Info()
        .title("Spring Boot 3 Starter REST API")
        .description("Some custom description of API.")
        .version("v1.0.0")
        .license(license);
    var securityItem = new SecurityRequirement().addList("JWT");
    return new OpenAPI()
        .info(info)
        .components(createComponents())
        .addSecurityItem(securityItem);
  }

  private Components createComponents() {
    var components = new Components();
    components.addSecuritySchemes("JWT", createSecurityScheme());

    return components;
  }

  private SecurityScheme createSecurityScheme() {
    return new SecurityScheme()
        .name("Authorization")
        .type(SecurityScheme.Type.APIKEY)
        .in(In.HEADER)
        .scheme("apiKey");
  }

}
