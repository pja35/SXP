package controller.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import model.entity.ElGamalKey;
import model.entity.sigma.Responses;

public class MapResponseKeyDeserializer extends StdDeserializer<Map<Responses, ElGamalKey>> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected MapResponseKeyDeserializer() {
        super(Map.class);
    }

    @Override
    public Map<Responses, ElGamalKey> deserialize(JsonParser jsonParser,
                                             DeserializationContext deserializationContext) throws IOException {
        Map<Responses, ElGamalKey> result = new HashMap<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        for (JsonNode element : node) {
            result.put(
                    jsonParser.getCodec().treeToValue(element.get("key"), Responses.class),
                    jsonParser.getCodec().treeToValue(element.get("value"), ElGamalKey.class)
            );
        }
        return result;
    }

}
