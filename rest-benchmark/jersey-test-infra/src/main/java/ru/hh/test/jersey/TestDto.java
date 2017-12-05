package ru.hh.test.jersey;

import java.util.List;
import java.util.UUID;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestDto {
  @HeaderParam("anInt")
  @XmlElement
  public int anInt;
  @QueryParam("string")
  @XmlElement
  public List<String> strings;
  @FormParam("uuid")
  @XmlElement
  public UUID uuid;
}
