package me.steffenjacobs.iotplatformintegrator.service.storage.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.manage.render.ItemPlaceholderFactory;

/** @author Steffen Jacobs */
public class SharedItemJsonTransformer implements ItemPlaceholderFactory {
	private static final Logger LOG = LoggerFactory.getLogger(SharedItemJsonTransformer.class);
	private static final JsonTransformerHelper jsonHelper = new JsonTransformerHelper();

	private static final String KEY_NAME = "name";
	private static final String KEY_LABEL = "label";
	private static final String KEY_TYPE = "type";

	public SharedItem fromJson(String jsonStr) {
		try {
			JSONObject json = new JSONObject(jsonStr);
			String name = json.getString(KEY_NAME);
			String label = json.getString(KEY_LABEL);
			String typeValue = json.getString(KEY_TYPE);
			ItemType type = ItemType.valueOf(typeValue);
			return new SharedItem(name, label, type);
		} catch (JSONException e) {
			LOG.warn("Invalid JSON detected: {}", e.getMessage(), e);
			return getItemOrPlaceholder(null);
		}
	}

	public JSONObject toJson(SharedItem item) {
		JSONObject json = new JSONObject();
		jsonHelper.putIfNotNull(json, KEY_NAME, item.getName());
		jsonHelper.putIfNotNull(json, KEY_LABEL, item.getLabel());
		jsonHelper.putIfNotNull(json, KEY_TYPE, item.getType().name());
		return json;
	}
}
