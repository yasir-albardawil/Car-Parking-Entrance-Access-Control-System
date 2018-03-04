package net.yasir.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.yasir.app.MainApp;
import net.yasir.app.VehicleDataDetails;
import net.yasir.connection.MySQLJDBCDriverConnection;
import net.yasir.connection.SQLiteJDBCDriverConnection;
import net.yasir.utils.DateUtil;

public class VehicleDataDetailsController {
	Stage primaryStage;

	@FXML
	private TextField filterField;
	@FXML
	public TableView<VehicleDataDetails> CarDataTableView;
	@FXML
	private TableColumn<VehicleDataDetails, String> idNoCarDataColumn;
	@FXML
	private TableColumn<VehicleDataDetails, String> ownerNameCarDataColumn;
	@FXML
	private TableColumn<VehicleDataDetails, String> plateNumberCarDataColumn;

	@FXML
	private Label idNoLabel;
	@FXML
	private Label ownerNameLabel;
	@FXML
	private Label plateNumberLabel;
	@FXML
	private Label carBrandLabel;
	@FXML
	private Label carModelLabel;
	@FXML
	private Label vinLabel;
	@FXML
	private Label manufacturingYearLabel;
	@FXML
	private Label colorLabel;
	@FXML
	private Label expiryDateLabel;

	@FXML
	private Button deleteButton;

	private Stage dialogStage;
	private VehicleDataDetails vehicleDataDetails;
	private boolean okClicked = false;
	private SortedList<VehicleDataDetails> sortedData;
	private MainOverviewController controller;
	String theme;

	// Reference to the main application.
	private MainApp mainApp;

	public Stage getStage() {
		return primaryStage;
	}

	public void setStage(Stage stage) {
		this.primaryStage = stage;
	}

	private ObservableList<VehicleDataDetails> masterData = FXCollections.observableArrayList();

