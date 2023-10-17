package vn.hcmute.springboot.serviceImpl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.model.User;
import vn.hcmute.springboot.model.UserStatus;
import vn.hcmute.springboot.repository.UserRepository;
import vn.hcmute.springboot.response.LoginResponse;
import vn.hcmute.springboot.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  public LoginResponse login(LoginResponse userLoginResponse) {
    LoginResponse response = new LoginResponse();
    List<User> users = userRepository.findAll();
    for(User user:users){
      if(user.getEmail().equals(userLoginResponse.getEmail())){
        if(user.getStatus().equals(UserStatus.INACTIVE)){
          response.setStatus("Tài khoản chưa được kích hoạt");
          System.out.println(response.getEmail());
          System.out.println(response.getPassword());
          System.out.println(response.getStatus());
        }
        else if (!user.getPassword().equals(userLoginResponse.getPassword())) {
          response.setStatus("Mật khẩu sai");
          System.out.println(response.getEmail());
          System.out.println(response.getPassword());
          System.out.println(response.getStatus());
        }
        else{
          response.setEmail(user.getEmail());
          response.setPassword(user.getPassword());
          response.setId(user.getId());
          response.setStatus("Đăng nhập thành công");
        }
        break;
      }
      else{
        if(!user.getEmail().equals(userLoginResponse.getEmail())){
          response.setStatus("Email này chưa đăng ký");
          System.out.println(response.getEmail());
          System.out.println(response.getEmail());
          System.out.println(response.getPassword());
          System.out.println(response.getStatus());
        }
      }
    }
    return response;
  }
}
