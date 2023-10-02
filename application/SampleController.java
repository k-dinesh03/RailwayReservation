package application;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.lang.Math;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SampleController implements Initializable {
	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML private TextField Lname, PassName, PassAge, Sname, Fname, trainName, trainNumber, getPNR, getUsr, adminName, adminPass;
	@FXML private PasswordField Lpass, Fpass, Scpass, Spass;
	@FXML private Button Llogin, ticket, Screate, Freset, confirm, bookedTicketBtn, trainDetailBtn, exitBtn, 
		availTicketsBtn, cancelTicketBtn, bookTicketbtn, yesContinue, noContinue, cancelTBtn, bookExitBtn, adminLogin, adminBtn, userBtn;
	@FXML private DatePicker journeydate, Adate;
	@FXML private RadioButton Mgender, Fgender, Ogender, Lberth, Mberth, Uberth;
	@FXML private Hyperlink dontAccount, forgetPass, cancelBack, ticketBack;
	@FXML private ToggleGroup genderButtons, berthButtons;
	@FXML private ComboBox<String> source, destination;
	@FXML private Label bPNR, bFromTo, bTrain, bTravelDate, bookedDate;
	
	// Constructor for connecting monogdb
	public SampleController() {
		connect();
	}
	
	// To Connect with Mongodb
	MongoClient mongo;
	MongoDatabase db;
	MongoCollection<org.bson.Document> userSignin, passengerDetails, trainDetails;
	Document Signindoc;
	
	public void connect() {
		mongo = new MongoClient("localhost", 27017);
		db = mongo.getDatabase("RailwaySystem");
		userSignin = db.getCollection("userSignin");
		passengerDetails = db.getCollection("passengerDetails");
		trainDetails = db.getCollection("trainDetails");
	}
	
	// Login authentication method call
	UserSession user;
	public void loginMenu(ActionEvent event) throws IOException{		
		MongoDBUtil mongoDBUtil = new MongoDBUtil();
		String username = Lname.getText();
        String password = Lpass.getText();
        
        user = new UserSession(username); // For to know who booked tickets
        
        if(mongoDBUtil.authenticateUser(username, password)) {
        	actionsMenu(event);
    	}
    	else {
    		Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Occured");
			alert.setHeaderText(null);
			alert.setContentText("Entered username or password was incorrect! Check your login credentials before login...");
			alert.showAndWait();
   		}	
	}
	
	// After creation of account navigate to login page
	public void switchLogin(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("login_user.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	// if we click create account navigate to sign up
	public void getToSignup(ActionEvent event) throws IOException{
		root = (BorderPane) FXMLLoader.load(getClass().getResource("signup.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	// for creating account
	public void createAccount(ActionEvent event) throws IOException{
		
		if (Sname.getText().matches("^[a-zA-Z0-9@.-_].{6,}$")) {
	    	
	    	if(Spass.getText().matches("^[a-zA-Z0-9@.-_].{8,}$")) {
	    		
	    		if(Spass.getText().equals(Scpass.getText())) {
	    			
	    			Signindoc = new Document("Username", Sname.getText());
	    			Signindoc.append("Password", Scpass.getText());
	    			
	    			Alert alert = new Alert(AlertType.CONFIRMATION);
	    			alert.setTitle("Confirmation");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Your account was created successfully!");
	    			alert.showAndWait();
	    			
	    			userSignin.insertOne(Signindoc);
	    			switchLogin(event);

	    		}
	    		else {
	    	    	Alert alert = new Alert(AlertType.WARNING);
	    			alert.setTitle("Validating Password");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Please confirm both passwords are same!");
	    			alert.showAndWait();
	    	    }
				
			}
	    	else {
		    	Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Validating Password");
				alert.setHeaderText(null);
				alert.setContentText("Please Provide valid Password!");
				alert.setContentText("Use Lowercase, Uppercase, Digits with the length of atleast 8 characters for password");
				alert.showAndWait();
		    }
	    } 
		
		else {
	    	Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Validating Username");
			alert.setHeaderText(null);
			alert.setContentText("Please Provide valid Username!");
			alert.setContentText("Use Alphabets, Digits, `@`, `.` with 6 characters to create username");
			alert.showAndWait();
	    }
		
	}		
	
	// if we click forgot password navigate to forgot pass form
	public void getToForgetPass(ActionEvent event) throws IOException{
		root = (BorderPane) FXMLLoader.load(getClass().getResource("forgetPass.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	// For changing password
	public void changePassword(ActionEvent event) throws IOException{
		String fPassword = Fpass.getText();
		
		if(fPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
	        Document filter = new Document("Username", Fname.getText());
	        Document update = new Document("$set", new Document("Password", fPassword));
	        userSignin.updateOne(filter, update);
	        mongo.close();
	        
	        Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText(null);
			alert.setContentText("Password changed successfully!");
			alert.showAndWait();

	        switchLogin(event);
		}
		else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Validating Password");
			alert.setHeaderText(null);
			alert.setContentText("Please Provide valid Password!");
			alert.setContentText("Use Lowercase, Uppercase, Digits with the length of atleast 8 characters for password");
			alert.showAndWait();
		}
	}
	
	// To show the menus
	public void actionsMenu(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("userMenu.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	// To book ticket
	public void bookTicketMenu(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("reservation_form.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
		  
	// Reservation form gender selection method
	String gender;
	public boolean genderSelect(ActionEvent event) throws IOException {
		RadioButton userGender = (RadioButton) genderButtons.getSelectedToggle();
			
	    if (userGender == null) {
	        return true;
	    }
	    gender = userGender.getText();
	    return false;
	}
	
	// Reservation form berth selection method
	String berthPreference;
	public boolean berthSelect(ActionEvent event) throws IOException{
		RadioButton berth = (RadioButton) berthButtons.getSelectedToggle();
			
	    if (berth == null) {
	        return true;
        }
        berthPreference = berth.getText();
	    return false;
	}
	
	// Reservation form Source and Destination selection method
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {		
		
		ObservableList<String> l1 = FXCollections.observableArrayList("Chennai", "Delhi", "Mumbai", "Kolkata", "Bangalore");
		ObservableList<String> l2 = FXCollections.observableArrayList("Mumbai", "Bangalore", "Kolkata", "Chennai", "Delhi");
		
		try{
			source.setItems(l1);
			destination.setItems(l2);
		}
		catch(Exception e) {
			System.out.print("");
		}
	}
	
	// for selecting the source and destination
	String getSource, getDest;
	public void selectSource(ActionEvent event) throws IOException{
		getSource = source.getSelectionModel().getSelectedItem().toString();
	}
	public void selectDest(ActionEvent event) throws IOException{
		getDest = destination.getSelectionModel().getSelectedItem().toString();
	}
	
	// Validates the user inputs before storing in mongodb
	String passengerName;
	private boolean validate(ActionEvent event) throws IOException {
		passengerName = PassName.getText();
		
		if(PassName.getText().isEmpty() || passengerName.length() <= 2) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Please Provide valid Information");
			alert.setHeaderText(null);
			alert.setContentText("Please enter Passenger's Name correctly!");
			alert.showAndWait();
			
			return false;
		}		
		else if(PassAge.getText().isEmpty() || Integer.parseInt(PassAge.getText()) <= 0 || Integer.parseInt(PassAge.getText()) >= 120) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Please Provide valid Information");
			alert.setHeaderText(null);
			alert.setContentText("Please enter Passenger age correctly!");
			alert.showAndWait();
			
			return false;
		}
		else if(genderSelect(event)) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Please Provide valid Information");
			alert.setHeaderText(null);
			alert.setContentText("Please enter Passenger's gender!");
			alert.showAndWait();
			
			return false;
		}
		else if(berthSelect(event)) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Please Provide valid Information");
			alert.setHeaderText(null);
			alert.setContentText("Please enter Passenger's berth Preference!");
			alert.showAndWait();
			
			return false;
		}
		else if(getSource == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Please Provide valid Information");
			alert.setHeaderText(null);
			alert.setContentText("Please enter Passenger's Travel Source!");
			alert.showAndWait();
			
			return false;
		}
		else if(getDest == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Please Provide valid Information");
			alert.setHeaderText(null);
			alert.setContentText("Please enter Passenger's Travel Destination!");
			alert.showAndWait();
			
			return false;
		}
		else if(getSource == getDest) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Please Provide valid Information");
			alert.setHeaderText(null);
			alert.setContentText("Passenger's Travel Source and Destination must not be same!");
			alert.showAndWait();
			
			return false;
		}
		else if(journeydate.getEditor().getText().isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Please Provide valid Information");
			alert.setHeaderText(null);
			alert.setContentText("Please enter Passenger's Journey date!");
			alert.showAndWait();
			
			return false;
		}
		
		return true;
	}
	
	// To save the info of the passenger 
	// Store data to the mongodb that got from reservation form
	Document bookTicket, bookDetails, toTName;
	String getBerth, tNumber = null, formattedDate, pnr, fillAge, tNameNo;
	int toCheckOlder, passengerAge;
	LocalDate dateofjourney;
	
	public void saveInfo(ActionEvent event) throws IOException {
		
		String tName = getSource.substring(0,3) + "-" + getDest.substring(0,3) + " Express";
		try {
			toTName = trainDetails.find(new Document("trainName", tName)).first();
			tNumber = toTName.getString("trainNumber");
			
            Document filter = trainDetails.find(new Document("trainName", tName)).first();
            
    		try {    			
    			toCheckOlder = Integer.parseInt(PassAge.getText());
    			getBerth = checkAvailability(filter, berthPreference, trainDetails, toCheckOlder, tName);
    		}
    		catch (Exception e) {
				System.out.println(e);
			}
        }
		catch (Exception e) {
			System.out.println(e);
		}
		
		if(getBerth != null && validate(event)) {
			
			pnr = String.valueOf(UserSession.getPnrNumber());
			passengerName = PassName.getText();
			fillAge = PassAge.getText();
			passengerAge = Integer.parseInt(PassAge.getText());			   
			tNameNo = tNumber + " : " + tName;
			
			dateofjourney = journeydate.getValue();
			
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	        formattedDate = dateofjourney.format(outputFormatter);
			
			docCreateToStore(tNameNo);
		}
		else if(getBerth == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("No tickets");
			alert.setHeaderText(null);
			alert.setContentText("Ticket for the journey wasn't available. It was sold out!");
			alert.showAndWait();
			
			actionsMenu(event);
		}
		
		confirmation(event);
	}
	
	// To store into database
	public void docCreateToStore(String tNameNo) throws IOException {
		String userName = UserSession.getUserName();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		
		bookTicket = new Document("Ticket_BookerName", userName);
		bookDetails = new Document("PNR_Number", pnr);
		bookDetails.append("Train_Name_Number", tNameNo);
		bookDetails.append("Passenger_Name", passengerName);
	    bookDetails.append("Passenger_Age", passengerAge);
	    bookDetails.append("Passenger_Gender", gender);
	    bookDetails.append("Passenger_Berth", getBerth);
	    bookDetails.append("Travel_Source", getSource);
	    bookDetails.append("Travel_Destination", getDest);
	    bookDetails.append("Passenger_JourneyDate", formattedDate);
	    bookDetails.append("Booked_On", dtf.format(now));
        
		bookTicket.append("Ticket_Details",bookDetails);
		passengerDetails.insertOne(bookTicket);
	}
	
	// Confirmation message
	public void confirmation(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("confirmation.fxml"));
		root = loader.load();
		
		Controller c = loader.getController();
		c.displayDetails(pnr, passengerName, fillAge, gender, getBerth, getSource, getDest, formattedDate);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	// To check the tickets availability before booking
	public String checkAvailability(Document filter, String berthPreference, MongoCollection<Document> trainDetails, int toCheckOlder, String tName) throws IOException{
		
		if(filter != null && checkTicketAvail(tName)) {
			return null;
		}
		
		else if(filter != null && toCheckOlder > 60 && filter.getInteger("lowerBerth") > 0) {
			berthPreference = "We arrange you Lower berth";
			int currentValue = filter.getInteger("lowerBerth");
			trainDetails.updateOne(filter, Updates.set("lowerBerth", currentValue - 1));
			return berthPreference;
		}
		
		else if(filter != null && ( 
				(berthPreference.equals("Lower") && filter.getInteger("lowerBerth") > 0) ||
				(berthPreference.equals("Middle") && filter.getInteger("middleBerth") > 0) || 
				(berthPreference.equals("Upper") && filter.getInteger("upperBerth") > 0) ) ) {
			
			if(berthPreference.equals("Lower")) {
				int currentValue = filter.getInteger("lowerBerth");
				trainDetails.updateOne(filter, Updates.set("lowerBerth", currentValue - 1));
				berthPreference = "Lower";
				return berthPreference;
			}
			else if(berthPreference.equals("Middle")) {
				int currentValue = filter.getInteger("middleBerth");
				trainDetails.updateOne(filter, Updates.set("middleBerth", currentValue - 1));
				berthPreference = "Middle";
				return berthPreference;
			}
			else if(berthPreference.equals("Upper")) {
				int currentValue = filter.getInteger("upperBerth");
				trainDetails.updateOne(filter, Updates.set("upperBerth", currentValue - 1));
				berthPreference = "Upper";
				return berthPreference;
			}
		}
		
		else if(filter.getInteger("lowerBerth") > 0) {
			int currentValue = filter.getInteger("lowerBerth");
			trainDetails.updateOne(filter, Updates.set("lowerBerth", currentValue - 1));
			berthPreference = "Lower";
			return berthPreference;
		}
		
		else if(filter.getInteger("middleBerth") > 0) {
			int currentValue = filter.getInteger("middleBerth");
			trainDetails.updateOne(filter, Updates.set("middleBerth", currentValue - 1));
			berthPreference = "Middle";
			return berthPreference;
		}
		
		else if(filter.getInteger("upperBerth") > 0) {
			int currentValue = filter.getInteger("upperBerth");
			trainDetails.updateOne(filter, Updates.set("upperBerth", currentValue - 1));
			berthPreference = "Upper";
			return berthPreference;
		}
		
		else if(filter.getInteger("racBerth") > 0) {
			int currentValue = filter.getInteger("racBerth");
			trainDetails.updateOne(filter, Updates.set("racBerth", currentValue - 1));
			berthPreference = "RAC";
			return berthPreference;
		}
		
		else if(filter.getInteger("waitingList") > 0) {
			int currentValue = filter.getInteger("waitingList");
			trainDetails.updateOne(filter, Updates.set("waitingList", currentValue - 1));
			berthPreference = "WaitingList";
			return berthPreference;
		}
		return null;
	}
	
	// Checks ticket availability
	public boolean checkTicketAvail(String tName) throws IOException {
		Document ticketsAvail = trainDetails.find(new Document("trainName", tName)).first();
		
		if(ticketsAvail != null && ticketsAvail.getInteger("lowerBerth") == 0 
								&& ticketsAvail.getInteger("middleBerth") == 0
								&& ticketsAvail.getInteger("upperBerth") == 0
								&& ticketsAvail.getInteger("racBerth") == 0
								&& ticketsAvail.getInteger("waitingList") == 0) return true;
		
		return false;
	}
	
	
	// For Cancel Ticket Form display
	public void cancelTicketMenu(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("cancellationForm.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	// To cancel ticket by getting document matched with users pnr
	public void cancelTicket(ActionEvent event) throws IOException {
		String usersPnr = getPNR.getText();
		String pname, source, destination;
		int page;
		
		try{
			Document pnrFinder = passengerDetails.find(new Document("Ticket_Details.PNR_Number", usersPnr)).first();
			
			if(pnrFinder != null) {
				
				// get the sub document of the pnrFinder document
				Document ticketDetails = pnrFinder.get("Ticket_Details", Document.class); 
				
				pname = ticketDetails.getString("Passenger_Name");
				page = ticketDetails.getInteger("Passenger_Age");
				source = ticketDetails.getString("Travel_Source");
				destination = ticketDetails.getString("Travel_Destination");
				
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation for Cancel");
				alert.setHeaderText(null);
				alert.setContentText("Are you sure want to cancel ?\n" + "\n\t\tName             : " + pname + "\n\t\tAge                : " + page + "\n\t\tSource           : " + source + "\n\t\tDestination    : " + destination);
				alert.showAndWait();

				cancelOperation(usersPnr);
				
				root = (BorderPane) FXMLLoader.load(getClass().getResource("userMenu.fxml"));
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
		        
			}
			else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Error on fetching Information");
				alert.setHeaderText(null);
				alert.setContentText("Enter Your PNR Number Correctly!");
				alert.showAndWait();
				
				cancelTicketMenu(event);
			}
    	}
		catch(Exception e) {
			System.out.print(e);
		}
	}
	
	// cancel the ticket and increase seats count which was cancelled
	public void cancelOperation(String usersPnr) throws IOException {

	    Bson query = Filters.eq("Ticket_Details.PNR_Number", usersPnr);

	    // To increase seat numbers
	    Document result = passengerDetails.find(new Document("Ticket_Details.PNR_Number", usersPnr)).first();

	    if (result != null) {
	        if (result.containsKey("Ticket_Details") && result.get("Ticket_Details") instanceof Document) {
	            Document trainSeat = (Document) result.get("Ticket_Details");

	            if (trainSeat.containsKey("Passenger_Berth") && trainSeat.containsKey("Train_Name_Number")) {

	                String trainNo = trainSeat.getString("Train_Name_Number");
	                trainNo = trainNo.substring(0, 4);
	                String berth = trainSeat.getString("Passenger_Berth");

	                Document seat = trainDetails.find(new Document("trainNumber", trainNo)).first();
	                ObjectId objectId = seat.getObjectId("_id");

	                try {
	                	

	                	// Delete the passengers informations
	                    passengerDetails.deleteOne(query);
	                    
	                		                    
	                    // Reassign the cancelled lower berth
	                    if (berth.equals("Lower")) {
	                    	Document racPassenger = passengerDetails.find(new Document("Ticket_Details.Passenger_Berth", "RAC")).first();
	                    	
	                    	if(racPassenger != null) {
	                    		passengerDetails.updateOne(racPassenger, Updates.set("Ticket_Details.Passenger_Berth", "Lower"));
	                    		
	                    		Document wl = passengerDetails.find(new Document("Ticket_Details.Passenger_Berth", "WaitingList")).first();
	                    		
	                    		if(wl != null) {
	                    			passengerDetails.updateOne(wl, Updates.set("Ticket_Details.Passenger_Berth", "RAC"));
	                    			
	                    			// To increment waitinglist count
		                            Document wlChange = new Document("_id", objectId);
		                        	Bson toupdate = Updates.inc("waitingList", 1);
		                        	trainDetails.updateOne(wlChange, toupdate);
	                    		}
	                    		// increment rac if no one in waiting list
	                    		else {
	                    			Document rac = new Document("_id", objectId);
	                            	Bson toupdate = Updates.inc("racBerth", 1);
	                            	trainDetails.updateOne(rac, toupdate);
	                    		}
	                    	}
	                    	// increment lower berth if no one in rac
	                    	else {
	                    		Document correspondBerth = new Document("_id", objectId);
	                        	Bson berthUpdate = Updates.inc("lowerBerth", 1);
	                        	trainDetails.updateOne(correspondBerth, berthUpdate);
	                    	}
	                    } 
	                    
	                    else if (berth.equals("Middle")) {
	                    	Document racPassenger = passengerDetails.find(new Document("Ticket_Details.Passenger_Berth", "RAC")).first();
	                    	
	                    	if(racPassenger != null) {
	                    		passengerDetails.updateOne(racPassenger, Updates.set("Ticket_Details.Passenger_Berth", "Middle"));
	                    		
	                    		Document wl = passengerDetails.find(new Document("Ticket_Details.Passenger_Berth", "WaitingList")).first();
	                    		
	                    		if(wl != null) {
	                    			passengerDetails.updateOne(wl, Updates.set("Ticket_Details.Passenger_Berth", "RAC"));
	                    			
	                    			// To increment waitinglist count
		                            Document wlChange = new Document("_id", objectId);
		                        	Bson toupdate = Updates.inc("waitingList", 1);
		                        	trainDetails.updateOne(wlChange, toupdate);
	                    		}
	                    		// increment rac if no one in waiting list
	                    		else {
	                    			Document rac = new Document("_id", objectId);
	                            	Bson toupdate = Updates.inc("racBerth", 1);
	                            	trainDetails.updateOne(rac, toupdate);
	                    		}
	                    	}
	                    	// increment middle berth if no one in rac
							else {
								Document correspondBerth = new Document("_id", objectId);
					        	Bson berthUpdate = Updates.inc("middleBerth", 1);
					        	trainDetails.updateOne(correspondBerth, berthUpdate);
							}
	                    } 
	                    
	                    else if (berth.equals("Upper")) {
	                    	Document racPassenger = passengerDetails.find(new Document("Ticket_Details.Passenger_Berth", "RAC")).first();
	                    	
	                    	if(racPassenger != null) {
	                    		passengerDetails.updateOne(racPassenger, Updates.set("Ticket_Details.Passenger_Berth", "Upper"));
	                    		
	                    		Document wl = passengerDetails.find(new Document("Ticket_Details.Passenger_Berth", "WaitingList")).first();
	                    		
	                    		if(wl != null) {
	                    			passengerDetails.updateOne(wl, Updates.set("Ticket_Details.Passenger_Berth", "RAC"));
	                    			
	                    			// To increment waitinglist count
		                            Document wlChange = new Document("_id", objectId);
		                        	Bson toupdate = Updates.inc("waitingList", 1);
		                        	trainDetails.updateOne(wlChange, toupdate);
	                    		}
	                    		// increment rac if no one in waiting list
	                    		else {
	                    			Document rac = new Document("_id", objectId);
	                            	Bson toupdate = Updates.inc("racBerth", 1);
	                            	trainDetails.updateOne(rac, toupdate);
	                    		}
	                    	}
	                    	// increment upper berth if no one in rac
	                    	else {
	                    		Document correspondBerth = new Document("_id", objectId);
	                        	Bson berthUpdate = Updates.inc("upperBerth", 1);
	                        	trainDetails.updateOne(correspondBerth, berthUpdate);
	                    	}
	                    }
	                    
	                    else if (berth.equals("RAC")) {
	                    	Document wl = passengerDetails.find(new Document("Ticket_Details.Passenger_Berth", "WaitingList")).first();
                    		
                    		if(wl != null) {
                    			passengerDetails.updateOne(wl, Updates.set("Ticket_Details.Passenger_Berth", "RAC"));
                    			
                    			// To increment waitinglist count
	                            Document wlChange = new Document("_id", objectId);
	                        	Bson toupdate = Updates.inc("waitingList", 1);
	                            trainDetails.updateOne(wlChange, toupdate);
                    		}
                    		// increment rac if no one in waiting list
                    		else {
                    			Document rac = new Document("_id", objectId);
                            	Bson toupdate = Updates.inc("racBerth", 1);
                            	trainDetails.updateOne(rac, toupdate);
                    		}
	                    }
	                    
	                    //increment waiting list if cancelled ticket was in waiting list
	                    else if (berth.equals("WaitingList")) {
	                    	Document wl = new Document("_id", objectId);
	                    	Bson toupdate = Updates.inc("waitingList", 1);
	                    	
	                    	trainDetails.updateOne(wl, toupdate);
	                    } 
	                    
	                    Alert alert = new Alert(AlertType.CONFIRMATION);
	                    alert.setTitle("Confirmation for Cancel");
	                    alert.setHeaderText(null);
	                    alert.setContentText("Your ticket was canceled successfully!");
	                    alert.showAndWait();
	                     
	                }  // try  part
	                
	                catch (MongoException e) {
	                    System.err.println(e);
	                }
	                
	            } // cancelOperation 3rd if part
	        }// cancelOperation 2nd if part
	    } // cancelOperation 1st if part
	    
	    else {
	        Alert alert = new Alert(AlertType.WARNING);
	        alert.setTitle("Error on fetching Information");
	        alert.setHeaderText(null);
	        alert.setContentText("Check PNR Number and Passenger Name!");
	        alert.showAndWait();
	    }

	}

	// To check available tickets
	public void checkTicketMenu(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("checkAvailTickets.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();	
	}
	
	// To show the booked tickets
	Iterable<Document> findResult;
	
	public void bookedTicketMenu(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		VBox vbox = new VBox();
		vbox.setSpacing(10);
		
        findResult = getFindResult(UserSession.getUserName());
        int count = 0;
        Bson filter =  Filters.eq("Ticket_BookerName", UserSession.getUserName());
        long docCount = passengerDetails.countDocuments(filter);
        
        if(docCount == 0) {
        	Label l = new Label("No Tickets were Booked!");
        	vbox.getChildren().add(l);
        }
        
        for (Document doc : findResult) {
			
			String name = doc.get("Ticket_Details", Document.class).getString("Passenger_Name");
			String train = doc.get("Ticket_Details", Document.class).getString("Train_Name_Number");
			String pnr = doc.get("Ticket_Details", Document.class).getString("PNR_Number");
			String berth = doc.get("Ticket_Details", Document.class).getString("Passenger_Berth");
			String source = doc.get("Ticket_Details", Document.class).getString("Travel_Source");
			String destination = doc.get("Ticket_Details", Document.class).getString("Travel_Destination");
			String travelDate = doc.get("Ticket_Details", Document.class).getString("Passenger_JourneyDate");
			String bookedDate = doc.get("Ticket_Details", Document.class).getString("Booked_On");	
		    count++;
		    
		    Label label;
		    
		    if(count != docCount) {
			    label = new Label("\nPassenger " + count + " : " +
			    		"\n\nTrain Name and Number : " + train + 
						"\nPassenger Name : " + name + 
						"\nPassenger PNR Number : " + pnr +
						"\nPassenger Berth : " + berth +
						"\nTravel Source : " + source +
						"\nTravel Destination : " + destination +
						"\nJourney Date : " + travelDate +
						"\nTicket Booked Date : " + bookedDate + "\n");
		    }
		    else {
		    	label = new Label("Passenger " + count + " : " +
						"\n\nTrain Name and Number : " + train + 
						"\nPassenger Name : " + name + 
						"\nPassenger PNR Number : " + pnr +
						"\nPassenger Berth : " + berth +
						"\nTravel Source : " + source +
						"\nTravel Destination : " + destination +
						"\nJourney Date : " + travelDate +
						"\nTicket Booked Date : " + bookedDate + "\n\n\n\n");
		    }
            vbox.getChildren().add(label);
	    }
        
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 400, 250);
        vbox.setAlignment(Pos.CENTER);
        stage.setScene(scene);
        stage.setTitle("Railway Reservation System");
        stage.show();
	}
	
	// For finding all the documents in the username
	private Iterable<Document> getFindResult(String uName) {
		findResult = passengerDetails.find(new Document("Ticket_BookerName", uName));
		if(findResult != null) return findResult;
        return null;
    }
	
}

// To authenticate the user information
class MongoDBUtil {
    public boolean authenticateUser(String username, String password) {
        try (com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoCollection<Document> collection = mongoClient.getDatabase("RailwaySystem").getCollection("userSignin");

            Document user = collection.find(new Document("Username", username)).first();
            
            if (user != null && user.getString("Password").equals(password)) return true; 
        }
        return false;
    }
}

// For get the logged in user's name for ticket bookers details
// and for generation pnr number for each user
class UserSession {
	
    private static String userName;
    static int max = Integer.MAX_VALUE, min = 2147123451;
    private static int pnrNumber = 0;

    UserSession(String userName) {    	
        UserSession.userName = userName;
    }
    
    public static int getPnrNumber() {
    	UserSession.pnrNumber = (int) (Math.random() * (max - min + 1) + min);
    	return pnrNumber;
    }

    public static String getUserName() {
        return userName;
    }
    
}
