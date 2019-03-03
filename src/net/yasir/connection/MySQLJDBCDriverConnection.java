package net.yasir.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author sqlitetutorial.net
 */
public class MySQLJDBCDriverConnection {

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

	public static Connection getConnection() throws ClassNotFoundException {
		final String DB_URL = "jdbc:mysql://eu-mm-auto-fra-02-c.cleardb.net/heroku_512373cbbdfb928";
		final String USER = "bf2baa656a577a";
		final String PASS = "60c93ad3";
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public static boolean login(String sql) throws ClassNotFoundException {
		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			// loop through the result set
			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return false;
	}

	public static String getRole(String sql) throws ClassNotFoundException {
		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			// loop through the result set
			if (rs.next()) {
				return rs.getString("Role");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return "";
	}

	public static void Check(String sql) throws ClassNotFoundException {
		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			// loop through the result set
			while (rs.next()) {
				System.out.println("From MySQL Database:\nID: " + rs.getInt("ID") + "\t" + "Car name: "
						+ rs.getString("CarName") + "\t" + "Plate number: " + rs.getString("PlateNumber")
						+ "Expiry date: " + rs.getString("expiryDate"));

			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
	}

	public static boolean isAllowed(String sql) throws ClassNotFoundException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			int count = 0;
			while (rs.next()) {
				count++;
				return true;
			}
			if (count < 1) {
				return false;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return false;
	}

	public static String getCOMPort(String sql) throws ClassNotFoundException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			if (rs.next()) {
				return rs.getString("COMPort");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return "";
	}

	public static boolean isDuplicate(String sql) throws ClassNotFoundException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			int count = 0;
			while (rs.next()) {
				count++;
			}

			if (count == 0) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return false;
	}

	public static boolean isDuplicateAddCarData(String sql) throws ClassNotFoundException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			int count = 0;
			while (rs.next()) {
				count++;
			}

			if (count > 0) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return false;
	}

	public static boolean isDuplicateEditCarData(String sql) throws ClassNotFoundException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			int count = 0;
			while (rs.next()) {
				count++;
			}

			if (count >= 1) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return false;
	}

