package view;

import java.util.Scanner;

import control.DBMS;

public class ConsoleInterface {

	
	private static Scanner scanner;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DBMS databaseManager=new DBMS();
		scanner = new Scanner(System.in);
		String userQuery="";
		while(true){
			System.out.println(">>\n");
			userQuery=scanner.nextLine();
			userQuery=userQuery.trim();
			while(!userQuery.trim().endsWith(";")){
				userQuery=userQuery+" "+scanner.nextLine();
			}
			databaseManager.implementQuery(userQuery);
		}
	}
	
	
	
	
}
