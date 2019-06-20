package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.TestEvent;

/** @author Steffen Jacobs */
public class TestEventBus {

	@Test
	public void testSimple() throws InterruptedException {

		EventBus.getInstance().reset();
		CallAwareConsumer<TestEvent> r = new CallAwareConsumer<>();

		Assert.assertNotNull(EventBus.getInstance());
		EventBus.getInstance().addEventHandler(EventType.Test, r);

		EventBus.getInstance().fireEvent(new TestEvent());
		Assert.assertTrue(r.isCalled());
		Assert.assertEquals(1, r.getCallCount());
	}

	@Test
	public void testAddAndRemove() throws InterruptedException {

		EventBus.getInstance().reset();
		CallAwareConsumer<TestEvent> r = new CallAwareConsumer<>();

		Assert.assertNotNull(EventBus.getInstance());
		EventBus.getInstance().addEventHandler(EventType.Test, r);
		EventBus.getInstance().removeEventHandler(EventType.Test, r);

		EventBus.getInstance().fireEvent(new TestEvent());

		Assert.assertFalse(r.isCalled());
	}

	@Test
	public void concurrencyTest() throws InterruptedException {
		final int threadCount = 2000;

		EventBus.getInstance().reset();

		CopyOnWriteArrayList<CallAwareConsumer<TestEvent>> runnables = new CopyOnWriteArrayList<CallAwareConsumer<TestEvent>>();

		Assert.assertNotNull("EventBus cannot be null.", EventBus.getInstance());
		ExecutorService es = Executors.newFixedThreadPool(threadCount);
		for (int j = 0; j < threadCount; j++) {
			es.submit(() -> {
				CallAwareConsumer<TestEvent> r = new CallAwareConsumer<>();
				runnables.add(r);
				EventBus.getInstance().addEventHandler(EventType.Test, r);
				EventBus.getInstance().fireEvent(new TestEvent());
				EventBus.getInstance().removeEventHandler(EventType.Test, r);
			});
		}
		es.shutdown();

		Assert.assertTrue("Not all threads terminated within 10 seconds.", es.awaitTermination(10, TimeUnit.SECONDS));

		EventBus.getInstance().fireEvent(new TestEvent());

		Assert.assertEquals("Not all runnables were created.", threadCount, runnables.size());
		runnables.forEach(r -> Assert.assertTrue("Runnable was not run!", r.isCalled()));
	}

	class CallAwareConsumer<T> implements Consumer<T> {
		private final AtomicInteger callCount = new AtomicInteger(0);

		@Override
		public void accept(T t) {
			callCount.incrementAndGet();
		}

		public boolean isCalled() {
			return callCount.get() > 0;
		}

		public int getCallCount() {
			return callCount.get();
		}

	}

}
