package controller.tools;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTools<Entity> {
	
	private TypeReference<Entity> type;
	
	
	public JsonTools(final TypeReference<Entity> type) {
		this.type = type;
	}

	public String toJson(Entity entity) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "error";
	}
	
	@SuppressWarnings("unchecked")
	public Entity toEntity(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (Entity) mapper.readValue(json, type);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
