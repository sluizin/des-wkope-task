package des.wangku.operate.standard.task;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import des.wangku.operate.standard.utls.UtilsSWTPOI;

/**
 * 修改poi中某个表格的状态
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceExcelChange {
	/** 日志 */
	static Logger logger = Logger.getLogger(InterfaceExcelChange.class);

	/**
	 * 修改Cell内容状态 颜色，样式等内容
	 * @param x int
	 * @param y int
	 * @param cell Cell
	 * @param maxRows int
	 * @param maxCols int
	 */
	public void ChangeCell(int x, int y, Cell cell, int maxRows, int maxCols);

	/**
	 * 修改sheet信息,如设置宽度、高度等信息<br>
	 * 针对于sheet进行操作
	 * @param sheet Sheet
	 */
	public void ChangeSheet(Sheet sheet);

	/**
	 * 设置Sheet，遍历sheet里的所有单元，进行详细的后期设置
	 * @param sheet Sheet
	 */
	public default void afterWork(Sheet sheet) {
		ChangeSheet(sheet);
		int maxRows = sheet.getLastRowNum();
		int maxCols = 0;
		Row firstRow = sheet.getRow(0);
		if (firstRow == null) return;
		maxCols = firstRow.getLastCellNum();
		for (int i = 0; i <= maxRows; i++)
			for (int ii = 0; ii <= maxCols; ii++) {
				Cell cell = sheet.getRow(i).getCell(ii);
				if (cell != null) ChangeCell(i, ii, cell, maxRows, maxCols);
			}
	}

	/**
	 * 设置背景色
	 * @param style CellStyle
	 * @param ref HSSFColorPredefined
	 */
	public static void setStyleBGColor(CellStyle style, HSSFColorPredefined ref) {
		if (style == null) return;
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(ref.getIndex());
	}

	/**
	 * 修改CellStyle 有四个方面的边框和居中与上下居中设置
	 * @param style CellStyle
	 */
	public static void initExcelCellStyle(CellStyle style) {
		style.setBorderBottom(BorderStyle.THIN); //下边框    
		style.setBorderLeft(BorderStyle.THIN);//左边框    
		style.setBorderTop(BorderStyle.THIN);//上边框    
		style.setBorderRight(BorderStyle.THIN);//右边框   
		style.setAlignment(HorizontalAlignment.CENTER);// 居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);
	}

	/**
	 * 以某个单元格，同列，向下合并空格，直至非空数据
	 * @param style CellStyle
	 * @param cell Cell
	 * @param x int
	 * @param y int
	 */
	public static void CellRangeAddressRows(CellStyle style, Cell cell, int x, int y) {
		Sheet sheet = cell.getSheet();
		int downX = x;
		for (int i = x + 1; i <= sheet.getLastRowNum(); i++) {
			String value = UtilsSWTPOI.getCellValueByCell(sheet.getRow(i).getCell(y));
			if (value == null || value.length() == 0) downX = i;
			else break;
		}
		if (downX == x) return;
		/* 合并向下多行单元格 */
		CellRangeAddress region1 = new CellRangeAddress(x, downX, y, y);
		cell.getSheet().addMergedRegion(region1);
	}

	public static final int ExcelColWdith = 12;
	public static final short ExcelRowHeight = 400;
}
