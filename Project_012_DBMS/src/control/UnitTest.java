package control;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import control.DBMS;

public class UnitTest {

	DBMS obj = new DBMS();

	@Test
	public void testAll() {
		implementQueryTest();
		createDatabaseTest();
		dropDatabaseTest();
		connectedToDatabaseTest();
		createTableTest();
		dropTableTest();
		addNewRecordTest();
		deleteRecordTest();
		updateRecordTest();
		retireveRecordTest();

	}

	public void implementQueryTest() {
		String query1 = "CREATE DATABASE School;";
		//String query2 = "CREATE DATABASE ;";// missing the name of database
		String query3 = "CREATE Table School(StudentName varchar(255));";
		//String query4 = "CREATE Table School;";// no columns Name.
		String query5 = "SELECT StudentName,Marks From School;";
		//String query6 = "SELECT StudentName,Marks School;"; // from isn't Exist
		String query7 = "SELECT * From School;";
		//String query8 = "SELECT From School;";// no columns Name.
		String query9 = "SELECT * FROM Customers WHERE Country= 'Mexico';";
		//String query10 = "SELECT * FROM Customers WHERE Country;"; // no //
																	// condition
		String query11 = "UPDATE School SET Marks=5  WHERE Marks > 20;";
		String query12 = "INSERT INTO School (StudentName,Marks) VALUES ('Mohamed',17);";
		assertEquals(true, obj.implementQuery(query1));
		//assertEquals(false, obj.implementQuery(query2));
		assertEquals(true, obj.implementQuery(query3));
		//assertEquals(false, obj.implementQuery(query4));
		assertEquals(true, obj.implementQuery(query5));
		//assertEquals(false, obj.implementQuery(query6));
		assertEquals(true, obj.implementQuery(query7));
		//assertEquals(false, obj.implementQuery(query8));
		assertEquals(true, obj.implementQuery(query9));
		//assertEquals(false, obj.implementQuery(query10));
		assertEquals(true, obj.implementQuery(query11));
		assertEquals(true, obj.implementQuery(query12));
	}

	public void createDatabaseTest() {
		String data1 = "School";
		String data2 = "Hospital";
		String data3 = "University";
		String data4 = "Shapes";

		assertEquals(true, obj.createDatabase(data1));
		assertEquals(false, obj.createDatabase(data1)); // already Exist.
		assertEquals(true, obj.createDatabase(data2));
		assertEquals(false, obj.createDatabase(data2)); // already Exist.
		assertEquals(true, obj.createDatabase(data3));
		assertEquals(true, obj.createDatabase(data4));
	}

	public void dropDatabaseTest() {
		String data1 = "School";
		String data2 = "Hospital";
		String data3 = "Company";// not Exist database.

		assertEquals(true, obj.dropDatabase(data1));
		assertEquals(false, obj.dropDatabase(data1));// deleted =it isn't
														// Exist
														// now.
		assertEquals(true, obj.dropDatabase(data2));
		assertEquals(false, obj.dropDatabase(data2));// deleted =it isn't
														// Exist
														// now.
		assertEquals(false, obj.dropDatabase(data3)); // already isn't
														// exist.

	}

	public void connectedToDatabaseTest() {

		String data1 = "School";
		String data2 = "Hospital";
		String data3 = "University";
		String data4 = "Shapes";

		assertEquals(false, obj.connectToDatabase(data1));// it's already
															// deleted
		assertEquals(false, obj.connectToDatabase(data2));// it's already
															// deleted
		assertEquals(true, obj.connectToDatabase(data3));
		assertEquals(true, obj.connectToDatabase(data4));
		assertEquals(true, obj.connectToDatabase(data3));

	}

	public void createTableTest() {
		String table1 = "Student";
		String table2 = "professor";
		String directory1 = "DatabaseRepository" + File.separator + "University" + File.separator + table1;
		String directory2 = "DatabaseRepository" + File.separator + "University" + File.separator + table2;
		String xmlFileDirectory1 = obj.currentDirectory + File.separator + table1 + ".xml";
		String xmlFileDirectory2 = obj.currentDirectory + File.separator + table2 + ".xml";
		String dtdFileDirectory1 = obj.currentDirectory + File.separator + table1 + ".dtd";
		String dtdFileDirectory2 = obj.currentDirectory + File.separator + table2 + ".dtd";

		String[] colNamesForTable1 = { "StudentName", "Class", "Marks" };
		String[] colNamesForTable2 = { "ProfessorName", "Courses", "salary" };
		String[] colTypesForTable1 = { "varchar", "varchar", "int" };
		String[] colTypesForTable2 = { "varchar", "varchar", "int" };
		assertEquals(true, obj.createTable(table1, colNamesForTable1, colTypesForTable1));
		assertEquals(true, obj.createTable(table2, colNamesForTable2, colTypesForTable2));

		assertEquals(directory1 + ".xml", xmlFileDirectory1);
		assertEquals(directory1 + ".dtd", dtdFileDirectory1);// check position
		assertEquals(directory2 + ".xml", xmlFileDirectory2);
		assertEquals(directory2 + ".dtd", dtdFileDirectory2);

		assertEquals(false, obj.createTable(table1, colNamesForTable1, colTypesForTable1)); // table
																							// already
																							// Exist
		assertEquals(false, obj.createTable(table2, colNamesForTable2, colTypesForTable2)); // table
																							// already
																							// Exist
		obj.disconnectFromDatabase();
		assertEquals(false, obj.createTable(table1, colNamesForTable1, colTypesForTable1)); // no
																							// connected
																							// database
		assertEquals(false, obj.createTable(table2, colNamesForTable2, colTypesForTable2));// no
																							// connected
																							// database
	}

