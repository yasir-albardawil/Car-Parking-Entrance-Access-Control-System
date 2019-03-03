package net.yasir.view;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.fazecast.jSerialComm.SerialPort;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.yasir.app.VehicleDataDetails;
import net.yasir.app.VehicleLoginInformation;
import net.yasir.connection.MySQLJDBCDriverConnection;
import net.yasir.connection.SQLiteJDBCDriverConnection;
import net.yasir.utils.DateUtil;
import net.yasir.utils.Utils;

import static net.yasir.connection.MySQLJDBCDriverConnection.getConnection;
import static net.yasir.connection.SQLiteJDBCDriverConnection.getConnectionSQLite;

public class VehicleLoginInformationManualDialogController {
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
	private ImageView plateNumberImageView;

	@FXML
	private TextField colorField;
	@FXML
	private DatePicker expiryDateDatePicker;

	private VehicleDataDetails carData;
	private VehicleLoginInformation accessCar;
	private Stage dialogStage;

	private boolean okClicked = false;

	public static SerialPort arduino;

	private String theme;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws s
	 */
	@FXML
	private void initialize() throws ClassNotFoundException, IOException {
		// try {
		InputStream is = MySQLJDBCDriverConnection.getPlateNumberImage(
				"SELECT Image FROM vehicle_Login_Information where id=(select max(id) from vehicle_Login_Information);");

		plateNumberImageView.setImage(new Image(is));
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
	public void setCarData(VehicleDataDetails carData) throws ClassNotFoundException {
		this.carData = carData;

		idNoField.setText(carData.getIdNo());
		ownerNameField.setText(carData.getOwnerName());
		plateNumberField.setText(carData.getPlateNumber());
		carBrandField.setText(carData.getCarBrand());
		carModelField.setText(carData.getCarModel());
		vinField.setText(carData.getVin());
		manufacturingYearField.setText(carData.getManufacturingYear());
		colorField.setText(carData.getColor());
		expiryDateDatePicker.setValue(carData.getExpiryDate());
	}

	/**
	 * Connect to the sql.sqlite database
	 * 
	 * @return the Connection object
	 * @throws ClassNotFoundException
	 */
	private static Connection connectMySQL() throws ClassNotFoundException {
		// MySQL connection string
		return getConnection();
	}

	/**
	 * Connect to the sql.sqlite database
	 * 
	 * @return the Connection object
	 */
	private static Connection connectsqlite() {
		// SQLite connection string
		return getConnectionSQLite()
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
		openGate();

		okClicked = true;

		dialogStage.close();
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected person.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleShowCarDataDetails() throws ClassNotFoundException {
		VehicleDataDetails tempCarData = new VehicleDataDetails();
		showCarDatadetailsDialog(tempCarData);
	}

	/**
	 * Called when the user clicks ok.
	 * 
	 * @throws Exception
	 */
	@FXML
	private void handleSave() throws Exception {
		if (isInputValid()) {
			carData.setIdNo(idNoField.getText());
			carData.setOwnerName(ownerNameField.getText());
			carData.setPlateNumber(plateNumberField.getText());
			carData.setCarBrand(carBrandField.getText());
			carData.setCarModel(carBrandField.getText());
			carData.setVin(vinField.getText());
			carData.setManufacturingYear(manufacturingYearField.getText());
			carData.setColor(colorField.getText());
			carData.setExpiryDate(expiryDateDatePicker.getValue());

			MySQLJDBCDriverConnection.insert(idNoField.getText(), ownerNameField.getText(), plateNumberField.getText(),
					carBrandField.getText(), carModelField.getText(), vinField.getText(),
					manufacturingYearField.getText(), colorField.getText(),
					DateUtil.format(expiryDateDatePicker.getValue()));
			SQLiteJDBCDriverConnection.insert(idNoField.getText(), ownerNameField.getText(), plateNumberField.getText(),
					carBrandField.getText(), carModelField.getText(), vinField.getText(),
					manufacturingYearField.getText(), colorField.getText(),
					DateUtil.format(expiryDateDatePicker.getValue()));

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

	/**
	 * When the user clicks the Open button. the gate will be open
	 * 
	 */
	public void openGate() {
		if (arduino.openPort() == true) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						OutputStream a = arduino.getOutputStream();
						a.write(80); // Write to serial
						a.flush();

						a.close(); // close serial connection
					} catch (Exception e) {
						System.out.println("Error");
					}
					// arduino.closePort();
					System.out.println("CLOSE COM");
				}
			};
			thread.start();
		} else {
			System.out.println("Cannot open port:");
		}
	}

