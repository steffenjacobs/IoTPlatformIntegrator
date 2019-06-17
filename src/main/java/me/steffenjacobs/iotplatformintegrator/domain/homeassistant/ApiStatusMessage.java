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
@JsonPropertyOrder({ "base_url", "location_name", "requires_api_password", "version" })
public class ApiStatusMessage {

	@JsonProperty("base_url")
	private String baseUrl;
	@JsonProperty("location_name")
	private String locationName;
	@JsonProperty("requires_api_password")
	private Boolean requiresApiPassword;
	@JsonProperty("version")
	private String version;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("base_url")
	public String getBaseUrl() {
		return baseUrl;
	}

	@JsonProperty("base_url")
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@JsonProperty("location_name")
	public String getLocationName() {
		return locationName;
	}

	@JsonProperty("location_name")
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	@JsonProperty("requires_api_password")
	public Boolean getRequiresApiPassword() {
		return requiresApiPassword;
	}

	@JsonProperty("requires_api_password")
	public void setRequiresApiPassword(Boolean requiresApiPassword) {
		this.requiresApiPassword = requiresApiPassword;
	}

	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	@JsonProperty("version")
	public void setVersion(String version) {
		this.version = version;
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
