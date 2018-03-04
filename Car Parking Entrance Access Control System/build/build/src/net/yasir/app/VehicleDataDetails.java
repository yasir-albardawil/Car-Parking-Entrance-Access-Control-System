package net.yasir.app;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.yasir.utils.LocalDateAdapter;
public class VehicleDataDetails {
		private IntegerProperty id;
		private StringProperty idNo;
		private final StringProperty ownerName;
		private StringProperty carBrand;
		private StringProperty carModel;
		private StringProperty vin;
        private final StringProperty plateNumber;
		private StringProperty manufacturingYear;
		private StringProperty color;
        private ObjectProperty<LocalDate> expiryDate;
        
        
        /**
         * Default constructor.
         */
        public VehicleDataDetails() {
            this(null, null, null);
        }

        
        public VehicleDataDetails(String idNo, String ownerName, String plateNumber) {
            this.idNo = new SimpleStringProperty(idNo);
            this.ownerName = new SimpleStringProperty(ownerName);
            this.plateNumber = new SimpleStringProperty(plateNumber);
            
         // Some initial dummy data, just for convenient testing.
            this.carBrand = new SimpleStringProperty(null);
            this.carModel = new SimpleStringProperty(null);
            this.vin = new SimpleStringProperty(null);
            this.manufacturingYear = new SimpleStringProperty(null);
            this.color = new SimpleStringProperty(null);
            this.expiryDate = new SimpleObjectProperty<LocalDate>(null);
        }
        
//        public CarData(Integer id, String ownerName, String plateNumber) {
//        	this.id = new SimpleIntegerProperty(id);
//            this.ownerName = new SimpleStringProperty(ownerName);
//            this.plateNumber = new SimpleStringProperty(plateNumber);
//            this.expiryDate = new SimpleObjectProperty<LocalDate>(LocalDate.of(1999, 2, 21));
//        }
        
        public VehicleDataDetails(Integer id, String idNo, String ownerName, String plateNumber) {
        	this.id = new SimpleIntegerProperty(id);
            this.idNo = new SimpleStringProperty(idNo);
            this.ownerName = new SimpleStringProperty(ownerName);
            this.plateNumber = new SimpleStringProperty(plateNumber);
//            this.carBrand = new SimpleStringProperty(carBrand);
//            this.carModel = new SimpleStringProperty(carModel);
//            this.structureNo = new SimpleStringProperty(structureNo);
//            
//            this.manufacturingYear = new SimpleStringProperty(manufacturingYear);
//            this.color = new SimpleStringProperty(color);
//            new SimpleObjectProperty<LocalDate>(LocalDate.of(2000, 01, 01));
//            this.expiryDate = new SimpleObjectProperty<LocalDate>(expiryDate);
        }
        
        /**
         * 
         * @param id
         * @param idNo
         * @param ownerName
         * @param carBrand
         * @param carModel
         * @param structureNo
         * @param plateNumber
         * @param manufacturingYear
         * @param color
         * @param expiryDate
         */
        public VehicleDataDetails(Integer id, String idNo, String ownerName, String plateNumber,  String carBrand, String carModel, String vin, String manufacturingYear, String color, LocalDate expiryDate) {
        	this.id = new SimpleIntegerProperty(id);
            this.idNo = new SimpleStringProperty(idNo);
            this.ownerName = new SimpleStringProperty(ownerName);
            this.carBrand = new SimpleStringProperty(carBrand);
            this.carModel = new SimpleStringProperty(carModel);
            this.vin = new SimpleStringProperty(vin);
            this.plateNumber = new SimpleStringProperty(plateNumber);
            this.manufacturingYear = new SimpleStringProperty(manufacturingYear);
            this.color = new SimpleStringProperty(color);
            new SimpleObjectProperty<LocalDate>(LocalDate.of(1999, 01, 01));
            this.expiryDate = new SimpleObjectProperty<LocalDate>(expiryDate);
        }
 

		public void setId(int id) {
			this.id.set(id);
		}
		
		public int getId() {
			return id.get();
		}
		
		public IntegerProperty getIdProperty() {
			return id;
		}
		
		public void setIdNo(String id) {
			this.idNo.set(id);
		}
		
		public String getIdNo() {
			return idNo.get();
		}
		
		public StringProperty getIdNoProperty() {
			return idNo;
		}

		public void setOwnerName(String ownerName) {
			this.ownerName.set(ownerName);
		}
		
		public String getOwnerName() {
	        return ownerName.get();
		}
	 
		public StringProperty getOwnerNameProperty() {
			return ownerName;
		}
		
		public void setCarBrand(String carBrand) {
			this.carBrand.set(carBrand);
		}
		
		public String getCarBrand() {
	        return carBrand.get();
		}
	 
		public StringProperty getCarBrandProperty() {
			return carBrand;
		}
		
		public void setCarModel(String carModel) {
			this.carModel.set(carModel);
		}
		
		public String getCarModel() {
	        return carModel.get();
		}
	 
		public StringProperty getCarModelProperty() {
			return carModel;
		}
		
		public void setVin(String vin) {
			this.vin.set(vin);
		}
		
		public String getVin() {
	        return vin.get();
		}
	 
		public StringProperty getStructureNoProperty() {
			return vin;
		}
		
		public void setPlateNumber(String plateNumber) {
			this.plateNumber.set(plateNumber);
		}
		
		public String getPlateNumber() {
			return plateNumber.get();
		}

		public StringProperty getPlateNumberProperty() {
			return plateNumber;
		}
		
		public void setManufacturingYear(String manufacturingYear) {
			this.manufacturingYear.set(manufacturingYear);
		}
		
		public String getManufacturingYear() {
			return manufacturingYear.get();
		}

		public StringProperty getManufacturingYearProperty() {
			return manufacturingYear;
		}
		
		//
		
		public void setColor(String color) {
			this.color.set(color);
		}
		
		public String getColor() {
			return color.get();
		}

		public StringProperty getColorrProperty() {
			return color;
		}
		
		 @XmlJavaTypeAdapter(LocalDateAdapter.class)
		public LocalDate getExpiryDate() {
		    return expiryDate.get();
		}

		public void setExpiryDate(LocalDate expiryDate) {
		    this.expiryDate.set(expiryDate);
		}

		public ObjectProperty<LocalDate> expiryDateProperty() {
		    return expiryDate;
		}
        
}