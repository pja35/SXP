package network.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;

import network.api.Peer;
import network.api.advertisement.Advertisement;
import network.api.annotation.AdvertisementAttribute;
import network.api.annotation.ServiceName;

public abstract class AbstractAdvertisement implements Advertisement{

	protected String sourceURI = null;
	
	
	/**
	 * {@inheritDoc}
	 */
	/*@Override
	public byte[] getHashableData() {
		ArrayList<String> data = new ArrayList<>();
		for(Field field: this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			AdvertisementAttribute a = field.getAnnotation(AdvertisementAttribute.class);
			if(a != null && a.enabled() && a.signable()) {
				try {
					data.add((String) field.get(this));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			field.setAccessible(false);
		}
		
		java.util.Collections.sort(data);
		ArrayList<byte[]> bytesList = new ArrayList<>();
		int total = 0;
		for(String s : data) {
			bytesList.add(s.getBytes());
			total += s.getBytes().length;
		}
		byte[] res = new byte[total];
		
		int i = 0;
		for(byte[] b : bytesList) {
			System.arraycopy(b, 0, res, i, b.length);
			i += b.length;
		}
		return res;
	}*/

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String getName();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String getAdvertisementType();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void publish(Peer peer) {
		ServiceName name = this.getClass().getAnnotation(ServiceName.class);
		if(name == null) {
			throw new RuntimeException("No service name defined for this class");
		}
		peer.getService(name.name()).publishAdvertisement(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(Document doc) {
		Element root = doc.getRootElement();
		for(Element e: root.getChildren()) {
			try {
				Field field = this.getClass().getDeclaredField(e.getName());
				field.setAccessible(true);
				AdvertisementAttribute a = field.getAnnotation(AdvertisementAttribute.class);
				if(a != null && a.enabled()) {
					field.set(this, e.getValue());
				} else {
					throw new NoSuchFieldException();
				}
				field.setAccessible(false);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
				System.err.println("Field " + e.getName() + "not found. Is it annoted ?");
				e1.printStackTrace();
			}
			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Document getDocument() {
		Element root = new Element(this.getName());
		
		Element el = new Element("advertisementClass");
		el.addContent(getClass().getCanonicalName());
		root.addContent(el);
		
		for(Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			AdvertisementAttribute a = field.getAnnotation(AdvertisementAttribute.class);
			if(a != null && a.enabled()) {
				Element e = new Element(field.getName());
				root.addContent(e);
				try {
					e.addContent((String) field.get(this));
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					e1.printStackTrace();
				}
			}
			field.setAccessible(false);
		}
		return new Document(root);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getIndexFields() {
		ArrayList<String> indexes = new ArrayList<>();
		for(Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			AdvertisementAttribute a = field.getAnnotation(AdvertisementAttribute.class);
			if(a != null && a.enabled() && a.indexed()) {
				indexes.add(field.getName());
			}
			field.setAccessible(false);
		}
		return indexes.toArray(new String[1]);
	}

	public String getSourceURI() {
		return sourceURI;
	}
	
	public void setSourceURI(String uri) {
		this.sourceURI = uri;
	}
	
}
