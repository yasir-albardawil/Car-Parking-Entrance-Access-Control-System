package net.yasir.view;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import com.fazecast.jSerialComm.SerialPort;
import com.openalpr.jni.Alpr;
import com.openalpr.jni.AlprPlate;
import com.openalpr.jni.AlprPlateResult;
import com.openalpr.jni.AlprResults;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.yasir.app.Login;
import net.yasir.app.MainApp;
import net.yasir.app.Settings;
import net.yasir.app.VehicleDataDetails;
import net.yasir.app.VehicleLoginInformation;
import net.yasir.connection.MySQLJDBCDriverConnection;
import net.yasir.connection.SQLiteJDBCDriverConnection;
import net.yasir.utils.DateUtil;
import net.yasir.utils.Utils;

/**
 * The controller for our application, where the application logic is
 * implemented. It handles the button for starting/stopping the camera and the
 * acquired video stream.
 *
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @author <a href="http://max-z.de">Maximilian Zuleger</a> (minor fixes)
 * @version 2.0 (2016-09-17)
 * @since 1.0 (2013-10-20)
 *
 */
public class MainOverviewController {

	// the FXML button
	@FXML
	private Button button;
	// the FXML image view
	@FXML
	private ImageView currentFrame;

	@FXML
	private Label lblShow;

	@FXML
	private TableView<VehicleLoginInformation> vehicleLoginInformationTableView;
	@FXML
	private TableColumn<VehicleLoginInformation, Integer> noColumn;
	@FXML
	private TableColumn<VehicleLoginInformation, String> plateNumberColumn;
	@FXML
	private TableColumn<VehicleLoginInformation, Float> confidenceColumn;
	@FXML
	private TableColumn<VehicleLoginInformation, ImageView> statusColumn;
	@FXML
	private TableColumn<VehicleLoginInformation, String> dateColumn;
	@FXML
	private TableColumn<VehicleLoginInformation, ImageView> imageColumn;

	@FXML
	private ImageView gateCheck;
	@FXML
	private Button btnAdd;
	@FXML
	private TextField carPlateField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField colorField;
	@FXML
	private Button addNew;
	@FXML
	private TextField portField;
	@FXML
	private Label comPortLabel;
	@FXML
	private Button bttnCeckPort;
	@FXML
	private Button bttnShow;
	@FXML
	private Button bttnRefresh;
	@FXML
	private TextField filterField;
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
	private boolean cameraActive = false;
	// the id of the camera to be used
	private static int cameraId = 1;
	private static String username;
	private static String passowrd;
	private static String ipCameraURL;
	private byte[] imagebyte;
	private InputStream ImageInputStream;
	public static SerialPort arduino;

	public static SerialPort getArduino() {
		return arduino;
	}

	public static void setArduino(SerialPort arduino) {
		MainOverviewController.arduino = arduino;
	}

	private boolean checkModeMySQL;
	private boolean checkModeSQLite;
	private Stage dialogStage;

	private AlprPlate plate;
	private String line = null;
	private BufferedImage image = null;
	// Reference to the main application.
	private MainApp mainApp;

	private SortedList<VehicleLoginInformation> sortedData;

	private int count = 1;
	private Settings settings;

	private VehicleDataDetails data;
	private String theme;

	public Stage getStage() {
		return dialogStage;
	}

	public void setStage(Stage stage) {
		this.dialogStage = stage;

		// Set the dialog icon.
	}

	public byte[] getImagebyte() {
		return imagebyte;
	}

