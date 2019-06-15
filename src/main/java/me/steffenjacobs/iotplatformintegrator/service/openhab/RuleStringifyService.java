package me.steffenjacobs.iotplatformintegrator.service.openhab;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;

/** @author Steffen Jacobs */
public class RuleStringifyService {

	public String getReadeableStatus(ExperimentalRule rule) {
		if (rule.getStatus() == null) {
			return "-";
		}
		return rule.getStatus().getStatus();
	}

}
