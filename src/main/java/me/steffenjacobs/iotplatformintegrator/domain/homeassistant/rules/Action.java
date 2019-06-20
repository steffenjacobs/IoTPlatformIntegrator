
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
    "service",
    "condition",
    "entity_id",
    "state",
    "delay",
    "event",
    "event_data"
})
public class Action {

    @JsonProperty("service")
    private String service;
    @JsonProperty("condition")
    private String condition;
    @JsonProperty("entity_id")
    private String entityId;
    @JsonProperty("state")
    private String state;
    @JsonProperty("delay")
    private String delay;
    @JsonProperty("event")
    private String event;
    @JsonProperty("event_data")
    private EventData eventData;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
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

    @JsonProperty("delay")
    public String getDelay() {
        return delay;
    }

    @JsonProperty("delay")
    public void setDelay(String delay) {
        this.delay = delay;
    }

    @JsonProperty("event")
    public String getEvent() {
        return event;
    }

    @JsonProperty("event")
    public void setEvent(String event) {
        this.event = event;
    }

    @JsonProperty("event_data")
    public EventData getEventData() {
        return eventData;
    }

    @JsonProperty("event_data")
    public void setEventData(EventData eventData) {
        this.eventData = eventData;
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
