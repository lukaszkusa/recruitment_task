package io.getint.recruitment_task;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.getint.recruitment_task.model.Issue;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.getint.recruitment_task.JiraClientProperties.BASEAPIPATH;
import static io.getint.recruitment_task.JiraClientProperties.getBasicAuthenticationHeader;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.IntStream.range;

class JiraSynchronizer {
    IssueMapper issueMapper = new IssueMapper();
    private List<Issue> get5TaskFromProject() {
        try (final var client = HttpClients.custom().build()) {
            HttpUriRequest request = RequestBuilder.get()
                    .setUri(BASEAPIPATH + "/rest/agile/1.0/board/1/backlog?startAt=0&maxResults=5")
                    .setHeader(HttpHeaders.AUTHORIZATION, getBasicAuthenticationHeader())
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .build();
            HttpResponse response = client.execute(request);
            return issueMapper.mapFromGetResponse(response);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return emptyList();
    }

    private Map<String, Issue> bulkCreateIssue(
            final List<Issue> issues,
            final String otherProjectKey) throws UnsupportedEncodingException {

        if (issues.isEmpty()) return emptyMap();

        ObjectNode nodeIssueUpdate = issueMapper.toNodeIssueBulkUpdate(issues, otherProjectKey);
        HttpEntity httpEntity = new StringEntity(nodeIssueUpdate.toString());
        try (final var client = HttpClients.custom().build()) {
            HttpUriRequest request = RequestBuilder.post()
                    .setUri(BASEAPIPATH + "/rest/api/2/issue/bulk")
                    .setHeader(HttpHeaders.AUTHORIZATION, getBasicAuthenticationHeader())
                    .setHeader(HttpHeaders.ACCEPT, "application/json")
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .setEntity(httpEntity)
                    .build();

            HttpResponse response = client.execute(request);
            List<String> issueKey = issueMapper.mapIssueKey(response);

            return range(0, issueKey.size())
                    .boxed()
                    .collect(Collectors.toMap(issueKey::get, issues::get));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return emptyMap();
    }

    private void synchronizeComments(Map<String, Issue> map) {
        try (final var client = HttpClients.custom().build()) {
            for (final var entry : map.entrySet()) {
                for (final var comment : entry.getValue().getComments()) {
                    HttpEntity httpEntity =
                            new StringEntity(issueMapper.createPostComment(comment).toString());

                    HttpUriRequest request = RequestBuilder.post()
                            .setUri(BASEAPIPATH + "/rest/api/2/issue/" + entry.getKey() + "/comment")
                            .setHeader(HttpHeaders.AUTHORIZATION, getBasicAuthenticationHeader())
                            .setHeader(HttpHeaders.ACCEPT, "application/json")
                            .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .setEntity(httpEntity)
                            .build();
                    HttpResponse response = client.execute(request);
                    System.out.println(response.getStatusLine().getStatusCode());
                }

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Search for 5 tickets in one project, and move them
     * to the other project within same Jira instance.
     * When moving tickets, please move following fields:
     * - summary (title)
     * - description
     * - priority
     * Bonus points for syncing comments.
     */
    public void moveTasksToOtherProject() throws Exception {
        var takenIssues = get5TaskFromProject();
        var takenIssuesByNewKey = bulkCreateIssue(takenIssues, "TWO");
        synchronizeComments(takenIssuesByNewKey);
    }

    public static void main(String[] args) throws Exception {
        JiraSynchronizer jiraSynchronizer = new JiraSynchronizer();
        jiraSynchronizer.moveTasksToOtherProject();
    }
}
