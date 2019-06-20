
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
    "event_data",
    "event_type",
    "platform",
    "event",
    "source",
    "zone",
    "payload",
    "topic",
    "above",
    "below",
    "entity_id",
    "for",
    "offset",
    "at",
    "hours",
    "minutes",
    "seconds",
    "webhook_id",
    "from",
    "to"
})
public class Trigger {

    @JsonProperty("event_data")
    private EventData eventData;
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("platform")
    private String platform;
    @JsonProperty("event")
    private String event;
    @JsonProperty("source")
    private String source;
    @JsonProperty("zone")
    private String zone;
    @JsonProperty("payload")
    private String payload;
    @JsonProperty("topic")
    private String topic;
    @JsonProperty("above")
    private String above;
    @JsonProperty("below")
    private String below;
    @JsonProperty("entity_id")
    private String entityId;
    @JsonProperty("for")
    private String _for;
    @JsonProperty("offset")
    private String offset;
    @JsonProperty("at")
    private String at;
    @JsonProperty("hours")
    private String hours;
    @JsonProperty("minutes")
    private String minutes;
    @JsonProperty("seconds")
    private String seconds;
    @JsonProperty("webhook_id")
    private String webhookId;
    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("event_data")
    public EventData getEventData() {
        return eventData;
    }

    @JsonProperty("event_data")
    public void setEventData(EventData eventData) {
        this.eventData = eventData;
    }

    @JsonProperty("event_type")
    public String getEventType() {
        return eventType;
    }

    @JsonProperty("event_type")
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @JsonProperty("platform")
    public String getPlatform() {
        return platform;
    }

    @JsonProperty("platform")
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @JsonProperty("event")
    public String getEvent() {
        return event;
    }

    @JsonProperty("event")
    public void setEvent(String event) {
        this.event = event;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("zone")
    public String getZone() {
        return zone;
    }

    @JsonProperty("zone")
    public void setZone(String zone) {
        this.zone = zone;
    }

    @JsonProperty("payload")
    public String getPayload() {
        return payload;
    }

    @JsonProperty("payload")
    public void setPayload(String payload) {
        this.payload = payload;
    }

    @JsonProperty("topic")
    public String getTopic() {
        return topic;
    }

    @JsonProperty("topic")
    public void setTopic(String topic) {
        this.topic = topic;
    }

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

    @JsonProperty("entity_id")
    public String getEntityId() {
        return entityId;
    }

    @JsonProperty("entity_id")
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @JsonProperty("for")
    public String getFor() {
        return _for;
    }

    @JsonProperty("for")
    public void setFor(String _for) {
        this._for = _for;
    }

    @JsonProperty("offset")
    public String getOffset() {
        return offset;
    }

    @JsonProperty("offset")
    public void setOffset(String offset) {
        this.offset = offset;
    }

    @JsonProperty("at")
    public String getAt() {
        return at;
    }

    @JsonProperty("at")
    public void setAt(String at) {
        this.at = at;
    }

    @JsonProperty("hours")
    public String getHours() {
        return hours;
    }

    @JsonProperty("hours")
    public void setHours(String hours) {
        this.hours = hours;
    }

    @JsonProperty("minutes")
    public String getMinutes() {
        return minutes;
    }

    @JsonProperty("minutes")
    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    @JsonProperty("seconds")
    public String getSeconds() {
        return seconds;
    }

    @JsonProperty("seconds")
    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    @JsonProperty("webhook_id")
    public String getWebhookId() {
        return webhookId;
    }

    @JsonProperty("webhook_id")
    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(String from) {
        this.from = from;
    }

    @JsonProperty("to")
    public String getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(String to) {
        this.to = to;
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
