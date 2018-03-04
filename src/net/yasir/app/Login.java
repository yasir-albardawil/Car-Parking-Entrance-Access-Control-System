package net.yasir.app;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Login {
	private IntegerProperty ID;
	private StringProperty username;
	private StringProperty password;
	private StringProperty role;

	public Login() {
		// TODO Auto-generated constructor stub
		this(0, null, null, null);
	}

	public Login(int ID, String username, String password, String role) {
		this.ID = new SimpleIntegerProperty(ID);
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.role = new SimpleStringProperty(role);
	}

	public void setID(int id) {
		this.ID.set(id);
	}

	public int getID() {
		return ID.get();
	}

	public IntegerProperty getIDProperty() {
		return ID;
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public String getUsername() {
		return username.get();
	}

	public StringProperty getUsernameProperty() {
		return username;
	}

	public void setPassword(String pasword) {
		this.password.set(pasword);
	}

	public String getPassword() {
		return password.get();
	}

	public StringProperty getPasswordProperty() {
		return password;
	}

	public void setRole(String role) {
		this.role.set(role);
	}

	public String getRole() {
		return role.get();
	}

	public StringProperty getRoleProperty() {
		return role;
	}

}