	public TextField getIdNoField() {
		return idNoField;
	}

	public void setIdNoField(TextField idNoField) {
		this.idNoField = idNoField;
	}

	public TextField getOwnerNameField() {
		return ownerNameField;
	}

	public void setOwnerNameField(TextField ownerNameField) {
		this.ownerNameField = ownerNameField;
	}

	public TextField getPlateNumberField() {
		return plateNumberField;
	}

	public void setPlateNumberField(TextField plateNumberField) {
		this.plateNumberField = plateNumberField;
	}

	public TextField getCarBrandField() {
		return carBrandField;
	}

	public void setCarBrandField(TextField carBrandField) {
		this.carBrandField = carBrandField;
	}

	public TextField getCarModelField() {
		return carModelField;
	}

	public void setCarModelField(TextField carModelField) {
		this.carModelField = carModelField;
	}

	public TextField getVinField() {
		return vinField;
	}

	public void setVinField(TextField vinField) {
		this.vinField = vinField;
	}

	public TextField getManufacturingYearField() {
		return manufacturingYearField;
	}

	public void setManufacturingYearField(TextField manufacturingYearField) {
		this.manufacturingYearField = manufacturingYearField;
	}

	public TextField getColorField() {
		return colorField;
	}

	public void setColorField(TextField colorField) {
		this.colorField = colorField;
	}

	public DatePicker getExpiryDateDatePicker() {
		return expiryDateDatePicker;
	}

	public void setExpiryDateDatePicker(DatePicker expiryDateDatePicker) {
		this.expiryDateDatePicker = expiryDateDatePicker;
	}

	public static SerialPort getArduino() {
		return arduino;
	}

	public static void setArduino(SerialPort arduino) {
		VehicleLoginInformationManualDialogController.arduino = arduino;
	}

	public VehicleDataDetails getCarData() {
		return carData;
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setOkClicked(boolean okClicked) {
		this.okClicked = okClicked;
	}

	public ImageView getPlateNumberImageView() {
		return plateNumberImageView;
	}

	public void setPlateNumberImageView(ImageView plateNumberImageView) {
		this.plateNumberImageView = plateNumberImageView;
	}

	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	public static BufferedImage byteArrayToImage(byte[] bytes) {
		BufferedImage bufferedImage = null;
		try {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			bufferedImage = ImageIO.read(inputStream);
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		return bufferedImage;
	}

	public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;)
				os.write(buffer, 0, len);

			os.flush();

			return os.toByteArray();
		}
	}

	/**
	 * Opens a dialog to edit details for the specified Car data. If the user
	 * clicks OK, the changes are saved into the provided Car data object and
	 * true is returned.
	 * 
	 * @param carData
	 *            data the Car data object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public void showCarDatadetailsDialog(VehicleDataDetails carData) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/VehicleDataDetails.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Vehicle data details");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(this.dialogStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			theme();
			// scene.getStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());

			// Note - CSS file has to be in src dir
			String css = "file:resources/style/" + theme + ".css";
			scene.getStylesheets().clear();
			scene.getStylesheets().add(css);
			// Set the Car data into the controller.
			VehicleDataDetailsController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:resources/icons/database.png"));

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exception Dialog");
			alert.setHeaderText("Look, an Exception Dialog");
			alert.setContentText("Input/output problem!");

			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();

		}
	}

	/**
	 * @throws ClassNotFoundException
	 */
	public void theme() throws ClassNotFoundException {
		try (Connection conn = connectMySQL();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT Theme FROM settings")) {
			if (rs.next()) {
				theme = rs.getString("Theme");
				if (theme == null || theme.equals("")) {
					theme = "bootstrap3";
					System.out.println("Defualt theme bootstrap3");
				}
			} else {
				theme = "bootstrap3";
				System.out.println("Defualt theme bootstrap3");
				System.out.println("Empty set.");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
	}

}
