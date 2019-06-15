
package me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule;

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
    "context",
    "defaultValue",
    "description",
    "label",
    "name",
    "required",
    "type",
    "min",
    "max",
    "stepsize",
    "pattern",
    "readOnly",
    "multiple",
    "multipleLimit",
    "groupName",
    "advanced",
    "verify",
    "limitToOptions",
    "unit",
    "unitLabel",
    "options",
    "filterCriteria"
})
public class ConfigDescription {

    @JsonProperty("context")
    private String context;
    @JsonProperty("defaultValue")
    private String defaultValue;
    @JsonProperty("description")
    private String description;
    @JsonProperty("label")
    private String label;
    @JsonProperty("name")
    private String name;
    @JsonProperty("required")
    private Boolean required;
    @JsonProperty("type")
    private String type;
    @JsonProperty("min")
    private Integer min;
    @JsonProperty("max")
    private Integer max;
    @JsonProperty("stepsize")
    private Integer stepsize;
    @JsonProperty("pattern")
    private String pattern;
    @JsonProperty("readOnly")
    private Boolean readOnly;
    @JsonProperty("multiple")
    private Boolean multiple;
    @JsonProperty("multipleLimit")
    private Integer multipleLimit;
    @JsonProperty("groupName")
    private String groupName;
    @JsonProperty("advanced")
    private Boolean advanced;
    @JsonProperty("verify")
    private Boolean verify;
    @JsonProperty("limitToOptions")
    private Boolean limitToOptions;
    @JsonProperty("unit")
    private String unit;
    @JsonProperty("unitLabel")
    private String unitLabel;
    @JsonProperty("options")
    private List<Option> options = null;
    @JsonProperty("filterCriteria")
    private List<FilterCriterium> filterCriteria = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("context")
    public String getContext() {
        return context;
    }

    @JsonProperty("context")
    public void setContext(String context) {
        this.context = context;
    }

    @JsonProperty("defaultValue")
    public String getDefaultValue() {
        return defaultValue;
    }

    @JsonProperty("defaultValue")
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("required")
    public Boolean getRequired() {
        return required;
    }

    @JsonProperty("required")
    public void setRequired(Boolean required) {
        this.required = required;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("min")
    public Integer getMin() {
        return min;
    }

    @JsonProperty("min")
    public void setMin(Integer min) {
        this.min = min;
    }

    @JsonProperty("max")
    public Integer getMax() {
        return max;
    }

    @JsonProperty("max")
    public void setMax(Integer max) {
        this.max = max;
    }

    @JsonProperty("stepsize")
    public Integer getStepsize() {
        return stepsize;
    }

    @JsonProperty("stepsize")
    public void setStepsize(Integer stepsize) {
        this.stepsize = stepsize;
    }

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

    @JsonProperty("multiple")
    public Boolean getMultiple() {
        return multiple;
    }

    @JsonProperty("multiple")
    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    @JsonProperty("multipleLimit")
    public Integer getMultipleLimit() {
        return multipleLimit;
    }

    @JsonProperty("multipleLimit")
    public void setMultipleLimit(Integer multipleLimit) {
        this.multipleLimit = multipleLimit;
    }

    @JsonProperty("groupName")
    public String getGroupName() {
        return groupName;
    }

    @JsonProperty("groupName")
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @JsonProperty("advanced")
    public Boolean getAdvanced() {
        return advanced;
    }

    @JsonProperty("advanced")
    public void setAdvanced(Boolean advanced) {
        this.advanced = advanced;
    }

    @JsonProperty("verify")
    public Boolean getVerify() {
        return verify;
    }

    @JsonProperty("verify")
    public void setVerify(Boolean verify) {
        this.verify = verify;
    }

    @JsonProperty("limitToOptions")
    public Boolean getLimitToOptions() {
        return limitToOptions;
    }

    @JsonProperty("limitToOptions")
    public void setLimitToOptions(Boolean limitToOptions) {
        this.limitToOptions = limitToOptions;
    }

    @JsonProperty("unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty("unitLabel")
    public String getUnitLabel() {
        return unitLabel;
    }

    @JsonProperty("unitLabel")
    public void setUnitLabel(String unitLabel) {
        this.unitLabel = unitLabel;
    }

    @JsonProperty("options")
    public List<Option> getOptions() {
        return options;
    }

    @JsonProperty("options")
    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @JsonProperty("filterCriteria")
    public List<FilterCriterium> getFilterCriteria() {
        return filterCriteria;
    }

    @JsonProperty("filterCriteria")
    public void setFilterCriteria(List<FilterCriterium> filterCriteria) {
        this.filterCriteria = filterCriteria;
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
