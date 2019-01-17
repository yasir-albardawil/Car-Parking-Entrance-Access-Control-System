package net.yasir.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.yasir.app.MainApp;
import net.yasir.connection.MySQLJDBCDriverConnection;
import net.yasir.connection.SQLiteJDBCDriverConnection;
import net.yasir.utils.MD5encrption;

public class LoginDialogController {
	@FXML
	private TextField usernameTextField;
	@FXML
	private TextField passwordTextField;
	@FXML
	private ComboBox<String> roleComboBox;

	// Reference to the main application.
	private MainApp mainApp;
	private Stage dialogStage;

	private boolean okClicked = false;

	ObservableList<String> list = FXCollections.observableArrayList("Admin", "User");

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public LoginDialogController() {
		// TODO Auto-generated constructor stub
	}

	@FXML
	public void initialize() {
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
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	public void handleLogin() throws ClassNotFoundException {
		String sql = "SELECT * FROM login WHERE Username='" + usernameTextField.getText() + "' AND Password='"
				+ MD5encrption.EncryptText(passwordTextField.getText()) + "' AND Role='" + roleComboBox.getValue()
				+ "';";
		if (isInputValid()) {
			okClicked = true;

			boolean isValidMySQL = MySQLJDBCDriverConnection.login(sql);
			boolean isValidSQLite = SQLiteJDBCDriverConnection.login(sql);
			String getRoleMySQL = MySQLJDBCDriverConnection.getRole(sql);
			String getRoleSQLite = MySQLJDBCDriverConnection.getRole(sql);
			if (isValidMySQL || isValidSQLite) {
				if (getRoleMySQL.equals("Admin") || getRoleSQLite.equals("Admin")) {
					dialogStage.close();
					mainApp.showMainOverviewAdmin();
				}

				if (getRoleMySQL.equals("User") || getRoleSQLite.equals("User")) {
					dialogStage.close();
					mainApp.showMainOverviewUser();
				}
			}

			if (!getRoleMySQL.equals("Admin") && !getRoleMySQL.equals("User")
					|| !getRoleSQLite.equals("Admin") && !getRoleSQLite.equals("User")) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(dialogStage);
				alert.setTitle("Invalid Fields");
				alert.setHeaderText("Please correct invalid fields");
				alert.setContentText("Wrong username and passowrd. Try again.");

				alert.showAndWait();
			}

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
	 */
	private boolean isInputValid() {
		String errorMessage = "";

		if (usernameTextField.getText() == null || usernameTextField.getText().length() == 0) {
			errorMessage += "No valid username!\n";
		}

		if (passwordTextField.getText() == null || passwordTextField.getText().length() == 0) {
			errorMessage += "No valid password!\n";
		}

		if (roleComboBox.getValue() == null || roleComboBox.getValue().length() == 0) {
			errorMessage += "No selected role!\n";

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
