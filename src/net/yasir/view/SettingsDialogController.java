package net.yasir.view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;

import gnu.io.CommPortIdentifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import net.yasir.app.MainApp;
import net.yasir.app.Settings;
import net.yasir.connection.MySQLJDBCDriverConnection;
import net.yasir.connection.SQLiteJDBCDriverConnection;

import static net.yasir.connection.MySQLJDBCDriverConnection.getConnection;
import static net.yasir.connection.SQLiteJDBCDriverConnection.getConnectionSQLite;

public class SettingsDialogController {
   
	private MainApp mainApp;
	@FXML public TextField ipAddressTextField;
	@FXML public TextField usernameTextField;
	@FXML public TextField passwordTextField;
	@FXML public TextField comPortTextField;
	@FXML public ComboBox<String> themeComboBox;
	@FXML public Button saveSettings;
	@FXML public RadioButton autoRadioButton;
	@FXML public RadioButton manualRadioButton;
	@FXML public ComboBox<String> portsComboBox;
	@FXML public Label comPortLabel;
	private Stage dialogStage;
	private Settings settings;
	private boolean okClicked = false;
	private ObservableList<String> portList;
	public static SerialPort arduino;
	

	 private ObservableList<String> list = FXCollections.observableArrayList(
		"JMetroDarkTheme", "JMetroLightTheme", "brume", "modena", "DarkTheme", "bootstrap2", "bootstrap3"
	);
	/**
	 * 
	 * @throws ClassNotFoundException
	 */
	 public void initialize() throws ClassNotFoundException {
		 	getComPorts();
		 	//detectPort();
	    	getSettingsData();
	    	themeComboBox.setItems(list);
	    	String sql = "SELECT COMPort FROM settings";
	    	String comPort = MySQLJDBCDriverConnection.getCOMPort(sql);
	    	portsComboBox.getSelectionModel().select(comPort);
	    } 
	    
	    /**
	     * Sets the stage of this dialog.
	     * 
	     * @param dialogStage
	     */
	    public void setDialogStage(Stage dialogStage) {
	        this.dialogStage = dialogStage;
	        
	        // Set the dialog icon.
	        this.dialogStage.getIcons().add(new Image("file:resources/icons/settings.png"));
	    }

	    
	    /**
	     * Sets the settings to be edited in the dialog.
	     * 
	     * @param settings
	     * @throws ClassNotFoundException 
	     */
	    public void setSettings(Settings settings) throws ClassNotFoundException {
	        this.settings = settings;
	       
	    }
	    
	    /**
	     * Returns true if the user clicked OK, false otherwise.
	     * 
	     * @return okClicked
	     */
	    public boolean isOkClicked() {
	        return okClicked;
	    }

