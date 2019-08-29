
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
    "audios",
    "transcriptions"
})
public class Pronunciations {

    @JsonProperty("audios")
    private List<Object> audios = null;
    @JsonProperty("transcriptions")
    private List<Object> transcriptions = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("audios")
    public List<Object> getAudios() {
        return audios;
    }

    @JsonProperty("audios")
    public void setAudios(List<Object> audios) {
        this.audios = audios;
    }

    @JsonProperty("transcriptions")
    public List<Object> getTranscriptions() {
        return transcriptions;
    }

    @JsonProperty("transcriptions")
    public void setTranscriptions(List<Object> transcriptions) {
        this.transcriptions = transcriptions;
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
