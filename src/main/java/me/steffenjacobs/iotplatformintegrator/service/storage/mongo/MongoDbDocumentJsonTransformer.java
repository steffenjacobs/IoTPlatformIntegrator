package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import org.bson.Document;
import org.json.JSONObject;

/** @author Steffen Jacobs */
public class MongoDbDocumentJsonTransformer {

	public Document toDocument(JSONObject json) {
		return Document.parse(json.toString());
	}

	public String toJSON(Document doc) {
		return doc.toJson();
	}
}
