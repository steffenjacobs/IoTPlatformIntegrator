package me.steffenjacobs.iotplatformintegrator.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/** @author Steffen Jacobs */
public class CodeEditor extends JPanel {

	private static final long serialVersionUID = 9063897186457029715L;

	private final JTextPane tp;

	public CodeEditor() {
		super();
		this.setLayout(new BorderLayout());
		tp = new JTextPane();
		super.add(tp, BorderLayout.CENTER);
		showHelpText();
	}

	public void appendToPane(String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}

	public void clear() {
		tp.setText("");
	}

	public void showHelpText() {
		clear();
		appendToPane("Select a rule to see the generated pseudocode.", Color.BLACK);
	}

}
