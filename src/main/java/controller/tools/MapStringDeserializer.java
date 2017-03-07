package controller.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class MapStringDeserializer extends StdDeserializer<Map<String, String>> {

	private static final long serialVersionUID = 1L;

	protected MapStringDeserializer() {
        super(Map.class);
    }

    @Override
    public Map<String, String> deserialize(JsonParser jsonParser,
                                             DeserializationContext deserializationContext) throws IOException {
        Map<String, String> result = new HashMap<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        for (JsonNode element : node) {
            result.put(
                    jsonParser.getCodec().treeToValue(element.get("key"), String.class),
                    jsonParser.getCodec().treeToValue(element.get("value"), String.class)
            );
        }
        return result;
    }
}
