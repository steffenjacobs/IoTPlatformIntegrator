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
@JsonPropertyOrder({ "errors", "result" })
public class ValidationMessage {

	@JsonProperty("errors")
	private String errors;
	@JsonProperty("result")
	private String result;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("errors")
	public String getErrors() {
		return errors;
	}

	@JsonProperty("errors")
	public void setErrors(String errors) {
		this.errors = errors;
	}

	@JsonProperty("result")
	public String getResult() {
		return result;
	}

	@JsonProperty("result")
	public void setResult(String result) {
		this.result = result;
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
