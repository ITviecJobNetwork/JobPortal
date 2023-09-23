> :warning: **Only Support Setter Injection**

### Methods is annotated @HttpMethod(POST):
 - return type is com.fashion.dto.base.Result
 
### Methods is annotated @HttpMethod(GET)
 - return type is com.fashion.dto.base.PageContent

### Run with docker
- Setup Mysql: Edit username/password in `hibernate-docker.cfg.xml`
- Setup tomcat account: `.\tomcat\tomcat-users.xml`
- For window: Run command `.\docker-start.bat`