package ru.hh.test.jersey.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.hh.test.jersey.Dao;
import ru.hh.test.jersey.TestDto;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static ru.hh.test.jersey.spring.TestResource.VERSION;

@Component
@RequestMapping(VERSION)
public class TestResource {
  public static final String VERSION = "mvc";
  private final Dao dao;

  @Autowired
  public TestResource(Dao dao) {
    this.dao = dao;
  }

  @RequestMapping(value = "/string", method = GET)
  public @ResponseBody String version() {
    return VERSION;
  }

  @RequestMapping(value = "/testGet", method = GET, produces = MediaType.APPLICATION_XML_VALUE)
  @ResponseBody
  public TestDto test() {
    return dao.getTestDto();
  }

  @RequestMapping(value = "/testGetJson", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public TestDto testJson() {
    return dao.getTestDto();
  }

  @RequestMapping(value = "/testGetJackson", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public byte[] testJackson() throws JsonProcessingException {
    return dao.getWriter().writeValueAsBytes(dao.getTestDto());
  }

  @RequestMapping(value = "/testBuild", method = POST, produces = MediaType.APPLICATION_XML_VALUE)
  @ResponseBody
  public TestDto testIncoming(@RequestHeader("anInt") int anInt, @RequestParam("string") List<String> strings, @RequestParam("uuid") UUID uuid) {
    TestDto testDto = new TestDto();
    testDto.anInt = anInt;
    testDto.strings = strings;
    testDto.uuid = uuid;
    return testDto;
  }
}
