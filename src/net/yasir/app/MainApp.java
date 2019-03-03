package net.yasir.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.yasir.view.ExpiryDateStatisticsController;
import net.yasir.view.LoginDialogController;
import net.yasir.view.MainOverviewController;
import net.yasir.view.SettingsDialogController;
import net.yasir.view.VehicleDataEditDialogController;
import net.yasir.view.VehicleLoginInformationEditDialogController;
import net.yasir.view.VehicleLoginInformationManualDialogController;

import static net.yasir.connection.MySQLJDBCDriverConnection.getConnection;

public class MainApp extends Application {
	MainOverviewController controller = new MainOverviewController();
	Stage primaryStage;
	private String theme;
	/**
	 * The data as an observable list of Car data.
	 */
	private ObservableList<VehicleDataDetails> carData = FXCollections.observableArrayList();

	/**
	 * The data as an observable list of vehicle Login Information.
	 */
	private ObservableList<VehicleLoginInformation> vehicleLoginInformation = FXCollections.observableArrayList();

	/**
	 * The data as an observable list of settings.
	 */
	private ObservableList<Settings> settings = FXCollections.observableArrayList();

	/**
	 * Returns the data as an observable list of Car data.
	 * 
	 * @return
	 */
	public ObservableList<VehicleDataDetails> getCarData() {
		return carData;
	}

	/**
	 * Returns the data as an observable list of Settings.
	 * 
	 * @return
	 */
	public ObservableList<Settings> getSettings() {
		return settings;
	}

	@Override
	public void start(Stage primaryStage) throws ClassNotFoundException {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Car Parking Entrance Access Control System");
		// Set the application icon.
		this.primaryStage.getIcons().add(new Image("file:icons/barrier.png"));
		// showMainOverview();
		showLoginDialog();
	}

