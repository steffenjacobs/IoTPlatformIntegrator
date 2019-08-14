package me.steffenjacobs.iotplatformintegrator.service.storage.mongo;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.mongodb.reactivestreams.client.Success;

import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection.PlatformType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectoryHolder;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer;
import me.steffenjacobs.iotplatformintegrator.service.storage.json.SharedRuleElementDiffJsonTransformer.RuleDiffParts;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

/** @author Steffen Jacobs */
public class MongoDbStorageService {

	private static final Logger LOG = LoggerFactory.getLogger(MongoDbStorageService.class);
	private static final String COLLECTION_NAME_DIFF_STORE = "diffStore";
	private static final String COLLECTION_NAME_RULE_STORE = "ruleStore";
	private static final String COLLECTION_NAME_USER_STORE = "userStore";
	private static final String COLLECTION_NAME_ITEM_STORE = "itemStore";
	private static final String DATABASE_NAME = "IotPlatformIntegrator";

	private final SettingService settingService;

	private MongoCollection<Document> ruleCollection;
	private MongoCollection<Document> diffCollection;
	private MongoCollection<Document> userCollection;
	private MongoCollection<Document> itemCollection;
	private MongoDatabase database;
	private MongoClient mongoClient;

	private ServerConnection databaseConnectionObject;

	private CompletableFuture<Boolean> initialized = new CompletableFuture<>();

	public MongoDbStorageService(SettingService settingService) {
		this.settingService = settingService;
	}

	public synchronized void checkAndValidateConnection() throws IOException {
		if (databaseConnectionObject != null) {
			return;
		}
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
		if (itemCollection == null) {
			itemCollection = database.getCollection(COLLECTION_NAME_ITEM_STORE);
			if (itemCollection == null) {
				database.createCollection(COLLECTION_NAME_ITEM_STORE).subscribe(new CollectionCreationSubscriber(COLLECTION_NAME_ITEM_STORE));
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

	private MongoCollection<Document> getItemCollection() {
		if (itemCollection == null) {
			itemCollection = getDatabase().getCollection(COLLECTION_NAME_ITEM_STORE);
		}
		return itemCollection;
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
				LOG.info("Inserted document into {}: {}", collection.getNamespace(), document.toJson());
			}

			@Override
			public void onError(final Throwable t) {
				LOG.error("Could not insert document into {}: {} ", collection.getNamespace(), t.getMessage());
				callWhenDone.run();
			}

			@Override
			public void onComplete() {
				LOG.info("Inserted document into {}: {} complete", collection.getNamespace(), document.toJson());
				callWhenDone.run();
			}
		});
	}

	private void upsert(MongoCollection<Document> collection, Bson filter, Document document, Runnable callWhenDone) {
		Publisher<UpdateResult> publisher = collection.replaceOne(filter, document, new ReplaceOptions().upsert(true));
		publisher.subscribe(new Subscriber<UpdateResult>() {
			@Override
			public void onSubscribe(final Subscription s) {
				s.request(1);
			}

			@Override
			public void onNext(final UpdateResult success) {
				LOG.info("Upserted document into {}: {}", collection.getNamespace(), document.toJson());
			}

			@Override
			public void onError(final Throwable t) {
				LOG.error("Could not upsert document into {}: {} ", collection.getNamespace(), t.getMessage());
				callWhenDone.run();
			}

			@Override
			public void onComplete() {
				LOG.info("Upserted document into {}: {} complete", collection.getNamespace(), document.toJson());
				callWhenDone.run();
			}
		});
	}

