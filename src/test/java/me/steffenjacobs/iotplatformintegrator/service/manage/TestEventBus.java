package me.steffenjacobs.iotplatformintegrator.service.manage;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;

/** @author Steffen Jacobs */
public class TestEventBus {

	@Test
	public void testSimple() throws InterruptedException {

		EventBus.getInstance().reset();
		CallAwareRunnable r = new CallAwareRunnable();

		Assert.assertNotNull(EventBus.getInstance());
		EventBus.getInstance().addEventHandler(EventType.ServerConnectionChanged, r);

		EventBus.getInstance().fireEvent(EventType.ServerConnectionChanged);
		Assert.assertTrue(r.isCalled());
		Assert.assertEquals(1, r.getCallCount());
	}

	@Test
	public void testAddAndRemove() throws InterruptedException {

		EventBus.getInstance().reset();
		CallAwareRunnable r = new CallAwareRunnable();

		Assert.assertNotNull(EventBus.getInstance());
		EventBus.getInstance().addEventHandler(EventType.ServerConnectionChanged, r);
		EventBus.getInstance().removeEventHandler(EventType.ServerConnectionChanged, r);

		EventBus.getInstance().fireEvent(EventType.ServerConnectionChanged);

		Assert.assertFalse(r.isCalled());
	}

	@Test
	public void concurrencyTest() throws InterruptedException {
		final int threadCount = 2000;

		EventBus.getInstance().reset();

		CopyOnWriteArrayList<CallAwareRunnable> runnables = new CopyOnWriteArrayList<CallAwareRunnable>();

		Assert.assertNotNull("EventBus cannot be null.", EventBus.getInstance());
		ExecutorService es = Executors.newFixedThreadPool(threadCount);
		for (int j = 0; j < threadCount; j++) {
			es.submit(() -> {
				CallAwareRunnable r = new CallAwareRunnable();
				runnables.add(r);
				EventBus.getInstance().addEventHandler(EventType.ServerConnectionChanged, r);
				EventBus.getInstance().fireEvent(EventType.ServerConnectionChanged);
				EventBus.getInstance().removeEventHandler(EventType.ServerConnectionChanged, r);
			});
		}
		es.shutdown();

		Assert.assertTrue("Not all threads terminated within 10 seconds.", es.awaitTermination(10, TimeUnit.SECONDS));

		EventBus.getInstance().fireEvent(EventType.ServerConnectionChanged);

		Assert.assertEquals("Not all runnables were created.", threadCount, runnables.size());
		runnables.forEach(r -> Assert.assertTrue("Runnable was not run!", r.isCalled()));
	}

	class CallAwareRunnable implements Runnable {
		private final AtomicInteger callCount = new AtomicInteger(0);

		@Override
		public void run() {
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
