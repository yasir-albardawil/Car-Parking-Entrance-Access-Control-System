package net.yasir.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import net.yasir.app.VehicleDataDetails;
import net.yasir.app.VehicleLoginInformation;
import net.yasir.connection.MySQLJDBCDriverConnection;
import net.yasir.connection.SQLiteJDBCDriverConnection;
import net.yasir.utils.DateUtil;
import net.yasir.utils.LocalDateAdapter;

import java.net.URL;
import java.util.ResourceBundle;


public class VehicleLoginInformationEditDialogController {
	@FXML
	private TextField carNameTextField;
	@FXML
	private TextField confidenceTextField;
	@FXML 
	private TextField statusTextField;
	@FXML
	private TextField dateTextField;

	 private Stage dialogStage;
	 private VehicleLoginInformation vehicleLoginInformation;
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
	     * Sets the vehicle Login Information to be edited in the dialog.
	     * 
	     * @param vehicleLoginInformation
	     * @throws ClassNotFoundException 
	     */
	    public void setVehicleLoginInformation(VehicleLoginInformation vehicleLoginInformation) throws ClassNotFoundException {
	        this.vehicleLoginInformation = vehicleLoginInformation;

	        
	        carNameTextField.setText(vehicleLoginInformation.getPlateNumber());
	        confidenceTextField.setText(String.valueOf(vehicleLoginInformation.getConfidence()));
	        statusTextField.setText("Open gate");
	        dateTextField.setText(vehicleLoginInformation.getDate());
	        
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
	     * @throws ClassNotFoundException 
	     */
	    @FXML
	    private void handleOk() throws ClassNotFoundException {
	        if (isInputValid()) {
	        	vehicleLoginInformation.setPlateNumber(carNameTextField.getText());
	        	vehicleLoginInformation.setConfidence(Float.parseFloat(confidenceTextField.getText()));
	            vehicleLoginInformation.setStatus(statusTextField.getText());
	            vehicleLoginInformation.setDate(dateTextField.getText());
	            
//	            MySQLJDBCDriverConnection.updateCarData(vehicleLoginInformation.getNo(), carNameField.getText(), plateNumberField.getText(), DateUtil.format(expiryDateDatePicker.getValue()));
//	            SQLiteJDBCDriverConnection.updateCarData(carData.getId(), carNameField.getText(), plateNumberField.getText(), DateUtil.format(expiryDateDatePicker.getValue()));
//	        	
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

	        if (carNameTextField.getText() == null || carNameTextField.getText().length() == 0) {
	            errorMessage += "No valid car name!\n"; 
	        }
	        
	        if (confidenceTextField.getText() == null || confidenceTextField.getText().length() == 0) {
	        	errorMessage += "No valid plate number!\n";
	        } 
	        
	        if (statusTextField.getText() == null || statusTextField.getText().length() == 0) {
	            errorMessage += "No valid expiry date!\n";
	        }
	        
	        if (dateTextField.getText() == null|| dateTextField.getText().length() == 0) {
	            errorMessage += "No valid expiry date!\n";
	        } else {
	            if (!DateUtil.validDate(DateUtil.format(DateUtil.parse(dateTextField.getText())))) {
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
