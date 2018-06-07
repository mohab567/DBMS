package model;

import java.io.File;
import java.sql.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;



public class InputValidator {

	
	//*****************************************************************
	//00_Testing*******************************************************
		public static void main(String args[]) {	
			
			String xmlFileDiretcory="DatabaseRepository"+File.separator+"pepsi"+File.separator+"clients.xml";
			InputValidator tmp=new InputValidator();
			String[] completeColNames={"name", "email", "phone"};
			String[] completeColValues={"Mohamed", "Gmail", "123"};
			
			boolean test=tmp.validateQueryValues(xmlFileDiretcory, completeColNames, completeColValues);
			System.out.println(test);
		}
		
	//*****************************************************************
	//02_Constructor***************************************************
		public InputValidator(){
			
		}
		
	//*****************************************************************
	//03_Methods*******************************************************
		public boolean validateQueryValues(String tableXMLDirectory, String[] completeColNames, String[] completeColValues){
			//This method validates userQuery against the table's DTD file
			//Column Names & Values must be ordered and complete
			File tableFile=new File(tableXMLDirectory);
			String tableName=tableFile.getName().replaceAll(".xml", "");
			String tmpXMLFileDir=tableXMLDirectory.replaceAll(tableFile.getName(), "tmpTest.xml");
			String dtdFileName=tableName+".dtd";
			String dtdFileDirectory=tableXMLDirectory.replaceAll(".xml", ".dtd");
			
			//Create Tmp Table with the Input Records
			XMLFileHandler fileHandler=new XMLFileHandler();
			fileHandler.createEmptyXML(tableName, tmpXMLFileDir, dtdFileName);
			fileHandler.addNewRecord(tableName, tmpXMLFileDir, completeColNames, completeColValues);
			
			//Run Tests
			boolean structureValidation=validateDocumentStructure(tmpXMLFileDir);
			boolean dataValueValidation=validateDocumentValues(tmpXMLFileDir, dtdFileDirectory);
			if((structureValidation==true)&&(dataValueValidation==true)){
				fileHandler.deleteFile(new File(tmpXMLFileDir));
				return true;
			}
			fileHandler.deleteFile(new File(tmpXMLFileDir));
			return false;
		}
		//****************************************************
		//****************************************************
		private boolean validateDocumentStructure(String xmlFileDirectory){
			//01_Overwrite Exceptions
			class SimpleErrorHandler implements ErrorHandler {
			    public void warning(SAXParseException e) throws SAXException {
			        System.out.println(e.getMessage());
			    	throw new SAXException();
			    }
			    public void error(SAXParseException e) throws SAXException {
			        System.out.println(e.getMessage());
			    	throw new SAXException();
			    }
			    public void fatalError(SAXParseException e) throws SAXException {
			        System.out.println(e.getMessage());
			    	throw new SAXException();
			    }
			}
			//02_Validate Document
			try{
				 SAXParserFactory factory = SAXParserFactory.newInstance();
		            factory.setValidating(true);
		            factory.setNamespaceAware(true);

		            SAXParser parser = factory.newSAXParser();
		            XMLReader reader = parser.getXMLReader();
		            reader.setErrorHandler(new SimpleErrorHandler());
		            reader.parse(new InputSource(xmlFileDirectory));
		            return true;
				}
			catch (Exception e){
				return false;
			}
		}
		//****************************************************
		private boolean validateDocumentValues(String xmlFileDirectory, String dtdFileDirectory){
			File dtdFile=new File(dtdFileDirectory);
			String elementName=dtdFile.getName().replaceAll(".dtd", "");
			
			XMLFileHandler tmp=new XMLFileHandler();
			String[] properColumnTypes=tmp.getTableAllColumnTypes(dtdFileDirectory);
			
			try {	
				//01_Import Document
					File inputFile = new File(xmlFileDirectory);
					DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
					Document doc=dBuilder.parse(inputFile);
		        //02_Read From Document
					NodeList docElements=doc.getElementsByTagName(elementName);
					for (int i=1; i<=docElements.getLength(); i++) {
						Node currentElement=docElements.item(i-1);
						NodeList elementChildren=currentElement.getChildNodes();
						int pointer=1;
						for(int j=1; j<=elementChildren.getLength(); j++){
							Node currentChild=elementChildren.item(j-1);
							try{
								Element currentColumn=(Element)currentChild;
								String properColType=properColumnTypes[pointer-1];
								pointer++;
								if(properColType.equalsIgnoreCase("int")){
									String storedData=currentColumn.getTextContent();
									try{
										Integer.parseInt(storedData);
									}catch(Exception e){
										if(storedData.equalsIgnoreCase("null")){
											continue;
										}else{
											return false;
										}
									}
								}else if(properColType.equalsIgnoreCase("float")){
									String storedData=currentColumn.getTextContent();
									try{
										Float.parseFloat(storedData);
									}catch(Exception e){
										if(storedData.equalsIgnoreCase("null")){
											continue;
										}else{
											return false;
										}
									}
								}else if(properColType.equalsIgnoreCase("date")){
									String storedData=currentColumn.getTextContent();
									try{
										Date.valueOf(storedData);
									}catch(Exception e){
										if(storedData.equalsIgnoreCase("null")){
											continue;
										}else{
											return false;
										}
									}
								}
							}catch(Exception x){}
						}
					}
					return true;
			}catch(Exception ex){}
			return false;
		}
		//****************************************************
		public String[][] getCompleteInputData(String dtdFileDirectory, String[] colNames, String[] colValues){
			//This method returns table all complete column names with all values filled if not
			//This method assumes that input colNames are all correct
			XMLFileHandler fileHandler=new XMLFileHandler();
			String[] completeColNames=fileHandler.getTableAllColumnNames(dtdFileDirectory);
			String[] completeColValues= new String[completeColNames.length];
			//Filling in empty values
			if(colNames.length<colValues.length){
				throw new RuntimeException();
			}
			for(int i=1; i<=completeColNames.length; i++){
				completeColValues[i-1]="null";
				for(int j=1; j<=colValues.length; j++){
					if(completeColNames[i-1].equalsIgnoreCase(colNames[j-1])){
						completeColValues[i-1]=colValues[j-1];
						break;
					}
				}
			}
			String[][] result={completeColNames, completeColValues};
			return result;
		}
		//****************************************************
		public boolean validateInputColNames(String dtdFileDirectory, String[] colNames){
			XMLFileHandler fileHandler=new XMLFileHandler();
			String[] completeColNames=fileHandler.getTableAllColumnNames(dtdFileDirectory);
			for(String inputCol:colNames){
				boolean matchFound=false;
				for(String validColName:completeColNames){
					if(inputCol.equalsIgnoreCase(validColName)){
						matchFound=true;
					}
				}
				if(matchFound==false){
					return false;
				}
			}
			return true;
		}
		//****************************************************
		public boolean validateInputColumnTypes(String[] colTypes){
			for(String colType:colTypes){
				boolean test=(colType.equalsIgnoreCase("int"))||(colType.equalsIgnoreCase("varchar"))||
						(colType.equalsIgnoreCase("float"))||(colType.equalsIgnoreCase("date"));
				if(test==false){
					return false;
				}
			}
			return true;
		}
		//****************************************************

		
		

		
		
}
