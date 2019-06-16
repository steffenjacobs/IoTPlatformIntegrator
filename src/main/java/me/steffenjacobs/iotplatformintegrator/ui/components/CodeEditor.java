package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.commons.lang3.StringUtils;

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
				int cnt = StringUtils.countMatches(tp.getText().subSequence(0, pos), " ");
				System.out.println(cnt);
				return controller.getTooltipForTokenByIndex(cnt);
				// int wordStart = tp.getText().lastIndexOf(" ", pos);
				// int wordEnd = tp.getText().indexOf(" ", pos);
				// String tt = tp.getText().substring(wordStart < 0 ? 0 : wordStart, wordEnd < 0
				// ? tp.getText().length() : wordEnd);
				// return tt;
			}
		};
		tp.setToolTipText("test");
		super.add(tp, BorderLayout.CENTER);
		showHelpText();
	}

	public void appendToPane(String msg, Color col) {
		if ("0".equals(settingService.getSetting(SettingKey.SHOW_WHITESPACES))) {
			appendStringToPane(msg, col);
		} else {
			msg.chars().forEach(c -> {
				if (c == ' ') {
					appendWhitespace();
				} else {
					appendStringToPane("" + (char) c, col);
				}
			});
		}
	}

	private void appendWhitespace() {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.LIGHT_GRAY);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(" ");
	}

	private void appendStringToPane(String msg, Color c) {
		if (msg.equals(" ")) {
			appendWhitespace();
		} else {
			StyleContext sc = StyleContext.getDefaultStyleContext();
			AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.WHITE);

			aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
			aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
			aset = sc.addAttribute(aset, StyleConstants.Foreground, c);

			int len = tp.getDocument().getLength();
			tp.setCaretPosition(len);
			tp.setCharacterAttributes(aset, false);
			tp.replaceSelection(msg);
		}
	}

	public void clear() {
		tp.setText("");
	}

	public void showHelpText() {
		clear();
		appendToPane("Select a rule to see the generated pseudocode.", Color.BLACK);
	}

	public void setController(CodeEditorController controller) {
		this.controller = controller;
	}

}
