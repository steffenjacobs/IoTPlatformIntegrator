package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.commons.lang3.StringUtils;

import me.steffenjacobs.extern.TextLineNumber;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingKey;
import me.steffenjacobs.iotplatformintegrator.service.ui.SettingService;
import me.steffenjacobs.iotplatformintegrator.service.ui.components.CodeEditorController;

/** @author Steffen Jacobs */
public class CodeEditor extends JPanel {

	private static final long serialVersionUID = 9063897186457029715L;

	private final JTextPane tp;
	private final SettingService settingService;

	private CodeEditorController controller;

	public CodeEditor(SettingService settingService) {
		super();
		this.settingService = settingService;
		this.setLayout(new BorderLayout());
		tp = new JTextPane() {
			private static final long serialVersionUID = -7101861942131948484L;

			@Override
			public String getToolTipText(MouseEvent event) {
				int pos = tp.viewToModel(event.getPoint());
				String textToCursor = tp.getText().substring(0, pos);

				// viewToModel does not take "\n" into account -> quick fix
				int lines = StringUtils.countMatches(textToCursor, '\n');
				textToCursor = tp.getText().substring(0, pos + lines);

				// remove whitespaces
				int cnt = StringUtils.countMatches(textToCursor.replaceAll("\\s+", " "), " ");
				String tt = controller.getTooltipForTokenByIndex(cnt);
				return tt;
			}
		};
		tp.setToolTipText("test");
		tp.setEditable(false);
		

		TextLineNumber tln = new TextLineNumber(tp);
		JScrollPane sp = new JScrollPane(tp);
		sp.setRowHeaderView(tln);
		super.add(sp, BorderLayout.CENTER);
		showHelpText();
	}

	public void appendToPane(String msg, Color col, boolean bold) {
		if ("0".equals(settingService.getSetting(SettingKey.SHOW_WHITESPACES))) {
			appendStringToPane(msg, col, bold);
		} else {
			msg.chars().forEach(c -> {
				if (c == ' ') {
					appendWhitespace();
				} else {
					appendStringToPane("" + (char) c, col, bold);
				}
			});
		}
	}

	private void appendWhitespace() {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background,
				"0".equals(settingService.getSetting(SettingKey.SHOW_WHITESPACES)) ? Color.WHITE : Color.LIGHT_GRAY);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.setEditable(true);
		tp.replaceSelection(" ");
		tp.setEditable(false);
	}

	private void appendStringToPane(String msg, Color c, boolean bold) {
		if (msg.equals(" ")) {
			appendWhitespace();
		} else {
			StyleContext sc = StyleContext.getDefaultStyleContext();
			AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.WHITE);

			aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
			aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
			aset = sc.addAttribute(aset, StyleConstants.Foreground, c);
			aset = sc.addAttribute(aset, StyleConstants.Bold, bold);

			int len = tp.getDocument().getLength();
			tp.setCaretPosition(len);
			tp.setCharacterAttributes(aset, false);
			tp.setEditable(true);
			tp.replaceSelection(msg);
			tp.setEditable(false);
		}
	}

	public void clear() {
		tp.setEditable(true);
		tp.setText("");
		tp.setEditable(false);
	}

	public void showHelpText() {
		clear();
		appendToPane("Select a rule to see the generated pseudocode.", Color.BLACK, false);
	}

	public void setController(CodeEditorController controller) {
		this.controller = controller;
	}
}
