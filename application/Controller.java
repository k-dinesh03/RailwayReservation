package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Controller implements Initializable{
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML private Label CpnrNumber, CtrainName, CpassName, CpassAge, CpassGender, CpassBerth, CpassSource, CpassDest, CpassDate;
	@FXML private Button wantContinue, adSCreate, adminLogin;
	@FXML private ComboBox<String> Asource, Adestination;
	@FXML private DatePicker Adate, bdate;
	@FXML private Button showTicketsBtn, adFReset, askSubmit, userBtn, adminBtn;
	@FXML private Hyperlink availBack, forgetPass;
	@FXML private TextField adSPass, adSEmail, adSName, adFName, adFPass, adminName, adminPass;
	
	// Constructor for connecting monogdb
	public Controller() {
		connect();
	}

	// To Connect with Mongodb
	MongoClient mongo;
	MongoDatabase db;
	MongoCollection<org.bson.Document> trainDetails, passengerDetails, adminSignin, letAdminIn;
	
	public void connect() {
		mongo = new MongoClient("localhost", 27017);
		db = mongo.getDatabase("RailwaySystem");
		trainDetails = db.getCollection("trainDetails");
		passengerDetails = db.getCollection("passengerDetails");
		adminSignin = db.getCollection("adminSignin");
		letAdminIn = db.getCollection("letAdminIn");
	}
	
	// To show the menus if availtickets back clicked
	public void actionsMenu(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("userMenu.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
		
	// To display details of the user in confirmation form

	String requiredTainName, requiredTrainNo, pberth, AgetDest, AgetSource;
	public void displayDetails(String pnr, String name, String age, String gender, String berth, String source, String destination, String date) throws IOException {
		
		CpnrNumber.setText(pnr);
		CpassName.setText(name);
		CpassAge.setText(age + " years");
		
		if(berth.equals("WaitingList")) CpassBerth.setText("You are in waiting list");
		
		else if(Integer.parseInt(age) > 60) CpassBerth.setText(berth);
		
		else CpassBerth.setText(berth + " Berth Given");
		
		pberth = berth;
		CpassGender.setText(gender);
		CpassSource.setText(source);
		CpassDest.setText(destination);
		CpassDate.setText(date);
		
		requiredTainName = source.substring(0,3) + "-" + destination.substring(0,3) + " Express";
		
		try{
			Document user = trainDetails.find(new Document("trainName", requiredTainName)).first();
            requiredTrainNo = user.getString("trainNumber");
        }
		catch (Exception e) {
			System.out.print("");
		}
		
		CtrainName.setText(requiredTrainNo + " : " + requiredTainName);
	}
	
	// Want to continue method declaration
	public void wantToContinue(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("continue.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	// Tickets Available Checking form Source and Destination selection method
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ObservableList<String> l1 = FXCollections.observableArrayList("Chennai", "Delhi", "Mumbai", "Kolkata", "Bangalore");
		ObservableList<String> l2 = FXCollections.observableArrayList("Mumbai", "Bangalore", "Kolkata", "Chennai", "Delhi");

		try{
			Asource.setItems(l1);
			Adestination.setItems(l2);
		}
		catch(Exception e) {
			System.out.print("");
		}
	
	}
	
	// To select source for the available ticket form
	public String selectSource() throws IOException {
		String s1 = Asource.getSelectionModel().getSelectedItem();
		if (s1 == null) {
			return null;
		}
		return s1.toString();
	}
	
	// To select destination for the available ticket form
	public String selectDest() throws IOException {
		String s2 = Adestination.getSelectionModel().getSelectedItem();
		if (s2 == null) {
			return null;
		}
		return s2.toString();
	}
	
	// To show available tickets to user
	public void showTickets(ActionEvent event) throws IOException {
		AgetSource = selectSource();
		AgetDest = selectDest();
		
		if(AgetSource != null && AgetDest != null && AgetSource != AgetDest && !Adate.getEditor().getText().isEmpty()) {
			
			String nameOfTrain = AgetSource.substring(0,3) + "-" + AgetDest.substring(0,3) + " Express";
			Document trainSeatBal = trainDetails.find(new Document("trainName", nameOfTrain)).first();
			
			if(trainSeatBal != null) {
				
				Alert alert = new Alert(AlertType.INFORMATION);
	            alert.setTitle("Available Tickets for `" + AgetSource + "` to `" + AgetDest + "`");
	            alert.setHeaderText(null);
	            alert.setContentText("Available Tickets in Lower Berth : " + String.valueOf(trainSeatBal.getInteger("lowerBerth")) +
	            "\nAvailable Tickets in Middle Berth : " + String.valueOf(trainSeatBal.getInteger("middleBerth")) + 
	            "\nAvailable Tickets in Upper Berth : " + String.valueOf(trainSeatBal.getInteger("upperBerth")) + 
	            "\nAvailable Tickets in RAC Berth : " + String.valueOf(trainSeatBal.getInteger("racBerth")) + 
	            "\nWaiting List Tickets : " + String.valueOf(trainSeatBal.getInteger("waitingList")));
	            alert.showAndWait();
	            
	            root = (BorderPane) FXMLLoader.load(getClass().getResource("userMenu.fxml"));
	    		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
	    		scene = new Scene(root);
	    		stage.setScene(scene);
	    		stage.show();
			}
		}
		else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Please Provide Valid Information");
			alert.setHeaderText(null);
			alert.setContentText("Please Enter Passenger's Journey Details Correctly!");
			alert.showAndWait();
		}
	}
	
//-------------------------------------------------------------------------------------------------------------------------------------//
//-------------------------------------------------------------------------------------------------------------------------------------//
	@FXML private Button bookedTickets, passengerInfo, editTrain, adForgotPass, makeAdmin, adPassSubmit;
	@FXML private BorderPane adPane, mainPane = new BorderPane();
	@FXML private TextField adPassNumber;
	@FXML private ComboBox<String> bsource, bdestination;
	Admin admin = new Admin();

	// Login as admin
	public void loginAdmin(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("login_admin.fxml"));
		Pane view = loader.load();
		mainPane.setCenter(view);
	}
	
	// Login as user
	public void loginUser(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("login_user.fxml"));
		Pane view = loader.load();
		mainPane.setCenter(view);
	}
	
	// Admin see booked tickets of a train
	@FXML private TextField checkTrainName;
	@FXML private Button trainSubmit;
	
	public void bookedTickets(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("adBookedTicket.fxml"));
		Pane view = loader.load();
		adPane.setCenter(view);
	}
	
	// Admin checking the tickets booked by the passengers
	public void checkTrainTickets(ActionEvent event) throws IOException {
		System.out.println("Edukku");
	}
	
	
	// Admin see the passenger info
	// For finding all the documents in the username
	Iterable<Document> findResult;
	
	public void passengerInfo(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("adPassInfo.fxml"));
		Pane view = loader.load();
		adPane.setCenter(view);
	}
	
	private Iterable<Document> getFindResult(String tNoName) {		
		findResult = passengerDetails.find(new Document("Ticket_Details.Train_Name_Number", tNoName));
		if(findResult != null) return findResult;
        return null;
    }
	
	// Admin check passenger info by submitting train number
	public void checkPassInfo(ActionEvent event) throws IOException {
		String tnumber = adPassNumber.getText();
		Document trainDoc = trainDetails.find(new Document("trainNumber", tnumber)).first();
		
		if(!tnumber.isEmpty()  || trainDoc != null) {
		 
			Stage stage = new Stage();
			VBox vbox = new VBox();
			vbox.setSpacing(10);
			
	
			
			String trainName = trainDoc.getString("trainName");
			
			String tNoName = tnumber + " : " + trainName;
			
			findResult = getFindResult(tNoName);
			
			int count = 0;
			Bson filter =  Filters.eq("Ticket_Details.Train_Name_Number", tNoName);
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
		else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Occurs");
			alert.setHeaderText(null);
			alert.setContentText("Please Provide Valid Train Number!");
			alert.showAndWait();
		}
	}
	
	// Admin can edit train schedule
	public void editTrain(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("login_admin.fxml"));
		Pane view = loader.load();
		adPane.setCenter(view);
	}
	
	// To make someone admin
	public void makeAdmin(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("adSignup.fxml"));
		Pane view = loader.load();
		adPane.setCenter(view);
	}
	
	// To change password for an admin account
	public void adForgotPass(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("adForgetPass.fxml"));
		Pane view = loader.load();
		adPane.setCenter(view);
	}
	
	// For admin forgot password
	public void getToForgetPass(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("adForgetPass.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	//To change password of the admin
	public void adminPassChange(ActionEvent event) throws IOException {
		String fPassword = adFPass.getText();
		
		if(fPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
	        Document filter = new Document("Username", adFName.getText());
	        Document update = new Document("$set", new Document("Password", fPassword));
	        adminSignin.updateOne(filter, update);
	        mongo.close();
	        
	        Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText(null);
			alert.setContentText("Password was changed successfully!");
			alert.showAndWait();
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
	
	// For Logging in the admin
	public void isThisAdmin(ActionEvent event) throws IOException {
		
		if(admin.nameAdmin(adminName.getText(), adminPass.getText()) || 
				admin.emailName(adminName.getText(), adminPass.getText())) {
			
			adminMenu(event);
    	}
    	else {
    		Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Occured");
			alert.setHeaderText(null);
			alert.setContentText("Enter your credentials correctly!");
			alert.showAndWait();
   		}
	}

	// For admin menus
	public void adminMenu(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("adMenu.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	
	// For admins sign up
	public void getToSignup(ActionEvent event) throws IOException {
		root = (BorderPane) FXMLLoader.load(getClass().getResource("adSignup.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	// To create admin account
	Document adminAcc;
	public void adSCreateAcc(ActionEvent event) throws IOException {
		final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
		
		if (adSName.getText().matches("^[a-zA-Z0-9@.-_].{6,}$")) {
			
			if(adSEmail.getText().matches(EMAIL_REGEX)) {
				
				if(adSPass.getText().matches("^[a-zA-Z0-9@.-_].{8,}$")) {
					
					adminAcc = new Document("Username", adSName.getText());
					adminAcc.append("Email", adSEmail.getText());
					adminAcc.append("Password", adSPass.getText());
	    			
	    			Alert alert = new Alert(AlertType.CONFIRMATION);
	    			alert.setTitle("Confirmation");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Your account was created successfully!");
	    			alert.showAndWait();
	    			
	    			adminSignin.insertOne(adminAcc);
	    			
				}
				else {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Validating Password");
					alert.setHeaderText(null);
					alert.setContentText("Please Provide valid Password! Use Lowercase, Uppercase, Digits with the length of atleast 8 characters for password");
					alert.showAndWait();
				}
			}
			else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Validating Email ID");
				alert.setHeaderText(null);
				alert.setContentText("Please Provide valid Email!");
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
		
}

// For authenticating admin
class Admin{
	
	public Admin() {
		connect();
	}

	// To Connect with Mongodb
	MongoClient mongo;
	MongoDatabase db;
	MongoCollection<org.bson.Document> adminSignin;
	
	public void connect() {
		mongo = new MongoClient("localhost", 27017);
		db = mongo.getDatabase("RailwaySystem");
		adminSignin = db.getCollection("adminSignin");
	}
	
	public boolean nameAdmin(String username, String password) {
		try {
            Document user = adminSignin.find(new Document("Username", username)).first();
            
            if (user != null && user.getString("Password").equals(password)) return true; 
        }
		catch (Exception e) {
			System.out.println(e);
		}
        return false;
    }
	
	public boolean emailName(String email, String password) {
		try{
            Document user = adminSignin.find(new Document("Email", email)).first();
            
            if (user != null && user.getString("Password").equals(password)) return true; 
        }
		catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}
}