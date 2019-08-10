package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.manage.SharedRuleElementDiff;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ClearAllRemoteItemsEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ClearAllRemoteRulesEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteItemAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.RemoteRuleAddedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.StoreRuleToDatabaseEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbRuleDiffStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbSharedItemStorageService;
import me.steffenjacobs.iotplatformintegrator.service.storage.mongo.MongoDbSharedRuleStorageService;

/** @author Steffen Jacobs */
public class RemoteRuleController {

	private final MongoDbRuleDiffStorageService diffStorage;
	private final MongoDbSharedRuleStorageService ruleStorage;
	private final MongoDbSharedItemStorageService itemStorage;

	public RemoteRuleController(MongoDbRuleDiffStorageService diffStorage, MongoDbSharedRuleStorageService ruleStorage, MongoDbSharedItemStorageService itemStorage) {
		this.diffStorage = diffStorage;
		this.ruleStorage = ruleStorage;
		this.itemStorage = itemStorage;

		EventBus.getInstance().addEventHandler(EventType.StoreRuleToDatabase, e -> {
			StoreRuleToDatabaseEvent event = (StoreRuleToDatabaseEvent) e;
			SharedRule rule = new SharedRule(event.getNewRuleName(), event.getSelectedRule());
			uploadRule(rule);
		});

		refreshRules();
	}

	public void getDiffs(SharedRule rule, Consumer<SharedRuleElementDiff> consumer) {
		diffStorage.findForRule(rule, new SimplifiedSubscriber<SharedRuleElementDiff>() {
			@Override
			public void onNext(SharedRuleElementDiff diff) {
				consumer.accept(diff);
			}
		});
	}

	public void getRules(Consumer<SharedRule> consumer) {
		ruleStorage.getRules(new SimplifiedSubscriber<SharedRule>() {
			@Override
			public void onNext(SharedRule t) {
				consumer.accept(t);
			}
		}, App.getDatabaseConnectionObject().getItemDirectory());
	}

	public void getItemsSync(Consumer<SharedItem> consumer) {
		CompletableFuture<Void> complete = new CompletableFuture<>();
		itemStorage.getItems(new SimplifiedSubscriber<SharedItem>() {
			@Override
			public void onNext(SharedItem t) {
				consumer.accept(t);
			}

			@Override
			public void onComplete() {
				complete.complete(null);
			}
			
			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		});
		try {
			complete.get(10, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
	}

	public void uploadRule(SharedRule selectedRule) {
		ruleStorage.insertRule(selectedRule, this::refreshRules);
		aggregateItemsFromRule(selectedRule).forEach(i -> itemStorage.insertItem(i, () -> {
		}));
	}

	private Iterable<SharedItem> aggregateItemsFromRule(SharedRule rule) {
		Set<SharedItem> items = new HashSet<>();
		rule.getActions().forEach(a -> a.getActionTypeContainer().getActionTypeSpecificValues().values().forEach(e -> {
			if (e instanceof SharedItem) {
				items.add((SharedItem) e);
			}
		}));
		rule.getConditions().forEach(c -> c.getConditionTypeContainer().getConditionTypeSpecificValues().values().forEach(e -> {
			if (e instanceof SharedItem) {
				items.add((SharedItem) e);
			}
		}));
		rule.getTriggers().forEach(t -> t.getTriggerTypeContainer().getTriggerTypeSpecificValues().values().forEach(e -> {
			if (e instanceof SharedItem) {
				items.add((SharedItem) e);
			}
		}));
		return items;
	}

	private void refreshRules() {
		EventBus.getInstance().fireEvent(new ClearAllRemoteItemsEvent());
		getItemsSync(i -> EventBus.getInstance().fireEvent(new RemoteItemAddedEvent(i)));

		EventBus.getInstance().fireEvent(new ClearAllRemoteRulesEvent());
		getRules(r -> EventBus.getInstance().fireEvent(new RemoteRuleAddedEvent(r)));
	}
}