	/**
	 * Insert a user
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void insertUser(String username, String passowrd, String role) throws ClassNotFoundException {
		String insertStmt = "INSERT INTO login\n" + "(Username, Password, Role)\n" + "VALUES\n" + "('" + username
				+ "', '" + passowrd + "','" + role + "');";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Insert a new Car
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void insert(String carName, String plateNumber) throws ClassNotFoundException {
		String insertStmt = "INSERT INTO car_data\n" + "(CarName, PlateNumber)\n" + "VALUES\n" + "('" + carName + "', '"
				+ plateNumber + "');";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Insert a new Car
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void insert(String idNo, String ownerName, String plateNumber, String carBrand, String carModel,
			String vin, String manufacturingYear, String color, String expiryDate) throws ClassNotFoundException {
		String insertStmt = "INSERT INTO car_data\n"
				+ "(idNo, `OwnerName`, `PlateNumber`, `CarBrand`, `CarModel`, `VIN`, `ManufacturingYear`, `Color`, `ExpiryDate`)\n"
				+ "VALUES\n" + "('" + idNo + "', '" + ownerName + "','" + plateNumber + "','" + carBrand + "','"
				+ carModel + "','" + vin + "','" + manufacturingYear + "', '" + color + "','" + expiryDate + "');";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Update Users
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void updateUsers(String username, String password, String role) throws ClassNotFoundException {

		String updateStmt = "UPDATE `login` SET `Username`='" + username + "' ,`Password`='" + password + "' ,`Role`='"
				+ role + "' WHERE `Username`='" + username + "';";
		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(updateStmt)) {
			stmt.executeUpdate(updateStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Update Car data
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void updateCarData(String ID, String carName, String plateNumber) throws ClassNotFoundException {

		String insertStmt = "UPDATE `car_data` SET `CarName`='" + carName + "',`PlateNumber`='" + plateNumber
				+ "' WHERE ID='" + ID + "';";
		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Update Car data
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void updateCarData(int ID, String carName, String plateNumber) throws ClassNotFoundException {
		String insertStmt = "UPDATE `car_data` SET CarName='" + carName + "',PlateNumber='" + plateNumber
				+ "' WHERE ID='" + ID + "';";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Update Car data
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void updateCarData(String idNo, String ownerName, String plateNumber, String carBrand,
			String carModel, String vin, String manufacturingYear, String color, String expiryDate)
			throws ClassNotFoundException {
		String insertStmt = "UPDATE `car_data` SET idNo='" + idNo + "', OwnerName='" + ownerName + "',PlateNumber='"
				+ plateNumber + "',CarBrand='" + carBrand + "',CarModel='" + carModel + "',VIN='" + vin
				+ "',ManufacturingYear='" + manufacturingYear + "', Color='" + color + "',ExpiryDate='" + expiryDate
				+ "' WHERE PlateNumber='" + plateNumber + "';";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Insert for vehicle Login Information
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void insert(String plateNumber, float confidence, String status, String date)
			throws ClassNotFoundException {
		String insertStmt = "INSERT INTO vehicle_Login_Information\n" + "(PlateNumber,Confidence, Status, Date)\n"
				+ "VALUES\n" + "('" + plateNumber + "', '" + confidence + "', '" + status + "', '" + date + "');";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Insert for vehicle Login Information
	 * 
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 */
	public static void insert(String plateNumber, float confidence, String status, String date, InputStream image)
			throws ClassNotFoundException, FileNotFoundException {
		String insertStmt = "INSERT INTO vehicle_Login_Information\n"
				+ "(PlateNumber,Confidence, Status, Date, Image)\n" + "VALUES\n" + "(?, ?, ?, ?, ?);";

		FileInputStream input = null;

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {

			// 3. Set parameter for resume file name
			File theFile = new File(status);
			input = new FileInputStream(theFile);
			preparedStatement.setString(1, plateNumber);
			preparedStatement.setDouble(2, confidence);
			preparedStatement.setBinaryStream(3, input);
			preparedStatement.setString(4, date);
			preparedStatement.setBinaryStream(5, image);
			theFile.getAbsolutePath();

			// 4. Execute statement
			preparedStatement.executeUpdate();
			// stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Insert for vehicle Login Information
	 * 
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 */
	public static void insertBlob(String plateNumber, float confidence, String status, String date, InputStream image)
			throws ClassNotFoundException, FileNotFoundException {
		String insertStmt = "INSERT INTO vehicle_Login_Information\n"
				+ "(PlateNumber,Confidence, Status, Date, Image)\n" + "VALUES\n" + "(?, ?, ?, ?, ?);";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {

			preparedStatement.setString(1, plateNumber);
			preparedStatement.setDouble(2, confidence);
			preparedStatement.setString(3, status);
			preparedStatement.setString(4, date);
			preparedStatement.setBinaryStream(5, image);
			preparedStatement.executeUpdate();
			// stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Insert for vehicle Login Information
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void insert(int ID, String plateNumber, float confidence, String status, String date)
			throws ClassNotFoundException {
		String insertStmt = "INSERT INTO vehicle_Login_Information\n" + "(ID, PlateNumber,Confidence, Status, Date)\n"
				+ "VALUES\n" + "('" + ID + "', '" + plateNumber + "', '" + confidence + "', '" + status + "', '" + date
				+ "');";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Insert Camera settings
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void insertSettings(String username, String password) throws ClassNotFoundException {
		String insertStmt = "INSERT INTO settings\n" + "(Username, Password)\n" + "VALUES\n" + "('" + username + "', '"
				+ password + "');";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * update settings
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void updateSettings(String username, String password) throws ClassNotFoundException {
		String insertStmt = "UPDATE `settings` SET `Username`='" + username + "',`Password`='" + password + "';";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Insert COM Port settings
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void updateSettings(String ipAddress, String username, String password, String comPortTextField)
			throws ClassNotFoundException {
		String insertStmt = "UPDATE `settings` SET IPAddress ='" + ipAddress + "', Username='" + username
				+ "', Password='" + password + "', COMPort='" + comPortTextField + "';";

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(insertStmt)) {
			stmt.executeUpdate(insertStmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Insert COM Port settings
	 * 
	 * @throws ClassNotFoundException
	 */
	public static void updateSettings(String ipAddress, String username, String password, String comPortTextField,
			String theme, String mode) throws ClassNotFoundException {
		String updateStmt = "UPDATE `settings` SET IPAddress ='" + ipAddress + "', Username='" + username
				+ "', Password='" + password + "', COMPort='" + comPortTextField + "', Theme='" + theme + "', Mode='"
				+ mode + "'  WHERE id=1;";

		String insertStmt = "INSERT INTO settings\n" + "(ID, IPAddress, Username, Password, COMPort, Theme)\n"
				+ "VALUES\n" + "('" + "1" + "','" + ipAddress + "', '" + username + "', '" + password + "', '"
				+ comPortTextField + "', '" + theme + "');";

		boolean check = checkData("SELECT * FROM settings");
		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				PreparedStatement preparedStatement = conn.prepareStatement(check ? updateStmt : insertStmt)) {

			stmt.executeUpdate(check ? updateStmt : insertStmt);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void settingsData() throws ClassNotFoundException {
		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT Username, Password, COMPort, Theme FROM settings")) {
			if (rs.next()) {
				// System.out.println("Username: "+rs.getString("Username") +" "
				// + "Password: "+rs.getString("Password"));
				// controller.usernameTextField.setText(rs.getString("Username"));
				// controller.passwordTextField.setText("x");
			} else {
				System.out.println("Empty set.");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}

	}

	public static boolean checkData(String sql) throws ClassNotFoundException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			int count = 0;
			while (rs.next()) {
				count++;
				return true;

			}

			if (count < 1) {
				return false;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return false;
	}

	public static int getID(String sql) throws ClassNotFoundException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				return rs.getInt("ID");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return 0;
	}

	public static int checkDataCount(String sql) throws ClassNotFoundException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			int count = 0;
			while (rs.next()) {
				count++;
				return rs.getInt("count(*)");
			}
			if (count < 1) {
				return 0;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return 0;
	}

	public static boolean checkMode(String sql) throws ClassNotFoundException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			if (rs.next()) {
				if (rs.getString("Mode").equals("Auto")) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}
		return false;
	}

	public static InputStream getPlateNumberImage(String sql) throws ClassNotFoundException, IOException {

		try (Connection conn = connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				return rs.getBinaryStream("Image");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Not allowed");
		}

		return null;

	}

	public byte[] convertBlob(Blob blob) {
		if (blob == null)
			return null;
		try {
			InputStream in = blob.getBinaryStream();
			int len = (int) blob.length(); // read as long long pos = 1;
											// //indexing starts from 1
			byte[] bytes = blob.getBytes(1, len);
			in.close();
			return bytes;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public String decodeCharByteArray(byte[] inputArray, String charSet) { // Ex
																			// charSet="US-ASCII"
		Charset theCharset = Charset.forName(charSet);
		CharsetDecoder decoder = theCharset.newDecoder();
		ByteBuffer theBytes = ByteBuffer.wrap(inputArray);
		CharBuffer inputArrayChars = null;
		try {
			inputArrayChars = decoder.decode(theBytes);
		} catch (CharacterCodingException e) {
			System.err.println("Error decoding");
		}
		return inputArrayChars.toString();
	}

	public static void delete(String sql) throws ClassNotFoundException {
		try (Connection conn = connect(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}