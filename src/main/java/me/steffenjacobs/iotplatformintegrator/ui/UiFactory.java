package me.steffenjacobs.iotplatformintegrator.ui;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.ui.util.DocumentAdapter;

/** @author Steffen Jacobs */
public class UiFactory {

	private final SettingService settingService;

	public UiFactory() {
		settingService = new SettingService("./settings.config");
	}

	public JFrame createSettingsFrame() {
		JFrame frame = new JFrame("OpenHAB Settings");
		frame.setSize(400, 400);

		JButton btnSave = new JButton("Save");
		JPanel contentPanel = new JPanel();

		contentPanel.add(createSettingField(SettingKey.OPENHAB_URI, btnSave));
		contentPanel.add(btnSave);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		frame.add(contentPanel);

		btnSave.setEnabled(false);
		btnSave.addActionListener(e -> {
			btnSave.setEnabled(false);
		});

		return frame;
	}

	private JPanel createSettingField(final SettingKey key, final JButton saveButton) {
		JLabel settingsLabel = new JLabel(key.getTitle() + ":");
		JTextField settingsField = new JTextField(settingService.getSetting(SettingKey.OPENHAB_URI));
		JButton settingsButton = new JButton("Restore Default");

		settingsField.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				saveButton.setEnabled(true);
				settingsButton.setEnabled(!settingsField.getText().equals(SettingKey.OPENHAB_URI.getDefaultValue()));
			}
		});

		settingsButton.addActionListener(e -> {
			settingsField.setText(SettingKey.OPENHAB_URI.getDefaultValue());
			saveButton.doClick();
		});
		settingsButton.setEnabled(!settingsField.getText().equals(SettingKey.OPENHAB_URI.getDefaultValue()));

		saveButton.addActionListener(e -> {
			settingService.setSetting(SettingKey.OPENHAB_URI, settingsField.getText());
		});

		return wrapToPanel(settingsLabel, settingsField, settingsButton);

	}

	private JPanel wrapToPanel(JLabel label, JComponent... components) {
		JPanel panel = new JPanel();
		panel.add(label);
		for (JComponent component : components) {
			panel.add(component);
		}
		panel.setLayout(new FlowLayout());
		return panel;
	}
}
