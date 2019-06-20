
package me.steffenjacobs.iotplatformintegrator.domain.homeassistant.rules;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "above",
    "below",
    "condition",
    "entity_id",
    "state",
    "after",
    "after_offset",
    "before",
    "before_offset",
    "zone"
})
public class Condition {

    @JsonProperty("above")
    private String above;
    @JsonProperty("below")
    private String below;
    @JsonProperty("condition")
    private String condition;
    @JsonProperty("entity_id")
    private String entityId;
    @JsonProperty("state")
    private String state;
    @JsonProperty("after")
    private String after;
    @JsonProperty("after_offset")
    private String afterOffset;
    @JsonProperty("before")
    private String before;
    @JsonProperty("before_offset")
    private String beforeOffset;
    @JsonProperty("zone")
    private String zone;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("above")
    public String getAbove() {
        return above;
    }

    @JsonProperty("above")
    public void setAbove(String above) {
        this.above = above;
    }

    @JsonProperty("below")
    public String getBelow() {
        return below;
    }

    @JsonProperty("below")
    public void setBelow(String below) {
        this.below = below;
    }

    @JsonProperty("condition")
    public String getCondition() {
        return condition;
    }

    @JsonProperty("condition")
    public void setCondition(String condition) {
        this.condition = condition;
    }

    @JsonProperty("entity_id")
    public String getEntityId() {
        return entityId;
    }

    @JsonProperty("entity_id")
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("after")
    public String getAfter() {
        return after;
    }

    @JsonProperty("after")
    public void setAfter(String after) {
        this.after = after;
    }

    @JsonProperty("after_offset")
    public String getAfterOffset() {
        return afterOffset;
    }

    @JsonProperty("after_offset")
    public void setAfterOffset(String afterOffset) {
        this.afterOffset = afterOffset;
    }

    @JsonProperty("before")
    public String getBefore() {
        return before;
    }

    @JsonProperty("before")
    public void setBefore(String before) {
        this.before = before;
    }

    @JsonProperty("before_offset")
    public String getBeforeOffset() {
        return beforeOffset;
    }

    @JsonProperty("before_offset")
    public void setBeforeOffset(String beforeOffset) {
        this.beforeOffset = beforeOffset;
    }

    @JsonProperty("zone")
    public String getZone() {
        return zone;
    }

    @JsonProperty("zone")
    public void setZone(String zone) {
        this.zone = zone;
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
