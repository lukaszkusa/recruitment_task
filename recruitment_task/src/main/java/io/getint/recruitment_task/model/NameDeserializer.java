package io.getint.recruitment_task.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class NameDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(
             JsonParser jsonParser,
             DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return node.get("name").asText();
    }
}
