package me.steffenjacobs.iotplatformintegrator.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.iotplatformintegrator.service.authentication.AuthenticationService;
import me.steffenjacobs.iotplatformintegrator.service.ui.ServerConnectionManager;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.ui.perspectives.AdoptionPerspective;
import me.steffenjacobs.iotplatformintegrator.ui.perspectives.ImportPerspective;
import me.steffenjacobs.iotplatformintegrator.ui.perspectives.Perspective;

/** @author Steffen Jacobs */
public class UiEntrypoint {

	private final static Logger LOG = LoggerFactory.getLogger(UiEntrypoint.class);

	private final SettingsFrameFactory settingsFrameFactory;

	private final ImportPerspective importPerspective;
	private final AdoptionPerspective adoptionPerspective;
	private final ServerConnectionManager serverConnectionManager;
	private final AuthenticationService authenticationService;
	private final SettingService settingService;

	private Perspective currentPerspective = null;
	private JFrame frame;

	public UiEntrypoint() {
		settingService = new SettingService("./settings.config");
		settingsFrameFactory = new SettingsFrameFactory(settingService);
		importPerspective = new ImportPerspective(settingService);
		adoptionPerspective = new AdoptionPerspective();
		serverConnectionManager = new ServerConnectionManager(settingService);
		authenticationService = new AuthenticationService(settingService);
	}

	private void createAndShowGUI() {
		// Creating the Frame
		frame = setupFrame();

		// Creating the MenuBar and adding components
		frame.setJMenuBar(setupMenu(frame));

		// setup docking environment
		setActivePerspective(importPerspective);

		// authentication if required
		if (authenticationService.isSignupRequired() || !authenticationService.isLoginSuccessful()) {
			JDialog loginFrame = new LoginFrameFactory(settingService, authenticationService).createSettingsFrame(this.frame);
			loginFrame.setVisible(true);
		}
		LOG.info("Logged in as user {} ({})", settingService.getSetting(SettingKey.USERNAME), settingService.getSetting(SettingKey.USERID));

		frame.setVisible(true);
	}

	private void setActivePerspective(Perspective perspective) {
		if (currentPerspective != perspective) {
			if (currentPerspective != null) {
				currentPerspective.removeFromFrame(frame);
			}
			currentPerspective = perspective;
			currentPerspective.onAppear();
			currentPerspective.addToFrame(frame);
			frame.revalidate();
			frame.repaint();
		}
	}

	private JFrame setupFrame() {
		JFrame frame = new JFrame("IoT Platform Integrator");
		try {
			BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/icon.png"));
			Graphics2D g = bufferedImage.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			frame.setIconImage(bufferedImage);
		} catch (IOException e1) {
			LOG.error("Could not load icon: " + e1.getMessage());
		}

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			LOG.error("Error while setting look and feel: " + e.getMessage(), e);
		}
		SwingUtilities.updateComponentTreeUI(frame);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1600, 900);
		return frame;
	}

	private JMenuBar setupMenu(JFrame frame) {
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("File");
		JMenu m2 = new JMenu("Help");
		JMenu mPerspective = new JMenu("Perspective");
		mb.add(m1);
		mb.add(m2);
		mb.add(mPerspective);
		JMenu mConnect = new JMenu("Connect");

		JMenuItem mImportFromOpenhab = new JMenuItem("OpenHAB");
		mConnect.add(mImportFromOpenhab);
		mImportFromOpenhab.addActionListener(e -> {
			try {
				serverConnectionManager.loadOpenHABData();
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(frame, String.format("Error while trying to connect to '%s' (%s).\nYou can change the URL and the port under File -> Settings.",
						serverConnectionManager.getOHUrlWithPort(), e2.getMessage()), "Could not connect to openHAB server.", JOptionPane.ERROR_MESSAGE);
			}
		});

		JMenuItem mImportFromHomeAssistant = new JMenuItem("HomeAssistant");
		mConnect.add(mImportFromHomeAssistant);
		mImportFromHomeAssistant.addActionListener(e -> {
			try {
				serverConnectionManager.loadHomeAssistantData();
			} catch (Exception e2) {
				e2.printStackTrace();
				JOptionPane.showMessageDialog(frame, String.format("Error while trying to connect to '%s' (%s).\nYou can change the URL and the port under File -> Settings.",
						serverConnectionManager.getHAUrlWithPort(), e2.getMessage()), "Could not connect to HomeAssistant server.", JOptionPane.ERROR_MESSAGE);
			}
		});

		JMenuItem mSettings = new JMenuItem("Settings");
		mSettings.addActionListener(e -> settingsFrameFactory.createSettingsFrame().setVisible(true));

		m1.add(mConnect);
		m1.add(mSettings);

		JMenuItem mImportPerspective = new JMenuItem("Import Perspective");
		mImportPerspective.addActionListener(e -> {
			setActivePerspective(importPerspective);
		});
		JMenuItem mAdoptionPerspective = new JMenuItem("Adoption Perspective");
		mAdoptionPerspective.addActionListener(e -> {
			setActivePerspective(adoptionPerspective);
		});

		mPerspective.add(mImportPerspective);
		mPerspective.add(mAdoptionPerspective);
		return mb;
	}

	public void createAndShowGUIAsync() {
		javax.swing.SwingUtilities.invokeLater(this::createAndShowGUI);
	}

}
