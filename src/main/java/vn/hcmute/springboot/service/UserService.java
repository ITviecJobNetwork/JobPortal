package vn.hcmute.springboot.service;

import vn.hcmute.springboot.request.LoginRequest;
import vn.hcmute.springboot.response.LoginResponse;

public interface UserService {
  LoginResponse login(LoginResponse userLogin);

}
