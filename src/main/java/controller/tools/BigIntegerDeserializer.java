package controller.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerDeserializer extends JsonDeserializer<BigInteger> {

    @Override
    public BigInteger deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return new BigInteger(p.getValueAsString(), 16);
    }

}
