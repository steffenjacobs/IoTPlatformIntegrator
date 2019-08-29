
package me.steffenjacobs.extern.babelnetconnector.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "senses",
    "wnOffsets",
    "mainSense",
    "glosses",
    "examples",
    "images",
    "synsetType",
    "categories",
    "translations",
    "domains",
    "lnToCompound",
    "lnToOtherForm",
    "filterLangs",
    "bkeyConcepts"
})
public class Synset {

    @JsonProperty("senses")
    private List<Sense> senses = null;
    @JsonProperty("wnOffsets")
    private List<Object> wnOffsets = null;
    @JsonProperty("mainSense")
    private String mainSense;
    @JsonProperty("glosses")
    private List<Gloss> glosses = null;
    @JsonProperty("examples")
    private List<Object> examples = null;
    @JsonProperty("images")
    private List<Image> images = null;
    @JsonProperty("synsetType")
    private String synsetType;
    @JsonProperty("categories")
    private List<Category> categories = null;
//    @JsonProperty("translations")
//    private Translations translations;
    @JsonProperty("domains")
    private Domains domains;
    @JsonProperty("lnToCompound")
    private LnToCompound lnToCompound;
    @JsonProperty("lnToOtherForm")
    private LnToOtherForm lnToOtherForm;
    @JsonProperty("filterLangs")
    private List<String> filterLangs = null;
    @JsonProperty("bkeyConcepts")
    private Boolean bkeyConcepts;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("senses")
    public List<Sense> getSenses() {
        return senses;
    }

    @JsonProperty("senses")
    public void setSenses(List<Sense> senses) {
        this.senses = senses;
    }

    @JsonProperty("wnOffsets")
    public List<Object> getWnOffsets() {
        return wnOffsets;
    }

    @JsonProperty("wnOffsets")
    public void setWnOffsets(List<Object> wnOffsets) {
        this.wnOffsets = wnOffsets;
    }

    @JsonProperty("mainSense")
    public String getMainSense() {
        return mainSense;
    }

    @JsonProperty("mainSense")
    public void setMainSense(String mainSense) {
        this.mainSense = mainSense;
    }

    @JsonProperty("glosses")
    public List<Gloss> getGlosses() {
        return glosses;
    }

    @JsonProperty("glosses")
    public void setGlosses(List<Gloss> glosses) {
        this.glosses = glosses;
    }

    @JsonProperty("examples")
    public List<Object> getExamples() {
        return examples;
    }

    @JsonProperty("examples")
    public void setExamples(List<Object> examples) {
        this.examples = examples;
    }

    @JsonProperty("images")
    public List<Image> getImages() {
        return images;
    }

    @JsonProperty("images")
    public void setImages(List<Image> images) {
        this.images = images;
    }

    @JsonProperty("synsetType")
    public String getSynsetType() {
        return synsetType;
    }

    @JsonProperty("synsetType")
    public void setSynsetType(String synsetType) {
        this.synsetType = synsetType;
    }

    @JsonProperty("categories")
    public List<Category> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

//    @JsonProperty("translations")
//    public Translations getTranslations() {
//        return translations;
//    }
//
//    @JsonProperty("translations")
//    public void setTranslations(Translations translations) {
//        this.translations = translations;
//    }

    @JsonProperty("domains")
    public Domains getDomains() {
        return domains;
    }

    @JsonProperty("domains")
    public void setDomains(Domains domains) {
        this.domains = domains;
    }

    @JsonProperty("lnToCompound")
    public LnToCompound getLnToCompound() {
        return lnToCompound;
    }

    @JsonProperty("lnToCompound")
    public void setLnToCompound(LnToCompound lnToCompound) {
        this.lnToCompound = lnToCompound;
    }

    @JsonProperty("lnToOtherForm")
    public LnToOtherForm getLnToOtherForm() {
        return lnToOtherForm;
    }

    @JsonProperty("lnToOtherForm")
    public void setLnToOtherForm(LnToOtherForm lnToOtherForm) {
        this.lnToOtherForm = lnToOtherForm;
    }

    @JsonProperty("filterLangs")
    public List<String> getFilterLangs() {
        return filterLangs;
    }

    @JsonProperty("filterLangs")
    public void setFilterLangs(List<String> filterLangs) {
        this.filterLangs = filterLangs;
    }

    @JsonProperty("bkeyConcepts")
    public Boolean getBkeyConcepts() {
        return bkeyConcepts;
    }

    @JsonProperty("bkeyConcepts")
    public void setBkeyConcepts(Boolean bkeyConcepts) {
        this.bkeyConcepts = bkeyConcepts;
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
    	Set<String> test = new HashSet<String>();
    	
    	if (mainSense != null)
    		test.add(mainSense);
    	
    	
    	int limit = senses.size() > 3 ? 3 : senses.size();
    	
    	for (int i = 0; i < limit; i++) {
    		if (!test.contains(senses.get(i).getProperties().getFullLemma())) {
    			test.add(senses.get(i).getProperties().getFullLemma());
    		}
    	}
    	
    	return test.stream().collect(Collectors.joining(" * "));  			
    			
    			
    }

}
