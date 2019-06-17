package me.steffenjacobs.iotplatformintegrator.domain.homeassistant;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "event", "listener_count" })
public class HomeAssistantEvent {

	@JsonProperty("event")
	private String event;
	@JsonProperty("listener_count")
	private Integer listenerCount;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("event")
	public String getEvent() {
		return event;
	}

	@JsonProperty("event")
	public void setEvent(String event) {
		this.event = event;
	}

	@JsonProperty("listener_count")
	public Integer getListenerCount() {
		return listenerCount;
	}

	@JsonProperty("listener_count")
	public void setListenerCount(Integer listenerCount) {
		this.listenerCount = listenerCount;
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
