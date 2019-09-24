import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class CustomerInterface {
	private static Console c;
	private static Connection con;
	private static Statement st;

	public static boolean run(Console console, Connection connection, Statement statement) {
		c = console;
		con = connection;
		st = statement;
		System.out.println("\nWelcome Esteemed Customer!");
		int customer = -1;
		while((customer = login()) > 0) {
			int response = -1;
			while((response = mainMenu(customer)) > 0) {} //handles back navigation of menus
			//if response == 0, logout was chosen
			if(response == -1) {
				return true; //Quit
			}
		}
		if(customer == 0) { //Back
			return false;
		}
		return true; //Quit
	}

	private static String hashPassword(String email, String password) {
		CallableStatement callSt = null;
		try {
			callSt = con.prepareCall("{? = call get_hash(?,?)}");
			callSt.registerOutParameter(1, Types.VARCHAR);
			callSt.setString(2, email);
			callSt.setString(3, password);
			callSt.execute();
			return callSt.getString(1);
		}
		catch(Exception e) {
			System.out.println("\nAn error occurred hashing the password. Please try again.");
			return "";
		}
		finally {
			try{callSt.close();} catch(Exception e){};
		}
	}

	private static int login() { //returns the customer_id
		while(true) {
			System.out.println("\nPlease choose an option:\n1 Sign-in\n2 New Customer\n3 Back\n4 Quit");
			int loginMethod = Helper.getIntInput(c);
			String email = "";
			String password = "";
			switch(loginMethod) {
				case 1: //Sign-in
					System.out.print("Email: ");
					email = Helper.convertEmail(Helper.checkSingleQuote(c.readLine()));
					if(email.equals(""))
					{
						System.out.println("\nInvalid email.");
						break;
					}
					System.out.print("Password: ");
					password = String.valueOf(c.readPassword());
					if(password.equals("")) { //empty string password
						System.out.println("\nInvalid password.");
						break;
					}
					password = Helper.checkSingleQuote(hashPassword(email, password));
					try(
						ResultSet result = st.executeQuery("select * from customer where email = '" + email + "' and password = '" + password + "'");
					){
						if(result.next()) {
							System.out.println("\nLogin successful! Welcome back " + result.getString("name") + "!");
							return result.getInt("customer_id");
						}
						else {
							System.out.println("\nIncorrect email/password. Login failed.");
						}
					}
					catch(Exception e2) {
						System.out.println("\nSorry, an error occurred. Please try again.");
					}
					break;
				case 2: //New Customer
					//get input for new customer
					//get name and email
					System.out.print("Full Name: ");
					String name = Helper.checkSingleQuote(c.readLine());
					System.out.print("Email: ");
					email = Helper.convertEmail(Helper.checkSingleQuote(c.readLine()));
					if(email.equals(""))
					{
						System.out.println("Invalid email.");
						break;
					}
					int validEmail = Helper.checkExistence(st, "select * from customer where email = '" + email + "'");
					if(validEmail == 0) {
						System.out.println("That email is already in use by another account!");
						break;
					}
					else if(validEmail == -1) {
						System.out.println("\nSorry, an error occurred. Please try again.");
						break;
					}

					//get address info //doesn't call "createAddress()" because we want to confirm all input (including name/email), not just address info
					System.out.print("Address: ");
					String address = Helper.checkSingleQuote(c.readLine());
					System.out.print("City: ");
					String city = Helper.checkSingleQuote(c.readLine());
					System.out.print("State/Province: ");
					String state = Helper.checkSingleQuote(c.readLine());
					System.out.print("Country: ");
					String country = Helper.checkSingleQuote(c.readLine());
					System.out.print("Planet: ");
					String planet = Helper.checkSingleQuote(c.readLine());
					
					//confirm info
					System.out.println("Name: " + name + "\nEmail: " + email + "\nAddress: " + address + "\nCity: "
						+ city + "\nState/Province: " + state + "\nCountry: " + country + "\nPlanet: " + planet);
					System.out.println("\nIs this information correct (you can also edit your info later in your Account->Personal Information):\n1 Yes\n2 No");
					int confirm = Helper.getIntInput(c);
					switch(confirm){
						case 1: //yes
							//create a password
							String checkPassword = "";
							do {
								System.out.print("\nCreate Password: ");
								password = String.valueOf(c.readPassword());
								System.out.print("Confirm Password: ");
								checkPassword = String.valueOf(c.readPassword());
							} while(!password.equals(checkPassword));
							password = Helper.checkSingleQuote(hashPassword(email, password));
							if(password.equals("")){
								break;
							}

							//create address and customer
							int address_id = Helper.runGeneratedIdInsert(st, "insert into address (address, city, state_province, country, planet) values ('" 
								+ address + "', '" + city + "', '" + state + "', '" + country + "', '" + planet + "')", "address_id");
							//create address
							if(address_id == 0) {
								System.out.println("\nSorry, an error occurred. Please try again.");
								break;
							}
							//create customer
							int customer = Helper.runGeneratedIdInsert(st, "insert into customer (name, email, address_id, password) values ('" 
								+ name + "', '" + email + "', " + address_id + ", '" + password + "')", "customer_id");
							if(customer == 0) {
								System.out.println("\nSorry, an error occurred. Please try again.");
								break;
							}
							return customer;
						case 2: //no
							break; //future ideas: allow user to edit previously inputted data
					}
					break;
				case 3: //Back
					return 0;
				case 4: //Quit
					return -1;
			}
		}
	}

	private static int mainMenu(int customer) {
		while(true){
			System.out.println("\nHome Page:\n1 Account\n2 Buy New Vehicle\n3 Location Information\n4 Logout\n5 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1: //Account
					if(account(customer) == -1) {
						return -1; //quit, otherwise back
					}
					break;
				case 2: //Buy Vehicle
					if(buyNewVehicle(customer) == -1) {
						return -1; //quit, otherwise back
					}
					break;
				case 3: //Lookup Locations
					if(locationInfo(customer) == -1) {
						return -1; //quit, otherwise back
					}
					break;
				case 4: //Logout
					return 0;
				case 5: //Quit
					return -1;
			}
		}
	}

	private static int account(int customer) {
		while(true){
			System.out.println("\nAccount:\n1 Personal Information\n2 View Owned Vehicles\n3 View Vehicles Ready for Pickup\n4 View Transactions\n5 Back\n6 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1: //Edit Personal Info
					if(personalInfo(customer) == -1) {
						return -1;
					}
					break;
				case 2: //View Owned Vehicles, if any
					Helper.viewOwnedVehicles(st, customer);
					break;
				case 3: //View vehicles that are waiting to be picked up
					Helper.viewVehiclesPickup(st, customer);
					break;
				case 4: //View Transactions with Alset
					viewTransactions(customer);
					break;
				case 5: //Logout
					return 0;
				case 6: //Quit
					return -1;
			}
		}
	}

	private static void viewTransactions(int customer) {
		//View transactions where customer bought a vehicle
		try(
			ResultSet rs1 = st.executeQuery("select * from transactions_sell S join vehicle using(vec_id) where S.customer_id = " + customer);
		){
			if(!rs1.next()) {
				System.out.println("\nYou have no history of purchasing vehicle(s) from Alset.");
			}
			else {
				System.out.println("\n---Purchase Transactions---");
				System.out.println(String.format("%-6s\t%-5s\t%-4s\t%-10s\t%-16s\t%-10s", "VEC_ID", "MODEL", "YEAR", "DATE", "CARD NUMBER", "TOTAL COST"));
				do {
					System.out.println(String.format("%-6d\t%-5s\t%-4d\t%-10s\t%-16d\t%-10d", rs1.getInt("vec_id"), rs1.getString("model"), rs1.getInt("year"),
						rs1.getDate("date_transaction").toString(), rs1.getLong("card_number"), rs1.getLong("total_cost")));
				} while(rs1.next());
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred when accessing your purchase history.");
		}
		//View transactions where customer sold back a vehicle
		try(
			ResultSet rs2 = st.executeQuery("select * from transactions_buy B join vehicle using(vec_id) where B.customer_id = " + customer);
		){
			if(!rs2.next()) {
				System.out.println("\nYou have no history of selling back vehicle(s) to Alset.");
			}
			else {
				System.out.println("\n---Sell Back Transactions---");
				System.out.println(String.format("%-6s\t%-5s\t%-4s\t%-10s\t%-16s\t%-10s", "VEC_ID", "MODEL", "YEAR", "DATE", "CARD NUMBER", "TOTAL COST"));
				do {
					System.out.println(String.format("%-6d\t%-5s\t%-4d\t%-10s\t%-16d\t%-10d", rs2.getInt("vec_id"), rs2.getString("model"), rs2.getInt("year"),
						rs2.getDate("date_transaction").toString(), rs2.getLong("card_number"), rs2.getLong("total_cost")));
				} while(rs2.next());
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred when accessing your sell back history.");
		}
	}

	private static int personalInfo(int customer) {
		while(true) {
			System.out.println("\nPersonal Information:\n1 Display Information\n2 Change Name\n3 Change Email\n4 Change Address\n5 View/Add Card(s)\n6 Change Password\n7 Back\n8 Quit");
			int command = Helper.getIntInput(c);
			//all changes requires password confirmation
			switch(command) {
				case 1: //display personal info
					displayInfo(customer);
					break;
				case 2:
					changeName(customer);
					break;
				case 3:
					changeEmail(customer);
					break;
				case 4:
					changeAddress(customer);
					break;
				case 5:
					if(handleCards(customer) == -1) {
						return -1;
					}
					break;
				case 6:
					changePassword(customer);
					break;
				case 7:
					return 0;
				case 8:
					return -1;
			}
		}
	}

	private static void displayInfo(int customer) {
		try(
			ResultSet rs = st.executeQuery("select * from customer join address using(address_id) where customer_id = " + customer);
		){
			if(!rs.next()) {
				throw new Exception();
			}
			System.out.println("\n---Account Information---");
			System.out.println("Name: " + rs.getString("name"));
			System.out.println("Email: " + rs.getString("email"));
			System.out.println("Address: " + rs.getString("address") + "\nCity: "+ rs.getString("city") + "\nState/Province: " 
				+ rs.getString("state_province") + "\nCountry: " + rs.getString("country") + "\nPlanet: " + rs.getString("planet"));
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
	}

	private static void changeName(int customer) {
		try(
			ResultSet rs = st.executeQuery("select * from customer where customer_id = " + customer);
		){
			if(!rs.next()) {
				throw new Exception();
			}
			System.out.print("Old Name: ");
			if(!rs.getString("name").equals(Helper.checkSingleQuote(c.readLine()))) {
				System.out.println("\nName is not correct!");
				return;
			}
			System.out.print("New Name: ");
			String newName = Helper.checkSingleQuote(c.readLine());
			System.out.print("Password: ");
			if(!rs.getString("password").equals(hashPassword(rs.getString("email"), String.valueOf(c.readPassword())))) {
				System.out.println("\nPassword is not correct!");
				return;
			}
			try{
				st.executeQuery("update customer set name = '" + newName + "' where customer_id = " + customer);
				System.out.println("\nName updated successfully to " + newName + "!");
			}
			catch(Exception e) {
				System.out.println("\nSorry, an error occurred. Please try again.");
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
	}

	private static void changeEmail(int customer) {
		try(
			ResultSet rs = st.executeQuery("select * from customer where customer_id = " + customer);
		){
			if(!rs.next()) {
				throw new Exception();
			}
			System.out.print("Old Email: ");
			if(!rs.getString("email").equals(Helper.convertEmail(Helper.checkSingleQuote(c.readLine())))) {
				System.out.println("\nEmail is not correct!");
				return;
			}
			System.out.print("New Email: ");
			String newEmail = Helper.convertEmail(Helper.checkSingleQuote(c.readLine()));
			if(newEmail.equals("")) {
				System.out.println("\nInvalid email.");
				return;
			}
			int validEmail = Helper.checkExistence(st, "select * from customer where email = '" + newEmail + "'");
			if(validEmail == 0) {
				System.out.println("\nEmail already in use by another account.");
				return;
			}
			else if(validEmail == -1) {
				throw new Exception();
			}
			System.out.print("Password: ");
			String password = String.valueOf(c.readPassword());
			if(!rs.getString("password").equals(hashPassword(rs.getString("email"), password))) {
				System.out.println("\nPassword is not correct!");
				return;
			}
			try{
				st.executeQuery("update customer set email = '" + newEmail + "', password = '" + Helper.checkSingleQuote(hashPassword(newEmail, password)) + "' where customer_id = " + customer);
				System.out.println("\nEmail updated successfully to " + newEmail + "!");
			}
			catch(Exception e) {
				System.out.println("\nSorry, an error occurred. Please try again. 2");
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again. 1");
		}
	}

	private static void changeAddress(int customer) {
		//get address info
		System.out.println("\nInput new address information:");
		System.out.print("Address: ");
		String address = Helper.checkSingleQuote(c.readLine());
		System.out.print("City: ");
		String city = Helper.checkSingleQuote(c.readLine());
		System.out.print("State/Province: ");
		String state = Helper.checkSingleQuote(c.readLine());
		System.out.print("Country: ");
		String country = Helper.checkSingleQuote(c.readLine());
		System.out.print("Planet: ");
		String planet = Helper.checkSingleQuote(c.readLine());
		System.out.println("Is the following address correct?\n");
		System.out.println("\nAddress: " + address + "\nCity: "+ city + "\nState/Province: " 
			+ state + "\nCountry: " + country + "\nPlanet: " + planet);
		System.out.println("\nIs this information correct:\n1 Yes\n2 No");
		int confirm = Helper.getIntInput(c);
		switch(confirm) {
			case 1: //yes
				try(
					ResultSet rs1 = st.executeQuery("select * from customer where customer_id = " + customer);
				){
					if(!rs1.next()) {
						throw new Exception();
					}
					//password check
					System.out.print("Password: ");
					String password = String.valueOf(c.readPassword());
					if(!rs1.getString("password").equals(hashPassword(rs1.getString("email"), password))) {
						System.out.println("\nPassword is not correct!");
						return;
					}
					try {
						st.executeQuery("update address set address = '" + address + "', city = '" + city 
							+ "', state_province = '" + state + "', country = '" + country + "', planet = '" + planet 
							+ "' where address_id = (select address_id from customer where customer_id = " + customer + ")");
						System.out.println("\nAddress updated successfully!");
					}
					catch(Exception e) {
						System.out.println("\nSorry, an error occurred. Please try again. 2");
					}
				}
				catch(Exception e) {
					System.out.println("\nSorry, an error occurred. Please try again. 1");
				}
				return;
			case 2: //no
				return;
		}
	}

	private static int handleCards(int customer) {
		while(true) {
			System.out.println("\nChoose an action:\n1 View Card(s)\n2 Add Card\n3 Back\n4 Quit");
			int command2 = Helper.getIntInput(c);
			switch(command2) {
				case 1:
					viewCards(customer);
					break;
				case 2:
					Helper.addCard(st, c, customer);
					break;
				case 3: //back
					return 0;
				case 4: //quit
					return -1;
			}
		}
	}

	private static void viewCards(int customer) {
		try(
			ResultSet rs = st.executeQuery("select * from payment_card join customer using(customer_id) where customer_id = " + customer);
		){
			if(!rs.next()) {
				System.out.println("\nYou have no cards on record!");
				return;
			}
			System.out.println("\n---Your Cards---");
			System.out.println(String.format("%-16s", "CARD_NUMBER"));
			do {
				System.out.println(String.format("%-16d", rs.getLong("card_number")));
			} while(rs.next());
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
	}

	private static void changePassword(int customer) {
		try(
			ResultSet rs = st.executeQuery("select * from customer where customer_id = " + customer);
		){
			if(!rs.next()) {
				throw new Exception();
			}
			String email = rs.getString("email");
			System.out.print("Old Password: ");
			String oldPassword = String.valueOf(c.readPassword());
			if(!rs.getString("password").equals(hashPassword(email, oldPassword))) {
				System.out.println("\nPassword is not correct!");
				return;
			}
			//get and confirm new password
			System.out.print("New Password: ");
			String newPassword = String.valueOf(c.readPassword());
			System.out.print("Confirm New Password: ");
			if(!newPassword.equals(String.valueOf(c.readPassword()))) {
				System.out.println("\nPasswords don't match!");
				return;
			}
			//update password
			try{
				st.executeQuery("update customer set password = '" + Helper.checkSingleQuote(hashPassword(email, newPassword)) + "' where customer_id = " + customer);
				System.out.println("\nPassword updated successfully!");
			}
			catch(Exception e) {
				System.out.println("\nSorry, an error occurred. Please try again.");
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
	}

	private static int buyNewVehicle(int customer) {
		//search model and year, then have customer choose one of the available options
		String model = Helper.getModel(c);
		try(
			ResultSet rs = st.executeQuery("select * from options_prices where model = '" + model + "'");
		){
			if(!rs.next()) {
				throw new Exception();
			}
			//print all current options
			System.out.println("\nChoose a Vehicle Option:");
			System.out.println(String.format("%-2s\t%-5s\t%-4s\t%-10s\t%-13s\t%-10s\t%-10s", "", "MODEL", "YEAR", "AUTO_PILOT", "EJECTOR_SEATS", "PHONE_SYNC", "PRICE"));
			int counter = 1;
			ArrayList<Integer> options = new ArrayList<>();
			do {
				options.add(rs.getInt("option_id"));
				String auto_pilot = "No";
				String ejector_seats = "No";
				String phone_sync = "No";
				if(rs.getInt("auto_pilot") == 1) {
					auto_pilot = "Yes";
				}
				if(rs.getInt("ejector_seats") == 1) {
					ejector_seats = "Yes";
				}
				if(rs.getInt("phone_sync") == 1) {
					phone_sync = "Yes";
				}
				System.out.println(String.format("%-2d\t%-5s\t%-4d\t%-10s\t%-13s\t%-10s\t%-10d", counter, rs.getString("model"), rs.getInt("year"), auto_pilot, ejector_seats, phone_sync, rs.getLong("price")));
				counter++;
			} while(rs.next());
			System.out.println(counter + " Back\n" + (counter + 1) + " Quit");
			int command = 0;
			while((command < 1) || (command > counter + 1)) {
				command = Helper.getIntInput(c);
				if(command == counter) { //back
					return 0;
				}
				if(command == counter + 1) { //quit
					return -1;
				}
				for(int i = 1; i <= counter; i++) {
					if(command == i) {
						return findPurchaseInfo(customer, options.get(i-1));
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
		return 0;
	}

	private static int findPurchaseInfo(int customer, int option) {
		long card_number = 0;
		int location_id = 0;
		while(true) {
			System.out.println("\nChoose an action (must specify card and location before purchase):\n1 Choose Card\n2 Choose Pickup Location\n3 Make Purchase\n4 Back\n5 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1: //choose card 
					card_number = Helper.chooseCard(st, c, customer);
					if(card_number == -1) {
						return -1;
					}
					break;
				case 2: //choose pickup location
					location_id = choosePickupLocation(customer, option);
					if(location_id == -1) {
						return -1;
					}
					break;
				case 3: //make transaction
					if(card_number == 0 || location_id == 0) {
						System.out.println("\nPlease choose both a card and location first!");
					}
					else {
						makePurchase(customer, card_number, location_id, option);
						return 0;
					}
					break;
				case 4: //back
					return 0;
				case 5: //quit
					return -1;
			}
		}
	}

	private static int choosePickupLocation(int customer, int option) {
		while(true) {
			System.out.println("\nChoose an option:\n1 Use Preferred Location\n2 Search by Planet\n3 Search by Model\n4 Back\n5 Quit");
			int command = Helper.getIntInput(c);
			String model = getModelofVehicle(option);
			int location = 0;
			if(model.equals("")) {
				System.out.println("\nSorry, an error occurred. Please try again.");
				return 0;
			}
			switch(command) {
				case 1:
					location = tryPreferredLocation(customer, model); //-1 doesn't exist, 0 doesn't support model
					if(location > 0) {
						System.out.println("\nLocation set!");
						return location;
					}
					else if(location == 0){
						System.out.println("\nPreferred location does not support the model " + model + "!");
					}
					else {
						System.out.println("\nYou do not have a preferred location set!");
					}
					break;
				case 2:
					location = searchByPlanet(model);
					if(location == -1) {
						return -1;
					}
					else if(location == 0) {
						break;
					}
					else {
						System.out.println("\nLocation set!");
						return location;
					}
				case 3: //search by model
					location = searchByModel(model);
					if(location == -1) {
						return -1;
					}
					else if(location == 0) {
						break;
					}
					else {
						System.out.println("\nLocation set!");
						return location;
					}
				case 4: //back
					return 0;
				case 5: //quit
					return -1;
			}
		}

	}

	private static String getModelofVehicle(int option) {
		try(
			ResultSet rs = st.executeQuery("select model from options_prices where option_id = " + option);
		){
			if(!rs.next()) {
				throw new Exception();
			}
			return rs.getString("model");
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
			return "";
		}
	}

	private static int tryPreferredLocation(int customer, String model) {
		int location = 0;
		//get preferred location
		try(
			ResultSet rs = st.executeQuery("select location_id from customer where customer_id = " + customer);
		){
			if(!rs.next()) {
				return -1;
			}
			location = rs.getInt("location_id");
		}
		catch(Exception e) {
			return -1;
		}

		//check preferred location
		if(location == 0) { //preferred location not set!
			return -1;
		}
		try(
			ResultSet rs2 = st.executeQuery("select * from location_models where model = '" + model + "' and location_id = " + location);
		){
			if(rs2.next()) {
				return location;
			}
			return 0;
		}
		catch(Exception e) {
			return 0;
		}
	}

	private static int searchByPlanet(String model) {
		System.out.print("\nPlanet: ");
		String planet = Helper.checkSingleQuote(c.readLine());
		try(
			ResultSet rs1 = st.executeQuery("select * from address join location L using(address_id) where planet = '" + planet 
				+ "' and exists(select * from location_models where location_id = L.location_id and model = '" + model + "')");
		){
			return Helper.chooseLocationFromList(rs1, c);
		}
		catch(Exception e) {}
		System.out.println("\nSorry, an error occurred. Please try again. 10");
		return 0;
	}

	private static int searchByModel(String model) {
		try(
			ResultSet rs1 = st.executeQuery("select * from location_models join location using(location_id) join address using(address_id) where model = '" + model + "'");
		){
			return Helper.chooseLocationFromList(rs1, c);
		}
		catch(Exception e) {}
		System.out.println("\nSorry, an error occurred. Please try again.");
		return 0;
	}

	private static void makePurchase(int customer, long card_number, int location_id, int option) {
		try(
			ResultSet rs1 = st.executeQuery("select * from vehicle_options_prices join vehicle using(vec_id) where option_id = " + option + " and customer_id is null and status = 'new'");
		){
			if(rs1.next()) { //use a new vehicle already created that isn't owned
				Helper.createTransactionSell(st, customer, rs1.getInt("vec_id"), card_number, location_id, true);
			}
			else { //create a new vehicle and use that
				//get model and year of the vehicle
				String model = "";
				int year = 0;
				try(
					ResultSet rs2 = st.executeQuery("select * from options_prices where option_id = " + option);
				){
					if(!rs2.next()) {
						throw new Exception();
					}
					model = rs2.getString("model");
					year = rs2.getInt("year");
				}
				catch(Exception e) {
					System.out.println("\nPurchase failed! Try again later. 2");
					return;
				}

				//insert into vehicle
				int vec_id = Helper.runGeneratedIdInsert(st, "insert into vehicle (customer_id, model, year, status, discount, condition, total_price) values(" 
					+ customer + ", '" + model + "', " + year + ", 'new', 0, 'great', 0)", "vec_id");
				if(vec_id == 0) {
					System.out.println("\nPurchase failed! Try again later. 3");
					return;
				}

				//insert into vehicle_options_prices
				//Does not handle case where query fails after creating vehicle, but before creating vehicle_options_prices which causes an illegal vehicle
				try{
					//vec_id, option_id, model, year
					st.executeQuery("insert into vehicle_options_prices values(" + vec_id + ", " + option + ", '" + model + "', " + year + ")");
				}
				catch(Exception e) {
					System.out.println("\nPurchase failed! Try again later. 4");
					return;
				}

				//create transaction sell
				Helper.createTransactionSell(st, customer, vec_id, card_number, location_id, true);
			}
		}
		catch(Exception e) {
			System.out.println("\nPurchase failed! Try again later. 1");
		}
	}

	private static int locationInfo(int customer) {
		while(true) {
			System.out.println("\nService Location Information:\n1 Search by Planet\n2 Search by Model Types\n3 Use Preferred Location\n4 Back\n5 Quit");
			int command = Helper.getIntInput(c);
			int location = -1;
			int locReturn = -1;
			switch(command) {
				case 1: //Search by Planet
					location = Helper.searchByPlanet(st, c);
					if(location == -1) {
						return -1;
					}
					else if(location == 0) {
						break;
					}
					locReturn = locationFunctions(customer, location);
					if(locReturn == -1) {
						return -1;
					}
					break;
				case 2: //Search by Model Types
					location = searchByModel();
					if(location == -1) {
						return -1;
					}
					else if(location == 0) {
						break;
					}
					locReturn = locationFunctions(customer, location);
					if(locReturn == -1) {
						return -1;
					}
					break;
				case 3:
					try(
						ResultSet rs3 = st.executeQuery("select * from address join location using(address_id) join customer using(location_id) where customer_id = '" 
							+ customer + "'");
					){
						if(rs3.next()) {
							location = rs3.getInt("location_id");
						}
						else {
							throw new Exception();
						}
						if(location != 0) { //Navigate to locationFunctions with preferred location
							System.out.println("\nPreferred Location Address: ");
							System.out.println(String.format("%-25s\t%-25s\t%-25s\t%-30s\t%-20s", "ADDRESS", "CITY", "STATE/PROVINCE", "COUNTRY", "PLANET"));
							System.out.println(String.format("%-25s\t%-25s\t%-25s\t%-30s\t%-20s", rs3.getString("address"), rs3.getString("city"),
								rs3.getString("state_province"), rs3.getString("country"), rs3.getString("planet")));
							locReturn = locationFunctions(customer, location);
							if(locReturn == -1) {
								return -1;
							}
						}
						else {
							System.out.println("\nNo Preferred Location Set!");
						}
					}
					catch(Exception e){
						System.out.println("\nSorry, an error occurred. Please try again.");
					}
					break;
				case 4: //Back
					return 0;
				case 5: //Quit
					return -1;
			}
		}
	}

	private static int searchByModel() {
		String model = Helper.getModel(c);
		try(
			ResultSet rs1 = st.executeQuery("select * from location_models join location using(location_id) join address using(address_id) where model = '" + model + "'");
		){
			return Helper.chooseLocationFromList(rs1, c);
		}
		catch(Exception e) {}
		System.out.println("\nSorry, an error occurred. Please try again.");
		return 0;
	}

	private static int locationFunctions(int customer, int location) {
		while(true) {
			System.out.println("\nLocation Functions:\n1 List Hours\n2 List Used Vehicle Inventory\n3 Set Preferred\n4 Back\n5 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1: //list hours
					Helper.displayHours(st, location);
					break;
				case 2: //list used vehicles
					//Could be fleshed out more to display more vehicle information
					Helper.viewLocationUsedInventory(st, location);
					break;
				case 3: //set location as preferred
					try{
						st.executeQuery("update customer set location_id = " + location + " where customer_id = " + customer);
						System.out.println("\nUpdated preferred location successfully!");
					}
					catch(Exception e) {
						System.out.println("\nSorry, an error occurred. Please try again.");
					}
					break;
				case 4: //back
					return 0;
				case 5: //quit
					return -1;
			}
		}
	}
}