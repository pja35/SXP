package controller.tools;

import java.io.IOException;

import java.util.Map;

import model.entity.ElGamalKey;
import protocol.impl.sigma.Responses;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MapResponseKeySerializer extends StdSerializer<Map<Responses, ElGamalKey>> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected MapResponseKeySerializer() {
        super(Map.class, true);
    }

    @Override
    public void serialize(Map<Responses, ElGamalKey> map,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException{

        jsonGenerator.writeStartArray();
        for (Map.Entry<Responses,ElGamalKey> element: map.entrySet()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("key", element.getKey());
            jsonGenerator.writeObjectField("value", element.getValue());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}