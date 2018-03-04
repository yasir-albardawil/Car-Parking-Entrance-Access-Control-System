package net.yasir.view;

import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.yasir.app.VehicleDataDetails;
import net.yasir.connection.MySQLJDBCDriverConnection;
import net.yasir.connection.SQLiteJDBCDriverConnection;
import net.yasir.utils.DateUtil;

public class VehicleDataAddDialogController {
	@FXML
	private TextField idNoField;
	@FXML
	private TextField ownerNameField;
	@FXML
	private TextField plateNumberField;
	@FXML
	private TextField carBrandField;
	@FXML
	private TextField carModelField;
	@FXML
	private TextField vinField;
	@FXML
	private TextField manufacturingYearField;
	@FXML
	private TextField colorField;
	@FXML
	private DatePicker expiryDateDatePicker;

	private VehicleDataDetails vehicleDataDetails;
	private Stage dialogStage;

	private boolean okClicked = false;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
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
	public void setCarData(VehicleDataDetails vehicleDataDetails) throws ClassNotFoundException {
		this.vehicleDataDetails = vehicleDataDetails;

		idNoField.setText(vehicleDataDetails.getIdNo());
		ownerNameField.setText(vehicleDataDetails.getOwnerName());
		plateNumberField.setText(vehicleDataDetails.getPlateNumber());
		carBrandField.setText(vehicleDataDetails.getCarBrand());
		carModelField.setText(vehicleDataDetails.getCarModel());
		vinField.setText(vehicleDataDetails.getVin());
		manufacturingYearField.setText(vehicleDataDetails.getManufacturingYear());
		colorField.setText(vehicleDataDetails.getColor());
		expiryDateDatePicker.setValue(vehicleDataDetails.getExpiryDate());
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
			vehicleDataDetails.setIdNo(idNoField.getText());
			vehicleDataDetails.setOwnerName(ownerNameField.getText());
			vehicleDataDetails.setPlateNumber(plateNumberField.getText());
			vehicleDataDetails.setCarBrand(carBrandField.getText());
			vehicleDataDetails.setCarModel(carModelField.getText());
			vehicleDataDetails.setVin(vinField.getText());
			vehicleDataDetails.setManufacturingYear(manufacturingYearField.getText());
			vehicleDataDetails.setColor(colorField.getText());
			vehicleDataDetails.setExpiryDate(expiryDateDatePicker.getValue());

			MySQLJDBCDriverConnection.insert(idNoField.getText(), ownerNameField.getText(), plateNumberField.getText(),
					carBrandField.getText(), carModelField.getText(), vinField.getText(),
					manufacturingYearField.getText(), colorField.getText(),
					DateUtil.format(expiryDateDatePicker.getValue()));
			SQLiteJDBCDriverConnection.insert(idNoField.getText(), ownerNameField.getText(), plateNumberField.getText(),
					carBrandField.getText(), carModelField.getText(), vinField.getText(),
					manufacturingYearField.getText(), colorField.getText(),
					DateUtil.format(expiryDateDatePicker.getValue()));

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

		if (idNoField.getText() == null || idNoField.getText().length() == 0) {
			errorMessage += "No valid ID number!\n";
		} else {
			if (idNoField.getText().length() != 10) {
				errorMessage += "No valid ID number!. The length must be 10!\n";
			} else {
				boolean isNumber = Pattern.matches("\\d+", idNoField.getText());
				if (!isNumber) {
					errorMessage += "No valid ID number!. Accpets only numbers!\n";
				}
			}
		}

		if (ownerNameField.getText() == null || ownerNameField.getText().length() == 0) {
			errorMessage += "No valid owner name!\n";
		}

		if (plateNumberField.getText() == null || plateNumberField.getText().length() == 0) {
			errorMessage += "No valid plate number!\n";
		} else {
			String sqlIsDuplicate = "SELECT * FROM car_data WHERE PlateNumber= '" + plateNumberField.getText() + "' ";
			System.out.println(plateNumberField.getText());
			boolean isDuplicateMySQL = MySQLJDBCDriverConnection.isDuplicateAddCarData(sqlIsDuplicate);
			boolean isDuplicateSQLite = SQLiteJDBCDriverConnection.isDuplicateAddCarData(sqlIsDuplicate);
			if (isDuplicateMySQL || isDuplicateSQLite) {
				errorMessage += "Valid Plate number!\n";
			}
		}

		if (carBrandField.getText() == null || carBrandField.getText().length() == 0) {
			errorMessage += "No valid vehicle brand!\n";
		}

		if (carModelField.getText() == null || carModelField.getText().length() == 0) {
			errorMessage += "No valid vehicle model!\n";
		}

		if (vinField.getText() == null || vinField.getText().length() == 0) {
			errorMessage += "No valid vehicle Information Number!\n";
		} else {
			if (vinField.getText().length() != 17) {
				errorMessage += "No valid vehicle Information Number!. The lenght must be 17!\n";
			}
		}

		if (manufacturingYearField.getText() == null || manufacturingYearField.getText().length() == 0) {
			errorMessage += "No valid manufacturing year!\n";
		} else {
			if (manufacturingYearField.getText().length() != 4) {
				errorMessage += "No valid manufacturing year!. Use the format yyyy!\n";
			}
		}

		if (colorField.getText() == null || colorField.getText().length() == 0) {
			errorMessage += "No valid color!\n";
		}

		if (expiryDateDatePicker.getValue() == null) {
			errorMessage += "No valid expiry date!\n";
		} else {
			if (!DateUtil.validDate(DateUtil.format(expiryDateDatePicker.getValue()))) {
				errorMessage += "No valid expiry date. Use the format dd/mm/yyyy!\n";
			}
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
