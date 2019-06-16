package me.steffenjacobs.iotplatformintegrator.service.ui.components;

import java.awt.Color;

import me.steffenjacobs.iotplatformintegrator.domain.shared.rule.SharedRule;
import me.steffenjacobs.iotplatformintegrator.service.shared.PseudocodeGenerator;
import me.steffenjacobs.iotplatformintegrator.ui.components.CodeEditor;

/** @author Steffen Jacobs */
public class CodeEditorController {

	private static final PseudocodeGenerator pseudocodeGenerator = new PseudocodeGenerator();
	private final CodeEditor codeEditor;

	public CodeEditorController(CodeEditor codeEditor) {
		this.codeEditor = codeEditor;
	}

	public void renderPseudocode(SharedRule rule) {
		String code = pseudocodeGenerator.generateCodeForRule(rule);
		codeEditor.clear();
		codeEditor.appendToPane(code, Color.BLACK);
	}

}