	public <T> void getStats(Subscriber<T> callback, Function<Document, T> transformation) {

		getUserCollection().countDocuments().subscribe(new SimplifiedSubscriber<Long>() {

			@Override
			public void onNext(Long count) {
				if (count > 0) {

					getDiffCollection().aggregate(Arrays.asList(
							// Aggregates.match(Filters.eq("categories", "Bakery")),
							Aggregates.group("$_id",
									Arrays.asList(Accumulators.sum(SharedRuleElementDiffJsonTransformer.KEY_PROPERTIES_ADDED, 1),
											Accumulators.sum(SharedRuleElementDiffJsonTransformer.KEY_PROPERTIES_REMOVED, 1),
											Accumulators.sum(SharedRuleElementDiffJsonTransformer.KEY_PROPERTIES_UPDATED, 1)))))
							.subscribe(new Subscriber<Document>() {
								int cnt = 0;

								@Override
								public void onSubscribe(Subscription s) {
									s.request(count);
								}

								@Override
								public void onNext(Document t) {
									callback.onNext(transformation.apply(t));
									cnt++;
									// bugfix because onComplete is never called
									if (cnt == count) {
										onComplete();
									}
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
				} else {
					callback.onComplete();
				}
			}

			@Override
			public void onError(Throwable t) {
				callback.onError(t);
			}
		});
	}

	public <T> void findDiffsForRule(SharedRule rule, Subscriber<T> callback, Function<Document, T> transformation) {

		Bson filter = Filters.eq("rule", rule.getName());
		getDiffCollection().countDocuments(filter).subscribe(new SimplifiedSubscriber<Long>() {

			@Override
			public void onNext(Long count) {
				if (count > 0) {
					getDiffCollection().find(filter).subscribe(new Subscriber<Document>() {
						int cnt = 0;

						@Override
						public void onSubscribe(Subscription s) {
							s.request(count);
						}

						@Override
						public void onNext(Document t) {
							callback.onNext(transformation.apply(t));
							cnt++;
							// bugfix because onComplete is never called
							if (cnt == count) {
								onComplete();
							}
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
				} else {
					callback.onComplete();
				}
			}

			@Override
			public void onError(Throwable t) {
				LOG.error("MongoDB Error: {} ", t.getMessage());
			}
		});
	}

	public synchronized <T> void getAll(Subscriber<T> callback, Function<Document, T> transformation, MongoCollection<Document> collection) {
		collection.countDocuments().subscribe(new SimplifiedSubscriber<Long>() {

			@Override
			public void onNext(Long count) {
				if (count > 0) {
					collection.find().subscribe(new Subscriber<Document>() {

						int cnt = 0;

						@Override
						public void onSubscribe(Subscription s) {
							s.request(count);
						}

						@Override
						public void onNext(Document t) {
							callback.onNext(transformation.apply(t));
							cnt++;
							// bugfix because onComplete is never called
							if (cnt == count) {
								onComplete();
							}
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
				} else {
					callback.onComplete();
				}
			}

			@Override
			public void onError(Throwable t) {
				LOG.error("MongoDB Error: {} ", t.getMessage());
				callback.onError(t);
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
			LOG.error("Unable to create collection '{}' in mongodb: {}", collectionName, t.getMessage());
			initialized.complete(false);
		}

		@Override
		public void onComplete() {
			LOG.info("Created collection '{}' successfully.", collectionName);
			initialized.complete(true);
		}
	}

	public ServerConnection getDatabaseConnection() {
		return databaseConnectionObject;
	}

	private ServerConnection createDatabaseConnectionObject() {
		/*
		 * CompletableFuture<String> futureVersion = new CompletableFuture<>();
		 * getDatabase().runCommand(new BsonDocument("buildInfo", new
		 * BsonInt32(1))).subscribe(new Subscriber<Document>() {
		 * 
		 * @Override public void onSubscribe(Subscription s) { }
		 * 
		 * @Override public void onNext(Document t) { futureVersion.complete((String)
		 * t.get("version")); }
		 * 
		 * @Override public void onError(Throwable t) { futureVersion.complete(""); }
		 * 
		 * @Override public void onComplete() { if (!futureVersion.isDone()) {
		 * futureVersion.complete(""); } } }); String version; try { version =
		 * futureVersion.get(5, TimeUnit.SECONDS); } catch (InterruptedException |
		 * ExecutionException | TimeoutException e) { version = ""; }
		 */
		String version = "";
		URI uri = URI.create(settingService.getSetting(SettingKey.DATABASE_URI));
		String instanceName = "MongoDB " + version;
		String url = uri.getHost();
		int port = uri.getPort();
		ServerConnection dbConnection = new ServerConnection(PlatformType.MONGO, version, instanceName, url, port);
		ItemDirectoryHolder.getInstance().add(dbConnection);
		return dbConnection;
	}

	public void insertItem(Document document, Runnable callWhenDone) {
		upsert(getItemCollection(), Filters.and(Filters.eq("name", document.get("name")), Filters.eq("type", document.get("type"))), document, callWhenDone);
	}

	public void getAllRules(Subscriber<SharedRule> subscriber, Function<Document, SharedRule> transformation, MongoDbStorageService storageService) {
		getAll(subscriber, transformation, getRuleCollection());
	}

	public void getAllItems(Subscriber<SharedItem> subscriber, Function<Document, SharedItem> transformation) {
		getAll(subscriber, transformation, getItemCollection());
	}

	public void getAllDiffs(Subscriber<RuleDiffParts> subscriber, Function<Document, RuleDiffParts> transformation) {
		getAll(subscriber, transformation, getDiffCollection());
	}
}
