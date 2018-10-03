package es.pryades.imedig.core.common;

import java.io.Serializable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelTableUtils implements Serializable 
{
	private static final long serialVersionUID = -7291752911700823148L;
	
	public static final String stringFormat = "";
	public static final String moneyFormat = "0.00 [$€-C0A];[RED]-0.00 [$€-C0A]";

	public static final int RIGHT_LIST 		= 0x01;
	public static final int RIGHT_INSERT 	= 0x02;
	public static final int RIGHT_UPDATE	= 0x04;
	public static final int RIGHT_DELETE	= 0x08;
	public static final int RIGHT_EXPORT	= 0x10;

	public static void createHeaderCell( Workbook wb, CellStyle cellStyle, Row row, int column, String title ) 
    {
		CreationHelper ch = wb.getCreationHelper();
        Cell cell = row.createCell(column);
        cell.setCellValue(ch.createRichTextString(title));
        
        cell.setCellStyle(cellStyle);
    }
	
	public static CellStyle createHeaderCellStyle( Workbook wb, short halign, short valign, boolean wrap ) 
    {
		Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short)11);
        cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(halign);
        cellStyle.setWrapText(wrap);
        cellStyle.setVerticalAlignment(valign);
        cellStyle.setFont(cellFont);
        
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        
        return cellStyle;
    }
	
	public static void createTitleCell( Workbook wb, Row row, int column, short halign, short valign, boolean wrap, String title ) 
    {
		CreationHelper ch = wb.getCreationHelper();
        Cell cell = row.createCell(column);
        cell.setCellValue(ch.createRichTextString(title));
        
        Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short)11);
        cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(halign);
        cellStyle.setWrapText(wrap);
        cellStyle.setVerticalAlignment(valign);
        cellStyle.setFont(cellFont);

        cell.setCellStyle(cellStyle);
        
    }
	
	public static Cell getFooterCell( CellStyle cellStyle, Row row, int column) 
    {
        Cell cell = row.createCell(column);
        
        cell.setCellStyle(cellStyle);
        
        return cell;
    }
	
	public static CellStyle createFooterCellStyle( Workbook wb, short halign, short valign, boolean wrap ) {
        Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short)11);
        cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(halign);
        cellStyle.setWrapText(wrap);
        cellStyle.setVerticalAlignment(valign);
        cellStyle.setFont(cellFont);
        
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        
        return cellStyle;
	}
	
    public static Cell createDataCell( CellStyle cs, Row row, int column ){
    	Cell cell = row.createCell(column);
        
        cell.setCellStyle( cs );
        
        return cell;
    }

    public static CellStyle createDataCellStyle( Workbook wb, short halign, short valign ) {
        Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short)11);
        
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(halign);
        cellStyle.setVerticalAlignment(valign);
        cellStyle.setFont(cellFont);
        
        cellStyle.setBorderBottom(CellStyle.BORDER_DOTTED);
        cellStyle.setBorderLeft(CellStyle.BORDER_DOTTED);
        cellStyle.setBorderRight(CellStyle.BORDER_DOTTED);
        
        return cellStyle;
	}
    
	/**
     * Creates a cell and aligns it a certain way.
     *
     * @param wb     the workbook
     * @param row    the row to create the cell in
     * @param column the column number to create the cell in
     * @param halign the horizontal alignment for the cell.
     */
    public static Cell createCell( Workbook wb, Row row, int column, short halign, short valign, String format, boolean bold, boolean left, boolean right, boolean top, boolean bottom ) 
    {
        Cell cell = row.createCell( column );
        
        CellStyle cellStyle = wb.createCellStyle();
        
        Font font = wb.createFont();
        font.setBoldweight( bold ? Font.BOLDWEIGHT_BOLD : Font.BOLDWEIGHT_NORMAL );
        
        cellStyle.setAlignment(halign);
        cellStyle.setFont( font );
        cellStyle.setVerticalAlignment(valign);
        cellStyle.setDataFormat( wb.createDataFormat().getFormat( format ) );
        cellStyle.setBorderLeft( left ? CellStyle.BORDER_THIN : CellStyle.BORDER_NONE );
        cellStyle.setBorderRight( right ? CellStyle.BORDER_THIN : CellStyle.BORDER_NONE );
        cellStyle.setBorderTop( top ? CellStyle.BORDER_THIN : CellStyle.BORDER_NONE );
        cellStyle.setBorderBottom( bottom ? CellStyle.BORDER_THIN : CellStyle.BORDER_NONE );
        
        cell.setCellStyle(cellStyle);
        
        return cell;
    }
    
    public static Cell createCell( Row row, int column ) 
    {
    	return row.createCell( column );
    }


    public static void setCellBorders( Cell cell, boolean left, boolean right, boolean top, boolean bottom )
    {
        CellStyle style = cell.getCellStyle();
        
        style.setBorderLeft( left ? CellStyle.BORDER_THIN : CellStyle.BORDER_NONE );
        style.setBorderRight( right ? CellStyle.BORDER_THIN : CellStyle.BORDER_NONE );
        style.setBorderTop( top ? CellStyle.BORDER_THIN : CellStyle.BORDER_NONE );
        style.setBorderBottom( bottom ? CellStyle.BORDER_THIN : CellStyle.BORDER_NONE );

        cell.setCellStyle( style );
    }
}
