package ru.hh.test.jersey;

import java.util.Arrays;
import java.util.UUID;

public class Dao {
  public TestDto getTestDto() {
    TestDto testDto = new TestDto();
    testDto.anInt = 1;
    testDto.uuid = UUID.randomUUID();
    testDto.strings = Arrays.asList("string1", "string2");
    return testDto;
  }
}
