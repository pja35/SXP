package controller.tools;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MapSerializer<K,V> extends StdSerializer<Map<K, V>> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected MapSerializer() {
        super(Map.class, true);
    }

    @Override
    public void serialize(Map<K, V> map,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException{

        jsonGenerator.writeStartArray();
        for (Map.Entry<K,V> element: map.entrySet()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("key", element.getKey());
            jsonGenerator.writeObjectField("value", element.getValue());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}