package me.steffenjacobs.iotplatformintegrator.ui;

import java.awt.Dimension;
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
public class SettingsFrameFactory {

	private final SettingService settingService;

	public SettingsFrameFactory(SettingService settingService) {
		this.settingService = settingService;
	}

	public JFrame createSettingsFrame() {
		JFrame frame = new JFrame("OpenHAB Settings");
		frame.setSize(590, 480);

		JButton btnSave = new JButton("Save");
		JPanel contentPanel = new JPanel();

		for (SettingKey settingKey : SettingKey.values()) {
			contentPanel.add(createSettingField(settingKey, btnSave));
		}
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
		JTextField settingsField = new JTextField(settingService.getSetting(key), 15);
		JButton settingsButton = new JButton("Restore Default");

		settingsField.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				saveButton.setEnabled(true);
				settingsButton.setEnabled(!settingsField.getText().equals(key.getDefaultValue()));
			}
		});

		settingsButton.addActionListener(e -> {
			settingsField.setText(key.getDefaultValue());
			saveButton.doClick();
		});
		settingsButton.setEnabled(!settingsField.getText().equals(key.getDefaultValue()));

		saveButton.addActionListener(e -> {
			settingService.setSetting(key, settingsField.getText());
		});

		return wrapToPanel(key.getTitle() + ":", settingsField, settingsButton);

	}

	private JPanel wrapToPanel(String label, JComponent... components) {
		final JPanel panel = new JPanel();
		final JLabel lab = new JLabel(label);
		panel.add(lab);
		final int height = (int)lab.getMinimumSize().getHeight();
		lab.setMinimumSize(new Dimension(230, height));
		lab.setPreferredSize(new Dimension(230, height));
		lab.setMaximumSize(new Dimension(230, height));
		for (JComponent component : components) {
			panel.add(component);
		}
		panel.setLayout(new FlowLayout());
		return panel;
	}
}
