package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.storage;

import java.util.concurrent.CompletableFuture;
import org.bson.Document;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.mongodb.reactivestreams.client.Success;

/** @author Steffen Jacobs */
public class MongoDbStorageService {

	private static final Logger LOG = LoggerFactory.getLogger(MongoDbStorageService.class);
	private static final String COLLECTION_NAME_DIFF_STORE = "diffStore";
	private static final String DATABASE_NAME = "IotPlatformIntegrator";

	private MongoCollection<Document> collection;
	private MongoDatabase database;
	private MongoClient mongoClient;

	private CompletableFuture<Boolean> initialized = new CompletableFuture<>();

	public void connect() {

		mongoClient = MongoClients.create("mongodb://localhost");

		database = mongoClient.getDatabase("IotPlatformIntegrator");

		collection = database.getCollection(COLLECTION_NAME_DIFF_STORE);
		if (collection == null) {
			database.createCollection(COLLECTION_NAME_DIFF_STORE).subscribe(new Subscriber<Success>() {

				@Override
				public void onSubscribe(Subscription s) {
					s.request(1);
				}

				@Override
				public void onNext(Success t) {
				}

				@Override
				public void onError(Throwable t) {
					LOG.error("Unable to create collection '{}' in mongodb: {}", COLLECTION_NAME_DIFF_STORE, t.getMessage());
					initialized.complete(false);
				}

				@Override
				public void onComplete() {
					LOG.info("Created collection '{}' successfully.", COLLECTION_NAME_DIFF_STORE);
					initialized.complete(true);
				}
			});
			;
		}
	}

	private MongoClient getClient() {
		if (mongoClient == null) {
			mongoClient = MongoClients.create("mongodb://localhost");
		}
		return mongoClient;
	}

	private MongoDatabase getDatabase() {
		if (database == null) {
			database = getClient().getDatabase(DATABASE_NAME);
		}
		return database;
	}

	private MongoCollection<Document> getCollection() {
		if (collection == null) {
			collection = getDatabase().getCollection(COLLECTION_NAME_DIFF_STORE);
		}
		return collection;
	}

	public void insert(Document document) {
		// Document doc = new Document("name", "MongoDB").append("type",
		// "database").append("count", 1).append("info", new Document("x",
		// 203).append("y", 102));

		Publisher<Success> publisher = getCollection().insertOne(document);
		publisher.subscribe(new Subscriber<Success>() {
			@Override
			public void onSubscribe(final Subscription s) {
				s.request(1);
			}

			@Override
			public void onNext(final Success success) {
				LOG.info("Inserted document into collection: {}", document.toJson());
			}

			@Override
			public void onError(final Throwable t) {
				LOG.error("Could not insert document into collection: {} ", t.getMessage());
			}

			@Override
			public void onComplete() {
				LOG.info("Inserted document into collection: {} complete", document.toJson());
			}
		});
	}
}
