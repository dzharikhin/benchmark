package ru.hh.test.jersey.one;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import static org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM;
import static org.springframework.web.context.ContextLoader.CONTEXT_CLASS_PARAM;

public final class V1App {
  public static void main(String[] args) throws Exception {

    ServletContextHandler context = new ServletContextHandler();

    context.addEventListener(new ContextLoaderListener());
    context.addEventListener(new RequestContextListener());

    context.setInitParameter(CONTEXT_CLASS_PARAM, XmlWebApplicationContext.class.getName());
    context.setInitParameter(CONFIG_LOCATION_PARAM, "classpath:ctx.xml");

    SpringServlet jerseyServlet = new SpringServlet();
    ServletHolder jerseyServletHolder = new ServletHolder(jerseyServlet);
    context.addServlet(jerseyServletHolder, "/rest/*");

    Server server = new Server(8081);
    server.setHandler(context);

    server.start();
    server.join();
  }
}