	/**
	 * Shows the Main overview inside the root layout.
	 *
	 */
	public void showLoginDialog() {
		try {
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/LoginDialog.fxml"));
			// store the root element so that the controllers can use it
			AnchorPane rootElement = loader.load();
			rootElement.getStyleClass().add("background");
			// create and style a scene
			Scene scene = new Scene(rootElement);
			theme();

			// scene.getStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());
			// scene.getStylesheets().add("")
			// Note - CSS file has to be in src dir
			String css = "file:resources/style/" + theme + ".css";
			scene.getStylesheets().clear();
			scene.getStylesheets().add(css);
			// create the stage with the given title and the previously created

			primaryStage.setScene(scene);
			// Set the application icon.

			// Give the controller access to the main app.
			LoginDialogController controller = loader.getController();
			controller.setDialogStage(primaryStage);
			controller.setMainApp(this);
			// show the GUI
			primaryStage.show();

			// set the proper behaviour on closing the application
			// primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
			// public void handle(WindowEvent we) {
			// controller.setClosed();
			// }
			// }));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the Main overview inside the root layout.
	 */
	public void showMainOverviewAdmin() {
		try {
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/MainOverview.fxml"));
			// store the root element so that the controllers can use it
			AnchorPane rootElement = loader.load();
			// create and style a scene
			Scene scene = new Scene(rootElement);
			theme();

			// scene.getStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());
			// scene.getStylesheets().add("")
			// Note - CSS file has to be in src dir
			String css = "file:resources/style/" + theme + ".css";
			scene.getStylesheets().clear();
			scene.getStylesheets().add(css);
			// create the stage with the given title and the previously created

			primaryStage.setScene(scene);
			// Set the application icon.

			// Give the controller access to the main app.
			MainOverviewController controller = loader.getController();
			controller.setDialogStage(primaryStage);
			controller.setMainApp(this);
			// show the GUI
			primaryStage.show();

			// set the proper behaviour on closing the application
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					primaryStage.close();
					System.exit(0);
					controller.setClosed();
				}
			}));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the Main overview inside the root layout.
	 */
	public void showMainOverviewUser() {
		try {
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/MainOverviewUser.fxml"));
			// store the root element so that the controllers can use it
			AnchorPane rootElement = loader.load();
			// create and style a scene
			Scene scene = new Scene(rootElement);
			theme();

			// scene.getStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());
			// scene.getStylesheets().add("")
			// Note - CSS file has to be in src dir
			String css = "file:resources/style/" + theme + ".css";
			scene.getStylesheets().clear();
			scene.getStylesheets().add(css);
			// create the stage with the given title and the previously created

			primaryStage.setScene(scene);
			// Set the application icon.

			// Give the controller access to the main app.
			MainOverviewController controller = loader.getController();
			controller.setDialogStage(primaryStage);
			controller.setMainApp(this);
			// show the GUI
			primaryStage.show();

			// set the proper behaviour on closing the application
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					primaryStage.close();
					System.exit(0);
					controller.setClosed();
				}
			}));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * Opens a dialog to edit details for the specified Car data. If the user
	// * clicks OK, the changes are saved into the provided Car data object and
	// true
	// * is returned.
	// *
	// * @param Car data the Car data object to be edited
	// * @return true if the user clicked OK, false otherwise.
	// * @throws ClassNotFoundException
	// */
	// public void showCarDatadetailsDialog(VehicleDataDetails carData) throws
	// ClassNotFoundException {
	// try {
	// // Load the fxml file and create a new stage for the popup dialog.
	// FXMLLoader loader = new
	// FXMLLoader(getClass().getResource("/com/openalpr/view/VehicleDataDetails.fxml"));
	// AnchorPane page = (AnchorPane) loader.load();
	//
	// // Create the dialog Stage.
	// Stage dialogStage = new Stage();
	// dialogStage.setTitle("Vehicle data details");
	// dialogStage.initModality(Modality.WINDOW_MODAL);
	// dialogStage.initOwner(primaryStage);
	// Scene scene = new Scene(page);
	// dialogStage.setScene(scene);
	// theme();
	// //scene.getStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());
	//
	// // Note - CSS file has to be in src dir
	// String css = "file:resources/style/"+theme+".css";
	// scene.getStylesheets().clear();
	// scene.getStylesheets().add(css);
	// // Set the Car data into the controller.
	// VehicleDataDetailsController controller = loader.getController();
	// controller.setDialogStage(dialogStage);
	// controller.setCarData(carData);
	// controller.loudCarData();
	//
	// // Set the dialog icon.
	// dialogStage.getIcons().add(new Image("file:resources/icons/gate.png"));
	//
	// // Show the dialog and wait until the user closes it
	// dialogStage.showAndWait();
	//
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	//
	// }
	// }
	//

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
	public boolean showCarDataEditDialog(VehicleDataDetails carData) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/VehicleDataEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Vehicle data");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
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

			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:resources/icons/barrier.png"));

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
	 * @param vehicleLoginInformation
	 *            data the Car data object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public boolean showAccessDataEditDialog(VehicleLoginInformation vehicleLoginInformation)
			throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/net/yasir/view/VehicleLoginInformationEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Login information");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			theme();
			// scene.getStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());

			// Note - CSS file has to be in src dir
			String css = "file:resources/style/" + theme + ".css";
			scene.getStylesheets().clear();
			scene.getStylesheets().add(css);
			// Set the Car data into the controller.
			VehicleLoginInformationEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setVehicleLoginInformation(vehicleLoginInformation);

			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:resources/icons/barrier.png"));

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Opens a dialog to add details for the specified Car data. If the user
	 * clicks OK, the changes are saved into the provided Car data object and
	 * true is returned.
	 * 
	 * @param carData
	 *            data the Car data object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public boolean ShowAccessCarManualRegisteredDialog(VehicleDataDetails carData) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/net/yasir/view/VehicleLoginInformationManualRegisteredDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			page.getStyleClass().add("background");
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Vehicle registered");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			theme();
			// scene.geManageUsersDialogtStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());
			// Note - CSS file has to be in src dir
			String css = "file:resources/style/" + theme + ".css";
			scene.getStylesheets().clear();
			scene.getStylesheets().add(css);

			// Set the Car data into the controller.
			VehicleLoginInformationManualDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCarData(carData);

			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:resources/icons/barrier.png"));

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Opens a dialog to add details for the specified Car data. If the user
	 * clicks OK, the changes are saved into the provided Car data object and
	 * true is returned.
	 * 
	 * @param vehicleDataDetails
	 *            the Car data object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public boolean ShowVehicleLoginInformationManualNotRegisterdDialog(VehicleDataDetails vehicleDataDetails)
			throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/net/yasir/view/VehicleLoginInformationManualNotRegisterdDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			page.getStyleClass().add("background");
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Vehicle not registered");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			theme();
			// scene.getStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());
			// Note - CSS file has to be in src dir
			String css = "file:resources/style/" + theme + ".css";
			scene.getStylesheets().clear();
			scene.getStylesheets().add(css);

			// Set the Car data into the controller.
			VehicleLoginInformationManualDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCarData(vehicleDataDetails);

			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:resources/icons/barrier.png"));

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
	 * @param settings
	 *            the settings object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public boolean showSettingsDialog(Settings settings) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/SettingsDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			page.getStyleClass().add("background");

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Settings");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			theme();
			// scene.getStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());

			// Note - CSS file has to be in src dir
			String css = "file:resources/style/" + theme + ".css";
			scene.getStylesheets().clear();
			scene.getStylesheets().add(css);
			// Set the Settings into the controller.
			SettingsDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setSettings(settings);

			SettingsDialogController controller2 = loader.getController();
			controller2.setMainApp(this);
			controller2.themeComboBox.getSelectionModel().select(theme);
			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:icons/settings.png"));

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Opens a dialog to show birthday statistics.
	 * 
	 * @throws ClassNotFoundException
	 */
	public void showExpiryDateStatistics() throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/ExpiryDateStatistics.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Expiry date Statistics");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			theme();
			// scene.getStylesheets().add(getClass().getResource("/com/openalpr/view/"+theme+".css").toExternalForm());

			// Note - CSS file has to be in src dir
			String css = "file:resources/style/" + theme + ".css";
			scene.getStylesheets().clear();
			scene.getStylesheets().add(css);

			// Set the access data into the controller.
			ExpiryDateStatisticsController controller = loader.getController();
			controller.setCarData(carData);

			// Set the dialog icon.
			dialogStage.getIcons().add(new Image("file:resources/icons/barrier.png"));

			// Show the dialog and wait until the user closes it
			dialogStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	/**
	 * Connect to the sql.sqlite database
	 * 
	 * @return the Connection object
	 * @throws ClassNotFoundException
	 */
	private static Connection connect() throws ClassNotFoundException {
		// MySQL connection string
		return getConnection();
	}

	/**
	 * @throws ClassNotFoundException
	 */
	public void theme() throws ClassNotFoundException {
		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT Theme FROM settings")) {
			if (rs.next()) {
				theme = rs.getString("Theme");
				if (theme == null || theme.equals("")) {
					theme = "bootstrap3";
					System.out.println("Defualt theme bootstrap3");
				}
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
	}

	/**
	 * For launching the application...
	 * 
	 * @param args
	 *            optional params
	 */

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		launch(args);

	}
}
