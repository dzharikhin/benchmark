package ru.hh.test.jersey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.util.Arrays;
import java.util.UUID;

public class Dao {

  public static final ObjectMapper mapper = new ObjectMapper();

  public ObjectWriter getWriter() {
    return mapper.writerFor(TestDto.class);
  }

  public TestDto getTestDto() {
    TestDto testDto = new TestDto();
    testDto.anInt = 1;
    testDto.uuid = UUID.randomUUID();
    testDto.strings = Arrays.asList("string1", "string2");
    return testDto;
  }
}
