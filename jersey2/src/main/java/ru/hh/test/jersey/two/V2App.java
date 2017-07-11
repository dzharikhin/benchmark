package ru.hh.test.jersey.two;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import static org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM;
import static org.springframework.web.context.ContextLoader.CONTEXT_CLASS_PARAM;

public final class V2App {
  public static void main(String[] args) throws Exception {

    ServletContextHandler context = new ServletContextHandler();

    context.addEventListener(new ContextLoaderListener());
    context.addEventListener(new RequestContextListener());

    context.setInitParameter(CONTEXT_CLASS_PARAM, XmlWebApplicationContext.class.getName());
    context.setInitParameter(CONFIG_LOCATION_PARAM, "classpath:ctx.xml");

    ResourceConfig resourceConfig = new ResourceConfig().packages(V2App.class.getPackage().getName());
    ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(resourceConfig));
    context.addServlet(jerseyServlet, "/rest/*");

    Server server = new Server(8082);
    server.setHandler(context);

    server.start();
    server.join();
  }
}
