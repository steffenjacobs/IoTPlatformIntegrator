package me.steffenjacobs.iotplatformintegrator.service.manage.util;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/** @author Steffen Jacobs */
public interface SimplifiedSubscriber<T> extends Subscriber<T> {

	@Override
	default void onSubscribe(Subscription s) {
		s.request(1);
	}

	@Override
	default void onNext(T t) {
	}

	@Override
	default void onComplete() {
	}

	@Override
	default void onError(Throwable t) {

	}

}
