import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class AnalyticsEmployeeInterface {
	private static Console c;
	private static Statement st;

	public static boolean run(Console console, Statement statement) {
		c = console;
		st = statement;
		System.out.println("\nWelcome Analytics Employee!");
		int response = -1;
		while((response = mainMenu()) > 0) {} //handles back navigation of menus
		//if response == 0, logout was chosen
		if(response == -1) {
			return true; //Quit
		}
		return false; //Back default
	}

	private static int mainMenu() {
		while(true) {
			System.out.println("\nHome Page:\n1 Aggregated Information\n2 List Transactions\n3 Logout\n4 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1:
					if(viewAggregatedInformation() == -1) {
						return -1;
					}
					break;
				case 2:
					if(listTransactions() == -1) {
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

	private static int viewAggregatedInformation() {
		while(true){
			System.out.println("\nAggregated Information:\n1 Overall Statistics\n2 Vehicles by Model\n3 Back\n4 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1:
					printOverallInfo();
					break;
				case 2:
					if(vehicleByModelInfo() == -1) {
						return -1;
					}
					break;
				case 3: //Back
					return 0;
				case 4: //Quit
					return -1;
			}
		}
	}

	private static void printOverallInfo() {
		long totalMakeExpenses = Helper.getTotal(st, "select sum(total_cost) from transactions_make");
		long soldMakeExpenses = Helper.getTotal(st, "select sum(M.total_cost) from transactions_make M join transactions_sell using(vec_id)");
		long buyExpenses = Helper.getTotal(st, "select sum(total_cost) from transactions_buy");
		long sellIncome = Helper.getTotal(st, "select sum(total_cost) from transactions_sell");
		long repairIncome = Helper.getTotal(st, "select sum(cost) from repairs");
		long numCustomers = Helper.getTotal(st, "select count(*) from customer");
		long numVehiclesMade = Helper.getTotal(st, "select count(*) from vehicle_options_prices");
		long numVehiclesSold = Helper.getTotal(st, "select count(*) from transactions_sell");
		long soldRevenue = sellIncome - soldMakeExpenses;
		if(totalMakeExpenses == -1 || soldMakeExpenses == -1 || buyExpenses == -1 || repairIncome == -1 || numCustomers == -1 || numVehiclesMade == -1 || numVehiclesSold == -1) {
			System.out.println("\nSorry, an error occurred when calculating Alset's information.");
			return;
		}
		System.out.println("\n---Alset Enterprise Information---");
		System.out.println("Number of Vehicles Made: " + numVehiclesMade);
		System.out.println("Number of Vehicles Sold: " + numVehiclesSold);
		System.out.println("Number of Registered Customers: " + numCustomers);
		System.out.println("Total Expenses: " + Helper.printMoney(totalMakeExpenses + buyExpenses));
		System.out.println("Total Income: " + Helper.printMoney(sellIncome + repairIncome));
		System.out.println("Total Income from Repairs: " + Helper.printMoney(repairIncome));
		System.out.println("Total Revenue: " + Helper.printMoney(sellIncome + repairIncome - totalMakeExpenses - buyExpenses));
		System.out.println("Total Revenue from Sold Vehicles: " + Helper.printMoney(soldRevenue));
		System.out.println("Avg Revenue/Vehicle per Sale: " + Helper.printMoney(soldRevenue / numVehiclesSold));
	}

	private static int vehicleByModelInfo() {
		String model = Helper.getModel(c);
		System.out.println("\nModel set successfully!");
		while(true){
			System.out.println("\nVehicle Model Information:\n1 List Vehicle Model Data\n2 Set Model\n3 Back\n4 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1:
					printVehicleModelData(model);
					break;
				case 2:
					model = Helper.getModel(c);
					System.out.println("\nModel set successfully!");
					break;
				case 3: //Back
					return 0;
				case 4: //Quit
					return -1;
			}
		}
	}

	//number of each model sold
	private static void printVehicleModelData(String model) {
		System.out.println("\n---Model " + model + "---");
		//number of each model made
		long numMadeVehicles = Helper.getTotal(st, "select count(*) from vehicle_options_prices where model = '" + model + "'");
		if(numMadeVehicles == -1) {
			System.out.println("\nSorry, an error occurred when counting the number of vehicles of this model that were made.\n");
		}
		else if(numMadeVehicles == 0) {
			System.out.println("\nNo vehicles of this model have been made.");
			return;
		}
		else {
			System.out.println("Number of vehicles made: " + numMadeVehicles);
		}

		//number of each model sold
		long numSoldVehicles = Helper.getTotal(st, "select count(*) from transactions_sell join vehicle_options_prices using(vec_id) where model = '" + model + "'");
		if(numSoldVehicles == -1) {
			System.out.println("\nSorry, an error occurred when counting the number of vehicles of this model that were sold.\n");
			return;
		}
		else if(numSoldVehicles == 0) {
			System.out.println("\nNo vehicles of this model have been sold.");
			return;
		}
		else {
			System.out.println("Number of vehicles sold (including resells): " + numSoldVehicles);
		}

		//percentage of total vehicles sold that are of this model
		long totalSoldVehicles = Helper.getTotal(st, "select count(*) from transactions_sell");
		if(totalSoldVehicles == -1) {
			System.out.println("\nSorry, an error occurred when counting the total number of vehicles sold.");
		}
		else if(totalSoldVehicles == 0) {
			System.out.println("\nSorry, something bad happened.");
			return;
		}
		else {
			System.out.println("Percentage of Total Vehicles Sold that are Model " + model + ": " + (numSoldVehicles * 100 / totalSoldVehicles) + "%");
		}

		//Total revenue for this model (transactions_sell total vs transactions_make total of vehicles sold)
		long revenue = 0;
		long sellIncome = Helper.getTotal(st, "select sum(total_cost) from transactions_sell join vehicle_options_prices using(vec_id) where model = '" + model + "'");
		long soldMakeExpenses = Helper.getTotal(st, "select sum(M.total_cost) from transactions_make M join transactions_sell using(vec_id) join vehicle_options_prices using(vec_id) where model = '" + model + "'");
		if(sellIncome != -1 && soldMakeExpenses != -1) {
			revenue = sellIncome - soldMakeExpenses;
			//print revenue for model
			System.out.println("Total Revenue from Sale(s): " + Helper.printMoney(revenue));
			//Revenue per model sold
			System.out.println("Avg Revenue/Vehicle per Sale: " + Helper.printMoney(revenue / numSoldVehicles));
		}
		else {
			System.out.println("\nSorry, an error occurred when calculating the total revenue for this model.\n");;
		}
	}

	private static int listTransactions() {
		while(true){
			System.out.println("\nList Transactions:\n1 Sell Vehicle Transactions\n2 Buyback Vehicle Transactions\n3 Make Vehicle Transactions\n4 Back\n5 Quit");
			int command = Helper.getIntInput(c);
			switch(command) {
				case 1:
					sellTransactions();
					break;
				case 2:
					buyTransactions();
					break;
				case 3:
					makeTransactions();
					break;
				case 4: //Back
					return 0;
				case 5: //Quit
					return -1;
			}
		}
	}

	private static int printTransactions(String query, String transactionType) {
		try(
			ResultSet rs = st.executeQuery(query);
		){
			if(!rs.next()) {
				return 0;
			}
			System.out.println("\n---" + transactionType + " Transactions---");
			System.out.println(String.format("%-14s\t%-6s\t%-5s\t%-4s\t%-9s\t%-16s\t%-10s\t", "TRANSACTION_ID", "VEC_ID", "MODEL", "YEAR", "OPTION_ID", "DATE_TRANSACTION", "TOTAL_COST"));
			do {
				System.out.println(String.format("%-14d\t%-6d\t%-5s\t%-4d\t%-9d\t%-16s\t%-10d\t", 
					rs.getInt("transaction_id"), rs.getInt("vec_id"), rs.getString("model"), rs.getInt("year"), rs.getInt("option_id"), rs.getDate("date_transaction"), rs.getLong("total_cost")));
			} while(rs.next());
		}
		catch(Exception e) {
			return -1;
		}
		return 1;
	}

	private static void sellTransactions() {
		int result = printTransactions("select * from transactions_sell join vehicle_options_prices using(vec_id) order by date_transaction", "Sell");
		if(result == 0) {
			System.out.println("\nThere are no sell transactions on record!");
		}
		else if(result == -1){
			System.out.println("\nSorry, an error occurred when attempting to print out the sell transactions. Try again later.");
		}
	}

	private static void buyTransactions() {
		int result = printTransactions("select * from transactions_buy join vehicle_options_prices using(vec_id) order by date_transaction", "Buyback");
		if(result == 0) {
			System.out.println("\nThere are no buy transactions on record!");
		}
		else if(result == -1){
			System.out.println("\nSorry, an error occurred when attempting to print out the buy transactions. Try again later.");
		}
	}

	private static void makeTransactions() {
		int result = printTransactions("select * from transactions_make join vehicle_options_prices using(vec_id) order by date_transaction", "Make");
		if(result == 0) {
			System.out.println("\nThere are no make transactions on record!");
		}
		else if(result == -1){
			System.out.println("\nSorry, an error occurred when attempting to print out the make transactions. Try again later.");
		}
	}
}