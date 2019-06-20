package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import me.steffenjacobs.iotplatformintegrator.domain.manage.ServerConnection;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ServerConnectedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.ServerDisconnectedEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SourceConnectionChangeEvent;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.TargetConnectionChangeEvent;

/** @author Steffen Jacobs */
public class InstanceChooserPanel extends JPanel {
	private static final long serialVersionUID = -4372629051756002301L;

	private final DefaultComboBoxModel<ServerConnection> sourceModel;
	private final DefaultComboBoxModel<ServerConnection> targetModel;

	public InstanceChooserPanel() {
		sourceModel = new DefaultComboBoxModel<ServerConnection>();
		targetModel = new DefaultComboBoxModel<ServerConnection>();
		JComboBox<ServerConnection> chooseSourceServer = new JComboBox<>(sourceModel);
		JComboBox<ServerConnection> chooseTargetServer = new JComboBox<>(targetModel);
		chooseSourceServer.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.getInstanceName() : ""));
		chooseTargetServer.setRenderer((l, v, i, iS, cHF) -> new JLabel(v != null ? v.getInstanceName() : ""));

		this.setLayout(new BorderLayout());
		this.add(createChooserPanelPanel(chooseSourceServer, chooseTargetServer), BorderLayout.CENTER);

		EventBus.getInstance().addEventHandler(EventType.ServerConnected, e -> addServerConnection(((ServerConnectedEvent) e).getServerConnection()));
		EventBus.getInstance().addEventHandler(EventType.ServerDisconnected, e -> removeServerConnection(((ServerDisconnectedEvent) e).getServerConnection()));

		chooseSourceServer.addActionListener(e -> EventBus.getInstance().fireEvent(new SourceConnectionChangeEvent((ServerConnection) chooseSourceServer.getSelectedItem())));
		chooseTargetServer.addActionListener(e -> EventBus.getInstance().fireEvent(new TargetConnectionChangeEvent((ServerConnection) chooseTargetServer.getSelectedItem())));
	}

	private void addServerConnection(ServerConnection serverConnection) {
		sourceModel.addElement(serverConnection);
		targetModel.addElement(serverConnection);
	}

	private void removeServerConnection(ServerConnection serverConnection) {
		sourceModel.removeElement(serverConnection);
		targetModel.removeElement(serverConnection);

	}

	private JPanel createChooserPanelPanel(JComboBox<ServerConnection> chooseSourceServer, JComboBox<ServerConnection> chooseTargetServer) {
		JPanel panel = new JPanel();
		final JPanel form = new JPanel();
		form.setLayout(new GridBagLayout());
		FormUtility formUtility = new FormUtility();

		// Add fields
		formUtility.addLabel("Source Server Connection: ", form);
		formUtility.addLastField(chooseSourceServer, form);

		formUtility.addLabel("Target Server Connection: ", form);
		formUtility.addLastField(chooseTargetServer, form);

		panel.add(new JLabel("Choose the source and target server connections."));
		panel.add(Box.createVerticalStrut(10));
		panel.add(form);

		return panel;
	}
}
