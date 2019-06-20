package me.steffenjacobs.iotplatformintegrator.service.homeassistant.transformation.in;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.SharedAction;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.action.ActionType.ActionTypeSpecificKey;
import me.steffenjacobs.iotplatformintegrator.service.shared.ItemDirectory;

/** @author Steffen Jacobs */
public class HomeAssistantActionTransformationAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(HomeAssistantActionTransformationAdapter.class);

	public SharedAction parseAction(Object o, ItemDirectory itemDirectory) {
		if (!(o instanceof Map)) {
			System.out.println(o);
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) o;

		if (map.containsKey("service")) {
			String service = "" + map.get("service");

			String itemName = "" + map.get("entity_id");
			String command = service.substring(service.lastIndexOf(".") + 1, service.length());
			Command cmd = Command.parse(command);

			SharedItem item = itemDirectory.getItemByName(itemName);
			String description = String.format("Execute service %s.", item);
			String label = "Service execution";

			Map<String, Object> properties = new HashMap<>();
			properties.put(ActionTypeSpecificKey.ItemName.getKeyString(), item);
			properties.put(ActionTypeSpecificKey.Command.getKeyString(), cmd);
			return new SharedAction(ActionType.ItemCommand, properties, description, label);
		} else {
			//TODO: implement
			LOG.error("Not implemented yet.");
		}

		return null;
	}

}
