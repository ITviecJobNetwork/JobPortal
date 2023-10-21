package vn.hcmute.springboot.service;
import com.fasterxml.jackson.databind.JsonNode;
import vn.hcmute.springboot.model.User;

public interface OtpService {
  String generateOtp();
  //boolean checkOtp(String otp);
  String generateNewPassword();

}