	public void dropTableTest() {
		String table1 = "Student";
		String table2 = "professor";
		String table3 = "Employee";

		String data1 = "Shapes";
		String data2 = "University";

		obj.disconnectFromDatabase(); // disconnected from any database.

		assertEquals(false, obj.dropTable(table1)); // you arn't connected to
													// database
		assertEquals(false, obj.dropTable(table2)); // you arn't connected to
													// database
		assertEquals(false, obj.dropTable(table3)); // you arn't connected to
													// database

		obj.connectToDatabase(data1); // connected to data base which not
										// contain Tables

		assertEquals(false, obj.dropTable(table1));
		assertEquals(false, obj.dropTable(table2));
		assertEquals(false, obj.dropTable(table3));

		obj.connectToDatabase(data2); // contain table1,table2 only

		assertEquals(true, obj.dropTable(table1));
		assertEquals(true, obj.dropTable(table2));
		assertEquals(false, obj.dropTable(table3));// isn't Exist
		assertEquals(false, obj.dropTable(table1));// deleted
	}

	public void addNewRecordTest() {
		String table1 = "Student";
		String table2 = "Professor";
		String table3 = "Employee";
		String data = "University";

		String[] colNamesForTable1 = { "StudentName", "Class", "Marks" };
		String[] colNamesForTable2 = { "ProfessorName", "Courses", "salary" };
		String[] errorColumnName1 = { "Student", "Edge", "Marks" };

		String[] colTypesForTable1 = { "varchar", "varchar", "int" };
		String[] colTypesForTable2 = { "varchar", "varchar", "int" };

		String[] colValue1 = { "Hassan", "A", "28" };
		String[] colValue2 = { "Khaled", "OOP", "11000" };
		String[] colValue3 = { "Mohab", "B", "29" };
		String[] colValue4 = { "Wessam", "Digital", "10000" };
		String[] errorInValue = { "Mohamed", "digital231", "five thousand" };

		obj.disconnectFromDatabase();
		assertEquals(false, obj.addNewRecord(table1, colNamesForTable1, colValue1));
		assertEquals(false, obj.addNewRecord(table2, colNamesForTable2, colValue2)); // you
																						// arn't
																						// connected
																						// to
																						// database.
		assertEquals(false, obj.addNewRecord(table3, colNamesForTable2, colValue2));

		obj.connectToDatabase(data);
		obj.createTable(table1, colNamesForTable1, colTypesForTable1);
		obj.createTable(table2, colNamesForTable2, colTypesForTable2);

		assertEquals(true, obj.addNewRecord(table1, colNamesForTable1, colValue1));
		assertEquals(true, obj.addNewRecord(table2, colNamesForTable2, colValue2));
		assertEquals(true, obj.addNewRecord(table1, colNamesForTable1, colValue3));
		assertEquals(true, obj.addNewRecord(table2, colNamesForTable2, colValue4));

		assertEquals(false, obj.addNewRecord(table3, colNamesForTable2, colValue2));// table
																					// isn't
																					// exist
		assertEquals(false, obj.addNewRecord(table1, errorColumnName1, colValue1));// Error
																					// in
																					// column
																					// name
		assertEquals(false, obj.addNewRecord(table2, colNamesForTable2, errorInValue)); // Error
																						// in
																						// int
																						// value

	}

	public void deleteRecordTest() {
		String table1 = "Student";
		String table2 = "Professor";
		String table3 = "Employee";
		String data = "University";
		String[] validCondition1 = { "Marks", ">", "30" };
		String[] validCondition2 = { "Salary", "=", "10000" };
		String[] validCondition4 = { "Courses", "=", "OOP" };
		String[] validCondition5 = { "Marks", ">", "28" };
		String[] invalidCondition1 = { "Name", ">", "C" }; // column Name isn't
															// Exist.
		String[] invalidCondition2 = { "salary", ">", "C" }; // error in value.

		obj.disconnectFromDatabase();
		assertEquals(false, obj.deleteRecord(table1, validCondition1)); // you
																		// arn't
																		// connect
																		// to
																		// database
		assertEquals(false, obj.deleteRecord(table2, validCondition2)); // you
																		// arn't
																		// connect
																		// to
																		// database

		obj.connectToDatabase(data);
		assertEquals(false, obj.deleteRecord(table1, invalidCondition1)); // Error
																			// in
																			// column
																			// name
		assertEquals(false, obj.deleteRecord(table2, invalidCondition2)); // Error
																			// in
																			// value
		assertEquals(false, obj.deleteRecord(table3, validCondition4));// Table
																		// isn't
																		// Exist
		assertEquals(false, obj.deleteRecord(table1, validCondition1)); // No
																		// deleted
																		// records
																		// because
																		// condition
																		// don't
																		// occur

		assertEquals(true, obj.deleteRecord(table1, validCondition5));
		assertEquals(true, obj.deleteRecord(table2, validCondition2));

	}

