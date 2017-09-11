package rest.impl;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.google.common.reflect.ClassPath;

import controller.tools.LoggerUtilities;
import crypt.api.certificate.CertificateGenerator;
import crypt.impl.certificate.X509V3Generator;
import rest.api.RestServer;
import rest.api.ServletPath;

public class JettyRestServer implements RestServer{
	private final static Logger log = LogManager.getLogger(JettyRestServer.class);
	private ServletContextHandler context;
	private Server server;
	private CertificateGenerator cert_gen;

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
			LoggerUtilities.logStackTrace(e);
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

		server = new Server();
		server.setHandler(context);

		@SuppressWarnings("unused")
		String signe_type = "self-signed";

		/*if( signe_type == "CA-signed" )
		{
			//Launching a simple http on 80 port for challenge
			//the CA serveur.
			createAndSetConnector(80, "http"); //Launch in sudo bc of 80;
			server.start();

			TimeUnit.SECONDS.sleep(3); //Give some time to Jetty to be on.

			this.cert_gen = X509V3Generator.getInstance("certConfig.conf");
			this.cert_gen.CreateCertificate("CA-signed");
			this.cert_gen.StoreInKeystore("keystore.jks");

			//Restarting the serveur with good certificate.
			server.stop();
			createAndSetConnector(port, "https");
			//server.setHandler(context);
		}
		else if( signe_type == "self-signed" )
		{*/
		this.cert_gen = X509V3Generator.getInstance("certConfig.conf");
		this.cert_gen.CreateCertificate("self-signed");
		this.cert_gen.StoreInKeystore("keystore.jks");
		createAndSetConnector(port, "https");
		//}

		server.start();
		server.join();
	}

	/**
	 * Create and link the proper connector to
	 * the jetty serveur.
	 * @param port Port the server will use for the given protocol.
	 * @param protocol Protocol used by the jetty serveur (currently available protocols : http, https).
	 */
	public void createAndSetConnector(int port, String protocol) throws Exception 
	{

		// Http config (base config)
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(port);
		http_config.setOutputBufferSize(38768);
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				new javax.net.ssl.HostnameVerifier(){

					@Override
					public boolean verify(String hostname,
							javax.net.ssl.SSLSession sslSession) {
						if (hostname.equals("localhost")) {
							return true;
						}
						return false;
					}
				});

	/*	switch (protocol)
		{
		case "http":
			// Http Connector
			ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config) );
			http.setPort(port);
			http.setIdleTimeout(30000);

			server.setConnectors(new Connector[] {http});
			break;
	 
		case "https":*/
			// SSL Context factory for HTTPS
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStorePath("keystore.jks");
			sslContextFactory.setKeyStorePassword(this.cert_gen.getKsPassword());
			sslContextFactory.setKeyManagerPassword(this.cert_gen.getKsPassword());

			// HTTPS Config
			HttpConfiguration https_config = new HttpConfiguration(http_config);
			SecureRequestCustomizer src = new SecureRequestCustomizer();
			src.setStsMaxAge(2000);
			src.setStsIncludeSubDomains(true);
			https_config.addCustomizer(src);

			// HTTPS Connector
			ServerConnector https = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
					new HttpConnectionFactory(https_config));
			https.setPort(port);
			https.setIdleTimeout(500000);
			log.debug("HTTPS context");
			server.setConnectors(new Connector[] {https}); 
		/*	break;

		case "http&https":
			// Http Connector
			ServerConnector httpb = new ServerConnector(server, new HttpConnectionFactory(http_config) );
			httpb.setPort(port);
			httpb.setIdleTimeout(30000);

			// SSL Context factory for HTTPS
			SslContextFactory sslContextFactoryb = new SslContextFactory();
			sslContextFactoryb.setKeyStorePath("keystore.jks");
			sslContextFactoryb.setKeyStorePassword(this.cert_gen.getKsPassword());
			sslContextFactoryb.setKeyManagerPassword(this.cert_gen.getKsPassword());

			// HTTPS Config
			HttpConfiguration https_configb = new HttpConfiguration(http_config);
			SecureRequestCustomizer srcb = new SecureRequestCustomizer();
			srcb.setStsMaxAge(2000);
			srcb.setStsIncludeSubDomains(true);
			https_configb.addCustomizer(srcb);

			// HTTPS Connector
			ServerConnector httpsb = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactoryb, HttpVersion.HTTP_1_1.asString()),
					new HttpConnectionFactory(https_configb));
			httpsb.setPort(port+1);
			httpsb.setIdleTimeout(500000);

			server.setConnectors(new Connector[] {httpb, httpsb}); 
			break;


		default: 
			System.out.println("Wrong connector protocol for jetty.");
			System.exit(1);
			break;
		}*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		server.destroy();
	}
}
