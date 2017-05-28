package protocol.impl.sigma.steps;

import protocol.impl.SigmaEstablisher;

public interface ProtocolStep {
	
	/**
	 * @return TITLE of the messages sent in this step
	 */
	public String getName();
	
	/**
	 * @return current round of the specific step
	 */
	public int getRound();
	
	/**
	 * Send the initating message
	 */
	public void sendMessage();
	
	/**
	 * Setup the protocol with the listener
	 */
	public void setupListener();
	
	/**
	 * Remove every listener previously set
	 */
	public void stop();
	
	/**
	 * Restart the step if establisher stopped before
	 * @param sigmaE : the sigmaEstablisher used
	 */
	public void restore(SigmaEstablisher sigmaE);
}
