package network.impl.jxta;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

import net.jxta.document.MimeMediaType;
import network.api.Peer;
import network.api.advertisement.Advertisement;

public class JxtaAdvertisement implements Advertisement{

	private Advertisement adv;
	
	
	public JxtaAdvertisement(Advertisement adv) {
		System.out.println("Adv : \n" + new XMLOutputter().outputString(adv.getDocument()));
		this.adv = adv;
	}
	
	@Override
	public String getName() {
		return adv.getName();
	}

	@Override
	public String getAdvertisementType() {
		return "jxta:" + getName();
	}
	
	public AdvertisementBridge getJxtaAdvertisementBridge() {
		System.out.println(new AdvertisementBridge(this).getDocument(MimeMediaType.XML_DEFAULTENCODING));
		return new AdvertisementBridge(this);
	}


	@Override
	public void publish(Peer peer) {
		adv.publish(peer);
	}

	@Override
	public void initialize(Document doc) {
		adv.initialize(doc);
	}

	@Override
	public Document getDocument() {
		return adv.getDocument();
	}

	@Override
	public String[] getIndexFields() {
		return adv.getIndexFields();
	}

	@Override
	public String getSourceURI() {
		return adv.getSourceURI();
	}

	@Override
	public void setSourceURI(String uri) {
		adv.setSourceURI(uri);
	}

}
