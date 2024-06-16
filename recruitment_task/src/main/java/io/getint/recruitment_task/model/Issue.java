package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    String key;

    String summary;

    String description;

    @JsonDeserialize(using = NameDeserializer.class)
    String issuetype;
    @JsonDeserialize(using = NameDeserializer.class)
    String priority;

    @JsonProperty(value = "comment")
    @JsonDeserialize(using = CommentDeserializer.class)
    List<String>comments;
}
