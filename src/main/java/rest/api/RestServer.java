package rest.api;

/**
 * Interface for implementing rest server
 * @author Julien Prudhomme
 *
 */
public interface RestServer {
	/**
	 * Initialize the server with the class that handle the REST api
	 * @param packageName Controller package that contains classes with REST annotations
	 */
	public void initialize(String packageName);
	
	/**
	 * Start the REST api on port {@code port}
	 * @param port the port the webserver will listen
	 * @throws Exception if server start failed.
	 */
	public void start(int port) throws Exception;
	
	/**
	 * Stop the server.
	 */
	public void stop();
}
