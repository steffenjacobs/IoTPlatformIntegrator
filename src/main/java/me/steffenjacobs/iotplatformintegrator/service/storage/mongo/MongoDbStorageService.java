package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.mongodb.reactivestreams.client.Success;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;

/** @author Steffen Jacobs */
public class MongoDbStorageService {

	private static final Logger LOG = LoggerFactory.getLogger(MongoDbStorageService.class);
	private static final String COLLECTION_NAME_DIFF_STORE = "diffStore";
	private static final String COLLECTION_NAME_RULE_STORE = "ruleStore";
	private static final String DATABASE_NAME = "IotPlatformIntegrator";

	private MongoCollection<Document> ruleCollection;
	private MongoCollection<Document> diffCollection;
	private MongoDatabase database;
	private MongoClient mongoClient;

	private CompletableFuture<Boolean> initialized = new CompletableFuture<>();

	public void checkAndValidateConnection() {
		if (mongoClient == null) {
			mongoClient = MongoClients.create("mongodb://localhost");
		}
		if (database == null) {
			database = mongoClient.getDatabase("IotPlatformIntegrator");
		}

		if (diffCollection == null) {
			diffCollection = database.getCollection(COLLECTION_NAME_DIFF_STORE);
			if (diffCollection == null) {
				database.createCollection(COLLECTION_NAME_DIFF_STORE).subscribe(new CollectionCreationSubscriber(COLLECTION_NAME_DIFF_STORE));
			}
		}
		if (ruleCollection == null) {
			ruleCollection = database.getCollection(COLLECTION_NAME_RULE_STORE);
			if (diffCollection == null) {
				database.createCollection(COLLECTION_NAME_RULE_STORE).subscribe(new CollectionCreationSubscriber(COLLECTION_NAME_RULE_STORE));
			}
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

	private MongoCollection<Document> getDiffCollection() {
		if (diffCollection == null) {
			diffCollection = getDatabase().getCollection(COLLECTION_NAME_DIFF_STORE);
		}
		return diffCollection;
	}

	private MongoCollection<Document> getRuleCollection() {
		if (ruleCollection == null) {
			ruleCollection = getDatabase().getCollection(COLLECTION_NAME_RULE_STORE);
		}
		return ruleCollection;
	}

	public void containsRule(String ruleName, SimplifiedSubscriber<Document> callback) {
		getRuleCollection().find(Filters.eq("name", ruleName)).first().subscribe(callback);
	}

	public void insertRule(Document document) {
		insert(getRuleCollection(), document);
	}

	public void insertDiff(Document document) {
		insert(getDiffCollection(), document);
	}

	private void insert(MongoCollection<Document> collection, Document document) {
		Publisher<Success> publisher = collection.insertOne(document);
		publisher.subscribe(new Subscriber<Success>() {
			@Override
			public void onSubscribe(final Subscription s) {
				s.request(1);
			}

			@Override
			public void onNext(final Success success) {
				LOG.info("Inserted document into diffCollection: {}", document.toJson());
			}

			@Override
			public void onError(final Throwable t) {
				LOG.error("Could not insert document into diffCollection: {} ", t.getMessage());
			}

			@Override
			public void onComplete() {
				LOG.info("Inserted document into diffCollection: {} complete", document.toJson());
			}
		});
	}

	public <T> void findDiffsForRule(SharedRule rule, Subscriber<T> callback, Function<Document, T> transformation) {

		Bson filter = Filters.eq("rule", rule.getName());
		getDiffCollection().countDocuments(filter).subscribe(new SimplifiedSubscriber<Long>() {

			@Override
			public void onNext(Long count) {
				getDiffCollection().find(filter).subscribe(new Subscriber<Document>() {

					@Override
					public void onSubscribe(Subscription s) {
						s.request(count);
					}

					@Override
					public void onNext(Document t) {
						callback.onNext(transformation.apply(t));
					}

					@Override
					public void onError(Throwable t) {
						callback.onError(t);
					}

					@Override
					public void onComplete() {
						callback.onComplete();
					}
				});
			}

			@Override
			public void onError(Throwable t) {
				LOG.error("MongoDB Error: {} ", t.getMessage());
			}
		});
	}

	public synchronized <T> void getAllRules(Subscriber<T> callback, Function<Document, T> transformation) {
		getRuleCollection().countDocuments().subscribe(new SimplifiedSubscriber<Long>() {

			@Override
			public void onNext(Long count) {
				getRuleCollection().find().subscribe(new Subscriber<Document>() {

					@Override
					public void onSubscribe(Subscription s) {
						s.request(count);
					}

					@Override
					public void onNext(Document t) {
						callback.onNext(transformation.apply(t));
					}

					@Override
					public void onError(Throwable t) {
						callback.onError(t);
					}

					@Override
					public void onComplete() {
						callback.onComplete();
					}
				});
			}

			@Override
			public void onError(Throwable t) {
				LOG.error("MongoDB Error: {} ", t.getMessage());
			}
		});
	}

	private class CollectionCreationSubscriber implements Subscriber<Success> {

		private final String collectionName;

		private CollectionCreationSubscriber(String collectionName) {
			this.collectionName = collectionName;
		}

		@Override
		public void onSubscribe(Subscription s) {
			s.request(1);
		}

		@Override
		public void onNext(Success t) {
		}

		@Override
		public void onError(Throwable t) {
			LOG.error("Unable to create diffCollection '{}' in mongodb: {}", collectionName, t.getMessage());
			initialized.complete(false);
		}

		@Override
		public void onComplete() {
			LOG.info("Created diffCollection '{}' successfully.", collectionName);
			initialized.complete(true);
		}
	}
}
