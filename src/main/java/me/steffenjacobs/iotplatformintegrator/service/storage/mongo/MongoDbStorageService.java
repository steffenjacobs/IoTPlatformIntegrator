package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.mongodb.reactivestreams.client.Success;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection.PlatformType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

/** @author Steffen Jacobs */
public class MongoDbStorageService {

	private static final Logger LOG = LoggerFactory.getLogger(MongoDbStorageService.class);
	private static final String COLLECTION_NAME_DIFF_STORE = "diffStore";
	private static final String COLLECTION_NAME_RULE_STORE = "ruleStore";
	private static final String COLLECTION_NAME_USER_STORE = "userStore";
	private static final String DATABASE_NAME = "IotPlatformIntegrator";

	private final SettingService settingService;

	private MongoCollection<Document> ruleCollection;
	private MongoCollection<Document> diffCollection;
	private MongoCollection<Document> userCollection;
	private MongoDatabase database;
	private MongoClient mongoClient;

	private ServerConnection databaseConnectionObject;

	private CompletableFuture<Boolean> initialized = new CompletableFuture<>();

	public MongoDbStorageService(SettingService settingService) {
		this.settingService = settingService;
	}

	public void checkAndValidateConnection() throws IOException {

		if (database == null) {
			database = getDatabase();
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
		if (userCollection == null) {
			userCollection = database.getCollection(COLLECTION_NAME_USER_STORE);
			if (userCollection == null) {
				database.createCollection(COLLECTION_NAME_USER_STORE).subscribe(new CollectionCreationSubscriber(COLLECTION_NAME_USER_STORE));
			}
		}

		databaseConnectionObject = createDatabaseConnectionObject();
	}

	private MongoClient getClient() {
		if (mongoClient == null) {
			mongoClient = MongoClients.create(settingService.getSetting(SettingKey.DATABASE_URI));
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

	private MongoCollection<Document> getUserCollection() {
		if (userCollection == null) {
			userCollection = getDatabase().getCollection(COLLECTION_NAME_USER_STORE);
		}
		return userCollection;
	}

	public void containsRule(String ruleName, SimplifiedSubscriber<Document> callback) {
		getRuleCollection().find(Filters.eq("name", ruleName)).first().subscribe(callback);
	}

	public void insertRule(Document document, Runnable callWhenDone) {
		insert(getRuleCollection(), document, callWhenDone);
	}

	public void insertDiff(Document document) {
		insert(getDiffCollection(), document);
	}

	public void insertUser(Document document) {
		insert(getUserCollection(), document);
	}

	public void getUser(String id, SimplifiedSubscriber<Document> callback) {
		getUserCollection().find(Filters.eq("_id", id)).first().subscribe(callback);
	}

	private void insert(MongoCollection<Document> collection, Document document) {
		insert(collection, document, () -> {
		});
	}

	private void insert(MongoCollection<Document> collection, Document document, Runnable callWhenDone) {
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
				callWhenDone.run();
			}

			@Override
			public void onComplete() {
				LOG.info("Inserted document into diffCollection: {} complete", document.toJson());
				callWhenDone.run();
			}
		});
	}

	public <T> void getStats(Subscriber<T> callback, Function<Document, T> transformation) {

		getUserCollection().countDocuments().subscribe(new SimplifiedSubscriber<Long>() {
			@Override
			public void onNext(Long count) {
				getDiffCollection().aggregate(Arrays.asList(
						// Aggregates.match(Filters.eq("categories", "Bakery")),
						Aggregates.group("$_id",
								Arrays.asList(Accumulators.sum(SharedRuleElementDiffJsonTransformer.KEY_PROPERTIES_ADDED, 1),
										Accumulators.sum(SharedRuleElementDiffJsonTransformer.KEY_PROPERTIES_REMOVED, 1),
										Accumulators.sum(SharedRuleElementDiffJsonTransformer.KEY_PROPERTIES_UPDATED, 1)))))
						.subscribe(new Subscriber<Document>() {

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
						t.printStackTrace();
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

	public ServerConnection getDatabaseConnection() {
		return databaseConnectionObject;
	}

	private ServerConnection createDatabaseConnectionObject() {
		CompletableFuture<String> futureVersion = new CompletableFuture<>();
		getDatabase().runCommand(new BsonDocument("buildinfo", new BsonString(""))).subscribe(new Subscriber<Document>() {

			@Override
			public void onSubscribe(Subscription s) {
			}

			@Override
			public void onNext(Document t) {
				futureVersion.complete((String) t.get("version"));
			}

			@Override
			public void onError(Throwable t) {
				futureVersion.complete("");
			}

			@Override
			public void onComplete() {
				if (!futureVersion.isDone()) {
					futureVersion.complete("");
				}
			}
		});
		String version;
		try {
			version = futureVersion.get(5, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			version = "";
		}

		URI uri = URI.create(settingService.getSetting(SettingKey.DATABASE_URI));
		String instanceName = version;
		String url = uri.getHost();
		int port = uri.getPort();
		return new ServerConnection(PlatformType.MONGO, version, instanceName, url, port);
	}
}
