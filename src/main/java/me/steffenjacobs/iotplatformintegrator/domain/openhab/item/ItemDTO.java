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
@JsonPropertyOrder({ "link", "state", "stateDescription", "editable", "type", "name", "label", "tags", "groupNames" })
public class ItemDTO {

	@JsonProperty("link")
	private String link;
	@JsonProperty("state")
	private String state;
	@JsonProperty("stateDescription")
	private StateDescriptionDTO stateDescription;
	@JsonProperty("editable")
	private Boolean editable;
	@JsonProperty("type")
	private String type;
	@JsonProperty("name")
	private String name;
	@JsonProperty("label")
	private String label;
	@JsonProperty("tags")
	private List<Object> tags = null;
	@JsonProperty("groupNames")
	private List<Object> groupNames = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("link")
	public String getLink() {
		return link;
	}

	@JsonProperty("link")
	public void setLink(String link) {
		this.link = link;
	}

	@JsonProperty("state")
	public String getState() {
		return state;
	}

	@JsonProperty("state")
	public void setState(String state) {
		this.state = state;
	}

	@JsonProperty("stateDescription")
	public StateDescriptionDTO getStateDescription() {
		return stateDescription;
	}

	@JsonProperty("stateDescription")
	public void setStateDescription(StateDescriptionDTO stateDescription) {
		this.stateDescription = stateDescription;
	}

	@JsonProperty("editable")
	public Boolean getEditable() {
		return editable;
	}

	@JsonProperty("editable")
	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

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

	@JsonProperty("tags")
	public List<Object> getTags() {
		return tags;
	}

	@JsonProperty("tags")
	public void setTags(List<Object> tags) {
		this.tags = tags;
	}

	@JsonProperty("groupNames")
	public List<Object> getGroupNames() {
		return groupNames;
	}

	@JsonProperty("groupNames")
	public void setGroupNames(List<Object> groupNames) {
		this.groupNames = groupNames;
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
		builder.append("ItemDTO [link=");
		builder.append(link);
		builder.append(", state=");
		builder.append(state);
		builder.append(", stateDescription=");
		builder.append(stateDescription);
		builder.append(", editable=");
		builder.append(editable);
		builder.append(", type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append(", label=");
		builder.append(label);
		builder.append(", tags=");
		builder.append(tags);
		builder.append(", groupNames=");
		builder.append(groupNames);
		builder.append(", additionalProperties=");
		builder.append(additionalProperties);
		builder.append("]");
		return builder.toString();
	}

}