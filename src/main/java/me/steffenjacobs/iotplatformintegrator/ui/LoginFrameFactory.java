package me.steffenjacobs.iotplatformintegrator.ui;

import java.awt.GridLayout;
import java.awt.Window;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import me.steffenjacobs.iotplatformintegrator.domain.authentication.User;
import me.steffenjacobs.iotplatformintegrator.service.authentication.AuthenticationService;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;

/** @author Steffen Jacobs */
public class LoginFrameFactory {

	private final SettingService settingService;
	private final AuthenticationService authenticationService;

	public LoginFrameFactory(SettingService settingService, AuthenticationService authenticationService) {
		this.settingService = settingService;
		this.authenticationService = authenticationService;
	}

	public JDialog createLoginFrame(Window parent) {
		JDialog frame = new JDialog(parent);
		frame.setTitle("Registration required!");
		frame.setSize(400, 300);

		JButton btnRegister = new JButton("Register");
		JPanel gridPanel = new JPanel();

		JTextField txtUsername = new JTextField();
		JTextField txtPassword = new JTextField();

		JLabel lblUser = new JLabel("Username:");
		JLabel lblPassword = new JLabel("Password:");

		GridLayout gl = new GridLayout(2, 2);

		gridPanel.setLayout(gl);

		gridPanel.add(lblUser);
		gridPanel.add(txtUsername);
		gridPanel.add(lblPassword);
		gridPanel.add(txtPassword);

		JPanel contentPanel = new JPanel();
		contentPanel.add(gridPanel);
		contentPanel.add(btnRegister);

		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		frame.add(contentPanel);

		btnRegister.addActionListener(e -> {
			User user = new User(UUID.randomUUID(), txtUsername.getText(), txtPassword.getText());
			authenticationService.registerUser(user);
			settingService.setSetting(SettingKey.USERID, user.getUserId().toString());
			settingService.setSetting(SettingKey.USERNAME, user.getUsername());
			settingService.setSetting(SettingKey.PASSWORD, user.getPassword());
			frame.setVisible(false);
		});

		frame.setModal(true);
		return frame;
	}
}
