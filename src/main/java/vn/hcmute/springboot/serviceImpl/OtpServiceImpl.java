package vn.hcmute.springboot.serviceImpl;


import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.hcmute.springboot.service.OtpService;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {



  @Override
  public String generateOtp() {
    Random random = new Random();
    int randomNumber = random.nextInt(999999);
    String output = Integer.toString(randomNumber);

    while (output.length() < 6) {
      output = "0" + output;
    }
    return output;
  }

  @Override
  public String generateNewPassword() {
    String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder newPassword= new StringBuilder(20);
    for(int i=0;i<20;i++){
      newPassword.append(characters.charAt(new Random().nextInt(characters.length())));

    }
    return newPassword.toString();
  }


}


