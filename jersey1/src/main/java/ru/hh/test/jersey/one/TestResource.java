package ru.hh.test.jersey.one;

import java.util.List;
import java.util.UUID;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.hh.test.jersey.Dao;
import ru.hh.test.jersey.TestDto;
import static ru.hh.test.jersey.one.TestResource.VERSION;

@Component
@Path(VERSION)
public class TestResource {
  public static final String VERSION = "v1";
  private final Dao dao;
  private final CacheControl cacheControl;

  @Autowired
  public TestResource(Dao dao, @Qualifier("noCache") CacheControl cacheControl) {
    this.dao = dao;
    this.cacheControl = cacheControl;
  }

  @GET
  public String version() {
    return VERSION;
  }

  @Path("testGet")
  @GET
  public Response test() {
    return Response.ok(dao.getTestDto()).cacheControl(cacheControl).build();
  }

  @Path("testGetJson")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response testJson() {
    return Response.ok(dao.getTestDto()).cacheControl(cacheControl).build();
  }

  @Path("testBuild")
  @POST
  public TestDto testIncoming(@HeaderParam("anInt") int anInt, @QueryParam("string") List<String> strings, @FormParam("uuid") UUID uuid) {
    TestDto testDto = new TestDto();
    testDto.anInt = anInt;
    testDto.strings = strings;
    testDto.uuid = uuid;
    return testDto;
  }
}
