
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
    "source",
    "sourceSense",
    "language",
    "gloss",
    "tokens"
})
public class Gloss {

    @JsonProperty("source")
    private String source;
    @JsonProperty("sourceSense")
    private String sourceSense;
    @JsonProperty("language")
    private String language;
    @JsonProperty("gloss")
    private String gloss;
    @JsonProperty("tokens")
    private List<Token> tokens = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("sourceSense")
    public String getSourceSense() {
        return sourceSense;
    }

    @JsonProperty("sourceSense")
    public void setSourceSense(String sourceSense) {
        this.sourceSense = sourceSense;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("gloss")
    public String getGloss() {
        return gloss;
    }

    @JsonProperty("gloss")
    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    @JsonProperty("tokens")
    public List<Token> getTokens() {
        return tokens;
    }

    @JsonProperty("tokens")
    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
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