	private ObservableList<VehicleDataDetails> carData = FXCollections.observableArrayList();
	private ObservableList<VehicleLoginInformation> vehicleLoginInformation = FXCollections.observableArrayList();

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public MainOverviewController() {
		// TODO Auto-generated constructor stub

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void initialize() throws ClassNotFoundException {

		getComPort();
		vehicleLoginInformationTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// Add observable list data to the table
		vehicleLoginInformationTableView.setItems(vehicleLoginInformation);
		// TODO Auto-generated method stub
		vehicleLoginInformationTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
					VehicleLoginInformation selectedAccessData = vehicleLoginInformationTableView.getSelectionModel()
							.getSelectedItem();
					if (selectedAccessData != null) {
						boolean okClicked = false;
						try {
							okClicked = mainApp.showAccessDataEditDialog(selectedAccessData);
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						System.out.println(okClicked);
					} else {
						// Nothing selected.
						Alert alert = new Alert(AlertType.WARNING);
						alert.initOwner(mainApp.getPrimaryStage());
						alert.setTitle("No Selection");
						alert.setHeaderText("No vehicle login information Selected");
						alert.setContentText("Please select an vehicle login information in the table.");

						alert.showAndWait();
					}
				}
			}
		});

		loudVehicleLoginInformation();

		// 1. Wrap the ObservableList in a FilteredList (initially display all
		// data).
		FilteredList<VehicleLoginInformation> filteredData = new FilteredList<>(vehicleLoginInformation, p -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(accessCar -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter
				// text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (accessCar.getPlateNumber().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches ID number.
				} else if (String.valueOf(accessCar.getConfidence()).toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches Confidence.
				} else if (accessCar.getDate().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches Date.
				}
				return false; // Does not match.
			});
		});

		// 3. Wrap the FilteredList in a SortedList.
		sortedData = new SortedList<VehicleLoginInformation>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(vehicleLoginInformationTableView.comparatorProperty());
		// 5. Add sorted (and filtered) data to the table.
		vehicleLoginInformationTableView.setItems(sortedData);

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

		// Add observable list data to the table
		// CarDataTableView.setItems(mainApp.getCarData());
	}

	// /**
	// * Fills all text fields to show details about the person.
	// * If the specified person is null, all text fields are cleared.
	// *
	// * @param person the person or null
	// */
	// private void showCarDataDetails(CarData carData) {
	// if (carData != null) {
	// // Fill the labels with info from the person object.
	// idNoLabel.setText(carData.getIdNo());
	// ownerNameLabel.setText(carData.getOwnerName());
	// plateNumberLabel.setText(carData.getPlateNumber());
	// carBrandLabel.setText(carData.getCarBrand());
	// carModelLabel.setText(carData.getCarModel());
	// structureNumberLabel.setText(carData.getStructureNo());
	// manufacturingYearLabel.setText(carData.getManufacturingYear());
	// colorLabel.setText(carData.getColor());
	// expiryDateLabel.setText(DateUtil.format(carData.getExpiryDate()));
	// } else {
	// // car date is null, remove all the text.
	// // Fill the labels with info from the person object.
	// idNoLabel.setText("");
	// ownerNameLabel.setText("");
	// plateNumberLabel.setText("");
	// carBrandLabel.setText("");
	// carModelLabel.setText("");
	// structureNumberLabel.setText("");
	// manufacturingYearLabel.setText("");
	// colorLabel.setText("");
	// expiryDateLabel.setText("");
	// }
	// }
	//
	//

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

	public void loudVehicleLoginInformation() throws ClassNotFoundException {
		// Get data from MySQL database
		this.plateNumberColumn.setCellValueFactory(e -> e.getValue().plateNumberProperty());
		this.confidenceColumn.setCellValueFactory(e -> e.getValue().plateConfidence().asObject());
		this.statusColumn.setCellValueFactory(e -> e.getValue().statusObjectProperty());
		this.dateColumn.setCellValueFactory(e -> e.getValue().dateProperty());
		this.imageColumn.setCellValueFactory(e -> e.getValue().imageProperty());
		try (Connection conn = connectMySQL();

				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM vehicle_login_information")) {
			System.out.println("From MySQL Database vehicle login information: \n");
			// loop through the result set
			while (rs.next()) {
				vehicleLoginInformationTableView.getItems()
						.add(new VehicleLoginInformation(rs.getInt("ID"), rs.getString("PlateNumber"),
								rs.getFloat("Confidence"), new ImageView(new Image(rs.getBinaryStream("Status"))),
								rs.getString("Date"),
								new ImageView(new Image(rs.getBinaryStream("Image"), 100, 60, false, false))));

				System.out.println("ID: " + rs.getInt("ID") + "\t" + "Plate number: " + rs.getString("PlateNumber")
						+ "\t" + "Confidence: " + rs.getFloat("Confidence") + "\t" + "Date" + rs.getString("Date"));

			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}

	}

	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event
	 *            the push button event
	 */
	public void m() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		t.start();
	}

	@FXML
	protected void startCamera(ActionEvent event) throws ClassNotFoundException {
		checkModeMySQL = MySQLJDBCDriverConnection.checkMode("SELECT Mode FROM settings WHERE ID=1;");
		checkModeSQLite = SQLiteJDBCDriverConnection.checkMode("SELECT Mode FROM settings WHERE ID=1;");
		System.out.println("Mode Status: " + checkModeMySQL + " And " + checkModeSQLite);
		if (arduino != null) {
			if (arduino.openPort() == true) {
				if (!this.cameraActive) {
					// start the video capture
					try (Connection conn = connectMySQL();
							Statement stmt = conn.createStatement();
							ResultSet rs = stmt
									.executeQuery("SELECT IPAddress, Username, Password, COMPort FROM settings")) {
						if (rs.next()) {
							ipCameraURL = rs.getString("IPAddress");
							username = rs.getString("Username");
							passowrd = rs.getString("Password");
						} else {
							System.out.println("Empty set.");
						}

					} catch (SQLException e) {
						System.out.println(e.getMessage());
						System.out.println("Not allowed");
					}

					try {

					} catch (Exception e) {
						// TODO: handle exception
					}

					if (ipCameraURL.contains("http://")) {
						String ipAddressReplace = ipCameraURL.replace("http://", "");
						this.capture.open("http://" + username + ":" + passowrd + "@" + ipAddressReplace);
					} else {
						// this.capture.open(0);
						this.capture.open("http://" + username + ":" + passowrd + "@" + ipCameraURL);
					}

					// is the video stream available?
					if (this.capture.isOpened()) {
						this.cameraActive = true;

						// grab a frame every 33 ms (30 frames/sec)
						Runnable frameGrabber = new Runnable() {

							@Override
							public void run() {
								// effectively grab and process a single frame
								Mat frame = grabFrame();
								// convert and show the frame
								Image imageToShow = Utils.mat2Image(frame);
								updateImageView(currentFrame, imageToShow);
							}
						};

						this.timer = Executors.newSingleThreadScheduledExecutor();
						this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

						// update the button content
						this.button.setText("Stop System");

					} else {
						// log the error
						Alert alert = new Alert(AlertType.ERROR);
						// alert.initOwner((Stage)addLView.getScene().getWindow());
						alert.setTitle("Error");
						alert.setHeaderText("Error");
						alert.setContentText("Impossible to open the camera connection...\n");

						alert.showAndWait();
						System.err.println("Impossible to open the camera connection...");
					}
				} else {
					// the camera is not active at this point
					this.cameraActive = false;
					// update again the button content

					this.button.setText("Start System");
					// stop the timer
					this.stopAcquisition();

					// this.currentFrame.setImage(new
					// javafx.scene.image.Image(getClass().getResourceAsStream("src/com/openalpr/images/video_stopped.png")));
				}
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Error");
				// Set the dialog icon.
				alert.initOwner(dialogStage);
				alert.setContentText("Connect the sensor\n");

				alert.showAndWait();
				System.err.println("Connect the sensor");
			}

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error");
			alert.initOwner(dialogStage);
			alert.setContentText("Connect the sensor\n");

			alert.showAndWait();
			System.err.println("Connect the sensor");
		}

	}

	/**
	 * Get a frame from the opened video stream (if any)
	 *
	 * @return the {@link Mat} to show
	 */
	private Mat grabFrame() {

		// init everything
		Mat frame = new Mat();

		// check if the capture is open
		if (this.capture.isOpened()) {

			try {
				// read the current frame
				this.capture.read(frame);

				image = this.MatToBufferedImage(frame);
				byte[] imagebyte = this.toByteArray(image);
				ImageInputStream = new ByteArrayInputStream(imagebyte);
				// OpenALPR
				if (checkModeMySQL || checkModeSQLite) {
					try {
						boolean pirSensor = PIRSensor();

						if (pirSensor == true) {

							String country = "sa", configfile = "src/openalpr.conf",
									runtimeDataDir = "src/runtime_data";
							Alpr alpr = new Alpr(country, configfile, runtimeDataDir);

							alpr.setTopN(5);
							alpr.setDefaultRegion("sa");

							AlprResults resultCamera = alpr.recognize(imagebyte);
							if (resultCamera.getPlates().size() > 0) {
								System.out.println("OpenALPR Version: " + alpr.getVersion());
								System.out.println("Image Size: " + resultCamera.getImgWidth() + "x"
										+ resultCamera.getImgHeight());
								System.out
										.println("Processing Time: " + resultCamera.getTotalProcessingTimeMs() + " ms");
								System.out.println("Found " + resultCamera.getPlates().size() + " results");
								System.out.format("  %-15s%-8s\n", "Plate Number", "Confidence");
							}

							for (AlprPlateResult result : resultCamera.getPlates()) {

								for (AlprPlate plate : result.getTopNPlates()) {

									// if (plate.isMatchesTemplate())
									// System.out.print(" * ");
									// else
									// System.out.print(" - ");
									// System.out.println("Best
									// plate"+result.getBestPlate().getCharacters());

									DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
									LocalDateTime now = LocalDateTime.now();

									System.out.format("%-15s%-8f\n", plate.getCharacters(),
											plate.getOverallConfidence());
									// this.noColumn.setCellValueFactory(e->e.getValue().getNo().asObject());
									this.plateNumberColumn.setCellValueFactory(e -> e.getValue().plateNumberProperty());
									this.confidenceColumn
											.setCellValueFactory(e -> e.getValue().plateConfidence().asObject());
									this.statusColumn.setCellValueFactory(e -> e.getValue().statusObjectProperty());
									this.dateColumn.setCellValueFactory(e -> e.getValue().dateProperty());
									this.imageColumn.setCellValueFactory(e -> e.getValue().imageProperty());
									String sql = "SELECT * FROM Car_data WHERE PlateNumber= '" + plate.getCharacters()
											+ "' ";
									if (plate.getOverallConfidence() > 0) {
										if (plate.isMatchesTemplate()) {
											boolean checkMySQL = MySQLJDBCDriverConnection.isAllowed(sql);
											boolean checkSQLite = SQLiteJDBCDriverConnection.isAllowed(sql);
											if (checkMySQL == true || checkSQLite == true) {
												// BufferedImage carPlate=
												// this.MatToBufferedImage(frame);
												System.out.println("Gate open");
												MySQLJDBCDriverConnection.insert(plate.getCharacters(),
														plate.getOverallConfidence(), "resources/icons/greenCircle.png",
														dtf.format(now).toString(), ImageInputStream);
												SQLiteJDBCDriverConnection.insert(plate.getCharacters(),
														plate.getOverallConfidence(), "resources/icons/greenCircle.png",
														dtf.format(now).toString(), ImageInputStream);
												vehicleLoginInformation.add(new VehicleLoginInformation(
														plate.getCharacters(), plate.getOverallConfidence(),
														new ImageView(
																new Image("file:resources/icons/greenCircle.png")),
														dtf.format(now).toString(), new ImageView(
																new Image(ImageInputStream, 100, 60, false, false))));
												saveCarPlateToFile(image, plate.getCharacters());

												if (arduino.openPort() == true) {
													Thread thread = new Thread() {
														@Override
														public void run() {
															try {
																OutputStream a = arduino.getOutputStream();
																a.write(80); // Write
																// to
																// serial
																a.flush();

																a.close(); // close
																// serial
																// connection
															} catch (Exception e) {
																e.getStackTrace();
															}
															// arduino.closePort();
															System.out.println("CLOSE COM");
														}
													};
													thread.start();
												} else {
													System.out.println("Cannot open port:");
												}

											} else {
												System.out.println("Not allowed");

												String sqlIsDuplicate = "SELECT * FROM vehicle_login_information WHERE PlateNumber= '"
														+ plate.getCharacters() + "' ";
												boolean isDuplicateMySQL = MySQLJDBCDriverConnection
														.isDuplicate(sqlIsDuplicate);
												boolean isDuplicateSQLite = SQLiteJDBCDriverConnection
														.isDuplicate(sqlIsDuplicate);
												if (isDuplicateMySQL || isDuplicateSQLite) {
													MySQLJDBCDriverConnection.insert(plate.getCharacters(),
															plate.getOverallConfidence(),
															"resources/icons/redCircle.png", dtf.format(now).toString(),
															ImageInputStream);
													SQLiteJDBCDriverConnection.insert(plate.getCharacters(),
															plate.getOverallConfidence(),
															"resources/icons/redCircle.png", dtf.format(now).toString(),
															ImageInputStream);

													vehicleLoginInformation.add(new VehicleLoginInformation(
															plate.getCharacters(), plate.getOverallConfidence(),
															new ImageView(
																	new Image("file:resources/icons/redCircle.png")),
															dtf.format(now).toString(),
															new ImageView(new Image(ImageInputStream, 100, 60, false,
																	false))));

												}

											}

										}
									}

								}

							}
							alpr.unload();
						}
					} catch (Exception e) {
						// TODO: handle exception
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Exception Dialog");
						alert.initOwner(dialogStage);
						alert.setHeaderText("Look, an Exception Dialog");
						alert.setContentText("Could not find file blabla.txt!");
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
				} else { // End for checkMode condition

					try {

						boolean pirSensor = PIRSensor();

						if (pirSensor == true) {
							// System.out.println("Motion detected!");
							String country = "sa", configfile = "src/openalpr.conf",
									runtimeDataDir = "src/runtime_data";
							Alpr alpr = new Alpr(country, configfile, runtimeDataDir);

							alpr.setTopN(5);
							alpr.setDefaultRegion("sa");

							AlprResults resultCamera = alpr.recognize(imagebyte);
							if (resultCamera.getPlates().size() > 0) {
								// Thread.sleep(100);
								System.out.println("OpenALPR Version: " + alpr.getVersion());
								System.out.println("Image Size: " + resultCamera.getImgWidth() + "x"
										+ resultCamera.getImgHeight());
								System.out
										.println("Processing Time: " + resultCamera.getTotalProcessingTimeMs() + " ms");
								System.out.println("Found " + resultCamera.getPlates().size() + " results");
								System.out.format("  %-15s%-8s\n", "Plate Number", "Confidence");
							}

							for (AlprPlateResult result : resultCamera.getPlates()) {

								for (AlprPlate plate : result.getTopNPlates()) {

									// if (plate.isMatchesTemplate())
									// System.out.print(" * ");
									// else
									// System.out.print(" - ");
									// System.out.println("Best
									// plate"+result.getBestPlate().getCharacters());

									DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
									LocalDateTime now = LocalDateTime.now();

									System.out.format("%-15s%-8f\n", plate.getCharacters(),
											plate.getOverallConfidence());
									this.plateNumberColumn.setCellValueFactory(e -> e.getValue().plateNumberProperty());
									this.confidenceColumn
											.setCellValueFactory(e -> e.getValue().plateConfidence().asObject());
									this.statusColumn.setCellValueFactory(e -> e.getValue().statusObjectProperty());
									this.dateColumn.setCellValueFactory(e -> e.getValue().dateProperty());
									this.imageColumn.setCellValueFactory(e -> e.getValue().imageProperty());
									String sql = "SELECT * FROM Car_data WHERE PlateNumber= '" + plate.getCharacters()
											+ "' ";
									if (plate.getOverallConfidence() > 0) {
										if (plate.isMatchesTemplate()) {
											boolean checkMySQL = MySQLJDBCDriverConnection.isAllowed(sql);
											boolean checkSQLite = SQLiteJDBCDriverConnection.isAllowed(sql);
											if (checkMySQL == true || checkSQLite == true) {
												MySQLJDBCDriverConnection.insert(plate.getCharacters(),
														plate.getOverallConfidence(), "resources/icons/greenCircle.png",
														dtf.format(now).toString(), ImageInputStream);
												SQLiteJDBCDriverConnection.insert(plate.getCharacters(),
														plate.getOverallConfidence(), "resources/icons/greenCircle.png",
														dtf.format(now).toString(), ImageInputStream);
												Thread delay = new Thread() {
													@Override
													public void run() {
														try {
															saveCarPlateToFile(image, plate.getCharacters());
														} catch (FileNotFoundException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														} catch (IOException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														}
													}
												};
												delay.start();

												Thread delay2 = new Thread() {
													@Override
													public void run() {
														vehicleLoginInformation.add(new VehicleLoginInformation(
																plate.getCharacters(), plate.getOverallConfidence(),
																new ImageView(new Image(
																		"file:resources/icons/greenCircle.png")),
																dtf.format(now).toString(),
																new ImageView(new Image(ImageInputStream, 100, 60,
																		false, false))));
													}
												};
												delay2.start();

												Task<Void> task = new Task<Void>() {
													@Override
													public Void call() throws Exception {
														return null;
													}
												};
												task.setOnSucceeded(e -> {
													try {
														VehicleLoginInformationManualRegisterd(plate.getCharacters());
													} catch (ClassNotFoundException e1) {
														// TODO Auto-generated
														// catch block
														e1.printStackTrace();
													}
												});
												new Thread(task).start();

											} else {
												// If car not registered
												MySQLJDBCDriverConnection.insert(plate.getCharacters(),
														plate.getOverallConfidence(), "resources/icons/blueCircle.png",
														dtf.format(now).toString(), ImageInputStream);
												SQLiteJDBCDriverConnection.insert(plate.getCharacters(),
														plate.getOverallConfidence(), "resources/icons/blueCircle.png",
														dtf.format(now).toString(), ImageInputStream);
												Thread delay = new Thread() {
													@Override
													public void run() {
														try {
															saveCarPlateToFile(image, plate.getCharacters());
														} catch (FileNotFoundException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														} catch (IOException e) {
															// TODO
															// Auto-generated
															// catch block
															e.printStackTrace();
														}
													}
												};
												delay.start();

												Thread delay2 = new Thread() {
													@Override
													public void run() {
														vehicleLoginInformation.add(new VehicleLoginInformation(
																plate.getCharacters(), plate.getOverallConfidence(),
																new ImageView(new Image(
																		"file:resources/icons/blueCircle.png")),
																dtf.format(now).toString(),
																new ImageView(new Image(ImageInputStream, 100, 60,
																		false, false))));
													}
												};
												delay2.start();

												Task<Void> task = new Task<Void>() {
													@Override
													public Void call() throws Exception {
														return null;
													}
												};
												task.setOnSucceeded(e -> {
													try {
														VehicleLoginInformationManualNotRegisterd(
																plate.getCharacters());
													} catch (ClassNotFoundException e1) {
														// TODO Auto-generated
														// catch block
														e1.printStackTrace();
													}
												});
												new Thread(task).start();
											}

										}
									}

								}

							}
							alpr.unload();
						}
					} catch (Exception e) {
						// TODO: handle exception
						// System.out.println("ERROR");
					}
				}

				// // if the frame is not empty, process it
				// if (!frame.empty())
				// {
				// Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
				// }

			} catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}

		return frame;
	}

	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}

		if (this.capture.isOpened()) {
			// release the camera
			this.capture.release();
		}
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

	/**
	 * On application close, stop the acquisition from the camera
	 */
	public void setClosed() {
		this.stopAcquisition();
	}

	public BufferedImage MatToBufferedImage(Mat frame) {
		// Mat() to BufferedImage
		int type = 0;
		if (frame.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else if (frame.channels() == 3) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
		WritableRaster raster = image.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] data = dataBuffer.getData();
		frame.get(0, 0, data);

		return image;
	}

	public byte[] toByteArray(BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
		encoder.encode(image);
		return baos.toByteArray();
	}

	public void comPort(String comPort) {
		arduino = SerialPort.getCommPort(comPort);
		VehicleLoginInformationManualDialogController.setArduino(arduino);
		arduino.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		if (arduino.openPort() == true) {
			comPortLabel.setText("Connected on prot: " + comPort.toUpperCase());
			comPortLabel.setTextFill(Paint.valueOf("#1B5E20"));
		} else {
			comPortLabel.setText("Cannot connected on prot");
			comPortLabel.setTextFill(Paint.valueOf("#BF360C"));
		}

		arduino.closePort();
	}

	public void getComPort() throws ClassNotFoundException {
		String sql = "SELECT COMPort FROM settings";
		String comPortMySQL = MySQLJDBCDriverConnection.getCOMPort(sql);
		// String comPortSQLite= SQLiteJDBCDriverConnection.getCOMPort(sql);
		comPort(comPortMySQL);
		if (arduino.openPort() == true) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					Scanner scanner = new Scanner(arduino.getInputStream());
					while (scanner.hasNextLine()) {
						try {
							line = scanner.nextLine();
							// System.out.println(line);
						} catch (Exception e) {
							System.out.println("Error");
						}
					}
					scanner.close();
					arduino.closePort();
					System.out.println("CLOSE COM");
				}
			};
			thread.start();
		} else {
			System.out.println("Cannot connected on port");
		}
	}
	// /**
	// * Called when the user clicks the new button. Opens a dialog to add
	// * details for a new Car data.
	// * @throws ClassNotFoundException
	// */
	// @FXML
	// private void handleNewCarData() throws ClassNotFoundException {
	// CarData tempCarPlate = new CarData();
	// boolean okClicked = mainApp.ShowCarDataAddDialog(tempCarPlate);
	// if (okClicked) {
	// mainApp.getCarData().add(tempCarPlate);
	//
	// // handleRefresh(new ActionEvent());
	// }
	// }

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
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected person.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleShowCarDataDetailsUser() throws ClassNotFoundException {
		VehicleDataDetails tempCarData = new VehicleDataDetails();
		showCarDatadetailsDialogUser(tempCarData);
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected person.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleShowManageUsersDetails() throws ClassNotFoundException {
		Login tempLogin = new Login();
		showManageUsersDetailsDialog(tempLogin);
	}

	/**
	 * refresh vehicle login information
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleRefresh() throws ClassNotFoundException {
		// Get data from MySQL database
		this.plateNumberColumn.setCellValueFactory(e -> e.getValue().plateNumberProperty());
		this.confidenceColumn.setCellValueFactory(e -> e.getValue().plateConfidence().asObject());
		this.statusColumn.setCellValueFactory(e -> e.getValue().statusObjectProperty());
		this.dateColumn.setCellValueFactory(e -> e.getValue().dateProperty());
		this.imageColumn.setCellValueFactory(e -> e.getValue().imageProperty());
		try (Connection conn = connectMySQL();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM vehicle_login_information")) {
			System.out.println("From MySQL Database vehicle login information: \n");
			// loop through the result set

			while (rs.next()) {
				vehicleLoginInformationTableView.getItems()
						.add(new VehicleLoginInformation(rs.getInt("ID"), rs.getString("PlateNumber"),
								rs.getFloat("Confidence"), new ImageView(new Image(rs.getBinaryStream("Status"))),
								rs.getString("Date"),
								new ImageView(new Image(rs.getBinaryStream("Image"), 100, 60, false, false))));

				System.out.println("ID: " + rs.getInt("ID") + "\t" + "Plate number: " + rs.getString("PlateNumber")
						+ "\t" + "Confidence: " + rs.getFloat("Confidence") + "\t" + "Date" + rs.getString("Date"));

			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}

	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected person.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleEditCarAccess() throws ClassNotFoundException {
		VehicleLoginInformation selectedCarAccess = vehicleLoginInformationTableView.getSelectionModel()
				.getSelectedItem();
		if (selectedCarAccess != null) {
			boolean okClicked = mainApp.showAccessDataEditDialog(selectedCarAccess);
			System.out.println(okClicked);
		} else {
			// Nothing selected.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Person Selected");
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
	private void handleSettings() throws ClassNotFoundException {
		Settings tempSettings = new Settings();
		mainApp.showSettingsDialog(tempSettings);
		// the camera is not active at this point
		this.cameraActive = false;
		// update again the button content

		this.button.setText("Start System");
		this.stopAcquisition();
	}

	@FXML
	private void handleExitCarPlate() {
		System.exit(0);
	}

	@FXML
	private void handleAbout(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Car Parking Entrance Access Control System");
		alert.setHeaderText("About");
		// Set the dialog icon.
		alert.initOwner(dialogStage);
		alert.setContentText("My name is: Yasir Al-bardawil\nMy Student ID is: 202524125\n" + "Version: 1.1");

		alert.showAndWait();
	}

	//
	// /**
	// * Called when the user clicks the edit button. Opens a dialog to edit
	// * details for the selected person.
	// */
	// @FXML
	// private void handleEditCarPlate() {
	// AccessData selectedCar =
	// accessDataTableView.getSelectionModel().getSelectedItem();
	// System.out.println(selectedCar);
	// }
	//
	/**
	 * Called when the user clicks on the delete button.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleDeleteAccessCar() throws ClassNotFoundException {

		VehicleLoginInformation selectedItems = vehicleLoginInformationTableView.getSelectionModel().getSelectedItem();
		if (selectedItems != null) {
			StringProperty vehicleLoginInformationCellValue = vehicleLoginInformationTableView.getSelectionModel()
					.getSelectedItem().dateProperty();
			String date = vehicleLoginInformationCellValue.getValue();
			String deletetStmt = "DELETE FROM vehicle_login_information WHERE Date= '" + date + "' ";
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initOwner(this.dialogStage);
			alert.setTitle("delete car plate " + selectedItems.getPlateNumber());
			alert.setHeaderText("Look, you will delete car plate " + selectedItems.getPlateNumber());
			alert.setContentText("Are you sure you wnat to delete car plate " + selectedItems.getPlateNumber() + "?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				vehicleLoginInformation.removeAll(selectedItems);
				// accessDataTableView.getSelectionModel().clearSelection();
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

	@FXML
	public void handleCheckPort(ActionEvent event) throws ClassNotFoundException {

	}

	// @FXML
	// public void handleCheckPortKeyboard(KeyEvent event) {
	// if(event.getCode().equals(KeyCode.ENTER)) {
	// comPort(portField.getText());
	// if(arduino.openPort()==true){
	// Thread thread = new Thread(){
	// @Override public void run(){
	// Scanner scanner = new Scanner(arduino.getInputStream());
	// while(scanner.hasNextLine()){
	// try{
	// line = scanner.nextLine();
	// //System.out.println(line);
	// }
	// catch(Exception e){
	// System.out.println("Error");
	// }
	// }
	// scanner.close();
	// arduino.closePort();
	// System.out.println("CLOSE COM");
	// }
	// };
	// thread.start();
	// }
	// }
	// }
	//
	/**
	 * Opens the birthday statistics.
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	private void handleShowExpiryDateStatistics() throws ClassNotFoundException {
		mainApp.showExpiryDateStatistics();
	}

	public boolean PIRSensor() {
		if (line.equals("1")) {
			return true;
		}

		return false;
	}

	public AlprPlate getPlate() {
		return plate;
	}

	public void setPlate(AlprPlate plate) {
		this.plate = plate;
	}

	public static void saveCarPlateToFile(BufferedImage image, String plate) throws FileNotFoundException, IOException {
		Thread delay = new Thread() {
			@Override
			public void run() {

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss-SSS");
				LocalDateTime now = LocalDateTime.now();
				File outputfile = new File(
						"C:\\Users\\Yasir\\Pictures\\" + plate + " " + dtf.format(now).toString() + ".png");
				try {
					ImageIO.write(image, "jpeg", outputfile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		delay.start();
	}

	/**
	 * Returns the data as an observable list of Car data.
	 * 
	 * @return
	 */
	public ObservableList<VehicleDataDetails> getCarData() {
		return carData;
	}

	public void getSettingsData(Settings settings) {
		this.settings = settings;

		settings.getUsername();
		settings.getPassword();
		settings.getcomPort();
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
	public void showCarDatadetailsDialog(VehicleDataDetails vehicleDataDetails) throws ClassNotFoundException {
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
			alert.initOwner(dialogStage);
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
	 * Opens a dialog to edit details for the specified Car data. If the user
	 * clicks OK, the changes are saved into the provided Car data object and
	 * true is returned.
	 * 
	 * @param Car
	 *            data the Car data object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public void showCarDatadetailsDialogUser(VehicleDataDetails vehicleDataDetails) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/VehicleDataDetailsUser.fxml"));
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
			alert.initOwner(dialogStage);
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
	 * Opens a dialog login
	 * 
	 * @throws ClassNotFoundException
	 */
	@FXML
	public void handleLogout() throws ClassNotFoundException {
		dialogStage.close();
		arduino.closePort();
		this.stopAcquisition();
		mainApp.showLoginDialog();
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
	public void showManageUsersDetailsDialog(VehicleDataDetailsController vehicleDataDetailsController)
			throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/VehicleDataDetails.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Manage users' details");
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
			alert.initOwner(dialogStage);
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
	 * Opens a dialog to edit details for the specified Car data. If the user
	 * clicks OK, the changes are saved into the provided Car data object and
	 * true is returned.
	 * 
	 * @param Car
	 *            data the Car data object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public void showManageUsersDetailsDialog(Login login) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/ManageUsersDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Manage users' details");
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
			ManageUsersDialogController controller = loader.getController();
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
			alert.initOwner(dialogStage);
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
	 * Opens a dialog to edit details for the specified Car data. If the user
	 * clicks OK, the changes are saved into the provided Car data object and
	 * true is returned.
	 * 
	 * @param Car
	 *            data the Car data object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 * @throws ClassNotFoundException
	 */
	public void showVehicleDataDetailsDialogUser(VehicleDataDetails vehicleDataDetails) throws ClassNotFoundException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/yasir/view/VehicleDataDetailsUser.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Vehicle data Details");
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
			VehicleDataDetailsController controller1 = loader.getController();
			controller1.setDialogStage(dialogStage);

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
			alert.initOwner(dialogStage);
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
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
	}

	public void VehicleLoginInformationManualRegisterd(String plateNumber) throws ClassNotFoundException {

		VehicleDataDetails tempCarPlate = new VehicleDataDetails();
		tempCarPlate.setPlateNumber(plateNumber);
		try (Connection conn = connectMySQL();

				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM car_data WHERE PlateNumber='" + plateNumber + "';")) {
			System.out.println("From MySQL Database: \n");
			// loop through the result set
			if (rs.next()) {
				tempCarPlate.setIdNo(rs.getString("idNo"));
				tempCarPlate.setOwnerName(rs.getString("OwnerName"));
				tempCarPlate.setCarBrand(rs.getString("CarBrand"));
				tempCarPlate.setCarModel(rs.getString("CarModel"));
				tempCarPlate.setVin(rs.getString("VIN"));
				tempCarPlate.setManufacturingYear(rs.getString("ManufacturingYear"));
				tempCarPlate.setColor(rs.getString("Color"));
				tempCarPlate.setExpiryDate(DateUtil.parse(rs.getString("expiryDate")));

			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		boolean okClicked = mainApp.ShowAccessCarManualRegisteredDialog(tempCarPlate);
		if (okClicked) {
			getCarData().add(tempCarPlate);
		}
	}

	public void VehicleLoginInformationManualNotRegisterd(String plateNumber) throws ClassNotFoundException {

		VehicleDataDetails tempCarPlate = new VehicleDataDetails();
		tempCarPlate.setPlateNumber(plateNumber);

		boolean okClicked = mainApp.ShowVehicleLoginInformationManualNotRegisterdDialog(tempCarPlate);
		if (okClicked) {
			getCarData().add(tempCarPlate);
		}
	}
}