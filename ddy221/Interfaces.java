import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Interfaces {
	public static void main(String[] args) {
		//log into edgar
		boolean successFlag = false;
		Console c = System.console();
		while(!successFlag) {
			System.out.print("Enter Oracle userid: ");
            String userid = c.readLine();
            System.out.print("Enter Oracle password: ");
            String password = String.valueOf(c.readPassword());
			try(
				Connection con = DriverManager.getConnection
					("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241", userid, password);
				Statement st = con.createStatement();
			){
				successFlag = true; //connection was successful
				password = "";
				System.out.println("\nWelcome to Alset Eccentric Vehicles!");

				//choose an interface
				boolean interfaceSuccessFlag = false;
				while(!interfaceSuccessFlag) {
					System.out.println("\nChoose an interface:\n1 Customer\n2 Employee\n3 Quit");
					int interfaceType = Helper.getIntInput(c);
					switch(interfaceType) {
						case 1:
							interfaceSuccessFlag = CustomerInterface.run(c, con, st); //handles running the customer interface
							break;
						case 2:
							interfaceSuccessFlag = employeeLogin(c, st);
							break; //Back default
						case 3:
							interfaceSuccessFlag = true;
							break;
					}
				}
				System.out.println("\nThank you for using our services!");
			}
			catch(Exception e) {
				System.out.println("\nInvalid userid/password. Failed to connect!\n");
				password = "";
			}
		}
	}

	private static boolean employeeLogin(Console c, Statement st) {
		System.out.print("\nUserid: "); //currently only accepts analytics or onsite
		String userid = c.readLine();
		System.out.print("Password: "); //currently only accepts blank string
		String password = String.valueOf(c.readPassword());
		if(userid.equals("analytics") && password.equals("")) {
			return AnalyticsEmployeeInterface.run(c, st);
		}
		else if(userid.equals("onsite") && password.equals("")) {
			return OnsiteEmployeeInterface.run(c, st);
		}
		else {
			System.out.println("\nIncorrect userid/password. Login failed. Contact your supervisor if trouble continues.\n");
			return false;
		}
	}
}