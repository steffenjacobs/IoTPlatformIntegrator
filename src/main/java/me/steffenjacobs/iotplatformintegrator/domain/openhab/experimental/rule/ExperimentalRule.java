
package me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "triggers",
    "conditions",
    "actions",
    "configuration",
    "configDescriptions",
    "templateUID",
    "uid",
    "name",
    "tags",
    "visibility",
    "description",
    "status"
})
public class ExperimentalRule {

    @JsonProperty("triggers")
    private List<Trigger> triggers = null;
    @JsonProperty("conditions")
    private List<Condition> conditions = null;
    @JsonProperty("actions")
    private List<Action> actions = null;
    @JsonProperty("configuration")
    private Configuration configuration;
    @JsonProperty("configDescriptions")
    private List<ConfigDescription> configDescriptions = null;
    @JsonProperty("templateUID")
    private String templateUID;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("tags")
    private List<String> tags = null;
    @JsonProperty("visibility")
    private String visibility;
    @JsonProperty("description")
    private String description;
    @JsonProperty("status")
    private Status status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("triggers")
    public List<Trigger> getTriggers() {
        return triggers;
    }

    @JsonProperty("triggers")
    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    @JsonProperty("conditions")
    public List<Condition> getConditions() {
        return conditions;
    }

    @JsonProperty("conditions")
    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @JsonProperty("actions")
    public List<Action> getActions() {
        return actions;
    }

    @JsonProperty("actions")
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @JsonProperty("configuration")
    public Configuration getConfiguration() {
        return configuration;
    }

    @JsonProperty("configuration")
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @JsonProperty("configDescriptions")
    public List<ConfigDescription> getConfigDescriptions() {
        return configDescriptions;
    }

    @JsonProperty("configDescriptions")
    public void setConfigDescriptions(List<ConfigDescription> configDescriptions) {
        this.configDescriptions = configDescriptions;
    }

    @JsonProperty("templateUID")
    public String getTemplateUID() {
        return templateUID;
    }

    @JsonProperty("templateUID")
    public void setTemplateUID(String templateUID) {
        this.templateUID = templateUID;
    }

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("visibility")
    public String getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
