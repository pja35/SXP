package controller.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import model.entity.ElGamalKey;

public class MapKeyStringDeserializer extends StdDeserializer<Map<ElGamalKey, String>> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected MapKeyStringDeserializer() {
        super(Map.class);
    }

    @Override
    public Map<ElGamalKey, String> deserialize(JsonParser jsonParser,
                                             DeserializationContext deserializationContext) throws IOException {

        Map<ElGamalKey, String> result = new HashMap<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        for (JsonNode element : node) {
            result.put(
                    jsonParser.getCodec().treeToValue(element.get("key"), ElGamalKey.class),
                    jsonParser.getCodec().treeToValue(element.get("value"), String.class)
            );
        }
        return result;
    }

}
