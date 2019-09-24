import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class Helper {
	protected static String checkSingleQuote(String input) {
		String returnStr = "";
		for(int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			returnStr += c;
			if(c == '\'') {
				returnStr += c;
			}
		}
		return returnStr;
	}

	protected static int getIntInput(Console c) {
		while(true) {
			try {
				return Integer.parseInt(c.readLine());
			}
			catch(Exception e) {
				System.out.println("\nPlease enter a valid number.");
			}
		}
	}

	protected static long getLongInput(Console c) {
		while(true) {
			try {
				return Long.parseLong(c.readLine());
			}
			catch(Exception e) {
				System.out.println("\nPlease enter a valid number.");
			}
		}
	}

	protected static int checkExistence(Statement st, String query) {
		try(
			ResultSet rs = st.executeQuery(query);
		){
			if(rs.next()){
				return 0;
			}
			return 1;
		}
		catch(Exception e){}
		return -1;
	}

	//Standardizes the email. The beginning portion to the left of the @ sign is case sensitive, while the domain on the right is not.
	protected static String convertEmail(String email) {
		int i = 0;
		if((i = email.indexOf("@")) != -1) {
			return (email.substring(0, i+1) + email.substring(i+1).toLowerCase());
		}
		else {
			return "";
		}
	}

	protected static int runGeneratedIdInsert(Statement st, String query, String generatedKey) {
		String [] return_key1 = new String [] { generatedKey };
		try{
			st.execute(query, return_key1);
			try(
				ResultSet rs = st.getGeneratedKeys();
			){
				if(rs.next()) {
					return rs.getInt(1);
				}
			}
			catch(Exception e) {}
		}
		catch(Exception e) {}
		return 0;
	}

	protected static long getTotal(Statement st, String query) {
		try(
			ResultSet rs1 = st.executeQuery(query);
		){
			if(rs1.next()) {
				return rs1.getLong(1);
			}
			else {
				return 0;
			}
		}
		catch(Exception e) {}
		return -1;
	}

	protected static String printMoney(Long number) {
		DecimalFormat format = new DecimalFormat("$#,##0.00;-$#,##0.00");
		return format.format(number);
	}

	protected static long chooseCard(Statement st, Console c, int customer) {
		try(
			ResultSet rs1 = st.executeQuery("select * from payment_card where customer_id = " + customer);
		){
			ArrayList<Long> cards = new ArrayList<>();
			int counter = 1;
			if(rs1.next()) {
				System.out.println("\nChoose a card/option:");
				System.out.println(String.format("%-2s\t%-25s", "", "LAST_4_DIGITS_CARD_NUMBER"));
				do {
					cards.add(rs1.getLong("card_number"));
					System.out.println(String.format("%-2d\t%-4d", counter, rs1.getLong("card_number") % 10000));
					counter++;
				} while(rs1.next());
			}
			System.out.println(counter + " New Card\n" + (counter + 1) + " Back\n" + (counter + 2) + " Quit");
			int command = 0;
			while((command < 1) || (command > counter + 2)) {
				command = getIntInput(c);
				if(command == counter) {
					long card = addCard(st, c, customer);
					if(card > 0){
						System.out.println("\nCard set!");
						return card;
					}
					break;
				}
				if(command == counter + 1) { //back
					return 0;
				}
				if(command == counter + 2) { //quit
					return -1;
				}
				for(int i = 1; i <= counter; i++) {
					if(command == i) {
						System.out.println("\nCard set!");
						return cards.get(i-1);
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
		return 0;
	}

	protected static long addCard(Statement st, Console c, int customer) {
		//get card info
		System.out.print("Card Number: ");
		long card_number = getLongInput(c);
		System.out.print("Card Holder: ");
		String card_holder = checkSingleQuote(c.readLine());
		System.out.print("CSV: ");
		int csv = getIntInput(c);
		System.out.print("Expiration Date (mm/yy): ");
		String exp_date = checkSingleQuote(c.readLine());

		//get billing address info
		int address_id = 0;
		while(address_id == 0){
			System.out.println("\nChoose an option, choose 3 to cancel:\n1 Create New Address\n2 Use Your Account Address\n3 Cancel");
			int command = getIntInput(c);
			switch(command) {
				case 1: //input new address
					address_id = createAddress(st, c);
					break;
				case 2: //use customer address
					try(
						ResultSet rs = st.executeQuery("select address_id from customer where customer_id = " + customer);
					){
						if(!rs.next()) { //a customer should always have an address on record
							throw new Exception();
						}
						address_id = rs.getInt("address_id");
					}
					catch(Exception e) {
						System.out.println("\nSorry, an error occurred. Please try again. 2");
						return 0;
					}
					break;
				case 3: //back
					return 0;
			}
		}

		//create payment_card
		try{
			st.executeQuery("insert into payment_card values(" + card_number + ", " + customer + ", '" + card_holder 
				+ "', " + csv + ", '" + exp_date + "', " + address_id + ")");
			System.out.println("\nCard added successfully!");
			return card_number;
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again. 1");
		}
		return 0;
	}

	protected static void createTransactionSell(Statement st, int customer, int vec_id, long card_number, int location_id, boolean online) {
		try{
			st.executeQuery("insert into transactions_sell (customer_id, vec_id, date_transaction, card_number, total_cost) values(" 
				+ customer + ", " + vec_id + ", sysdate, " + card_number + ", (select total_price from vehicle where vec_id = " + vec_id + "))");
			System.out.println("\nPurchase successful!");
			
			//future update: make pickup insertion reliant on when vehicle actually physically arrives at the location (handled by onsite employee when vehicle arrives).
			//Current model and limitations don't allow that, so for now, vehicle becomes immediately available for pickup
			if(online){
				st.executeQuery("insert into location_pickup values(" + location_id + ", " + vec_id + ", " + customer + ")");
				System.out.println("Your vehicle will be available for pickup within a week! Check your account information to see!");
			}
		}
		catch(Exception e) {
			System.out.println("\nPurchase failed! Try again later.");
		}
	}

	protected static int createAddress(Statement st, Console c) {
		//get address info
		System.out.println("\nInput new address information:");
		System.out.print("Address: ");
		String address = checkSingleQuote(c.readLine());
		System.out.print("City: ");
		String city = checkSingleQuote(c.readLine());
		System.out.print("State/Province: ");
		String state = checkSingleQuote(c.readLine());
		System.out.print("Country: ");
		String country = checkSingleQuote(c.readLine());
		System.out.print("Planet: ");
		String planet = checkSingleQuote(c.readLine());
		System.out.println("Is the following address correct?\n");
		System.out.println("\nAddress: " + address + "\nCity: "+ city + "\nState/Province: " 
			+ state + "\nCountry: " + country + "\nPlanet: " + planet);
		System.out.println("\nIs this information correct:\n1 Yes\n2 No");
		int confirm = getIntInput(c);
		switch(confirm) {
			case 1: //yes
				int address_id = runGeneratedIdInsert(st, "insert into address (address, city, state_province, country, planet) values ('" 
					+ address + "', '" + city + "', '" + state + "', '" + country + "', '" + planet + "')", "address_id");
				if((address_id == 0)) {
					System.out.println("\nSorry, an error occurred. Please try again.");
					break;
				}
				return address_id;
			case 2: //no
				break;
		}
		return 0;
	}

	protected static String getModel(Console c) {
		while(true) {
			System.out.println("\nModel (M(oon), U(ndersea), S(paceship), K(art)): ");
			String model = checkSingleQuote(c.readLine()).toUpperCase();
			if(model.equals("M") || model.equals("U") || model.equals("S") || model.equals("K")) { //confirm valid input
				return model;
			}
			else {
				System.out.println("\nPlease enter a valid model type.");
			}
		}
	}

	protected static ArrayList<Integer> viewOwnedVehicles(Statement st, int customer) {
		try(
			ResultSet rs = st.executeQuery("select * from vehicle where customer_id = " + customer);
		){
			if(!rs.next()) {
				System.out.println("\nNo vehicles found!");
				return null;
			}
			System.out.println("\n---Owned Vehicles---");
			System.out.println(String.format("%-2s\t%-5s\t%-5s\t%-4s\t%-9s", "", "VEC_ID", "MODEL", "YEAR", "CONDITION"));
			int counter = 1;
			ArrayList<Integer> vehicles = new ArrayList<>();
			do {
				vehicles.add(rs.getInt("vec_id"));
				System.out.println(String.format("%-2d\t%-5d\t%-5s\t%-4d\t%-9s", counter, rs.getInt("vec_id"), rs.getString("model"), rs.getInt("year"), rs.getString("condition")));
				counter++;
			} while(rs.next());
			return vehicles;
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
		return null;
	}

	protected static ArrayList<Integer> viewVehiclesPickup(Statement st, int customer) { //returns counter
		try(
			ResultSet rs = st.executeQuery("select * from location_pickup join vehicle using(vec_id, customer_id) join location using(location_id) join address using(address_id) where customer_id = " + customer);
		){
			if(!rs.next()) {
				System.out.println("\nNo vehicles found!");
				return null;
			}
			System.out.print("\n---Vehicles Waiting for Pickup---");
			int counter = 1;
			ArrayList<Integer> vehicles = new ArrayList<>();
			do {
				vehicles.add(rs.getInt("vec_id"));
				System.out.println("\nVehicle " + counter + ":");
				System.out.println("Vec_id: " + rs.getInt("vec_id") + "\nModel: " + rs.getString("model") + "\nYear: " + rs.getInt("year")
					+ "\nAddress: " + rs.getString("address") + "\nCity: " + rs.getString("city") + "\nState/Province: " 
					+ rs.getString("state_province") + "\nCountry: " + rs.getString("country") + "\nPlanet: " + rs.getString("planet"));
				counter++;
			} while(rs.next());
			return vehicles;
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
		return null;
	}

	protected static ArrayList<Integer> viewLocationUsedInventory(Statement st, int location) {
		try(
			ResultSet rs = st.executeQuery("select * from location_used join vehicle using(vec_id) where location_id = " + location);
		){
			if(!rs.next()) {
				System.out.println("\nSorry, this location does not have a used vehicle inventory.");
				return null;
			}
			System.out.println("\nIf you wish to buy a used vehicle at this location, visit this location and talk to an employee!");
			System.out.println("\n---Used Vehicles---");
			System.out.println(String.format("%-2s\t%-5s\t%-5s\t%-4s\t%-9s\t%-15s", "", "VEC_ID", "MODEL", "YEAR", "CONDITION", "ESTIMATED PRICE"));
			int counter = 1;
			ArrayList<Integer> vehicles = new ArrayList<>();
			do {
				vehicles.add(rs.getInt("vec_id"));
				System.out.println(String.format("%-2d\t%-5d\t%-5s\t%-4d\t%-9s\t%-15d", counter, rs.getInt("vec_id"), rs.getString("model"), rs.getInt("year"), rs.getString("condition"), rs.getInt("total_price")));
				counter++;
			} while(rs.next());
			return vehicles;
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
		return null;
	}

	protected static ArrayList<Integer> viewLocationDisplay(Statement st, int location) {
		try(
			ResultSet rs = st.executeQuery("select * from location_display join vehicle using(vec_id) where location_id = " + location);
		){
			if(!rs.next()) {
				System.out.println("\nSorry, this location does not have any vehicles on display.");
				return null;
			}
			System.out.println("\n---Display Vehicles---");
			System.out.println(String.format("%-2s\t%-5s\t%-5s\t%-4s\t%-15s", "", "VEC_ID", "MODEL", "YEAR", "ESTIMATED PRICE"));
			int counter = 1;
			ArrayList<Integer> vehicles = new ArrayList<>();
			do {
				vehicles.add(rs.getInt("vec_id"));
				System.out.println(String.format("%-2d\t%-5d\t%-5s\t%-4d\t%-15d", counter, rs.getInt("vec_id"), rs.getString("model"), rs.getInt("year"), rs.getInt("total_price")));
				counter++;
			} while(rs.next());
			return vehicles;
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
		return null;
	}

	protected static int searchByPlanet(Statement st, Console c) {
		System.out.print("\nPlanet: ");
		String planet = checkSingleQuote(c.readLine());
		try(
			ResultSet rs1 = st.executeQuery("select * from address join location using(address_id) where planet = '" + planet + "'");
		){
			return chooseLocationFromList(rs1, c);
		}
		catch(Exception e) {}
		System.out.println("\nSorry, an error occurred. Please try again. 10");
		return 0;
	}

	protected static int chooseLocationFromList(ResultSet rs, Console c) {
		try{
			ArrayList<Integer> locations = new ArrayList<>();
			int counter = 1;
			if(!rs.next()) {
				System.out.println("\nSorry, there are no service locations were found with that search parameter.");
				return 0;
			}
			System.out.println("\nChoose a location/option:");
			System.out.println(String.format("%-5s\t%-25s\t%-25s\t%-25s\t%-25s\t%-20s", "", "ADDRESS", "CITY", "STATE/PROVINCE", "COUNTRY", "PLANET"));
			do {
				locations.add(rs.getInt("location_id"));
				System.out.println(String.format("%-5d\t%-25s\t%-25s\t%-25s\t%-25s\t%-20s", counter, rs.getString("address"),
					rs.getString("city"), rs.getString("state_province"), rs.getString("country"), rs.getString("planet")));
				counter++;
			} while(rs.next());
			System.out.println(counter + " Back\n" + (counter + 1) + " Quit");
			int command = 0;
			while((command < 1) || (command > counter + 1)) {
				command = getIntInput(c);
				if(command == counter) { //back
					return 0;
				}
				if(command == counter + 1) { //quit
					return -1;
				}
				for(int i = 1; i <= counter; i++) {
					if(command == i) {
						return locations.get(i-1);
					}
				}
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again. 9");
		}
		return 0;
	}

	protected static void displayHours(Statement st, int location) {
		try(
			ResultSet rs1 = st.executeQuery("select * from hours where location_id = " + location);
		){
			if(!rs1.next()) {
				System.out.println("\nSorry, this location is closed.");
				return;
			}
			System.out.println("\n---Hours---");
			System.out.println(String.format("%-3s\t%-6s\t%-7s\t%-7s\t%-8s", "Day", "OpenHr", "OpenMin", "CloseHr", "CloseMin"));
			do {
				System.out.println(String.format("%-3s\t%-6d\t%-7d\t%-7d\t%-8d", rs1.getString("day"), rs1.getInt("open_hr"),
					rs1.getInt("open_min"), rs1.getInt("close_hr"), rs1.getInt("close_min")));
			} while(rs1.next());
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
	}
}