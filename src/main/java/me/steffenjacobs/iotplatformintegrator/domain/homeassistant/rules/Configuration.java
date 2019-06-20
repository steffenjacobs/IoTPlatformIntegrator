
package me.steffenjacobs.iotplatformintegrator.domain.homeassistant.rules;

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
    "alias",
    "trigger",
    "action",
    "id",
    "condition"
})
public class Configuration {

    @JsonProperty("alias")
    private String alias;
    @JsonProperty("trigger")
    private List<Trigger> trigger = null;
    @JsonProperty("action")
    private List<Action> action = null;
    @JsonProperty("id")
    private String id;
    @JsonProperty("condition")
    private List<Condition> condition = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonProperty("trigger")
    public List<Trigger> getTrigger() {
        return trigger;
    }

    @JsonProperty("trigger")
    public void setTrigger(List<Trigger> trigger) {
        this.trigger = trigger;
    }

    @JsonProperty("action")
    public List<Action> getAction() {
        return action;
    }

    @JsonProperty("action")
    public void setAction(List<Action> action) {
        this.action = action;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("condition")
    public List<Condition> getCondition() {
        return condition;
    }

    @JsonProperty("condition")
    public void setCondition(List<Condition> condition) {
        this.condition = condition;
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
