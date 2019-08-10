package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import java.io.IOException;

import org.reactivestreams.Subscriber;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedItemJsonTransformer;

/** @author Steffen Jacobs */
public class MongoDbSharedItemStorageService {

	private final SharedItemJsonTransformer jsonTransformer = new SharedItemJsonTransformer();
	private final MongoDbDocumentJsonTransformer documentTransformer = new MongoDbDocumentJsonTransformer();
	private final MongoDbStorageService storageService;

	public MongoDbSharedItemStorageService(MongoDbStorageService storageService) throws IOException {
		this.storageService = storageService;
		storageService.checkAndValidateConnection();
		// TODO: add correct event handler
	}

	public void getItems(Subscriber<SharedItem> subscriber) {
		storageService.getAllItems(subscriber, d -> jsonTransformer.fromJson(documentTransformer.toJSON(d)));
	}

	public void insertItem(SharedItem item, Runnable callWhenDone) {
		storageService.insertItem(documentTransformer.toDocument(jsonTransformer.toJson(item)), callWhenDone);
	}
}
