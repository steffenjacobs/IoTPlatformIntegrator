package me.steffenjacobs.iotplatformintegrator.ui.util;

import javax.swing.DefaultComboBoxModel;

public class SortedComboBoxModel<E extends Comparable<E>> extends DefaultComboBoxModel<E> {
	private static final long serialVersionUID = 2308832475647487351L;

	@Override
	public void addElement(E element) {
		insertElementAt(element, 0);
	}

	@Override
	public void insertElementAt(E element, int index) {
		for (index = 0; index < getSize(); index++) {
			if (getElementAt(index).compareTo(element) > 0)
				break;
		}

		super.insertElementAt(element, index);
		if (index == 0 && element != null) {
			setSelectedItem(element);
		}
	}
}
