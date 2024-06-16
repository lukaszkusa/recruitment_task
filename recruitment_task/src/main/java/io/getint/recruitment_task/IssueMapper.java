package io.getint.recruitment_task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.getint.recruitment_task.model.Issue;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
class IssueMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    List<Issue> mapFromGetResponse(final HttpResponse response) throws IOException {
        final var jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        final var issues = jsonObject.getJSONArray("issues");

        List<JSONObject> fields = IntStream.range(0, issues.length())
                .mapToObj(index -> ((JSONObject) issues.get(index)).getJSONObject("fields"))
                .collect(Collectors.toList());
        return objectMapper.readValue(fields.toString(), new TypeReference<>() {
        });
    }

    List <String> mapIssueKey(final HttpResponse response) throws IOException {
        final var jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        final var issues = jsonObject.getJSONArray("issues");

        return IntStream.range(0, issues.length())
                .mapToObj(index -> ((JSONObject) issues.get(index)).optString("key"))
                .collect(Collectors.toList());
    }

    ObjectNode toNodeIssueBulkUpdate(final List<Issue> issues, String anotherProjectKey) {
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();

        ArrayNode issueUpdates = payload.putArray("issueUpdates");
        for (final var issue : issues) {
            ObjectNode issueUpdates0 = issueUpdates.addObject();
            ObjectNode fields = issueUpdates0.putObject("fields");

            ObjectNode project = fields.putObject("project");
            project.put("key", anotherProjectKey );

            fields.put("summary", issue.getSummary());
            fields.put( "description", issue.getDescription());

            ObjectNode issuetype = fields.putObject("issuetype");
            issuetype.put("name", issue.getIssuetype());

            ObjectNode priority = fields.putObject("priority");
            priority.put("name", issue.getPriority());

        }
        return payload;
    }

    ObjectNode createPostComment(String body) {
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();

        payload.put("body", body );

        return payload;
    }
}



