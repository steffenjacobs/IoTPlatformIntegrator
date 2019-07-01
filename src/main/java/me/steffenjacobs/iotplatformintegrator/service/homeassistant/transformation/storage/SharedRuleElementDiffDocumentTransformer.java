package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import org.bson.Document;
import org.json.JSONObject;

/** @author Steffen Jacobs */
public class SharedRuleElementDiffDocumentTransformer {

	public Document toDocument(JSONObject json) {
		return Document.parse(json.toString());
	}

	public String toJSON(Document doc) {
		return doc.toJson();
	}
}
