package network.impl.jxta;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import org.jdom2.Element;

import net.jxta.document.Advertisement;
import net.jxta.document.Attributable;
import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.TextElement;
import net.jxta.id.ID;

public class AdvertisementBridge extends Advertisement{

	private network.api.advertisement.Advertisement adv;
	
	public AdvertisementBridge() { }
	
	public AdvertisementBridge(network.api.advertisement.Advertisement adv) {
		super();
		this.adv = adv;
	}
	
	/**
	 * Create a new AdvertisementBridge instance initialized with a Jxta xml root element.
	 * @param root
	 */
	public AdvertisementBridge(@SuppressWarnings("rawtypes") net.jxta.document.Element root) {
		super();
		@SuppressWarnings("rawtypes")
		TextElement doc = (TextElement) root;
		@SuppressWarnings("rawtypes")
		TextElement className = (TextElement) doc.getChildren("advertisementClass").nextElement();
		try {
			//try to find the class used for this advertisement
			Class<?> adv = Class.forName(className.getValue());
			Constructor<?> cons = adv.getConstructor();
			this.adv = (network.api.advertisement.Advertisement) cons.newInstance();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | 
				InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Element rootElement = new Element("root");
		@SuppressWarnings("rawtypes")
		Enumeration elements = doc.getChildren();
        while(elements.hasMoreElements()) {
        	@SuppressWarnings("rawtypes")
			TextElement elem = (TextElement) elements.nextElement();
        	if(elem.getName().equals("advertisementClass")) {
        		continue;
        	}
        	Element e = new Element(elem.getName()); //convert into a Jdom element.
        	e.addContent(elem.getValue());
        	rootElement.addContent(e);
        }
        this.adv.initialize(new org.jdom2.Document(rootElement));
	}
	
	/**
	 * {@inheritDoc}
	 * @param asMimeType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Document getDocument(MimeMediaType asMimeType) {
		@SuppressWarnings("rawtypes")
		StructuredDocument adv = StructuredDocumentFactory.newStructuredDocument(asMimeType, getAdvType());
		if (adv instanceof Attributable) {
            ((Attributable) adv).addAttribute("xmlns:jxta", "http://jxta.org");
        }
	
		for(Element e : this.adv.getDocument().getRootElement().getChildren()) {
			@SuppressWarnings("rawtypes")
			net.jxta.document.Element e1 = adv.createElement(e.getName(), e.getValue());
			adv.appendChild(e1);
		}
		return adv;
	}

	/**
	 * {@inheritDoc}
	 * @return
	 */
	@Override
	public ID getID() {
		// TODO see if we generate id for advs
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getIndexFields() {
		if(adv == null) {
			throw new RuntimeException("Adv is null");
		}
		return adv.getIndexFields();
	}
	
	@Override
	public String getAdvType() {
		return "jxta:" + this.getClass().getName();
	}

	public network.api.advertisement.Advertisement getAdvertisement() {
		return adv;
	}
	
}
