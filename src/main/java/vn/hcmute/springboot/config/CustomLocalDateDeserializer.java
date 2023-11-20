package vn.hcmute.springboot.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;


public class CustomLocalDateDeserializer extends JsonSerializer<YearMonth> {

  @Override
  public void serialize(YearMonth value, JsonGenerator gen,
                        SerializerProvider serializers) throws IOException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
    gen.writeString("dob:" + value.format(formatter));
  }
}
