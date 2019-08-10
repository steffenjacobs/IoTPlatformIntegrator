package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.authentication.UserScore;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;

/** @author Steffen Jacobs */
public class ScoreboardController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScoreboardController.class);

	public ScoreboardController scoreboardController;

	public UserScore[] refreshedTable() {

		final CompletableFuture<UserScore[]> future = new CompletableFuture<>();
		App.getMongoDbRuleDiffStorageService().getStats(new SimplifiedSubscriber<UserScore>() {

			final List<UserScore> scores = new ArrayList<>();

			@Override
			public void onNext(UserScore t) {
				scores.add(t);
			}

			@Override
			public void onComplete() {
				future.complete(scores.toArray(new UserScore[scores.size()]));
			}
			
			@Override
			public void onError(Throwable t) {
				LOG.error("Could not retrive user score: ", t);
				future.complete(new UserScore[0]);
			}
		});

		try {
			return future.get(10, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			LOG.warn("Database is empty or not available.");
			return new UserScore[0];
		}
	}

}
