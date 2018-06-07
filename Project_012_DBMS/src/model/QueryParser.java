package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import control.DBMS;

public class QueryParser {
	
	//*****************************************************************
	//01_Attributes****************************************************
	private Matcher queryMatcher;
	private int matchedQueryCode;
	private String[] validSQLPatterns;
	private DBMS databaseManager;
	
	
	//*****************************************************************
	//02_Constructor***************************************************
	public QueryParser(DBMS databaseManager){
		validSQLPatterns=generateValidQueryPattern();
		matchedQueryCode=-1;
		this.databaseManager=databaseManager;
	}
	
	
	//*****************************************************************
	//03_Methods*******************************************************
	public boolean parseQuery(String userQuery){
		if(isValidQueryStatement(userQuery)){
			this.extractQueryParameters();
			return true;
		}else{
			System.out.println("Invalid Query");
			return false;
		}
	}
	//**************************************************************
	private String[] generateValidQueryPattern(){
		String[] validPatterns=new String[13];
		//01_Database Queries
		validPatterns[0]="((?i)create)( +?)((?i)database)( +?)(.+)( *?)(;)";
		validPatterns[1]="((?i)drop)( +?)((?i)database)( +?)(.+)( *?)(;)";
		validPatterns[2]="((?i)use)( +?)(.+)( *?)(;)";
		//02_Table Queries
		validPatterns[3]="((?i)create)( +?)((?i)table)( +?)(.+)( *?)([(])(.+)([)])( *?)(;)";
		validPatterns[4]="((?i)drop)( +?)((?i)table)( +?)(.+)( *?)(;)";
		//03_Record Queries
		validPatterns[5]="((?i)insert)( +?)((?i)into)( +?)(.+)( *?)([(])(.+)([)])( +?)((?i)values)( *?)([(])(.+)([)])( *?)(;)";
		
		validPatterns[6]="((?i)select)( +?)(.+)( +?)((?i)from)( +?)(.+)( +?)((?i)where)( +?)(.+)([<>=])(.+)( *?)(;)";
		validPatterns[7]="((?i)select)( +?)(.+)( +?)((?i)from)( +?)(.+)( *?)(;)";

		validPatterns[8]="((?i)update)( +?)(.+)( +?)((?i)set)( +?)(.+)( +?)((?i)where)( +?)(.+)([<>=])(.+)( *?)(;)";
		validPatterns[9]="((?i)update)( +?)(.+)( +?)((?i)set)( +?)(.+)( *?)(;)";
		
		validPatterns[10]="((?i)delete)( +?)((?i)from)( +?)(.+)( +?)((?i)where)( +?)(.+)([<>=])(.+)( *?)(;)";
		validPatterns[11]="((?i)delete)( +?)((?i)from)( +?)(.+)( *?)(;)";
		
		validPatterns[12]="((?i)insert)( +?)((?i)into)( +?)(.+)( *?)([(])(.+)([)])( *?)(;)";

		return validPatterns;
	}
	//**************************************************************
	private boolean isValidQueryStatement(String userQuery){
		for(int i=1; i<=validSQLPatterns.length; i++){
			String pattern=validSQLPatterns[i-1];
			Matcher tmp=Pattern.compile(pattern).matcher(userQuery);
			if(tmp.matches()){
				this.queryMatcher=tmp;
				this.matchedQueryCode=i-1;
				return true;
			}
		}
		return false;
	}
	//**************************************************************
	private boolean extractQueryParameters(){
	    //01_Create Database
		if(matchedQueryCode==0){
			String databaseName=queryMatcher.group(5).replaceAll(" ", "");
			databaseManager.createDatabase(databaseName);
			return true;
		}
		//02_Drop Database
		if(matchedQueryCode==1){
			String databaseName=queryMatcher.group(5).replaceAll(" ", "");
			databaseManager.dropDatabase(databaseName);
			return true;
		}
		//03_Connect to Database
		if(matchedQueryCode==2){
			String databaseName=queryMatcher.group(3).replaceAll(" ", "");
			databaseManager.connectToDatabase(databaseName);
			return true;
		}
		//04_Create Table
		if(matchedQueryCode==3){
			String tableName=queryMatcher.group(5).replaceAll(" ", "");
			String[] tableColumns=queryMatcher.group(8).split(",");
			String[] colNames=new String[tableColumns.length];
			String[] colTypes=new String[tableColumns.length];
			String parameterPattern="( *?)(.+)( +?)(.+)( *?)";
			for(int i=1; i<=tableColumns.length; i++){
				Matcher tmpMatcher=Pattern.compile(parameterPattern).matcher(tableColumns[i-1]);
				if(tmpMatcher.matches()){
					colNames[i-1]=tmpMatcher.group(2).replaceAll(" ", "");
					colTypes[i-1]=tmpMatcher.group(4).replaceAll(" ", "");
				}else{
					System.out.println("Invalid Paramters");
					break;
				}
			}
			databaseManager.createTable(tableName, colNames, colTypes);
			return true;
		}
		//05_Drop Table
		if(matchedQueryCode==4){
			String tableName=queryMatcher.group(5).replaceAll(" ", "");
			databaseManager.dropTable(tableName);
			return true;
		}
		//06_Add New Record
		if(matchedQueryCode==5){
			String tableName=queryMatcher.group(5).replaceAll(" ", "");
			String[] colNames=queryMatcher.group(8).replaceAll(" ", "").split(",");
			String[] colValues=queryMatcher.group(14).split(",");
			for(int i=1; i<=colValues.length; i++){
				colValues[i-1]=colValues[i-1].trim().replaceAll("\"", "").replaceAll("\'", "");
			}
			databaseManager.addNewRecord(tableName, colNames, colValues);
			return true;
		}
		//07_Retrieve Record Pattern_Condition
		if(matchedQueryCode==6){
			String tableName=queryMatcher.group(7).replaceAll(" ", "");
			String[] colNames= queryMatcher.group(3).replaceAll(" ", "").split(",");
			String colToTest=queryMatcher.group(11).replaceAll(" ", "");
			String operation=queryMatcher.group(12).replaceAll(" ", "");
			String condition=queryMatcher.group(13).replaceAll(" ", "").replaceAll("\"", "").replaceAll("\'", "");
			String[] conditionStatement={colToTest, operation, condition};
			databaseManager.retireveRecord(tableName, colNames, conditionStatement);
			return true;
		}
		//08_Retrieve Record Pattern_NoCondition
		if(matchedQueryCode==7){
			String tableName=queryMatcher.group(7).replaceAll(" ", "");
			String[] colNames= queryMatcher.group(3).replaceAll(" ", "").split(",");
			String[] conditionStatement={"1", "1", "1"};
			databaseManager.retireveRecord(tableName, colNames, conditionStatement);
			return true;
		}
		//09_Update Record Pattern_Condition
		if(matchedQueryCode==8){
			String tableName=queryMatcher.group(3).replaceAll(" ", "");
			String[] updates= queryMatcher.group(7).split(",");
			String[] colNames=new String[updates.length];
			String[] newData=new String[updates.length];
			for(int i=1; i<=updates.length; i++){
				String[] update=updates[i-1].trim().split("=");
				colNames[i-1]=update[0].trim();
				newData[i-1]=update[1].trim().replaceAll("\"", "").replaceAll("\'", "");
			}
			String colToTest=queryMatcher.group(11).replaceAll(" ", "");
			String operation=queryMatcher.group(12).replaceAll(" ", "");
			String condition=queryMatcher.group(13).replaceAll(" ", "").replaceAll("\"", "").replaceAll("\'", "");
			String[] conditionStatement={colToTest, operation, condition};
			databaseManager.updateRecord(tableName, colNames, newData, conditionStatement);
			return true;
		}
		//10_Update Record Pattern_NoCondition
		if(matchedQueryCode==9){
			String tableName=queryMatcher.group(3).replaceAll(" ", "");
			String[] updates= queryMatcher.group(7).split(",");
			String[] colNames=new String[updates.length];
			String[] newData=new String[updates.length];
			for(int i=1; i<=updates.length; i++){
				String[] update=updates[i-1].trim().split("=");
				colNames[i-1]=update[0].trim();
				newData[i-1]=update[1].trim().replaceAll("\"", "").replaceAll("\'", "");
			}
			String[] conditionStatement={"1", "1", "1"};
			databaseManager.updateRecord(tableName, colNames, newData, conditionStatement);
			return true;
		}
		//11_Delete Record Pattern_Condition
		if(matchedQueryCode==10){
			String tableName=queryMatcher.group(5).replaceAll(" ", "");			
			String colToTest=queryMatcher.group(9).replaceAll(" ", "");
			String operation=queryMatcher.group(10).replaceAll(" ", "");
			String condition=queryMatcher.group(11).replaceAll(" ", "").replaceAll("\"", "").replaceAll("\'", "");
			String[] conditionStatement={colToTest, operation, condition};
			databaseManager.deleteRecord(tableName, conditionStatement);
			return true;
		}
		//12_Delete Record Pattern_NoCondition
		if(matchedQueryCode==11){
			String tableName=queryMatcher.group(5).replaceAll(" ", "");	
			String[] conditionStatement={"1", "1", "1"};
			databaseManager.deleteRecord(tableName, conditionStatement);
			return true;
		}
		if(matchedQueryCode==12){
			String tableName=queryMatcher.group(5).replaceAll(" ", "");
			String[] colValues=queryMatcher.group(8).replaceAll(" ", "").split(",");
			for(int i=1; i<=colValues.length; i++){
				colValues[i-1]=colValues[i-1].trim().replaceAll("\"", "").replaceAll("\'", "");
			}
			databaseManager.addNewRecord(tableName, null, colValues);
			return true;
		}
		return false;
	}
	//**************************************************************
	
	
	
	
	
	

}
