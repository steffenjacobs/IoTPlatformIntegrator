package me.steffenjacobs.iotplatformintegrator.service.manage.util;

import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/** @author Steffen Jacobs */
public interface SimplifiedSubscriber extends Subscriber<Document> {

	@Override
	default void onSubscribe(Subscription s) {
		s.request(1);
	}

	@Override
	default void onNext(Document t) {
	}

}
