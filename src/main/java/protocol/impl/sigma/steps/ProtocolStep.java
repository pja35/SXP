package protocol.impl.sigma.steps;

public interface ProtocolStep {
	
	public String getName();
	public int getRound();
	public void sendMessage();
	public void setupListener();
	public void stop();
}
