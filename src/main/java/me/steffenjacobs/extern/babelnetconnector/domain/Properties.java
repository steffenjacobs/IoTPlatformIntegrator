
package me.steffenjacobs.extern.babelnetconnector.domain;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fullLemma",
    "simpleLemma",
    "source",
    "senseKey",
    "frequency",
    "language",
    "pos",
    "synsetID",
    "translationInfo",
    "pronunciations",
    "bKeySense",
    "idSense"
})
public class Properties {

    @JsonProperty("fullLemma")
    private String fullLemma;
    @JsonProperty("simpleLemma")
    private String simpleLemma;
    @JsonProperty("source")
    private String source;
    @JsonProperty("senseKey")
    private String senseKey;
    @JsonProperty("frequency")
    private Integer frequency;
    @JsonProperty("language")
    private String language;
    @JsonProperty("pos")
    private String pos;
    @JsonProperty("synsetID")
    private SynsetID synsetID;
    @JsonProperty("translationInfo")
    private String translationInfo;
    @JsonProperty("pronunciations")
    private Pronunciations pronunciations;
    @JsonProperty("bKeySense")
    private Boolean bKeySense;
    @JsonProperty("idSense")
    private Integer idSense;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("fullLemma")
    public String getFullLemma() {
        return fullLemma;
    }

    @JsonProperty("fullLemma")
    public void setFullLemma(String fullLemma) {
        this.fullLemma = fullLemma;
    }

    @JsonProperty("simpleLemma")
    public String getSimpleLemma() {
        return simpleLemma;
    }

    @JsonProperty("simpleLemma")
    public void setSimpleLemma(String simpleLemma) {
        this.simpleLemma = simpleLemma;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("senseKey")
    public String getSenseKey() {
        return senseKey;
    }

    @JsonProperty("senseKey")
    public void setSenseKey(String senseKey) {
        this.senseKey = senseKey;
    }

    @JsonProperty("frequency")
    public Integer getFrequency() {
        return frequency;
    }

    @JsonProperty("frequency")
    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("pos")
    public String getPos() {
        return pos;
    }

    @JsonProperty("pos")
    public void setPos(String pos) {
        this.pos = pos;
    }

    @JsonProperty("synsetID")
    public SynsetID getSynsetID() {
        return synsetID;
    }

    @JsonProperty("synsetID")
    public void setSynsetID(SynsetID synsetID) {
        this.synsetID = synsetID;
    }

    @JsonProperty("translationInfo")
    public String getTranslationInfo() {
        return translationInfo;
    }

    @JsonProperty("translationInfo")
    public void setTranslationInfo(String translationInfo) {
        this.translationInfo = translationInfo;
    }

    @JsonProperty("pronunciations")
    public Pronunciations getPronunciations() {
        return pronunciations;
    }

    @JsonProperty("pronunciations")
    public void setPronunciations(Pronunciations pronunciations) {
        this.pronunciations = pronunciations;
    }

    @JsonProperty("bKeySense")
    public Boolean getBKeySense() {
        return bKeySense;
    }

    @JsonProperty("bKeySense")
    public void setBKeySense(Boolean bKeySense) {
        this.bKeySense = bKeySense;
    }

    @JsonProperty("idSense")
    public Integer getIdSense() {
        return idSense;
    }

    @JsonProperty("idSense")
    public void setIdSense(Integer idSense) {
        this.idSense = idSense;
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
