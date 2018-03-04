package net.yasir.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javafx.beans.property.IntegerProperty;
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
import net.yasir.app.Login;
import net.yasir.app.MainApp;
import net.yasir.connection.MySQLJDBCDriverConnection;
import net.yasir.connection.SQLiteJDBCDriverConnection;

public class ManageUsersDialogController {
	Stage primaryStage;

	@FXML
	private TextField filterField;
	@FXML
	public TableView<Login> usersTableView;
	@FXML
	private TableColumn<Login, Integer> IDColumn;
	@FXML
	private TableColumn<Login, String> usernameColumn;
	@FXML
	private TableColumn<Login, String> passwordColumn;
	@FXML
	private TableColumn<Login, String> roleColumn;

	@FXML
	private Label usernameLabel;
	@FXML
	private Label passwordLabel;
	@FXML
	private Label roleLabel;

	@FXML
	private Button deleteButton;

	private Stage dialogStage;
	private Login login;
	private boolean okClicked = false;
	private SortedList<Login> sortedData;
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

	private ObservableList<Login> masterData = FXCollections.observableArrayList();

	/**
	 * Sets the carData to be edited in the dialog.
	 * 
	 * @param carData
	 * @throws ClassNotFoundException
	 */
	public void setLogin(Login login) throws ClassNotFoundException {
		this.login = login;

		usernameColumn.setText(login.getUsername());
		passwordColumn.setText(login.getPassword());
		roleColumn.setText(login.getRole());
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
		IDColumn.setCellValueFactory(e -> e.getValue().getIDProperty().asObject());
		usernameColumn.setCellValueFactory(e -> e.getValue().getUsernameProperty());
		passwordColumn.setCellValueFactory(e -> e.getValue().getPasswordProperty());
		roleColumn.setCellValueFactory(cellData -> cellData.getValue().getRoleProperty());

		// Clear car data details.
		showUsersDetails(null);

		// Add observable list data to the table
		usersTableView.setItems(masterData);

		// Listen for selection changes and show the Car data details when
		// changed.
		usersTableView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showUsersDetails(newValue));

