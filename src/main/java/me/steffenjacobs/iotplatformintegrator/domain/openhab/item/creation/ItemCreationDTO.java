package me.steffenjacobs.iotplatformintegrator.domain.openhab.item.creation;

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
@JsonPropertyOrder({ "type", "name", "label", "category", "tags", "groupNames", "groupType", "function" })
public class ItemCreationDTO {

	@JsonProperty("type")
	private String type;
	@JsonProperty("name")
	private String name;
	@JsonProperty("label")
	private String label;
	@JsonProperty("category")
	private String category;
	@JsonProperty("tags")
	private List<String> tags = null;
	@JsonProperty("groupNames")
	private List<String> groupNames = null;
	@JsonProperty("groupType")
	private String groupType;
	@JsonProperty("function")
	private FunctionDTO function;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("label")
	public String getLabel() {
		return label;
	}

	@JsonProperty("label")
	public void setLabel(String label) {
		this.label = label;
	}

	@JsonProperty("category")
	public String getCategory() {
		return category;
	}

	@JsonProperty("category")
	public void setCategory(String category) {
		this.category = category;
	}

	@JsonProperty("tags")
	public List<String> getTags() {
		return tags;
	}

	@JsonProperty("tags")
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@JsonProperty("groupNames")
	public List<String> getGroupNames() {
		return groupNames;
	}

	@JsonProperty("groupNames")
	public void setGroupNames(List<String> groupNames) {
		this.groupNames = groupNames;
	}

	@JsonProperty("groupType")
	public String getGroupType() {
		return groupType;
	}

	@JsonProperty("groupType")
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	@JsonProperty("function")
	public FunctionDTO getFunction() {
		return function;
	}

	@JsonProperty("function")
	public void setFunction(FunctionDTO function) {
		this.function = function;
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