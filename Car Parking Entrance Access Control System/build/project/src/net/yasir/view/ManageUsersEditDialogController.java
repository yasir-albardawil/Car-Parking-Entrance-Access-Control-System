package net.yasir.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.yasir.app.Login;
import net.yasir.connection.MySQLJDBCDriverConnection;
import net.yasir.connection.SQLiteJDBCDriverConnection;
import net.yasir.utils.MD5encrption;

public class ManageUsersEditDialogController {
	@FXML
	private TextField usernameTextField;
	@FXML
	private PasswordField passwordTextField;
	@FXML
	private ComboBox<String> roleComboBox;

	private Login login;
	private Stage dialogStage;

	private boolean okClicked = false;

	ObservableList<String> list = FXCollections.observableArrayList("Admin", "User");

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		roleComboBox.setItems(list);
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;

		// Set the dialog icon.
		this.dialogStage.getIcons().add(new Image("file:resources/icons/barrier.png"));
	}

	/**
	 * Sets the carData to be edited in the dialog.
	 * 
	 * @param carData
	 * @throws ClassNotFoundException
	 */
	public void setUsers(Login login) throws ClassNotFoundException {
		this.login = login;

		usernameTextField.setText(login.getUsername());
		passwordTextField.setText(MD5encrption.DecryptText(login.getPassword()));
		roleComboBox.setValue(login.getRole());

	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 * 
	 * @throws Exception
	 */
	@FXML
	private void handleOk() throws Exception {
		if (isInputValid()) {
			login.setUsername(usernameTextField.getText());
			login.setPassword(MD5encrption.EncryptText(passwordTextField.getText()));
			login.setRole(roleComboBox.getValue());

			MySQLJDBCDriverConnection.updateUsers(usernameTextField.getText(),
					MD5encrption.EncryptText(passwordTextField.getText()), roleComboBox.getValue());
			SQLiteJDBCDriverConnection.updateUsers(usernameTextField.getText(),
					MD5encrption.EncryptText(passwordTextField.getText()), roleComboBox.getValue());

			okClicked = true;

			dialogStage.close();
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 * @throws ClassNotFoundException
	 */
	private boolean isInputValid() throws ClassNotFoundException {
		String errorMessage = "";

		if (usernameTextField.getText() == null || usernameTextField.getText().length() == 0) {
			errorMessage += "No valid username!\n";
		}

		if (passwordTextField.getText() == null || passwordTextField.getText().length() == 0) {
			errorMessage += "No valid password!\n";
		}

		if (roleComboBox.getValue() == null || roleComboBox.getValue().length() == 0) {
			errorMessage += "Not selected role !\n";

		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText(errorMessage);

			alert.showAndWait();

			return false;
		}
	}

}
