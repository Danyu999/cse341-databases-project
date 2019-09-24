import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class OnsiteEmployeeInterface {
	private static Console c;
	private static Statement st;

	public static boolean run(Console console, Statement statement) {
		c = console;
		st = statement;
		System.out.println("\nWelcome Onsite Employee!");
		int response = -1;
		while((response = mainMenu()) > 0) {} //handles back navigation of menus
		//if response == 0, logout was chosen
		if(response == -1) {
			return true; //Quit
		}
		return false; //Back default
	}

	private static int mainMenu() {
		int location = 0;
		while(location == 0) {
			System.out.println("\nPlease select your service location:\n1 Set Location\n2 Logout\n3 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1:
					location = setLocation();
					if(location == -1) {
						return -1;
					}
					break;
				case 2:
					return 0;
				case 3:
					return -1;
			}
		}
		while(true) {
			System.out.println("\nHome Page:\n1 Customer Interactions\n2 Location Management\n3 Logout\n4 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1:
					if(customerInteractions(location) == -1) {
						return -1;
					}
					break;
				case 2:
					if(locationManagement(location) == -1) {
						return -1;
					}
					break;
				case 3: //Logout
					return 0;
				case 4: //Quit
					return -1;
			}
		}
	}

	private static int setLocation() {
		int location = Helper.searchByPlanet(st, c);
		if(location == -1 || location == 0) {
			return location;
		}
		else {
			System.out.println("\nLocation set!");
			return location;
		}
	}

	private static int customerInteractions(int location) {
		int customer = setCustomer();
		if(customer == 0) {
			return 0;
		}
		while(true) {
			System.out.println("\nCustomer Interactions:\n1 Pickup Vehicle\n2 Repair Vehicle\n3 Buy Used Vehicle\n4 Evaluate/Discount Vehicle\n5 Buyback Vehicle\n6 Back\n7 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1:
					if(pickupVehicle(customer, location) == -1) {
						return -1;
					}
					break;
				case 2:
					if(repairVehicle(customer, location) == -1) {
						return -1;
					}
					break;
				case 3:
					if(buyUsedVehicle(customer, location) == -1) {
						return -1;
					}
					break;
				case 4:
					if(customerVehicleEvaluation(customer, location) == -1) {
						return -1;
					}
					break;
				case 5:
					if(buybackVehicle(customer, location) == -1) {
						return -1;
					}
					break;
				case 6: //back
					return 0;
				case 7: //quit
					return -1;
			}
		}
	}

	private static int setCustomer() {
		System.out.print("Customer name: ");;
		String name = Helper.checkSingleQuote(c.readLine());
		System.out.print("Customer email: ");
		String email = Helper.convertEmail(Helper.checkSingleQuote(c.readLine()));
		try(
			ResultSet result = st.executeQuery("select * from customer where email = '" + email + "' and name = '" + name + "'");
		){
			if(result.next()) {
				System.out.println("\nCustomer found successfully!");
				return result.getInt("customer_id");
			}
			else {
				System.out.println("\nCustomer not found.");
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred. Please try again.");
		}
		return 0;
	}

	private static int chooseVehicle(ArrayList<Integer> vehicles) {
		if(vehicles == null) {
			return 0;
		}
		int counter = vehicles.size() + 1;
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
					return vehicles.get(i-1);
				}
			}
		}
		return 0;
	}

	private static int pickupVehicle(int customer, int location) {
		//choose a vehicle to pickup
		int vehicle = chooseVehicle(Helper.viewVehiclesPickup(st, customer));
		if(vehicle == -1) {
			return -1;
		}
		else if(vehicle == 0) {
			return 0;
		}

		//car has been picked up, remove it from the pickup relation
		try{
			int deleted = st.executeUpdate("delete from location_pickup where vec_id = " + vehicle + " and location_id = " + location);
			if(deleted == 0) {
				throw new Exception();
			}
			System.out.println("\nPickup successful!");
		}
		catch(Exception e) {
			System.out.println("\nSorry, pickup failed! Please try again. Please make sure the vehicle being picked up is at this location!");
		}
		return 0;
	}

	private static int repairVehicle(int customer, int location) {
		//choose a vehicle to repair
		int vehicle = chooseVehicle(Helper.viewOwnedVehicles(st, customer));
		if(vehicle == -1) {
			return -1;
		}
		else if(vehicle == 0) {
			return 0;
		}

		try (
			ResultSet rs = st.executeQuery("select * from location_models where location_id = " + location + " and model = (select model from vehicle where vec_id = " + vehicle + ")");
		){
			if(!rs.next()) {
				System.out.println("\nThis service location does not support repairing this model of vehicle.");
				return 0;
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, repair failed! Please try again.");
			return 0;
		}

		//repair specified vehicle
		System.out.print("Cost for Repair: ");
		long repairCost = Helper.getLongInput(c);
		try{
			st.executeQuery("insert into repairs values(" + location + ", " + vehicle + ", sysdate, " + repairCost + ")");
			System.out.println("\nRepair successful!");
		}
		catch(Exception e) {
			System.out.println("\nSorry, repair failed! Please try again.");
		}
		return 0;
	}

	private static int buyUsedVehicle(int customer, int location) {
		int vehicle = 0;
		long card_number = 0;
		while(true) {
			System.out.println("\nChoose an action (must specify vehicle and card before purchase):\n1 Choose Card\n2 Choose Vehicle\n3 Make Purchase\n4 Back\n5 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1: //choose card
					card_number = Helper.chooseCard(st, c, customer);
					if(card_number == -1) {
						return -1;
					}
					break;
				case 2: //choose vehicle
					vehicle = chooseVehicle(Helper.viewLocationUsedInventory(st, location));
					if(vehicle == -1) {
						return -1;
					}
					break;
				case 3: //make purchase
					if(card_number == 0 || vehicle == 0) {
						System.out.println("\nPlease choose both a card and vehicle first!");
					}
					else {
						try{
							st.executeQuery("delete from location_used where vec_id = " + vehicle);
							Helper.createTransactionSell(st, customer, vehicle, card_number, location, false);
						}
						catch(Exception e) {
							System.out.println("\nSorry, purchase failed! Try again later.");
						}
						return 0;
					}
					break;
				case 4:
					return 0;
				case 5:
					return -1;
			}
		}
	}

	private static int customerVehicleEvaluation(int customer, int location) {
		int vehicle = chooseVehicle(Helper.viewOwnedVehicles(st, customer));
		if(vehicle == -1) {
			return -1;
		}
		else if(vehicle == 0) {
			return 0;
		}
		setDiscountVehicle(vehicle, location);
		return 0;
	}

	private static int setDiscountVehicle(int vehicle, int location) {
		try (
			ResultSet rs = st.executeQuery("select * from location_models where location_id = " + location + " and model = (select model from vehicle where vec_id = " + vehicle + ")");
		){
			if(!rs.next()) {
				System.out.println("\nThis service location does not support evaluating this model of vehicle.");
				return 0;
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, evaluation failed! Please try again. 1");
			return 0;
		}
		System.out.print("Discount for this Vehicle: ");
		long discount = Helper.getLongInput(c);
		long oldDiscount = 0;
		//get old discount
		try(
			ResultSet rs2 = st.executeQuery("select discount from vehicle where vec_id =" + vehicle);
		){
			if(rs2.next()) {
				oldDiscount = rs2.getLong("discount");
			}
			else {
				throw new Exception();
			}
		}
		catch(Exception e) {
			System.out.println("\nSorry, evaluation failed! Please try again. 2");
			return 0;
		}
		//update new discount and total_price
		try{
			st.executeQuery("update vehicle set discount = " + discount + ", total_price = total_price + " + (oldDiscount - discount) + " where vec_id = " + vehicle);
			System.out.println("\nEvaluation successful!");
			return 1;
		}
		catch(Exception e) {
			System.out.println("\nSorry, evaluation failed! Please try again. 3");
		}
		return 0;
	}

	private static int buybackVehicle(int customer, int location) {
		int vehicle = 0;
		long card_number = 0;
		while(true) {
			System.out.println("\nChoose an action (must specify vehicle and card before buyback):\n1 Choose Card\n2 Choose Vehicle\n3 Make Buyback\n4 Back\n5 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1: //choose card
					card_number = Helper.chooseCard(st, c, customer);
					if(card_number == -1) {
						return -1;
					}
					break;
				case 2: //choose vehicle
					vehicle = chooseVehicle(Helper.viewOwnedVehicles(st, customer));
					if(vehicle == -1) {
						return -1;
					}
					try (
						ResultSet rs = st.executeQuery("select * from location_models where location_id = " + location + " and model = (select model from vehicle where vec_id = " + vehicle + ")");
					){
						if(!rs.next()) {
							System.out.println("\nThis service location does not support this model of vehicle.");
							vehicle = 0;
						}
					}
					catch(Exception e) {
						System.out.println("\nSorry, buyback failed! Please try again.");
						return 0;
					}
					break;
				case 3: //make purchase
					if(card_number == 0 || vehicle == 0) {
						System.out.println("\nPlease choose both a card and vehicle first!");
					}
					else {
						if(setDiscountVehicle(vehicle, location) == 0) {
							return 0;
						}
						createTransactionBuy(customer, vehicle, card_number);
						return 0;
					}
					break;
				case 4:
					return 0;
				case 5:
					return -1;
			}
		}
	}

	private static void createTransactionBuy(int customer, int vec_id, long card_number) {
		try{
			st.executeQuery("insert into transactions_buy (customer_id, vec_id, date_transaction, card_number, total_cost) values(" 
				+ customer + ", " + vec_id + ", sysdate, " + card_number + ", (select total_price from vehicle where vec_id = " + vec_id + "))");
			System.out.println("\nVehicle bought back successfully!");
		}
		catch(Exception e) {
			System.out.println("\nPurchase failed! Try again later.");
		}
	}

	private static int locationManagement(int location) {
		while(true) {
			System.out.println("\nLocation Management:\n1 Set Discount for Used Inventory\n2 Change Hours\n3 Add to Display\n4 Remove from Display\n5 Back\n6 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1:
					if(locationVehicleEvaluation(location) == -1) {
						return -1;
					}
					break;
				case 2:
					Helper.displayHours(st, location);
					changeHours(location);
					break;
				case 3:
					addToDisplay(location);
					break;
				case 4:
					if(removeFromDisplay(location) == -1) {
						return -1;
					}
					break;
				case 5:
					return 0;
				case 6:
					return -1;
			}
		}
	}

	private static int locationVehicleEvaluation(int location) {
		int vehicle = chooseVehicle(Helper.viewLocationUsedInventory(st, location));
		if(vehicle == -1) {
			return -1;
		}
		else if(vehicle == 0) {
			return 0;
		}
		setDiscountVehicle(vehicle, location);
		return 0;
	}

	private static void changeHours(int location) {
		System.out.print("\nChoose a day (M(onday), T(uesday), W(ednesday), (thu)R(sday), F(riday)): ");
		String day = Helper.checkSingleQuote(c.readLine()).toUpperCase();
		if(!day.equals("M") && !day.equals("T") && !day.equals("W") && !day.equals("R") && !day.equals("F")) {
			System.out.println("\nInvalid day.");
			return;
		}
		System.out.println("\nInput -1 for any of the following time input to abort:");
		int openHour = -1;
		int openMinute = -1;
		int closeHour = -1;
		int closeMinute = -1;
		while(openHour < 0 || openHour > 23) {
			System.out.print("Open Hour (00 - 23): ");
			openHour = Helper.getIntInput(c);
			if(openHour == -1) {
				return;
			}
		}
		while(openMinute < 0 || openMinute > 59) {
			System.out.print("Open Minute (00 - 59): ");
			openMinute = Helper.getIntInput(c);
			if(openMinute == -1) {
				return;
			}
		}
		while(closeHour < 0 || closeHour > 23) {
			System.out.print("Close Hour (00 - 23): ");
			closeHour = Helper.getIntInput(c);
			if(closeHour == -1) {
				return;
			}
		}
		while(closeMinute < 0 || closeMinute > 59) {
			System.out.print("Close Minute (00 - 59): ");
			closeMinute = Helper.getIntInput(c);
			if(closeMinute == -1) {
				return;
			}
		}

		try{
			st.executeQuery("update hours set open_hr = " + openHour + ", open_min = " + openMinute + ", close_hr = " + closeHour 
				+ ", close_min = " + closeMinute + " where location_id = " + location + " and day = '" + day + "'");
			System.out.println("\nHours updated successfully!");
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred when attempting to update the hours. Try again later.");
		}
	}

	private static void addToDisplay(int location) {
		System.out.print("Enter the vehicle id: ");
		int vehicle = Helper.getIntInput(c);
		//check validity of that vehicle
		long exists = Helper.getTotal(st, "select count(*) from vehicle where vec_id = " + vehicle 
			+ " and customer_id is null and status = 'new' and exists(select * from location_models join vehicle using(model) where vec_id = " + vehicle + ")");
		if(exists == 0) {
			System.out.println("\nInvalid vehicle! Please make sure the vehicle is new, not owned by a customer, and is of a model supported by this location!");
			return;
		}
		//add to display
		try {
			st.executeQuery("insert into location_display values(" + location + ", " + vehicle + ")");
			System.out.println("\nAddition to display successful!");
		}
		catch(Exception e) {
			System.out.println("\nSorry, an error occurred when attempting to add the vehicle to display. Try again later.");
		}
	}

	private static int removeFromDisplay(int location) {
		int vehicle = chooseVehicle(Helper.viewLocationDisplay(st, location));
		if(vehicle == -1) {
			return -1;
		}
		else if(vehicle == 0) {
			return 0;
		}
		try{
			int deleted = st.executeUpdate("delete from location_display where vec_id = " + vehicle + " and location_id = " + location);
			if(deleted == 0) {
				throw new Exception();
			}
			System.out.println("\nRemoval from display successful!");
		}
		catch(Exception e) {
			System.out.println("\nRemoval from display failed! Try again later.");
		}
		return 0;
	}
}