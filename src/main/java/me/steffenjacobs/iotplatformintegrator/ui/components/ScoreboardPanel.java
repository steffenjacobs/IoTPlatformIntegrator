package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import me.steffenjacobs.iotplatformintegrator.domain.authentication.UserScore;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.ui.ScoreboardController;

/** @author Steffen Jacobs */
public class ScoreboardPanel extends JPanel {
	private static final long serialVersionUID = -8556971313008407678L;

	private final JTable scoreboard;

	private final List<UserScore> currentScores = new ArrayList<>();

	public ScoreboardPanel(ScoreboardController controller) {

		scoreboard = createItemsTable();

		// add refresh popup
		final JPopupMenu popup = new JPopupMenu();
		final JMenuItem refreshItem = new JMenuItem("Refresh");
		refreshItem.addActionListener(a -> refresh(controller));
		popup.add(refreshItem);
		scoreboard.setComponentPopupMenu(popup);

		this.add(scoreboard);

		refresh(controller);
	}

	private void refresh(ScoreboardController controller) {
		currentScores.clear();
		controller.refreshedTable(s -> {
			currentScores.add(s);
			updateItemsTable(currentScores.toArray(new UserScore[currentScores.size()]));
		});
	}

	public JTable getScoreboard() {
		return scoreboard;
	}

	private JTable createItemsTable() {

		// create table model
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.setColumnCount(3);

		// setup columns
		String[] columnNames = { "UserId", "Additions", "Deletions", "Modifications" };
		tableModel.setColumnIdentifiers(columnNames);

		// create JTable
		JTable table = new JTable(tableModel);
		table.setBounds(30, 40, 200, 300);

		return table;
	}

	private void updateItemsTable(UserScore[] scores) {
		DefaultTableModel tableModel = (DefaultTableModel) scoreboard.getModel();
		tableModel.setNumRows(0);

		for (UserScore score : scores) {
			final String[] arr = new String[4];
			arr[0] = score.getUserId().toString();
			arr[1] = Long.toString(score.getAdditions());
			arr[2] = Long.toString(score.getDeletions());
			arr[3] = Long.toString(score.getModifications());

			tableModel.addRow(arr);
		}
		tableModel.fireTableDataChanged();

	}

}
