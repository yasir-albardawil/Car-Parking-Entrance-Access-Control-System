package net.yasir.app;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;
import net.yasir.utils.LocalDateAdapter;

public class VehicleLoginInformation {
		private IntegerProperty  no;
        private StringProperty plateNumber;
        private FloatProperty  confidence;
        private ObjectProperty<ImageView> statusObject;
        private StringProperty status;
        private StringProperty date;
        private ObjectProperty<LocalDateTime> birthday;
        private ObjectProperty<ImageView> image;
        
        /**
         * Default constructor.
         */
        public VehicleLoginInformation() {
        	this(null, null, null, null, null);
        }
            
        public VehicleLoginInformation(Integer no, String plateNumber, Float confidence, ImageView statusObject, String date, ImageView image) {
        	this.no = new SimpleIntegerProperty(no);
            this.plateNumber = new SimpleStringProperty(plateNumber);
            this.confidence = new SimpleFloatProperty(confidence);
            this.statusObject = new SimpleObjectProperty<ImageView>(statusObject);
            this.date = new SimpleStringProperty(date);
            this.image = new SimpleObjectProperty<ImageView>(image);
        }
       

        
        public VehicleLoginInformation(String plateNumber, Float confidence, ImageView statusObject, String date) {
            this.plateNumber = new SimpleStringProperty(plateNumber);
            this.confidence = new SimpleFloatProperty(confidence);
            this.statusObject = new SimpleObjectProperty<ImageView>(statusObject);
            this.date = new SimpleStringProperty(date);
        }
        
        public VehicleLoginInformation(String plateNumber, Float confidence, ImageView statusObject, String date, ImageView image) {
            this.plateNumber = new SimpleStringProperty(plateNumber);
            this.confidence = new SimpleFloatProperty(confidence);
            this.statusObject = new SimpleObjectProperty<ImageView>(statusObject);
            this.date = new SimpleStringProperty(date);
            this.image = new SimpleObjectProperty<ImageView>(image);
        }
        

        public void setNo(Integer no) {
            this.no.set(no);
        }

        public int getNo() {
			return no.get();
		}
        
        public IntegerProperty getNoProperty() {
			return no;
		}
        
        public void setPlateNumber(String plateNumber) {
            this.plateNumber.set(plateNumber);
        }

        public StringProperty plateNumberProperty() {
            return plateNumber;
        }
        
        public String getPlateNumber() {
			return plateNumber.get();
		}
        
        public void setConfidence(Float confidence) {
            this.confidence.set(confidence);
        }

        public FloatProperty plateConfidence() {
            return confidence;
        }
        
        
		public Float getConfidence() {
			return confidence.get();
		}
		
				
		public void setStatus(ImageView statusObject) {
            this.statusObject.set(statusObject);
        }

        public ObjectProperty<ImageView> statusObjectProperty() {
            return statusObject;
        }
        
        
		public ImageView getStatusObject() {
			return statusObject.get();
		}
		
		public void setStatus(String status) {
            this.status.set(status);
        }

        public StringProperty statusProperty() {
            return status;
        }
        
        
		public String getStatus() {
			return status.get();
		}
		
		public void setDate(String date) {
	            this.date.set(date);
	    }

		public StringProperty dateProperty() {
	            return date;
	    }
	        
	    public String getDate() {
				return date.get();
        }
	    
		@XmlJavaTypeAdapter(LocalDateAdapter.class)
	    public String getDateLocalDate() {
				return date.get();
        }
	    public void setImage(ImageView image) {
            this.image.set(image);
        }

        public ObjectProperty<ImageView> imageProperty() {
            return image;
        }
        
        
		public ImageView getImage() {
			return image.get();
		}
	    
//	    @XmlJavaTypeAdapter(LocalDateAdapter.class)
//	    public LocalDate getBirthday() {
//	        return birthday.get();
//	    }
//
//	    public void setBirthday(LocalDate birthday) {
//	        this.birthday.set(birthday);
//	    }
//
//	    public ObjectProperty<LocalDate> birthdayProperty() {
//	        return birthday;
//	    }
        
}