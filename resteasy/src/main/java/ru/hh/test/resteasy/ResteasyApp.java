package ru.hh.test.resteasy;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.plugins.spring.SpringContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import static org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX;
import static org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM;
import static org.springframework.web.context.ContextLoader.CONTEXT_CLASS_PARAM;

public final class ResteasyApp {
  public static void main(String[] args) throws Exception {

    ServletContextHandler context = new ServletContextHandler();

    context.addEventListener(new ResteasyBootstrap());
    context.addEventListener(new SpringContextLoaderListener());

    context.setInitParameter(CONTEXT_CLASS_PARAM, XmlWebApplicationContext.class.getName());
    context.setInitParameter(CONFIG_LOCATION_PARAM, "classpath:ctx.xml");
    context.setInitParameter(RESTEASY_SERVLET_MAPPING_PREFIX, "/rest");

    ServletHolder resteasyServlet = new ServletHolder(HttpServletDispatcher.class);

    context.addServlet(resteasyServlet, "/rest/*");

    Server server = new Server(8084);
    server.setHandler(context);

    server.start();
    server.join();
  }
}