	public void updateRecordTest() {
		String table1 = "Student";
		String table2 = "Professor";
		String table3 = "Employee";
		String data = "University";

		String[] colValue1 = { "Shaban", "V", "25" };
		String[] colValue2 = { "Omar", "Discrete", "9000" };
		String[] colValue3 = { "Gamal", "C", "26" };
		String[] colValue4 = { "Aymn", "Statistics", "8500" };
		String[] colNamesForTable1 = { "StudentName", "Class", "Marks" };
		String[] colNamesForTable2 = { "ProfessorName", "Courses", "salary" };

		obj.addNewRecord(table1, colNamesForTable1, colValue1);
		obj.addNewRecord(table1, colNamesForTable1, colValue3);
		obj.addNewRecord(table2, colNamesForTable2, colValue2);
		obj.addNewRecord(table2, colNamesForTable2, colValue4);

		String[] validCondition1 = { "Marks", ">", "30" };
		String[] validCondition2 = { "Salary", "=", "8500" };
		String[] validCondition3 = { "Marks", ">", "25" };
		String[] validCondition4 = { "ProfessorName", "=", "Omar" };
		String[] invalidCondition1 = { "Name", ">", "C" }; // column Name isn't
															// Exist.
		String[] invalidCondition2 = { "salary", ">", "C" }; // error in value
																// it must be
																// int .
		String[] newData1 = { "Momen", "T", "28" };
		String[] newData2 = { "ALY", "Tech", "5500" };

		obj.disconnectFromDatabase();
		assertEquals(false, obj.updateRecord(table1, colNamesForTable1, newData1, validCondition3));// you
																									// arn't
																									// connect
																									// to
																									// database
		assertEquals(false, obj.updateRecord(table2, colNamesForTable2, newData2, validCondition2));// you
																									// arn't
																									// connect
																									// to
																									// database

		obj.connectToDatabase(data);
		assertEquals(false, obj.updateRecord(table1, colNamesForTable1, newData1, invalidCondition1)); // Error
																										// in
																										// column
																										// name
		assertEquals(false, obj.updateRecord(table2, colNamesForTable2, newData2, invalidCondition2)); // Error
																										// in
																										// value
		assertEquals(false, obj.updateRecord(table3, colNamesForTable1, newData1, validCondition3));// Table
																									// isn't
																									// Exist
		assertEquals(false, obj.updateRecord(table1, colNamesForTable1, newData1, validCondition1));// No
																									// deleted
																									// records
																									// because
																									// condition
																									// don't
																									// occur

		assertEquals(true, obj.updateRecord(table1, colNamesForTable1, newData1, validCondition3));
		assertEquals(true, obj.updateRecord(table2, colNamesForTable2, newData2, validCondition2));
		assertEquals(true, obj.updateRecord(table2, colNamesForTable2, newData2, validCondition4));
	}

	public void retireveRecordTest() {
		String table1 = "Student";
		String table2 = "Professor";
		String table3 = "Employee";
		String data = "University";
		String[] validCondition1 = { "Marks", ">", "30" };
		String[] validCondition2 = { "Salary", "=", "5500" };
		String[] validCondition3 = { "Marks", "<", "28" };
		String[] validCondition4 = { "ProfessorName", "=", "Wessam" };
		String[] invalidCondition1 = { "Name", ">", "C" }; // column Name isn't
															// Exist.
		String[] invalidCondition2 = { "salary", ">", "C" }; // error in value
																// it must be
																// int .
		String[] colNamesForTable1 = { "StudentName", "Class", "Marks" };
		String[] colNamesForTable2 = { "ProfessorName", "Courses", "salary" };
		String[][] result = { { "Shaban", "V", "25" } };

		obj.disconnectFromDatabase();
		assertNull(obj.retireveRecord(table1, colNamesForTable1, validCondition3));// you
																					// arn't
																					// connect
																					// to
																					// database
		assertNull(obj.retireveRecord(table2, colNamesForTable2, validCondition2));// you
																					// arn't
																					// connect
																					// to
																					// database
		obj.connectToDatabase(data);
		assertNull(obj.retireveRecord(table1, colNamesForTable1, invalidCondition1)); // Error
																						// in
																						// column
																						// name
		assertNull(obj.retireveRecord(table2, colNamesForTable2, invalidCondition2)); // Error
																						// in
																						// value
		assertNull(obj.retireveRecord(table3, colNamesForTable1, validCondition3));// Table
																					// isn't
																					// Exist
		assertNull(obj.retireveRecord(table1, colNamesForTable1, validCondition1));// No
																					// deleted
																					// records
																					// because
																					// condition
																					// don't
																					// occur
		assertArrayEquals(result, obj.retireveRecord(table1, colNamesForTable1, validCondition3));
	}

}
