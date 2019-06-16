package me.steffenjacobs.iotplatformintegrator.domain.openhab.item;

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
@JsonPropertyOrder({ "pattern", "readOnly", "options" })
public class StateDescriptionDTO {

	@JsonProperty("pattern")
	private String pattern;
	@JsonProperty("readOnly")
	private Boolean readOnly;
	@JsonProperty("options")
	private List<Object> options = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("pattern")
	public String getPattern() {
		return pattern;
	}

	@JsonProperty("pattern")
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@JsonProperty("readOnly")
	public Boolean getReadOnly() {
		return readOnly;
	}

	@JsonProperty("readOnly")
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	@JsonProperty("options")
	public List<Object> getOptions() {
		return options;
	}

	@JsonProperty("options")
	public void setOptions(List<Object> options) {
		this.options = options;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StateDescription [pattern=");
		builder.append(pattern);
		builder.append(", readOnly=");
		builder.append(readOnly);
		builder.append(", options=");
		builder.append(options);
		builder.append(", additionalProperties=");
		builder.append(additionalProperties);
		builder.append("]");
		return builder.toString();
	}

}