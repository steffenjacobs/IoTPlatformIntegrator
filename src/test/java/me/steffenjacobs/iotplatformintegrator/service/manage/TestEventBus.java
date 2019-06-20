package me.steffenjacobs.iotplatformintegrator.service.manage;

import static org.junit.Assert.fail;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
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
		Semaphore sem = new Semaphore(0);
		AtomicInteger i = new AtomicInteger(0);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				i.incrementAndGet();
				sem.release(1);
			}
		};

		Assert.assertNotNull(EventBus.getInstance());
		EventBus.getInstance().addEventHandler(EventType.ServerConnectionChanged, r);

		EventBus.getInstance().fireEvent(EventType.ServerConnectionChanged);
		sem.tryAcquire(1, 2, TimeUnit.SECONDS);
		Assert.assertEquals(1, i.get());
		Assert.assertEquals(0, sem.availablePermits());
	}

	@Test
	public void testAddAndRemove() throws InterruptedException {

		EventBus.getInstance().reset();
		Semaphore sem = new Semaphore(0);
		AtomicInteger i = new AtomicInteger(0);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				i.incrementAndGet();
				sem.release(1);
			}
		};

		Assert.assertNotNull(EventBus.getInstance());
		EventBus.getInstance().addEventHandler(EventType.ServerConnectionChanged, r);
		EventBus.getInstance().removeEventHandler(EventType.ServerConnectionChanged, r);

		EventBus.getInstance().fireEvent(EventType.ServerConnectionChanged);

		Assert.assertFalse(sem.tryAcquire(1, 2, TimeUnit.SECONDS));
		Assert.assertEquals(0, i.get());
		Assert.assertEquals(0, sem.availablePermits());
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
		boolean called = false;

		@Override
		public void run() {
			called = true;
		}

		public boolean isCalled() {
			return called;
		}

	}

}
