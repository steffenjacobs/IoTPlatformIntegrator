package me.steffenjacobs.iotplatformintegrator.ui;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;

import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.ui.util.DocumentAdapter;

/** @author Steffen Jacobs */
public class UiFactory {

	private final SettingService settingService;

	public UiFactory(SettingService settingService) {
		this.settingService = settingService;
	}

	public void updateRuleTable(JTable rulesTable, Iterable<SharedRule> rules) {
		DefaultTableModel tableModel = (DefaultTableModel) rulesTable.getModel();

		tableModel.setNumRows(0);
		for (SharedRule rule : rules) {
			String[] arr = new String[3];
			arr[0] = rule.getName();
			arr[1] = rule.getId();
			arr[2] = rule.getVisible();
			tableModel.addRow(arr);
		}
		tableModel.fireTableDataChanged();
	}

	public JTable createRulesTable() {

		// create table model
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.setColumnCount(3);

		// setup columns
		String[] columnNames = { "Name", "UUID", "visible" };
		tableModel.setColumnIdentifiers(columnNames);

		// create JTable
		JTable table = new JTable(tableModel);
		table.setBounds(30, 40, 200, 300);

		return table;
	}

	public JTable createItemsTable() {

		// create table model
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.setColumnCount(3);

		// setup columns
		String[] columnNames = { "Name", "Label", "Type" };
		tableModel.setColumnIdentifiers(columnNames);

		// create JTable
		JTable table = new JTable(tableModel);
		table.setBounds(30, 40, 200, 300);

		return table;
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

		return wrapToPanel(key.getTitle() + ":", settingsField, settingsButton);

	}

	public JPanel wrapToPanel(String label, JComponent... components) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(label));
		for (JComponent component : components) {
			panel.add(component);
		}
		panel.setLayout(new FlowLayout());
		return panel;
	}

	public void updateItemsTable(JTable itemsTable, Iterable<SharedItem> items) {
		DefaultTableModel tableModel = (DefaultTableModel) itemsTable.getModel();

		tableModel.setNumRows(0);
		for (SharedItem item : items) {
			String[] arr = new String[3];
			arr[0] = item.getName();
			arr[1] = item.getLabel();
			arr[2] = "" + item.getType();
			tableModel.addRow(arr);
		}
		tableModel.fireTableDataChanged();
	}
}
