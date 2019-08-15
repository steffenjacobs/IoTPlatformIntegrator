package me.steffenjacobs.iotplatformintegrator.service.openhab.transformation.out;

import me.steffenjacobs.iotplatformintegrator.domain.openhab.experimental.rule.ExperimentalRule;
import me.steffenjacobs.iotplatformintegrator.domain.openhab.item.ItemDTO;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformItemReverseTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformReverseTransformationAdapter;
import me.steffenjacobs.iotplatformintegrator.service.shared.PlatformRuleReverseTransformationAdapter;

/** @author Steffen Jacobs */
public class OpenHabReverseTransformationAdapter implements PlatformReverseTransformationAdapter<ItemDTO, ExperimentalRule> {

	private final PlatformItemReverseTransformationAdapter<ItemDTO> itemTransformer;
	private final PlatformRuleReverseTransformationAdapter<ExperimentalRule> ruleTransformer;

	public OpenHabReverseTransformationAdapter() {
		this.itemTransformer = new OpenHabItemReverseTransformationAdapter();
		this.ruleTransformer = new OpenHabRuleReverseTransformationAdapter();
	}

	@Override
	public PlatformItemReverseTransformationAdapter<ItemDTO> getItemTransformer() {
		return itemTransformer;
	}

	@Override
	public PlatformRuleReverseTransformationAdapter<ExperimentalRule> getRuleTransformer() {
		return ruleTransformer;
	}

}
