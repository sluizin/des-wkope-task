package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;

/**
 * excel中图表的操作 只针对2006，即以xlsx文件
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTPOIChartPicture {
	/** 日志 */
	static Logger logger = Logger.getLogger(UtilsSWTPOIChartPicture.class);

	/**
	 * 按名称找到图表
	 * @param sheet Sheet
	 * @param name String
	 * @return CTTwoCellAnchor[]
	 */
	public static final CTTwoCellAnchor[] getCTTwoCellAnchor(Sheet sheet, String name) {
		CTTwoCellAnchor[] arr = {};
		List<CTTwoCellAnchor> list = new ArrayList<>();
		if (sheet == null || name == null) return arr;
		for (POIXMLDocumentPart dr : ((XSSFSheet) sheet).getRelations()) {
			if (!(dr instanceof XSSFDrawing)) continue;
			XSSFDrawing drawing = (XSSFDrawing) dr;
			List<XSSFShape> shapes = drawing.getShapes();
			for (XSSFShape shape : shapes) {
				CTDrawing cc = shape.getDrawing().getCTDrawing();
				CTTwoCellAnchor[] arrss = cc.getTwoCellAnchorArray();
				for (CTTwoCellAnchor ctAnchor : arrss) {
					if (name.equals(ctAnchor.getGraphicFrame().getNvGraphicFramePr().getCNvPr().getName())) list.add(ctAnchor);
				}
			}
		}
		return list.toArray(arr);
	}

	/**
	 * 设置图表的位置，分别为左上角与右下角的定位，如null，则不修改定位信息
	 * @param sheet Sheet
	 * @param name String
	 * @param from ChartPointClass
	 * @param to ChartPointClass
	 * @return boolean
	 */
	public static final boolean setCTTCAnchorCTMarker(Sheet sheet, String name, ChartPointClass from, ChartPointClass to) {
		if (from == null && to == null) return false;
		CTTwoCellAnchor[] arr = getCTTwoCellAnchor(sheet, name);
		if (arr.length == 0) return false;
		for (CTTwoCellAnchor e : arr) {
			if (from != null) from.output(e.getFrom());
			if (to != null) to.output(e.getTo());
		}
		return true;
	}

	/**
	 * 指定图表移动位置，以行，列为标准，允许正负值
	 * @param sheet Sheet
	 * @param name String
	 * @param index int
	 * @param x int
	 * @param y int
	 */
	public static final void moveChart(Sheet sheet, String name, int index, int x, int y) {
		moveChart(sheet, name, index, x, y, 0, 0);
	}

	/**
	 * 指定图表移动位置，允许正负值
	 * @param sheet Sheet
	 * @param name String
	 * @param index int
	 * @param x int
	 * @param y int
	 * @param xOff long
	 * @param yOff long
	 */
	public static final void moveChart(Sheet sheet, String name, int index, int x, int y, long xOff, long yOff) {
		CTTwoCellAnchor e = getCTTwoCellAnchor(sheet, name, index);
		if (e == null) return;
		int rowsize = x - e.getFrom().getRow();
		long xSize = xOff - e.getFrom().getRowOff();
		int colsize = y - e.getFrom().getCol();
		long ySize = yOff - e.getFrom().getColOff();
		moveChartOff(sheet, name, index, rowsize, colsize, xSize, ySize);
	}

	/**
	 * 指定图表移动位置，以行，列为标准，允许正负值
	 * @param sheet Sheet
	 * @param name String
	 * @param index int
	 * @param xSize int
	 * @param ySize int
	 */
	public static final void moveChartOff(Sheet sheet, String name, int index, int xSize, int ySize) {
		moveChartOff(sheet, name, index, xSize, ySize, 0, 0);
	}

	/**
	 * 指定图表移动位置，允许正负值
	 * @param sheet Sheet
	 * @param name String
	 * @param index int
	 * @param xSize int
	 * @param ySize int
	 * @param xOff long
	 * @param yOff long
	 */
	public static final void moveChartOff(Sheet sheet, String name, int index, int xSize, int ySize, long xOff, long yOff) {
		CTTwoCellAnchor e = getCTTwoCellAnchor(sheet, name, index);
		if (e == null) return;
		ChartPointClass i = new ChartPointClass();
		/* 左上角 */
		i.input(e.getFrom());
		i.row += xSize;
		i.rowOff += xOff;
		i.col += ySize;
		i.colOff += yOff;
		i.output(e.getFrom());
		/* 右下角 */
		i.input(e.getTo());
		i.row += xSize;
		i.rowOff += xOff;
		i.col += ySize;
		i.colOff += yOff;
		i.output(e.getTo());
	}

	/**
	 * 得到某图表的定位信息<br>
	 * 通过sheet,name,index得到定位信息<br>
	 * isTo，如为true，则返回图表右下角定位，否则返回左上角定位信息
	 * @param sheet Sheet
	 * @param name String
	 * @param index int
	 * @param isTo boolean
	 * @return ChartPointClass
	 */
	public static final ChartPointClass getPoint(Sheet sheet, String name, int index, boolean isTo) {
		CTTwoCellAnchor e = getCTTwoCellAnchor(sheet, name, index);
		if (e == null) return null;
		ChartPointClass r = new ChartPointClass();
		if (isTo) r.input(e.getTo());
		else r.input(e.getFrom());
		return r;
	}

	/**
	 * 得到指定的图表
	 * @param sheet Sheet
	 * @param name String
	 * @param index int
	 * @return CTTwoCellAnchor
	 */
	public static final CTTwoCellAnchor getCTTwoCellAnchor(Sheet sheet, String name, int index) {
		CTTwoCellAnchor[] arr = getCTTwoCellAnchor(sheet, name);
		if (arr.length == 0) return null;
		if (index < 0 || index >= arr.length) return null;
		return arr[index];
	}

	/**
	 * 图表点的定位信息
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ChartPointClass {
		int row = 0;
		long rowOff = 0;
		int col = 0;
		long colOff = 0;

		public ChartPointClass() {

		}

		public ChartPointClass(int row, long rowOff, int col, long colOff) {
			this.row = row;
			this.rowOff = rowOff;
			this.col = col;
			this.colOff = colOff;
		}

		/**
		 * 把定位信息输入到CTMarker
		 * @param e CTMarker
		 */
		public void output(CTMarker e) {
			if (e == null) return;
			e.setRow(row);
			e.setRowOff(rowOff);
			e.setCol(col);
			e.setColOff(colOff);
		}

		/**
		 * 从CTMarker输出到定位类中
		 * @param e CTMarker
		 */
		public void input(CTMarker e) {
			if (e == null) return;
			this.row = e.getRow();
			this.rowOff = e.getRowOff();
			this.col = e.getCol();
			this.colOff = e.getColOff();
		}

		public final int getRow() {
			return row;
		}

		public final void setRow(int row) {
			this.row = row;
		}

		public final long getRowOff() {
			return rowOff;
		}

		public final void setRowOff(long rowOff) {
			this.rowOff = rowOff;
		}

		public final int getCol() {
			return col;
		}

		public final void setCol(int col) {
			this.col = col;
		}

		public final long getColOff() {
			return colOff;
		}

		public final void setColOff(long colOff) {
			this.colOff = colOff;
		}
	}
}
