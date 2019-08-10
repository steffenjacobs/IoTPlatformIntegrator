package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.bson.Document;

import me.steffenjacobs.iotplatformintegrator.domain.authentication.User;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.UserJsonTransformer;

/** @author Steffen Jacobs */
public class MongoDbUserStorageService {

	private final MongoDbDocumentJsonTransformer documentTransformer = new MongoDbDocumentJsonTransformer();
	private final UserJsonTransformer userTransformer = new UserJsonTransformer();
	private final MongoDbStorageService storageService;

	public MongoDbUserStorageService(MongoDbStorageService storageService) throws IOException {
		this.storageService = storageService;
		storageService.checkAndValidateConnection();
	}

	public void storeUser(User user) {
		storageService.insertUser(documentTransformer.toDocument(userTransformer.toJSON(user)));
	}

	public void isUserValid(User user, CompletableFuture<Boolean> callback) {
		if (user.getUserId() == null) {
			callback.complete(false);
			return;
		}
		storageService.getUser(user.getUserId().toString(), new SimplifiedSubscriber<Document>() {
			@Override
			public void onNext(Document t) {
				User storedUser = userTransformer.fromJSON(documentTransformer.toJSON(t));
				callback.complete(isValid(storedUser));
			}

			private boolean isValid(User storedUser) {
				return user.getUserId().equals(storedUser.getUserId()) && user.getPassword().equals(storedUser.getPassword());
			}
		});
	}

}
