package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.in;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.ItemDTO;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformItemTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformRuleTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformTransformationAdapter;

/** @author Steffen Jacobs */
public class OpenHabTransformationAdapter implements PlatformTransformationAdapter<ItemDTO, ExperimentalRule> {

	private final PlatformItemTransformationAdapter<ItemDTO> itemTransformer;
	private final PlatformRuleTransformationAdapter<ExperimentalRule> ruleTransformer;

	public OpenHabTransformationAdapter() {
		this.itemTransformer = new OpenHabItemTransformationAdapter();
		this.ruleTransformer = new OpenHabRuleTransformationAdapter();
	}

	@Override
	public PlatformItemTransformationAdapter<ItemDTO> getItemTransformer() {
		return itemTransformer;
	}

	@Override
	public PlatformRuleTransformationAdapter<ExperimentalRule> getRuleTransformer() {
		return ruleTransformer;
	}

}
