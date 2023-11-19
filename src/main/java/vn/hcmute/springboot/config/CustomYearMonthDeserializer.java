package vn.hcmute.springboot.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomYearMonthDeserializer extends JsonDeserializer<YearMonth> {

  @Override
  public YearMonth deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
    String date = jsonParser.getText();
    try {
      // Try parsing with the default format first (yyyy-MM)
      return YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));
    } catch (DateTimeParseException e) {
      return YearMonth.parse(date, DateTimeFormatter.ofPattern("MM-yyyy"));
    }
  }
}
