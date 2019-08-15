package me.steffenjacobs.iotplatformintegrator.ui.util;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** @author Steffen Jacobs */
public class DialogFactory {
	@SuppressWarnings("unchecked")
	public static <T> T showComboboxDialog(Collection<T> options) {

		CompletableFuture<T> fut = new CompletableFuture<>();
		final JFrame frame = new JFrame();

		final JPanel dialogPanel = new JPanel();
		final JComboBox<T> box = new JComboBox<T>(new Vector<T>(options));

		dialogPanel.add(box);

		final JButton button = new JButton("Ok");
		button.addActionListener(l -> {
			fut.complete((T) box.getSelectedItem());
			frame.setVisible(false);
		});
		dialogPanel.add(button);

		frame.setContentPane(dialogPanel);
		frame.setSize(150, 80);
		frame.setVisible(true);
		try {
			return fut.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
}
