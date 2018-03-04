package net.yasir.app;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Settings {
	private StringProperty username;
	private StringProperty password;
	private StringProperty comPort;
	private StringProperty mode;

	/**
	 * Default constructor.
	 */
	public Settings() {
		this(null, null, null, null);
	}

	public Settings(String username, String passowrd, String comPort) {
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(passowrd);
		this.comPort = new SimpleStringProperty(comPort);

	}

	public Settings(String username, String passowrd, String comPort, String mode) {
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(passowrd);
		this.comPort = new SimpleStringProperty(comPort);
		this.mode = new SimpleStringProperty(mode);

	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public String getUsername() {
		return username.get();
	}

	public StringProperty userNameProperty() {
		return username;
	}

	public void setPassword(String password) {
		this.password.set(password);
	}

	public String getPassword() {
		return password.get();
	}

	public StringProperty passwordProperty() {
		return password;
	}

	public void setComPortNumber(String comPort) {
		this.comPort.set(comPort);
	}

	public String getcomPort() {
		return comPort.get();
	}

	public StringProperty comPortProperty() {
		return comPort;
	}

	public void setMode(String mode) {
		this.mode.set(mode);
	}

	public String getMode() {
		return mode.get();
	}

	public StringProperty ModeProperty() {
		return mode;
	}
}