	/**
	 * Sets the carData to be edited in the dialog.
	 * 
	 * @param vehicleDataDetails
	 * @throws ClassNotFoundException
	 */
	public void setVehicleDataDetails(VehicleDataDetails vehicleDataDetails) throws ClassNotFoundException {
		this.vehicleDataDetails = vehicleDataDetails;

		idNoCarDataColumn.setText(vehicleDataDetails.getIdNo());
		ownerNameCarDataColumn.setText(vehicleDataDetails.getOwnerName());
		plateNumberCarDataColumn.setText(vehicleDataDetails.getPlateNumber());
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void initialize() throws ClassNotFoundException {

		// Initialize the person table with the two columns.
		// IDCarDataColumn.setCellValueFactory(cellData ->
		// cellData.getValue().getIdProperty().asObject());
		idNoCarDataColumn.setCellValueFactory(e -> e.getValue().getIdNoProperty());
		ownerNameCarDataColumn.setCellValueFactory(e -> e.getValue().getPlateNumberProperty());
		plateNumberCarDataColumn.setCellValueFactory(cellData -> cellData.getValue().getPlateNumberProperty());

		// Clear car data details.
		showCarDataDetails(null);

		// Add observable list data to the table
		CarDataTableView.setItems(masterData);

		// Listen for selection changes and show the Car data details when
		// changed.
		CarDataTableView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showCarDataDetails(newValue));

		// TODO Auto-generated method stub
		CarDataTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
					VehicleDataDetails selectedCarData = CarDataTableView.getSelectionModel().getSelectedItem();
					if (selectedCarData != null) {
						try {
							boolean okClicked = showCarDataEditDialog(selectedCarData);
							if (okClicked) {
								showCarDataDetails(selectedCarData);
							}
						} catch (ClassNotFoundException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						;
					} else {
						// Nothing selected.
						Alert alert = new Alert(AlertType.WARNING);
						alert.initOwner(VehicleDataDetailsController.this.dialogStage);
						alert.setTitle("No Selection");
						alert.setHeaderText("No Car data Selected");
						alert.setContentText("Please select a Car data in the table.");

						alert.showAndWait();
					}
				}
			}
		});

		loudCarData();

		// 1. Wrap the ObservableList in a FilteredList (initially display all
		// data).
		FilteredList<VehicleDataDetails> filteredData = new FilteredList<>(masterData, p -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(carData -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter
				// text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (carData.getIdNo().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches ID number.
				} else if (carData.getOwnerName().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches Owner name.
				} else if (carData.getPlateNumber().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches Plate number.
				}
				return false; // Does not match.
			});
		});

		// 3. Wrap the FilteredList in a SortedList.
		sortedData = new SortedList<VehicleDataDetails>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(CarDataTableView.comparatorProperty());
		// 5. Add sorted (and filtered) data to the table.
		CarDataTableView.setItems(sortedData);
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
	 * Connect to the sql.sqlite database
	 * 
	 * @return the Connection object
	 * @throws ClassNotFoundException
	 */
	private static Connection connect() throws ClassNotFoundException {
		// MySQL connection string
		final String DB_URL = "jdbc:mysql://localhost/cpeacs_database";
		final String USER = "root";
		final String PASS = "";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	/**
	 * Fills all text fields to show details about the person. If the specified
	 * person is null, all text fields are cleared.
	 * 
	 * @param person
	 *            the person or null
	 */
	private void showCarDataDetails(VehicleDataDetails carData) {
		if (carData != null) {
			// Fill the labels with info from the person object.
			idNoLabel.setText(carData.getIdNo());
			ownerNameLabel.setText(carData.getOwnerName());
			plateNumberLabel.setText(carData.getPlateNumber());
			carBrandLabel.setText(carData.getCarBrand());
			carModelLabel.setText(carData.getCarModel());
			vinLabel.setText(carData.getVin());
			manufacturingYearLabel.setText(carData.getManufacturingYear());
			colorLabel.setText(carData.getColor());
			expiryDateLabel.setText(DateUtil.format(carData.getExpiryDate()));
		} else {
			// car date is null, remove all the text.
			// Fill the labels with info from the person object.
			idNoLabel.setText("");
			ownerNameLabel.setText("");
			plateNumberLabel.setText("");
			carBrandLabel.setText("");
			carModelLabel.setText("");
			vinLabel.setText("");
			manufacturingYearLabel.setText("");
			colorLabel.setText("");
			expiryDateLabel.setText("");
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected person.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleEditCarData() throws ClassNotFoundException {
		VehicleDataDetails selectedCarData = CarDataTableView.getSelectionModel().getSelectedItem();
		if (selectedCarData != null) {
			boolean okClicked = showCarDataEditDialog(selectedCarData);
			if (okClicked) {
				showCarDataDetails(selectedCarData);
			}
		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(this.dialogStage);
			alert.setTitle("No Selection");
			alert.setHeaderText("No Person Selected");
			alert.setContentText("Please select a Car data in the table.");

			alert.showAndWait();
		}
	}

	/**
	 * Called when the user clicks on the delete button.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleDeleteCarData() throws ClassNotFoundException {
		VehicleDataDetails selectedItems = CarDataTableView.getSelectionModel().getSelectedItem();
		if (selectedItems != null) {
			StringProperty carPlateCellValue = CarDataTableView.getSelectionModel().getSelectedItem()
					.getPlateNumberProperty();
			String data = carPlateCellValue.getValue();
			String deletetStmt = "DELETE FROM car_data WHERE PlateNumber= '" + data + "' ";
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initOwner(this.dialogStage);
			alert.setTitle("delete car plate " + selectedItems.getPlateNumber());
			alert.setHeaderText("Look, you will delete car plate " + selectedItems.getPlateNumber());
			alert.setContentText("Are you sure you wnat to delete car plate " + selectedItems.getPlateNumber() + "?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				masterData.removeAll(selectedItems);
				MySQLJDBCDriverConnection.delete(deletetStmt);
				SQLiteJDBCDriverConnection.delete(deletetStmt);

			}
		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(this.dialogStage);
			alert.setTitle("No Selection");
			alert.setHeaderText("No Car data Selected");
			alert.setContentText("Please select a Car data in the table.");
			alert.showAndWait();
		}
	}

	/**
	 * Called when the user clicks the new button. Opens a dialog to add details
	 * for a new Car data.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleNewCarData() throws ClassNotFoundException {
		VehicleDataDetails tempCarPlate = new VehicleDataDetails();
		boolean okClicked = ShowCarDataAddDialog(tempCarPlate);
		if (okClicked) {
			masterData.add(tempCarPlate);

		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	public void handleRefresh() throws ClassNotFoundException {
		// Get data from MySQL database
		// this.IDCarDataColumn.setCellValueFactory(e->e.getValue().getIdProperty().asObject());
		this.idNoCarDataColumn.setCellValueFactory(e -> e.getValue().getIdNoProperty());
		this.ownerNameCarDataColumn.setCellValueFactory(e -> e.getValue().getOwnerNameProperty());
		this.plateNumberCarDataColumn.setCellValueFactory(e -> e.getValue().getPlateNumberProperty());
		try (Connection conn = connectMySQL();

				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM Car_data")) {

			// loop through the result set
			CarDataTableView.getItems().clear();
			while (rs.next()) {

				CarDataTableView.getItems()
						.add(new VehicleDataDetails(rs.getInt("ID"), rs.getString("idNo"), rs.getString("OwnerName"),
								rs.getString("PlateNumber"), rs.getString("CarBrand"), rs.getString("CarModel"),
								rs.getString("VIN"), rs.getString("ManufacturingYear"), rs.getString("Color"),
								DateUtil.parse(rs.getString("expiryDate"))));

			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
	}

	@FXML
	private void handleExitCarPlate() {
		System.exit(0);
	}

	/**
	 * Connect to the sql.sqlite database
	 * 
	 * @return the Connection object
	 * @throws ClassNotFoundException
	 */
	private static Connection connectMySQL() throws ClassNotFoundException {
		// MySQL connection string
		final String DB_URL = "jdbc:mysql://localhost/cpeacs_database";
		final String USER = "root";
		final String PASS = "";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	/**
	 * Connect to the sql.sqlite database
	 * 
	 * @return the Connection object
	 */
	private static Connection connectsqlite() {
		// SQLite connection string
		String url = "jdbc:sqlite:db.sqlite";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public void loudCarData() throws ClassNotFoundException {
		// Get data from MySQL database
		// this.IDCarDataColumn.setCellValueFactory(e->e.getValue().getIdProperty().asObject());
		this.idNoCarDataColumn.setCellValueFactory(e -> e.getValue().getIdNoProperty());
		this.ownerNameCarDataColumn.setCellValueFactory(e -> e.getValue().getOwnerNameProperty());
		this.plateNumberCarDataColumn.setCellValueFactory(e -> e.getValue().getPlateNumberProperty());
		try (Connection conn = connectMySQL();

				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM car_data")) {
			System.out.println("From MySQL Database: \n");
			// loop through the result set
			while (rs.next()) {
				CarDataTableView.getItems()
						.add(new VehicleDataDetails(rs.getInt("ID"), rs.getString("idNo"), rs.getString("OwnerName"),
								rs.getString("PlateNumber"), rs.getString("CarBrand"), rs.getString("CarModel"),
								rs.getString("VIN"), rs.getString("ManufacturingYear"), rs.getString("Color"),
								DateUtil.parse(rs.getString("expiryDate"))));

				//
				// System.out.println("ID: " + rs.getInt("ID") + "\t" +
				// "Car name: " + rs.getString("CarName") + "\t" +
				// "Plate number: " + rs.getString("PlateNumber") + "\t" +
				// "Expire date: " +
				// DateUtil.format(DateUtil.parse(rs.getString("ExpiryDate"))));
				//
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}

		//
		// //this.plateNumberCarDataColumn.setCellValueFactory(e->e.getValue().getPlateNumberProperty());
		// try (Connection conn = connectsqlite();
		//
		// Statement stmt = conn.createStatement();
		// ResultSet rs = stmt.executeQuery("SELECT * FROM car_data")){
		// System.out.println("From SQLite Database: \n");
		// // loop through the result set
		// while (rs.next()) {
		//// CarDataTableView.getItems().add(new CarData(
		//// rs.getInt("ID"),
		//// rs.getString("CarName"),
		//// rs.getString("PlateNumber")));
		//
		// System.out.println("ID: " + rs.getInt("ID") + "\t" +
		// "Car name: " + rs.getString("CarName") + "\t" +
		// "Plate number: " + rs.getString("PlateNumber"));
		//
		// }
		// stmt.close();
		// rs.close();
		// } catch (SQLException e) {
		// System.out.println(e.getMessage());
		// System.out.println("Not allowed");
		// }
	}

	/**
	 * Opens the birthday statistics.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleShowExpiryDateStatistics() throws ClassNotFoundException {
		mainApp.showExpiryDateStatistics();
	}

	/**
	 * Opens a dialog to add details for the specified Car data. If the user
	 * clicks OK, the changes are saved into the provided Car data object and
	 * true is returned.
	 * 
	 * @param Car
	 *            data the Car data object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public boolean ShowCarDataAddDialog(VehicleDataDetails carData) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/VehicleDataAddDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			page.getStyleClass().add("background");
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add new Vehicle data");
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
			VehicleDataAddDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCarData(carData);

			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:resources/icons/gate.png"));

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Opens a dialog to edit details for the specified Car data. If the user
	 * clicks OK, the changes are saved into the provided Car data object and
	 * true is returned.
	 * 
	 * @param Car
	 *            data the Car data object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public boolean showCarDataEditDialog(VehicleDataDetails carData) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/VehicleDataEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Vehicle data");
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
			VehicleDataEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCarData(carData);
			controller.setDataDetailsController(this);

			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:resources/icons/gate.png"));

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @throws ClassNotFoundException
	 */
	public void theme() throws ClassNotFoundException {
		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT Theme FROM settings WHERE ID=1")) {
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

	/**
	 * Returns the data as an observable list of Car data.
	 * 
	 * @return
	 */
	public ObservableList<VehicleDataDetails> getCarData() {
		return masterData;
	}

}