		// TODO Auto-generated method stub
		usersTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
					Login selectedCarData = usersTableView.getSelectionModel().getSelectedItem();
					if (selectedCarData != null) {
						try {
							boolean okClicked = showUserEditDialog(selectedCarData);
							if (okClicked) {
								showUsersDetails(selectedCarData);
							}
						} catch (ClassNotFoundException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						;
					} else {
						// Nothing selected.
						Alert alert = new Alert(AlertType.WARNING);
						alert.initOwner(ManageUsersDialogController.this.dialogStage);
						alert.setTitle("No Selection");
						alert.setHeaderText("No Car data Selected");
						alert.setContentText("Please select a Car data in the table.");

						alert.showAndWait();
					}
				}
			}
		});

		loudUsers();

		// 1. Wrap the ObservableList in a FilteredList (initially display all
		// data).
		FilteredList<Login> filteredData = new FilteredList<>(masterData, p -> true);

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

				if (carData.getUsername().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches ID number.
				} else if (carData.getPassword().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches Owner name.
				} else if (carData.getRole().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches Plate number.
				}
				return false; // Does not match.
			});
		});

		// 3. Wrap the FilteredList in a SortedList.
		sortedData = new SortedList<Login>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(usersTableView.comparatorProperty());
		// 5. Add sorted (and filtered) data to the table.
		usersTableView.setItems(sortedData);
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
	private void showUsersDetails(Login login) {
		if (login != null) {
			// Fill the labels with info from the person object.
			usernameLabel.setText(login.getUsername());
			passwordLabel.setText(login.getPassword());
			roleLabel.setText(login.getRole());
		} else {
			// car date is null, remove all the text.
			// Fill the labels with info from the person object.
			usernameLabel.setText("");
			passwordLabel.setText("");
			roleLabel.setText("");
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected person.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleEditUser() throws ClassNotFoundException {
		Login selecteduser = usersTableView.getSelectionModel().getSelectedItem();
		if (selecteduser != null) {
			boolean okClicked = showUserEditDialog(selecteduser);
			if (okClicked) {
				showUserEditDialog(selecteduser);
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
	private void handleDeleteUser() throws ClassNotFoundException {
		Login selectedItems = usersTableView.getSelectionModel().getSelectedItem();
		if (selectedItems != null) {
			IntegerProperty userCellValue = usersTableView.getSelectionModel().getSelectedItem().getIDProperty();
			int data = userCellValue.getValue();
			String deletetStmt = "DELETE FROM login WHERE ID= '" + data + "' ";
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initOwner(this.dialogStage);
			alert.setTitle("delete user " + selectedItems.getUsername());
			alert.setHeaderText("Look, you will delete user " + selectedItems.getUsername());
			alert.setContentText("Are you sure you wnat to delete user " + selectedItems.getUsername() + "?");

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
	private void handleAddUser() throws ClassNotFoundException {
		Login login = new Login();
		boolean okClicked = ShowUserAddDialog(login);
		if (okClicked) {
			masterData.add(login);
			// loudUsers();
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	// public void handleRefresh() throws ClassNotFoundException {
	// // Get data from MySQL database
	// //this.IDCarDataColumn.setCellValueFactory(e->e.getValue().getIdProperty().asObject());
	// this.usernameColumn.setCellValueFactory(e->e.getValue().getIdNoProperty());
	// this.passwordColumn.setCellValueFactory(e->e.getValue().getOwnerNameProperty());
	// this.roleColumn.setCellValueFactory(e->e.getValue().getPlateNumberProperty());
	// try (Connection conn = connectMySQL();
	//
	// Statement stmt = conn.createStatement();
	// ResultSet rs = stmt.executeQuery("SELECT * FROM Car_data")){
	//
	// // loop through the result set
	// CarDataTableView.getItems().clear();
	// while (rs.next()) {
	//
	// CarDataTableView.getItems().add(new VehicleDataDetails(
	// rs.getInt("ID"),
	// rs.getString("idNo"),
	// rs.getString("OwnerName"),
	// rs.getString("PlateNumber"),
	// rs.getString("CarBrand"),
	// rs.getString("CarModel"),
	// rs.getString("VIN"),
	// rs.getString("ManufacturingYear"),
	// rs.getString("Color"),
	// DateUtil.parse(rs.getString("expiryDate"))));
	//
	// }
	// stmt.close();
	// rs.close();
	// } catch (SQLException e) {
	// System.out.println(e.getMessage());
	// System.out.println("Not allowed");
	// }
	// }

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

	public void loudUsers() throws ClassNotFoundException {
		// Get data from MySQL database
		// this.IDCarDataColumn.setCellValueFactory(e->e.getValue().getIdProperty().asObject());
		this.usernameColumn.setCellValueFactory(e -> e.getValue().getUsernameProperty());
		this.passwordColumn.setCellValueFactory(e -> e.getValue().getPasswordProperty());
		this.roleColumn.setCellValueFactory(e -> e.getValue().getRoleProperty());
		try (Connection conn = connectMySQL();

				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM login")) {
			System.out.println("From MySQL Database: \n");
			// loop through the result set
			while (rs.next()) {
				usersTableView.getItems().add(new Login(rs.getInt("ID"), rs.getString("Username"),
						rs.getString("Password"), rs.getString("Role")));

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
	public boolean ShowUserAddDialog(Login login) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/ManageUsersAddDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			page.getStyleClass().add("background");
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Add new user data");
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
			ManageUsersAddDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUsers(login);

			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:resources/icons/database.png"));

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
	public boolean showUserEditDialog(Login login) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/ManageUsersEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit User data");
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
			ManageUsersEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setUsers(login);
			// controller.setDataDetailsController(this);

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
	public ObservableList<Login> getCarData() {
		return masterData;
	}

}