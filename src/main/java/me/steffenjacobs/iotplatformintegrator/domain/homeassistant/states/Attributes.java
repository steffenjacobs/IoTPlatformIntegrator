
package me.steffenjacobs.iotplatformintegrator.domain.homeassistant.states;

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
    "azimuth",
    "elevation",
    "friendly_name",
    "next_dawn",
    "next_dusk",
    "next_midnight",
    "next_noon",
    "next_rising",
    "next_setting",
    "hidden",
    "icon",
    "latitude",
    "longitude",
    "radius",
    "entity_id",
    "order",
    "unit_of_measurement",
    "editable",
    "id",
    "user_id",
    "last_triggered",
    "auto",
    "current_a",
    "current_power_w",
    "today_energy_kwh",
    "total_energy_kwh",
    "voltage",
    "message",
    "title"
})
public class Attributes {

    @JsonProperty("azimuth")
    private Double azimuth;
    @JsonProperty("elevation")
    private Double elevation;
    @JsonProperty("friendly_name")
    private String friendlyName;
    @JsonProperty("next_dawn")
    private String nextDawn;
    @JsonProperty("next_dusk")
    private String nextDusk;
    @JsonProperty("next_midnight")
    private String nextMidnight;
    @JsonProperty("next_noon")
    private String nextNoon;
    @JsonProperty("next_rising")
    private String nextRising;
    @JsonProperty("next_setting")
    private String nextSetting;
    @JsonProperty("hidden")
    private Boolean hidden;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("radius")
    private Integer radius;
    @JsonProperty("entity_id")
    private List<String> entityId = null;
    @JsonProperty("order")
    private Integer order;
    @JsonProperty("unit_of_measurement")
    private String unitOfMeasurement;
    @JsonProperty("editable")
    private Boolean editable;
    @JsonProperty("id")
    private String id;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("last_triggered")
    private String lastTriggered;
    @JsonProperty("auto")
    private Boolean auto;
    @JsonProperty("current_a")
    private String currentA;
    @JsonProperty("current_power_w")
    private String currentPowerW;
    @JsonProperty("today_energy_kwh")
    private String todayEnergyKwh;
    @JsonProperty("total_energy_kwh")
    private String totalEnergyKwh;
    @JsonProperty("voltage")
    private String voltage;
    @JsonProperty("message")
    private String message;
    @JsonProperty("title")
    private String title;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("azimuth")
    public Double getAzimuth() {
        return azimuth;
    }

    @JsonProperty("azimuth")
    public void setAzimuth(Double azimuth) {
        this.azimuth = azimuth;
    }

    @JsonProperty("elevation")
    public Double getElevation() {
        return elevation;
    }

    @JsonProperty("elevation")
    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    @JsonProperty("friendly_name")
    public String getFriendlyName() {
        return friendlyName;
    }

    @JsonProperty("friendly_name")
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @JsonProperty("next_dawn")
    public String getNextDawn() {
        return nextDawn;
    }

    @JsonProperty("next_dawn")
    public void setNextDawn(String nextDawn) {
        this.nextDawn = nextDawn;
    }

    @JsonProperty("next_dusk")
    public String getNextDusk() {
        return nextDusk;
    }

    @JsonProperty("next_dusk")
    public void setNextDusk(String nextDusk) {
        this.nextDusk = nextDusk;
    }

    @JsonProperty("next_midnight")
    public String getNextMidnight() {
        return nextMidnight;
    }

    @JsonProperty("next_midnight")
    public void setNextMidnight(String nextMidnight) {
        this.nextMidnight = nextMidnight;
    }

    @JsonProperty("next_noon")
    public String getNextNoon() {
        return nextNoon;
    }

    @JsonProperty("next_noon")
    public void setNextNoon(String nextNoon) {
        this.nextNoon = nextNoon;
    }

    @JsonProperty("next_rising")
    public String getNextRising() {
        return nextRising;
    }

    @JsonProperty("next_rising")
    public void setNextRising(String nextRising) {
        this.nextRising = nextRising;
    }

    @JsonProperty("next_setting")
    public String getNextSetting() {
        return nextSetting;
    }

    @JsonProperty("next_setting")
    public void setNextSetting(String nextSetting) {
        this.nextSetting = nextSetting;
    }

    @JsonProperty("hidden")
    public Boolean getHidden() {
        return hidden;
    }

    @JsonProperty("hidden")
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @JsonProperty("icon")
    public String getIcon() {
        return icon;
    }

    @JsonProperty("icon")
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @JsonProperty("latitude")
    public Double getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public Double getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("radius")
    public Integer getRadius() {
        return radius;
    }

    @JsonProperty("radius")
    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    @JsonProperty("entity_id")
    public List<String> getEntityId() {
        return entityId;
    }

    @JsonProperty("entity_id")
    public void setEntityId(List<String> entityId) {
        this.entityId = entityId;
    }

    @JsonProperty("order")
    public Integer getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(Integer order) {
        this.order = order;
    }

    @JsonProperty("unit_of_measurement")
    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    @JsonProperty("unit_of_measurement")
    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    @JsonProperty("editable")
    public Boolean getEditable() {
        return editable;
    }

    @JsonProperty("editable")
    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("last_triggered")
    public String getLastTriggered() {
        return lastTriggered;
    }

    @JsonProperty("last_triggered")
    public void setLastTriggered(String lastTriggered) {
        this.lastTriggered = lastTriggered;
    }

    @JsonProperty("auto")
    public Boolean getAuto() {
        return auto;
    }

    @JsonProperty("auto")
    public void setAuto(Boolean auto) {
        this.auto = auto;
    }

    @JsonProperty("current_a")
    public String getCurrentA() {
        return currentA;
    }

    @JsonProperty("current_a")
    public void setCurrentA(String currentA) {
        this.currentA = currentA;
    }

    @JsonProperty("current_power_w")
    public String getCurrentPowerW() {
        return currentPowerW;
    }

    @JsonProperty("current_power_w")
    public void setCurrentPowerW(String currentPowerW) {
        this.currentPowerW = currentPowerW;
    }

    @JsonProperty("today_energy_kwh")
    public String getTodayEnergyKwh() {
        return todayEnergyKwh;
    }

    @JsonProperty("today_energy_kwh")
    public void setTodayEnergyKwh(String todayEnergyKwh) {
        this.todayEnergyKwh = todayEnergyKwh;
    }

    @JsonProperty("total_energy_kwh")
    public String getTotalEnergyKwh() {
        return totalEnergyKwh;
    }

    @JsonProperty("total_energy_kwh")
    public void setTotalEnergyKwh(String totalEnergyKwh) {
        this.totalEnergyKwh = totalEnergyKwh;
    }

    @JsonProperty("voltage")
    public String getVoltage() {
        return voltage;
    }

    @JsonProperty("voltage")
    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
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
