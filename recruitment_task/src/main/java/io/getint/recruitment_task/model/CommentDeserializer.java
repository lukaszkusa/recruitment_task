package io.getint.recruitment_task.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommentDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        List<String> bodies = new ArrayList<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        JsonNode comments = node.get("comments");
        for(final var comment: comments){
            bodies.add(comment.get("body").asText());
        }
        return bodies;
    }
}
