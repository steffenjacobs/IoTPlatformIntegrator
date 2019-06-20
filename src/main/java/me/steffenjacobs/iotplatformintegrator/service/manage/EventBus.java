package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Steffen Jacobs */
public class EventBus {
	private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);
	private static final EventBus INSTANCE = new EventBus();

	public static enum EventType {
		ServerConnectionChanged;
	}

	private EventBus() {

	}

	private final Map<EventType, Collection<Runnable>> handlerMap = new HashMap<>();
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public static EventBus getInstance() {
		return INSTANCE;
	}

	public void addEventHandler(EventType eventType, Runnable handler) {
		Collection<Runnable> handlers = new ArrayList<Runnable>();
		handlers.add(handler);
		lock.writeLock().lock();
		if (handlerMap.putIfAbsent(eventType, handlers) != null) {
			handlers.addAll(handlerMap.get(eventType));
			handlerMap.put(eventType, handlers);
		}
		lock.writeLock().unlock();
		LOG.debug("Added event handler for type {}", eventType);
	}

	public boolean removeEventHandler(EventType eventType, Runnable handler) {
		try {
			lock.writeLock().lock();
			if (handlerMap.containsKey(eventType)) {
				Collection<Runnable> collection = handlerMap.get(eventType);
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

	public void fireEvent(EventType eventType) {
		Collection<Runnable> runnables = new ArrayList<>();
		lock.readLock().lock();
		if (handlerMap.containsKey(eventType)) {
			runnables.addAll(handlerMap.get(eventType));
		}
		lock.readLock().unlock();
		runnables.forEach(r -> r.run());
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
