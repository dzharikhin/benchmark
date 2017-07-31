package ru.hh.test.jersey.spring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import static org.springframework.web.context.ContextLoader.CONFIG_LOCATION_PARAM;
import static org.springframework.web.context.ContextLoader.CONTEXT_CLASS_PARAM;

public final class MvcApp {
  public static void main(String[] args) throws Exception {

    ServletContextHandler context = new ServletContextHandler();

    context.addEventListener(new ContextLoaderListener());
    context.addEventListener(new RequestContextListener());

    context.setInitParameter(CONTEXT_CLASS_PARAM, XmlWebApplicationContext.class.getName());
    context.setInitParameter(CONFIG_LOCATION_PARAM, "classpath:ctx.xml");

    AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
    webAppContext.setConfigLocation(MvcApp.class.getPackage().getName());

    DispatcherServlet dispatcherServlet = new DispatcherServlet(webAppContext);
    ServletHolder springServletHolder = new ServletHolder(dispatcherServlet);
    context.addServlet(springServletHolder, "/rest/*");

    Server server = new Server(8083);
    server.setHandler(context);

    server.start();
    server.join();
  }
}
