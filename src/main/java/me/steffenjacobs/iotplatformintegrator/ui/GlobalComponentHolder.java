package me.steffenjacobs.iotplatformintegrator.ui;

import me.steffenjacobs.iotplatformintegrator.ui.components.ConnectionExplorer;

/** @author Steffen Jacobs */
public class GlobalComponentHolder {

	private static final GlobalComponentHolder INSTANCE = new GlobalComponentHolder();

	public static GlobalComponentHolder getInstance() {
		return INSTANCE;
	}

	private final ConnectionExplorer connectionExplorer = new ConnectionExplorer();

	public ConnectionExplorer getConnectionExplorer() {
		return connectionExplorer;
	}

}
