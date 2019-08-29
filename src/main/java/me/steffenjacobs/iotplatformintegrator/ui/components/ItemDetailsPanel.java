package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.jfree.ui.tabbedui.VerticalLayout;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.extern.babelnetconnector.BabelLanguage;
import me.steffenjacobs.iotplatformintegrator.App;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.Command;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.ItemType.StateType;
import me.steffenjacobs.iotplatformintegrator.domain.shared.item.SharedItem;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus;
import me.steffenjacobs.iotplatformintegrator.service.manage.EventBus.EventType;
import me.steffenjacobs.iotplatformintegrator.service.manage.events.SelectedItemChangedEvent;

/** @author Steffen Jacobs */
public class ItemDetailsPanel extends JPanel {
	private static final long serialVersionUID = -3955999116242608088L;
	private static final Logger LOG = LoggerFactory.getLogger(ItemDetailsPanel.class);

	private final JTextField txtName;
	private final JTextField txtLabel;
	private final JTextField txtType;
	private final JTextField txtApplicableCommands;
	private final JTextField txtApplicableStates;
	private final JTextField txtDataType;

	private final JPanel noItemSelected, itemSelected;

	private SharedItem item = null;

	public ItemDetailsPanel() {
		super();
		final JPanel form = new JPanel();
		form.setLayout(new GridBagLayout());
		FormUtility formUtility = new FormUtility();

		// create form
		formUtility.addLabel("Name: ", form);
		txtName = new JTextField();
		formUtility.addLastField(txtName, form);

		formUtility.addLabel("Label: ", form);
		txtLabel = new JTextField();
		formUtility.addLastField(txtLabel, form);

		formUtility.addLabel("Item Type: ", form);
		txtType = new JTextField();
		formUtility.addLastField(txtType, form);

		formUtility.addLabel("Applicable Commands: ", form);
		txtApplicableCommands = new JTextField();
		formUtility.addLastField(txtApplicableCommands, form);

		formUtility.addLabel("Applicable States: ", form);
		txtApplicableStates = new JTextField();
		formUtility.addLastField(txtApplicableStates, form);

		formUtility.addLabel("Data Type: ", form);
		txtDataType = new JTextField();
		formUtility.addLastField(txtDataType, form);

		form.setBorder(new EmptyBorder(2, 2, 2, 2));

		// Add form panel to panel
		itemSelected = new JPanel();
		itemSelected.add(form);
		itemSelected.add(createBabelLayout());

		itemSelected.setBorder(BorderFactory.createTitledBorder("Item Details"));
		itemSelected.setLayout(new BoxLayout(itemSelected, BoxLayout.Y_AXIS));

		noItemSelected = new JPanel();
		noItemSelected.add(new JLabel("Please select an item to display the details.                                                                          "));

		super.add(noItemSelected, BorderLayout.NORTH);

		EventBus.getInstance().addEventHandler(EventType.SELECTED_ITEM_CHANGE, e -> setDisplayedItem(((SelectedItemChangedEvent) e).getItem()));
	}

	private JPanel createBabelLayout() {
		final JPanel babelPanel = new JPanel(new VerticalLayout());
		final JComboBox<BabelLanguage> selectSourceLang = new JComboBox<>(BabelLanguage.values());
		selectSourceLang.setRenderer((list, value, index, isSelected, cellHasFocus) -> new JLabel(value.getDisplayName()));

		final JButton searchButton = new JButton("Show Images");
		final JPanel imagePanel = new JPanel(new VerticalLayout());

		searchButton.addActionListener(l -> {
			imagePanel.removeAll();
			imagePanel.add(new JLabel(new ImageIcon(getClass().getClassLoader().getResource("spinner.gif"))));

			imagePanel.revalidate();
			imagePanel.repaint();
			new Thread(() -> {
				final Set<String> urls = new HashSet<>();
				App.getBabelnetrequester().requestSynsets(item.getLabel(), (BabelLanguage) selectSourceLang.getSelectedItem(), BabelLanguage.ENGLISH).values().stream()
						.flatMap(s -> s.getImages().stream().map(me.steffenjacobs.extern.babelnetconnector.domain.Image::getUrl).limit(5)).filter(i -> !urls.contains(i)).limit(5)
						.filter(i -> i != null).map(i -> {
							// resize graphic and render it onto label
							try {
								final BufferedImage img = ImageIO.read(new URL(i));
								if (img == null) {
									// invalid file format -> will be filtered out later
									LOG.warn("Could not load image from '{}'.", i);
									return null;
								}
								int newHeight = (int) (img.getHeight() / (double) img.getWidth() * 250);
								final BufferedImage resized = new BufferedImage(250, newHeight, img.getType());
								Graphics2D g = resized.createGraphics();
								g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
								g.drawImage(img, 0, 0, 250, newHeight, 0, 0, img.getWidth(), img.getHeight(), null);
								g.dispose();

								return new JLabel(new ImageIcon(resized));
							} catch (IOException e) {
								// will be filtered out later
								LOG.error(e.getMessage(), e);
								return null;
							}
						}).filter(i -> i != null).forEach(imagePanel::add);

				// remove spinner
				imagePanel.remove(0);
				SwingUtilities.invokeLater(() -> {
					// show label if no items found
					if (imagePanel.getComponentCount() == 0) {
						imagePanel.add(new JLabel("No images found for " + item.getLabel()));
					}
					imagePanel.revalidate();
					imagePanel.repaint();
				});

			}).start();

		});

		babelPanel.add(selectSourceLang);
		babelPanel.add(searchButton);
		babelPanel.add(imagePanel);
		return babelPanel;
	}

	private void setDisplayedItem(SharedItem item) {
		this.item = item;
		if (item == null) {
			super.removeAll();
			super.add(noItemSelected, BorderLayout.NORTH);
		} else {
			txtName.setText(item.getName());
			txtLabel.setText(item.getLabel());
			txtType.setText(item.getType().name());
			txtApplicableCommands.setText(Arrays.stream(item.getType().getAllowedCommands()).map(Command::name).collect(Collectors.joining(", ")));
			txtApplicableStates.setText(Arrays.stream(item.getType().getAllowedStates()).map(StateType::name).collect(Collectors.joining(", ")));
			txtDataType.setText(item.getType().getDatatype().name());

			super.removeAll();
			super.add(itemSelected, BorderLayout.CENTER);
		}
		this.repaint();
		this.revalidate();
	}
}
