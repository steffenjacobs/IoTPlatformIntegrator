package me.steffenjacobs.iotplatformintegrator.ui.util;

/** @author Steffen Jacobs */
public class Pair<T> {
	private final T left, right;

	private Pair(T left, T right) {
		this.left = left;
		this.right = right;
	}

	public static <T> Pair<T> of(T left, T right) {
		return new Pair<T>(left, right);
	}

	public T getLeft() {
		return left;
	}

	public T getRight() {
		return right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		Pair<?> other = (Pair<?>) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

}
