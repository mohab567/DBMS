package control;

import java.io.File;

import control.IDBMS;
import model.InputValidator;
import model.QueryParser;
import model.TablePrinter;
import model.XMLFileHandler;

public class DBMS implements IDBMS{
	
	//*****************************************************************
	//01_Attributes****************************************************
		private String repositoryName;
		public String currentDirectory;
		private boolean connectedToDatabase;
		private XMLFileHandler xmlHandler;
		private TablePrinter printer;
		private QueryParser queryParser;
		private InputValidator validator;
	

	//*****************************************************************
	//02_Constructor***************************************************
		public DBMS(){
			this.repositoryName="DatabaseRepository";
			this.currentDirectory=this.repositoryName;
			connectedToDatabase=false;
			queryParser=new QueryParser(this);
			xmlHandler=new XMLFileHandler();
			validator=new InputValidator();
			File file=new File(repositoryName);
	        if(!file.exists()){
	            if(file.mkdir()){System.out.println("Repository is created!");}
	            else{System.out.println("Failed to create Repository!");}
	        }else{System.out.println("Repository Already Exists!");}
		}
	
		
	//*****************************************************************
	//03_Methods*******************************************************

		@Override
		public boolean implementQuery(String userQuery) {
			// TODO Auto-generated method stub
			try{
				this.queryParser.parseQuery(userQuery);
				return true;
			}catch(Exception ex){
				System.out.println("Invalid Query");
				return false;
			}
		}
		//******************************************************
		//******************************************************
		@Override
		public boolean createDatabase(String databaseName){
			// TODO Auto-generated method stub
			String tmpDirectory=this.repositoryName+File.separator+databaseName;
			File targetDatabase=new File(tmpDirectory);
	        if(!targetDatabase.exists()){
	            if(targetDatabase.mkdir()){
	            	System.out.println("Database Successfully Created");
	            	return true;
	            }else{
	            	System.out.println("Unable to Create Database. Check Admin Rights");
	            	return false;
	            }
	        }
	        System.out.println("Database Already Exists!");
			return false;
		}
		//******************************************************
		@Override
		public boolean dropDatabase(String databaseName) {
			// TODO Auto-generated method stub
			String tmpDirectory=this.repositoryName+File.separator+databaseName;
			File selectedDatabase=new File(tmpDirectory);
	        if(selectedDatabase.exists()){
	        	try{
	        		xmlHandler.deleteFile(selectedDatabase);
		        	System.out.println("Database Successfully Deleted");
		        	disconnectFromDatabase();
		        	return true;
	        	}catch(Exception ex){
	        		System.out.println("Unable to Delete Database. Check Admin Rights");
	        		return false;
	        	}
	        }
	        System.out.println("Database Does NOT Exist!");
	        return false;
		}
		//******************************************************
		@Override
		public boolean connectToDatabase(String databaseName) {
			// TODO Auto-generated method stub
			String targetDirectory=this.repositoryName+File.separator+databaseName;
			File selectedDatabase=new File(targetDirectory);
	        if(selectedDatabase.exists()){
	        	this.currentDirectory=targetDirectory;
	        	this.connectedToDatabase=true;
	        	System.out.println("Connected To "+databaseName);
	        	return true;
	        }
	        System.out.println("Database Does NOT Exist!");
			return false;
		}
		//******************************************************
		@Override
		public boolean disconnectFromDatabase() {
			// TODO Auto-generated method stub
			this.currentDirectory=this.repositoryName;
			this.connectedToDatabase=false;
			System.out.println("Disconnected From Any Database!");
			return true;
		}
		//******************************************************
		//******************************************************
		@Override
		public boolean createTable(String tableName, String colNames[], String colTypes[]){
			// TODO Auto-generated method stub
			if(this.connectedToDatabase){
				String xmlFileDirectory=this.currentDirectory+File.separator+tableName+".xml";
				String dtdFileDirectory=this.currentDirectory+File.separator+tableName+".dtd";
				String dtdFileName=tableName+".dtd";
				File xmlFile=new File(xmlFileDirectory);
				if(!xmlFile.exists()){
					if(validator.validateInputColumnTypes(colTypes)){
						try{
							this.xmlHandler.createEmptyXML(tableName, xmlFileDirectory, dtdFileName);
							this.xmlHandler.createDTDFile(tableName, dtdFileDirectory, colNames, colTypes);
							System.out.println("Table Successfully Created!");
							return true;
						}catch(Exception ex){
							System.out.println("Unable to Create Table. Check Admin Rights");
							return false;
						}
					}else{
						System.out.println("Invalid Column Type");
						return false;
					}
				}else{
					System.out.println("Table Already Exists!");
					return false;
				}
			}else{
				System.out.println("Please Connect to Database First!");
				return false;
			}
		}
		//******************************************************
		@Override
		public boolean dropTable(String tableName) {
			// TODO Auto-generated method stub
			if(this.connectedToDatabase){
				String xmlFileDirectory=this.currentDirectory+File.separator+tableName+".xml";
				String dtdFileDirectory=this.currentDirectory+File.separator+tableName+".dtd";
				File xmlFile=new File(xmlFileDirectory);
				File dtdFile=new File(dtdFileDirectory);
		        if(xmlFile.exists()){
		        	try{
		        		if(dtdFile.exists()){
			        		xmlHandler.deleteFile(dtdFile);
			        	}
			        	xmlHandler.deleteFile(xmlFile);
			        	System.out.println("Table Removed Successfully");
			        	return true;
		        	}catch(Exception ex){
		        		System.out.println("Unable to Delete Table. Check Admin Rights");
		        		return false;
		        	}
		        }
		        System.out.println("This Table Does NOT Exist");
		        return false;
			}
			System.out.println("Please Connect to Database First!");
			return false;
		}
		//******************************************************
		//******************************************************
		@Override
		public boolean addNewRecord(String tableName, String[] colNames, String[] colValues){
			if(colNames==null){
				String dtdFileDirectory=this.currentDirectory+File.separator+tableName+".dtd";
				colNames=xmlHandler.getTableAllColumnNames(dtdFileDirectory);
			}
			if(connectedToDatabase){
				String xmlFileDirectory=this.currentDirectory+File.separator+tableName+".xml";
				File xmlFile=new File(xmlFileDirectory);
				if(xmlFile.exists()){
					String dtdFileDirectory=xmlFileDirectory.replaceAll(".xml", ".dtd");
					if(validator.validateInputColNames(dtdFileDirectory, colNames)){
						String[][] completeColumns=validator.getCompleteInputData(dtdFileDirectory, colNames, colValues);
						String[] completeColNames=completeColumns[0];
						String[] completeColValues=completeColumns[1];
						boolean inputDataValidation=validator.validateQueryValues(xmlFileDirectory, completeColNames, completeColValues);
						if(inputDataValidation){
							boolean attempt=xmlHandler.addNewRecord(tableName, xmlFileDirectory, completeColNames, completeColValues);
							if(attempt==true){
								System.out.println("Record Added...");
								return true;
							}
							System.out.println("Error Inserting New Record");
							return false;
						}else{
							System.out.println("Invalid Input Values");
							return false;
						}
					}else{
						System.out.println("Invalid Column Names");
						return false;
					}
				}else{
					System.out.println("Table Does NOT Exist!");
					return false;
				}
			}
			System.out.println("Please Connect to Database First!");
			return false;
			
		}
		//******************************************************
		@Override
		public String[][] retireveRecord(String tableName, String[] colNames, String[] conditionStatement) {
			// TODO Auto-generated method stub
			if(connectedToDatabase){
				String xmlFileDirectory=this.currentDirectory+File.separator+tableName+".xml";
				File xmlFile=new File(xmlFileDirectory);
				if(xmlFile.exists()){
					String dtdFileDirectory=xmlFileDirectory.replaceAll(".xml", ".dtd");
					if(colNames[0].equalsIgnoreCase("*")){
						colNames=xmlHandler.getTableAllColumnNames(dtdFileDirectory);
					}
					if(validator.validateInputColNames(dtdFileDirectory, colNames)){
						try{
							String colToTest=conditionStatement[0];
							String operation=conditionStatement[1];
							String condition=conditionStatement[2];
							String[][] result=new String[1][1];
							result=xmlHandler.retrieveRecord(tableName, xmlFileDirectory, colNames, colToTest, operation, condition);
							printer=new TablePrinter(colNames, result, dtdFileDirectory);
							printer.printTablePatternA();
							return result;
						}catch(Exception ex){
							System.out.println("No Match Found");
							return null;
						}
					}else{
						System.out.println("Invalid Column Names");
						return null;
					}
				}else{
					System.out.println("Table Does NOT Exist!");
					return null;
				}
			}else{
				System.out.println("Please Connect to Database First!");
				return null;
			}
			
		}
		//******************************************************
		@Override
		public boolean updateRecord(String tableName, String[] colNames, String[] newData, String[] conditionStatement){
			if(connectedToDatabase){
				String xmlFileDirectory=this.currentDirectory+File.separator+tableName+".xml";
				File xmlFile=new File(xmlFileDirectory);
				if(xmlFile.exists()){
					String dtdFileDirectory=xmlFileDirectory.replaceAll(".xml", ".dtd");
					if(validator.validateInputColNames(dtdFileDirectory, colNames)){
						String[][] completeColumns=validator.getCompleteInputData(dtdFileDirectory, colNames, newData);
						String[] completeColNames=completeColumns[0];
						String[] completeColValues=completeColumns[1];
						boolean inputDataValidation=validator.validateQueryValues(xmlFileDirectory, completeColNames, completeColValues);
						if(inputDataValidation){
							String colToTest=conditionStatement[0];
							String operation=conditionStatement[1];
							String condition=conditionStatement[2];
							boolean attempt=xmlHandler.updateRecord(tableName, xmlFileDirectory, colNames, newData, colToTest, operation, condition);
							if(attempt==true){
								System.out.println("Record Updated Successfully...");
								return true;
							}else{
								System.out.println("No Match Found");
								return false;
							}
						}else{
							System.out.println("Invalid Input Values");
							return false;
						}
					}else{
						System.out.println("Invalid Column Names");
						return false;
					}
				}else{
					System.out.println("Table Does NOT Exist!");
					return false;
				}
			}else{
				System.out.println("Please Connect to Database First!");
				return false;
			}
		}
		//******************************************************
		@Override
		public boolean deleteRecord(String tableName, String[] conditionStatement) {
			if(connectedToDatabase){
				String xmlFileDirectory=this.currentDirectory+File.separator+tableName+".xml";
				File xmlFile=new File(xmlFileDirectory);
				if(xmlFile.exists()){
					String colToTest=conditionStatement[0];
					String operation=conditionStatement[1];
					String condition=conditionStatement[2];
					boolean attempt=xmlHandler.deleteRecord(tableName, xmlFileDirectory, colToTest, operation, condition);
					if(attempt==true){
						System.out.println("Record Deleted...");
						return true;
					}else{
						System.out.println("No Match Found");
						return false;
					}
				}else{
					System.out.println("Table Does NOT Exist!");
					return false;
				}
			}else{
				System.out.println("Please Connect to Database First!");
				return false;
			}
			
		}
		//******************************************************

}
