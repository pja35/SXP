package rest.impl;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.common.reflect.ClassPath;

import rest.api.RestServer;
import rest.api.ServletPath;

public class JettyRestServer implements RestServer{
	
	private ServletContextHandler context;
	private Server server;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(String packageName) {
		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
			for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			  if (info.getName().startsWith(packageName + ".")) {
			    final Class<?> clazz = info.load();
			    ServletPath path = clazz.getAnnotation(ServletPath.class);
				if(path == null) {
					continue;
				}
				ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, path.value());
				jerseyServlet.setInitOrder(0);
				jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", clazz.getCanonicalName());
			  }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*for(Class<?> c : entryPoints) {
        	
        	ServletPath path = c.getAnnotation(ServletPath.class);
        	if(path == null) {
        		throw new RuntimeException("No servlet path annotation on class " + c.getCanonicalName());
        	}
        	ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, path.value());
        	jerseyServlet.setInitOrder(0);
        	jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", c.getCanonicalName());
        }*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(int port) throws Exception {
		server = new Server(port);
        server.setHandler(context);
		server.start();
        server.join();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		server.destroy();
	}

}
