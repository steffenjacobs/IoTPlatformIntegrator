package me.steffenjacobs.iotplatformintegrator.domain.shared.item;

/** @author Steffen Jacobs */
public class SharedItem implements Comparable<SharedItem> {

	private final String name;
	private final String label;
	private final ItemType type;

	public SharedItem(String name, String label, ItemType type) {
		super();
		this.name = name;
		this.label = label;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public ItemType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SharedItem other = (SharedItem) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public int compareTo(SharedItem o) {
		return this.getName().compareTo(o.getName());
	}

}
