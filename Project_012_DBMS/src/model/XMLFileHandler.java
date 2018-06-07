package model;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLFileHandler {
	
	//**************************************************************
	public boolean addNewRecord(String tableName, String fileDirectory, String[] colNames, String[] data){
		try {
			//01_Import Document
				File inputFile=new File(fileDirectory);
				DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
				Document doc=dBuilder.parse(inputFile);
				Element root=doc.getDocumentElement();
			//02_Create The New Element
				Element newElement=doc.createElement(tableName);
				root.appendChild(newElement);
				for(int i=1; i<=colNames.length; i++){
					String colName=colNames[i-1];
					String colValue=data[i-1];
					Element tmp=doc.createElement(colName);
					tmp.appendChild(doc.createTextNode(colValue));
					newElement.appendChild(tmp);
				}
	        //02_Overwrite XML file
				String dtdFileRelativeDir=tableName+".dtd";
				TransformerFactory transformerFactory=TransformerFactory.newInstance();
				Transformer transformer=transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdFileRelativeDir);
				DOMSource source=new DOMSource(doc);
				StreamResult result=new StreamResult(new File(fileDirectory));
				transformer.transform(source, result);
				return true;
			}catch(Exception ex){}
		return false;
	}
	
	//**************************************************************
	public String[][] retrieveRecord(String tableName, String fileDirectory, String[] colNames, String testCol, String operation, String condition){
		//A_Find index of records matching query condition
		int[] matchRecords=searchTable(tableName, fileDirectory, testCol, operation, condition);
		String[][] retrievedData=new String[matchRecords.length][colNames.length];
		//B_Obtain Table Data
		try {	
			//01_Import Document
				File inputFile = new File(fileDirectory);
				DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
				Document table=dBuilder.parse(inputFile);
				NodeList tableRecords=table.getElementsByTagName(tableName);
			//02_Retrieve Data
				int row=1; int column=1;
				for(int index:matchRecords){
					Node matchingRecord=tableRecords.item(index);
					NodeList childNodes=matchingRecord.getChildNodes();
					column=1;
					for(int i=1; i<=childNodes.getLength(); i++){
						Node currentChild=childNodes.item(i-1);
						try{
							Element currentCol=(Element)currentChild;
							for(String colName:colNames){
								boolean test=currentCol.getTagName().equalsIgnoreCase(colName);
								if(test){
									retrievedData[row-1][column-1]=currentCol.getTextContent();
									column++;
									break;
								}
							}
						}catch(Exception ex){}
					}
					row++;
				}
			//04_Return obtained values
				return retrievedData;
			}catch(Exception ex){}
		return null;
	}
	//**************************************************************
	public boolean updateRecord(String tableName, String fileDirectory, String[] colNames, String[] updateValues, String testCol, String operation, String condition){
		//01_Find index of records matching query condition
		int[] matchRecords=searchTable(tableName, fileDirectory, testCol, operation, condition);
		try {
			//01_Import Document
				File inputFile=new File(fileDirectory);
				DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
				Document doc=dBuilder.parse(inputFile);
				Element root=doc.getDocumentElement();
				NodeList tableRecords=root.getElementsByTagName(tableName);
			//03_Modify selected columns
				for(int index:matchRecords){
					Node matchingRecord=tableRecords.item(index);
					NodeList childNodes=matchingRecord.getChildNodes();
					for(int i=1; i<=childNodes.getLength(); i++){
						Node currentChild=childNodes.item(i-1);
						try{
							Element currentCol=(Element)currentChild;
							for(int k=1; k<=colNames.length; k++){
								String colName=colNames[k-1];
								boolean test=currentCol.getTagName().equalsIgnoreCase(colName);
								if(test){
									currentCol.setTextContent(updateValues[k-1]);
									break;
								}
							}
						}catch(Exception ex){}
					}
				}
	        //02_Write Updated Document to File
				String dtdFileRelativeDir=tableName+".dtd";
				TransformerFactory transformerFactory=TransformerFactory.newInstance();
				Transformer transformer=transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdFileRelativeDir);
				DOMSource source=new DOMSource(doc);
				StreamResult result=new StreamResult(new File(fileDirectory));
				transformer.transform(source, result);
				
				return true;
			}catch(Exception ex){}
		return false;
	}
	//**************************************************************
	public boolean deleteRecord(String tableName, String fileDirectory, String testCol, String operation, String condition){
		//01_Find index of records matching query condition
		int[] matchRecords=searchTable(tableName, fileDirectory, testCol, operation, condition);
		try {
			//01_Import Document
				File inputFile=new File(fileDirectory);
				DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
				Document doc=dBuilder.parse(inputFile);
				Element root=doc.getDocumentElement();
				NodeList tableRecords=root.getElementsByTagName(tableName);
			//02_Remove Matching Element(s)
				for(int i=matchRecords.length; i>=1; i--){
					int index=matchRecords[i-1];
					Node nodeToRemove=tableRecords.item(index);
					root.removeChild(nodeToRemove);
				}
			//03_Overwrite XML file
				int remainingElements=root.getChildNodes().getLength();
				if(remainingElements==0){
					createEmptyXML(tableName, fileDirectory, tableName+".dtd");
					return true;
				}else{
					String dtdFileRelativeDir=tableName+".dtd";
					TransformerFactory transformerFactory=TransformerFactory.newInstance();
					Transformer transformer=transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdFileRelativeDir);
					DOMSource source=new DOMSource(doc);
					StreamResult result=new StreamResult(new File(fileDirectory));
					transformer.transform(source, result);
					return true;
				}
			}catch(Exception ex){}
		
		return false;
	}
	//**************************************************************
	//**************************************************************
	public boolean createEmptyXML(String tableName, String targetDirectory, String dtdFileName){
		try{
			//01_Create File
			File xmlFile=new File(targetDirectory);
			PrintWriter fileWriter=new PrintWriter(xmlFile);
			//02_Create XML Column Structure
			dtdFileName="\""+dtdFileName+"\"";
			String prolog="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			String linkedDTDFileTag="<!DOCTYPE parent SYSTEM "+dtdFileName+">";
			String rootTag="<parent>\n</parent>";
			fileWriter.println(prolog);
			fileWriter.println(linkedDTDFileTag);
			fileWriter.println(rootTag);
			//04_Close file
			fileWriter.close();
			return true;
		}catch(IOException e){}	
		return false;
	}
	//**************************************************************
	public boolean createDTDFile(String tableName, String fileDirectory, String[] colNames, String[] colTypes){
		try{
			//01_Create File
			File dtdFile=new File(fileDirectory);
			PrintWriter fileWriter=new PrintWriter(dtdFile);
			//02_Create XML Column Structure
			String rootNode="<!ELEMENT parent ("+tableName+"*)>";
			fileWriter.println(rootNode);
			
			String parentNode="<!ELEMENT "+tableName+" (";
			for(int i=1; i<=colNames.length; i++){
				parentNode=parentNode+colNames[i-1];
				if(!(i==colNames.length)){parentNode+=",";}
			}
			parentNode=parentNode+")>";
			fileWriter.println(parentNode);
			for(String tableCol: colNames){
				String line="<!ELEMENT "+tableCol+" (#PCDATA)>";
				fileWriter.println(line);
			}
			//03_Create XML DataTypes
			fileWriter.println("");
			for(int i=1; i<=colNames.length; i++){
				String colName=colNames[i-1];
				String colType=colTypes[i-1].toLowerCase();
				String line="<!ATTLIST "+colName+" type CDATA \""+colType+"\">";
				fileWriter.println(line);
			}
			//04_Close file
			fileWriter.close();
			return true;
		}catch(IOException e){}	
		return false;
	}
	//**************************************************************
	public void deleteFile(File selectedFile){
	    if(selectedFile.isDirectory()){
	        for(File subFile:selectedFile.listFiles()){
	            deleteFile(subFile);
	        }
	    }
	    selectedFile.delete();
	}
	//**************************************************************
	//**************************************************************
	//**************************************************************
	private int[] searchTable(String tableName, String fileDirectory, String testCol, String operation, String condition){
		//This method returns the index of records matching query condition
		ArrayList<Integer> resultIndices=new ArrayList<Integer>();
		try{	
			//01_Import Document
				File inputFile = new File(fileDirectory);
				DocumentBuilderFactory dbFactory=DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder=dbFactory.newDocumentBuilder();
				Document doc=dBuilder.parse(inputFile);
	        //02_Read From Document
				NodeList tableRecords=doc.getElementsByTagName(tableName);
				if(tableRecords.getLength()==0){
					System.out.println("Empty Table...");
				}
				if(retrieveAllRecords(testCol, condition)){
					for(int i=1; i<=tableRecords.getLength(); i++){
						resultIndices.add(i-1);
					}
				}else{
					for(int j=1; j<=tableRecords.getLength(); j++){
						Node currentRecord=tableRecords.item(j-1);
						NodeList recordCols=currentRecord.getChildNodes();
						//03_Search Document Row
						for(int i=1; i<=recordCols.getLength(); i++){
							Node currentCol=recordCols.item(i-1);
							try{
								Element tmp=(Element)currentCol;
								boolean test=tmp.getTagName().equalsIgnoreCase(testCol);
								if(test){
									String data=tmp.getTextContent();
									boolean match=validateCondition(data, condition, operation);
									if(match){resultIndices.add(j-1);}
								}
							}catch(Exception ex){}
						}
					}
				}				
				if(resultIndices.size()>0){
					Integer[] tmp=resultIndices.toArray(new Integer[resultIndices.size()]);
					int[] finalResult = Arrays.stream(tmp).mapToInt(Integer::intValue).toArray();
					return finalResult;
				}
			}catch(Exception ex){}
		return null;
	}
	//**************************************************************
	private boolean validateCondition(String LHS, String RHS, String operation){
		if(operation.equalsIgnoreCase("=")){
			if(LHS.equalsIgnoreCase(RHS)){
				return true;
			}
			return false;
		}
		if(operation.equalsIgnoreCase(">")){
			try{
				int left=Integer.parseInt(LHS);
				int right=Integer.parseInt(RHS);
				if(left>right){return true;}
			}catch(Exception ex){}
			return false;
		}
		if(operation.equalsIgnoreCase("<")){
			try{
				int left=Integer.parseInt(LHS);
				int right=Integer.parseInt(RHS);
				if(left<right){return true;}
			}catch(Exception ex){}
			return false;
		}
		return false;
	}
	//**************************************************************
	private boolean retrieveAllRecords(String colName, String testValue){
		//this function tells you to retrieve all indices or not
		try{
			int lhs=Integer.parseInt(colName);
			int rhs=Integer.parseInt(testValue);
			if(lhs==rhs){
				return true;
			}
		}catch(Exception ex){}
		return false;
	}
	//**************************************************************
	public String[] getTableAllColumnNames(String dtdFileDirectory){
		ArrayList<String> result=new ArrayList<>();
		Matcher matchTester;
		String pattern="(<!)(ELEMENT)( +?)(.+)( +?)([(])(#PCDATA)([)])( *?)(>)";
		File targetFile=new File(dtdFileDirectory);
		try{
			Scanner fileReader=new Scanner(targetFile);
			while(fileReader.hasNext()){
				String currentLine=fileReader.nextLine();
				matchTester=Pattern.compile(pattern).matcher(currentLine);
				if(matchTester.matches()){
					String colName=matchTester.group(4).replaceAll(" ", "");
					result.add(colName);
				}
			}
			fileReader.close();	
			String[] finalResult=result.toArray(new String[result.size()]);
			return finalResult;
		}catch(Exception ex){}
		return null;
	}
	//**************************************************************
	public String[] getTableAllColumnTypes(String dtdFileDirectory){
		ArrayList<String> result=new ArrayList<>();
		Matcher matchTester;
		String pattern="(<!)(ATTLIST)( +?)(.+)( +?)(type)( +?)(CDATA)( +?)(.+)( *?)(>)";
		File targetFile=new File(dtdFileDirectory);
		try{
			Scanner fileReader=new Scanner(targetFile);
			while(fileReader.hasNext()){
				String currentLine=fileReader.nextLine();
				matchTester=Pattern.compile(pattern).matcher(currentLine);
				if(matchTester.matches()){
					String coType=matchTester.group(10).replaceAll(" ", "").replaceAll("\"", "");
					result.add(coType);
				}
			}
			fileReader.close();	
			String[] finalResult=result.toArray(new String[result.size()]);
			return finalResult;
		}catch(Exception ex){}
		return null;
	}
	//**************************************************************
	
	

}
