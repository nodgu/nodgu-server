package io.github.nodgu.core_server.global.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {
  private static final ObjectMapper M = new ObjectMapper();
  public String convertToDatabaseColumn(List<String> a){ try { return M.writeValueAsString(a); } catch(Exception e){ return "[]"; } }
  public List<String> convertToEntityAttribute(String s){ try { return M.readValue(s, new TypeReference<>(){}); } catch(Exception e){ return new ArrayList<>(); } }
}
