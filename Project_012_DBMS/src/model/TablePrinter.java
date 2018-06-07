package model;

import static java.lang.String.format;
import static java.lang.System.out;

import java.util.Arrays;

public final class TablePrinter {
	

		//******************************************************
		//01_Attributes*****************************************
			private String[] colNames;
			private String[] completeColNames;
			private String[][] dataToDisplay;
			private String[][] completeTable;
			
		    private static final char BORDER_KNOT = '+';
		    private static final char HORIZONTAL_BORDER = '-';
		    private static final char VERTICAL_BORDER = '|';
		    private static final String DEFAULT_AS_NULL = "(NULL)";


	    //******************************************************
	  	//02_Constructor****************************************
		    public TablePrinter(String[] colNames, String[][] dataToDisplay, String dtdFileDirectory){
		    	XMLFileHandler fileHandler=new XMLFileHandler();
				this.completeColNames=fileHandler.getTableAllColumnNames(dtdFileDirectory); 
				
		    	this.colNames=orderColNames(colNames, completeColNames);
				this.dataToDisplay=dataToDisplay;
				this.completeTable=constructCompleteTable(colNames, dataToDisplay);
		    }
	    
	    
		//******************************************************
		//03_Methods********************************************
		    public void printTablePatternA() {
		    	String[][] table=this.completeTable;
		    	final int[] widths = new int[getMaxColumns(table)];
		    	adjustColumnWidths(table, widths);
		    	printPreparedTable(table, widths, getHorizontalBorder(widths));
		    }
		    
		    public void printTablePatternB(){
				for(String[] row:this.completeTable){
					System.out.println(Arrays.toString(row));
				}
			}
	    //******************************************************
		//******************************************************
		    private String[][] constructCompleteTable(String[] colNames, String[][] dataToDisplay){
		    	int rows=dataToDisplay.length+1;
		    	int columns=dataToDisplay[0].length;
		    	String[][] result=new String[rows][columns];
		    	result[0]=this.colNames;
		    	for(int i=2; i<=rows; i++){
		    		result[i-1]=this.dataToDisplay[i-2];
		    	}
		    	return result;
		    }
		//******************************************************   
	    private String[] orderColNames(String[] colNames, String[] completeColNames){
	    	String[] result=new String[colNames.length];
	    	int pointer=1;
	    	for(String orderedCol:completeColNames){
	    		for(String colName:colNames){
	    			boolean found=orderedCol.equalsIgnoreCase(colName);
	    			if(found){
	    				result[pointer-1]=orderedCol;
	    				pointer++;
	    				break;
	    			}
	    		}
	    	}
	    	return result;
	    }
		//******************************************************
		//******************************************************
		    private void printPreparedTable(String[][] table, int widths[], String horizontalBorder) {
		        final int lineLength = horizontalBorder.length();
		        out.println(horizontalBorder);
		        for ( final String[] row : table ) {
		            if ( row != null ) {
		                out.println(getRow(row, widths, lineLength));
		                out.println(horizontalBorder);
		            }
		        }
		    }
	    //******************************************************
		    private String getRow(String[] row, int[] widths, int lineLength) {
		        final StringBuilder builder = new StringBuilder(lineLength).append(VERTICAL_BORDER);
		        final int maxWidths = widths.length;
		        for ( int i = 0; i < maxWidths; i++ ) {
		            builder.append(padRight(getCellValue(safeGet(row, i, null)), widths[i])).append(VERTICAL_BORDER);
		        }
		        return builder.toString();
		    }
	    //******************************************************
		    private String getHorizontalBorder(int[] widths) {
		        final StringBuilder builder = new StringBuilder(256);
		        builder.append(BORDER_KNOT);
		        for ( final int w : widths ) {
		            for ( int i = 0; i < w; i++ ) {
		                builder.append(HORIZONTAL_BORDER);
		            }
		            builder.append(BORDER_KNOT);
		        }
		        return builder.toString();
		    }
	    //******************************************************
		    private int getMaxColumns(String[][] rows) {
		        int max = 0;
		        for ( final String[] row : rows ) {
		            if ( row != null && row.length > max ) {
		                max = row.length;
		            }
		        }
		        return max;
		    }
	    //******************************************************
		    private void adjustColumnWidths(String[][] rows, int[] widths) {
		        for ( final String[] row : rows ) {
		            if ( row != null ) {
		                for ( int c = 0; c < widths.length; c++ ) {
		                    final String cv = getCellValue(safeGet(row, c, DEFAULT_AS_NULL));
		                    final int l = cv.length();
		                    if ( widths[c] < l ) {
		                        widths[c] = l;
		                    }
		                }
		            }
		        }
		    }
	    //******************************************************
		    private String padRight(String s, int n) {
		        return format("%1$-" + n + "s", s);
		    }
	    //******************************************************
		    private String safeGet(String[] array, int index, String defaultValue) {
		        return index < array.length ? array[index] : defaultValue;
		    }
	    //******************************************************
		    private String getCellValue(Object value) {
		        return value == null ? DEFAULT_AS_NULL : value.toString();
		    }
	    //******************************************************

}