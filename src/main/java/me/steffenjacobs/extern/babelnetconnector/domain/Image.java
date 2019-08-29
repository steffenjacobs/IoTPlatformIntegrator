
package me.steffenjacobs.extern.babelnetconnector.domain;

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
    "name",
    "languages",
    "urlSource",
    "license",
    "thumbUrl",
    "url",
    "badImage"
})
public class Image {

    @JsonProperty("name")
    private String name;
    @JsonProperty("languages")
    private List<String> languages = null;
    @JsonProperty("urlSource")
    private String urlSource;
    @JsonProperty("license")
    private String license;
    @JsonProperty("thumbUrl")
    private String thumbUrl;
    @JsonProperty("url")
    private String url;
    @JsonProperty("badImage")
    private Boolean badImage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("languages")
    public List<String> getLanguages() {
        return languages;
    }

    @JsonProperty("languages")
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    @JsonProperty("urlSource")
    public String getUrlSource() {
        return urlSource;
    }

    @JsonProperty("urlSource")
    public void setUrlSource(String urlSource) {
        this.urlSource = urlSource;
    }

    @JsonProperty("license")
    public String getLicense() {
        return license;
    }

    @JsonProperty("license")
    public void setLicense(String license) {
        this.license = license;
    }

    @JsonProperty("thumbUrl")
    public String getThumbUrl() {
        return thumbUrl;
    }

    @JsonProperty("thumbUrl")
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("badImage")
    public Boolean getBadImage() {
        return badImage;
    }

    @JsonProperty("badImage")
    public void setBadImage(Boolean badImage) {
        this.badImage = badImage;
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