	    /**
	     * Called when the user clicks ok.
	     * @throws ClassNotFoundException 
	     */
	    @FXML
	    private void handleOk() throws ClassNotFoundException {
	        if (isInputValid()) {
	        	settings.setUsername(usernameTextField.getText());
	        	settings.setPassword(passwordTextField.getText());
	        	settings.setComPortNumber(portsComboBox.getValue());
	        	System.out.println("Current theme " + mainApp.getTheme());
	        	if (autoRadioButton.isSelected()) {
					System.out.println(autoRadioButton.getText());
					
		        	MySQLJDBCDriverConnection.updateSettings(ipAddressTextField.getText(), usernameTextField.getText(), passwordTextField.getText(), portsComboBox.getValue(), themeComboBox.getValue(), autoRadioButton.getText());
		        	SQLiteJDBCDriverConnection.updateSettings(ipAddressTextField.getText(), usernameTextField.getText(), passwordTextField.getText(), portsComboBox.getValue(), themeComboBox.getValue(), manualRadioButton.getText());
	        	}
	        	
	        	if (manualRadioButton.isSelected()) {
					System.out.println(manualRadioButton.getText());
					MySQLJDBCDriverConnection.updateSettings(ipAddressTextField.getText(), usernameTextField.getText(), passwordTextField.getText(), portsComboBox.getValue(), themeComboBox.getValue(), manualRadioButton.getText());
					SQLiteJDBCDriverConnection.updateSettings(ipAddressTextField.getText(), usernameTextField.getText(), passwordTextField.getText(), portsComboBox.getValue(), themeComboBox.getValue(), manualRadioButton.getText());

	        	
	        	}
	        			

	        	//setTheme();
	            okClicked = true;
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

	        if (ipAddressTextField.getText() == null || ipAddressTextField.getText().length() == 0) {
	            errorMessage += "No valid IP Address!\n"; 
	        }
	        
	        if (usernameTextField.getText() == null || usernameTextField.getText().length() == 0) {
	            errorMessage += "No valid username!\n"; 
	        }

	        if (passwordTextField.getText() == null || passwordTextField.getText().length() == 0) {
				errorMessage += "No valid plate number!\n"; 
	        }
	        
	        if (portsComboBox.getValue() == null || portsComboBox.getValue().length() == 0) {
				errorMessage += "No seelcted COM Port!\n"; 
	        }
	        
	        if (themeComboBox.getValue() == null || themeComboBox.getValue().length() == 0) {
	            errorMessage += "No selected theme!\n"; 
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
	     * Connect to the sql.sqlite database
	     * @return the Connection object
		 * @throws ClassNotFoundException 
	     */
	    private static Connection connectMySQL() throws ClassNotFoundException {
	        // MySQL connection string
			return getConnection();
		}
	    /**
	     * Connect to the sql.sqlite database
	     * @return the Connection object
	     */
	    private static Connection connectsqlite() {
	        // SQLite connection string
			return getConnectionSQLite();
		}
    
	public void getSettingsData() throws ClassNotFoundException {
		try (Connection conn = connectMySQL();
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery("SELECT IPAddress, Username, Password, COMPort, Mode FROM settings")){
			if(rs.next()) {
				ipAddressTextField.setText(rs.getString("IPAddress"));
				usernameTextField.setText(rs.getString("Username"));
				passwordTextField.setText(rs.getString("Password"));
				if (rs.getString("Mode").equals("Auto")) {
					autoRadioButton.setSelected(true);
				} else {
					manualRadioButton.setSelected(true);
				}
				
				
				
				
			} else {
				System.out.println("Empty set.");
			}

           } catch (SQLException e) {
               System.out.println(e.getMessage());
               System.out.println("Not allowed");
           }
	}
	

	
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    
 // port detector method
    // Dummy method - Include arduino calls here
    private void detectPort(){

    	 System.out.println("\n1/3 Now detecting port...");

         portList = FXCollections.observableArrayList();

         String[] serialPortNames = {"COM1", "COM2", "COM3"}; //    SerialPortList.getPortNames();

         for(String name: serialPortNames){
             System.out.println("\nDetected Port: ");
             System.out.println(name);
             portList.add(name);
         }

         // No need to create a new combo instance
         // No need to add a change listener to refresh ports
         portsComboBox.setItems(portList); 
     }
    
    @FXML
    public void handleCheckPort(ActionEvent event) {
    	
    	comPort(portsComboBox.getValue());
//    	if(arduino.openPort()==true) {
//			Thread thread = new Thread(){
//				@Override public void run(){
//					Scanner scanner = new Scanner(arduino.getInputStream());
//					scanner.close();
//					arduino.closePort();
//					System.out.println("CLOSE COM");
//				}
//			};
//			thread.start();
//		} else {
//			Alert alert = new Alert(AlertType.ERROR);
//			alert.initOwner(dialogStage);
//			alert.setTitle("Port error");
//			alert.setHeaderText("port error");
//			alert.setContentText("please enter the cottect port.");
//			alert.showAndWait();
//			System.out.println("Cannot connected on port");
//		}
    }
    
    public void getComPorts(){
        String     port_type;
        Enumeration<?>  enu_ports  = CommPortIdentifier.getPortIdentifiers();
 
        while (enu_ports.hasMoreElements()) {
            CommPortIdentifier port_identifier = (CommPortIdentifier) enu_ports.nextElement();
 
            switch(port_identifier.getPortType()){
                case CommPortIdentifier.PORT_SERIAL:
                    port_type   =   "Serial";
                    break;
                case CommPortIdentifier.PORT_PARALLEL:
                    port_type   =   "Parallel";
                    break;
                case CommPortIdentifier.PORT_I2C:
                    port_type   =   "I2C";
                    break;
                case CommPortIdentifier.PORT_RAW:
                    port_type   =   "Raw";
                    break;
                case CommPortIdentifier.PORT_RS485:
                    port_type   =   "RS485";
                    break;
                default:
                    port_type   =   "Unknown";
                    break;
            }
            
            portList = FXCollections.observableArrayList();

            String serialPortNames = port_identifier.getName();
            

            portList.add(serialPortNames);

            // No need to create a new combo instance
            // No need to add a change listener to refresh ports
            portsComboBox.setItems(portList); 
            System.out.println("Port : "+port_identifier.getName() +" Port type : "+port_type);
        }
    }

    public void comPort(String comPort) {	
		arduino = SerialPort.getCommPort(comPort);
		VehicleLoginInformationManualDialogController.setArduino(arduino);
		arduino.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		if(arduino.openPort()==true) {
			comPortLabel.setText("Connected on prot: " + comPort.toUpperCase());
			comPortLabel.setTextFill(Paint.valueOf("#1B5E20"));
		} else {
			comPortLabel.setText("Cannot connected on prot");
			comPortLabel.setTextFill(Paint.valueOf("#BF360C"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Port error");
			alert.setHeaderText("port error");
			alert.setContentText("please enter the cottect port.");
			alert.showAndWait();
          
		}
		
		arduino.closePort();
    }
}
