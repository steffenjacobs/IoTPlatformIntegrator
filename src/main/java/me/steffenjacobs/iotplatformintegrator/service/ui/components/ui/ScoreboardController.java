package me.steffenjacobs.iotplatformintegrator.service.ui.components.ui;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.authentication.UserScore;
import me.steffenjacobs.iotplatformintegrator.service.manage.util.SimplifiedSubscriber;

/** @author Steffen Jacobs */
public class ScoreboardController {

	private static final Logger LOG = LoggerFactory.getLogger(ScoreboardController.class);

	public ScoreboardController scoreboardController;

	public void refreshedTable(Consumer<UserScore> scoreConsumer) {

		App.getMongoDbRuleDiffStorageService().getStats(new SimplifiedSubscriber<UserScore>() {
			@Override
			public void onNext(UserScore t) {
				scoreConsumer.accept(t);
			}

			@Override
			public void onError(Throwable t) {
				LOG.error("Could not retrive user score: ", t);
			}
		});
	}

}
