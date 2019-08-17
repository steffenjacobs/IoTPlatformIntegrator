package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.service.manage.events.Event;

/** @author Steffen Jacobs */
public class EventBus {
	private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);
	private static final EventBus INSTANCE = new EventBus();

	public static enum EventType {
		Test, //
		SELECTED_SERVER_CONNECTION_CHANGED, SERVER_DISCONNECTED, SERVER_CONNECTED, SELECTED_RULE_CHANGE, SOURCE_CONNECTION_CHANGE, TARGET_CONNECTION_CHANGE, //
		REFRESH_OPENHAB_DATA, //
		SELECTED_SOURCE_RULE_CHANGE, SELECTED_TARGET_RULE_CHANGE, RULE_ELEMENT_COPIED, RULE_ELEMENT_REMOVED, RULE_CHANGE, RULE_ELEMENT_CHANGE, RULE_ELEMENT_CREATED, //
		RULE_DIFF_CHANGE, RULE_DIFFS_REFRESH, //
		REMOTE_RULE_ADDED, CLEAR_ALL_REMOTE_RULES, REMOTE_RULE_CHANGE, REMOTE_ITEM_ADDED, CLEAR_ALL_REMOTE_ITEMS, //
		RULE_DIFF_ADDED, SELECTED_RULE_DIFF_CHANGE, //
		STORE_RULE_TO_DATABASE, EXPORT_RULE_TO_PLATFORM, SELECT_TARGET_RULE;
	}

	private EventBus() {

	}

	private final Map<EventType, Collection<Consumer<Event>>> handlerMap = new HashMap<>();
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public static EventBus getInstance() {
		return INSTANCE;
	}

	@SuppressWarnings("unchecked")
	public void addEventHandler(EventType eventType, Consumer<? extends Event> handler) {
		Collection<Consumer<Event>> handlers = new ArrayList<>();
		handlers.add((Consumer<Event>) handler);
		lock.writeLock().lock();
		if (handlerMap.putIfAbsent(eventType, handlers) != null) {
			handlers.addAll(handlerMap.get(eventType));
			handlerMap.put(eventType, handlers);
		}
		lock.writeLock().unlock();
		LOG.debug("Added event handler for type {}", eventType);
	}

	public boolean removeEventHandler(EventType eventType, Consumer<? extends Event> handler) {
		try {
			lock.writeLock().lock();
			if (handlerMap.containsKey(eventType)) {
				Collection<Consumer<Event>> collection = handlerMap.get(eventType);
				boolean result = collection.remove(handler);
				if (collection.isEmpty()) {
					handlerMap.remove(eventType);
				}
				return result;
			} else {
				return false;
			}
		} finally {
			lock.writeLock().unlock();
			LOG.debug("Removed event handler for type {}", eventType);
		}
	}

	public void fireEvent(Event event) {
		Collection<Consumer<Event>> handlers = new ArrayList<>();
		lock.readLock().lock();
		if (handlerMap.containsKey(event.getEventType())) {
			handlers.addAll(handlerMap.get(event.getEventType()));
		}
		lock.readLock().unlock();
		handlers.forEach(r -> r.accept(event));
		LOG.debug("delivered event {} to {} listeners.", event.getEventType(), handlers.size());
	}

	public void reset() {
		handlerMap.clear();
		if (!lock.hasQueuedThreads()) {
			lock = new ReentrantReadWriteLock();
		} else {
			throw new RuntimeException("Unabile to reset event bus: waiting threads");
		}
	}

}
