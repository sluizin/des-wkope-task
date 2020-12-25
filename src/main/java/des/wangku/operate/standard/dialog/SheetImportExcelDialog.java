package des.wangku.operate.standard.dialog;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.swt.AbstractCTabFolder;
import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.utls.UtilsDialogState;
import des.wangku.operate.standard.utls.UtilsPOI;
import des.wangku.operate.standard.utls.UtilsRnd;
import des.wangku.operate.standard.utls.UtilsSWTMessageBox;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * 导入excel中的sheet
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class SheetImportExcelDialog extends Dialog {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(SheetImportExcelDialog.class);
	protected Shell shlsheet;
	AbstractCTabFolder parentExcel;
	FileDialog fileselect = null;
	Combo combo = null;
	Button button_rndname =null;
	ImportSheetInfor result = null;
	String url = "";
	private Text text;

	public SheetImportExcelDialog(Shell parent, int style, AbstractCTabFolder parentExce) {
		super(parent, style);
		this.parentExcel = parentExce;
		this.shlsheet = new Shell(parent);
		UtilsDialogState.changeDialogCenter(parent, shlsheet);
		shlsheet.setText("导入excel中的sheet");
		shlsheet.setSize(239, 136);

		combo = new Combo(shlsheet, SWT.READ_ONLY);
		combo.setBounds(56, 51, 98, 21);
		Button button = new Button(shlsheet, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ImportSheetInfor newInfor = new ImportSheetInfor();
				if (url == null || combo.getText() == null || combo.getText().length() == 0) return;
				newInfor.url = url;
				newInfor.sheetname = combo.getText();
				result = newInfor;				
				if(!importExcel())return;
				
				
				shlsheet.dispose();

			}
		});
		button.setBounds(78, 78, 68, 23);
		button.setText("导入");

		Label lblSheet = new Label(shlsheet, SWT.RIGHT);
		lblSheet.setBounds(10, 54, 40, 13);
		lblSheet.setText("Sheet页");

		text = new Text(shlsheet, SWT.BORDER | SWT.READ_ONLY);
		text.setBounds(10, 10, 213, 19);
		text.setToolTipText("双击选择不同文件");
		
		button_rndname = new Button(shlsheet, SWT.CHECK);
		button_rndname.setBounds(160, 52, 63, 16);
		button_rndname.setText("随机名");
		button_rndname.setToolTipText("如果sheet出现同名\n如果选中则使用随机名称进行导入\n否则放弃导入");
		text.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				seletfile();
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseUp(MouseEvent e) {

			}

		});
	}
	/**
	 * 导入数据
	 * @return boolean
	 */
	boolean importExcel() {
		boolean isExist = parentExcel.isExistSheetName(result.sheetname);
		if (isExist && !button_rndname.getSelection()) {
			UtilsSWTMessageBox.Alert(shlsheet, "发现同名sheet!");
			return false;
		}
		if(button_rndname.getSelection()) {
			result.sheetname=UtilsRnd.getNewFilenameNow(4, 1);
		}
		return true;
	}
	/**
	 * 选择文件
	 */
	void seletfile() {
		FileDialog fileselect = new FileDialog(shlsheet, SWT.SINGLE);
		fileselect.setFilterNames(new String[] { "*.xlsx", "*.xls" });
		fileselect.setFilterExtensions(new String[] { "*.xlsx", "*.xls" });
		fileselect.setText("选择excel文件");
		url = fileselect.open();
		if (url == null) return;
		text.setText(url);
		readExcelFile();
	}

	void readExcelFile() {
		if (url == null) return;
		combo.removeAll();
		Sheet[] arr = UtilsPOI.getSheets(url);
		if (arr == null) return;
		if (arr.length == 0) return;
		for (Sheet e : arr) {
			combo.add(e.getSheetName());
		}
		Sheet sheet = arr[0];
		int select = sheet.getWorkbook().getActiveSheetIndex();
		combo.select(select);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		shlsheet.open();
		shlsheet.layout();
		Display display = getParent().getDisplay();
		while (!shlsheet.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	/**
	 * 导入sheet信息主类
	 * 
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ImportSheetInfor {
		String url = null;
		String sheetname = null;
		int select=-1;
		/**
		 * 向ExcelCTabFolder添加sheet
		 * @param base ExcelCTabFolder
		 */
		public void addSheet(AbstractCTabFolder base) {
			if (base == null || sheetname == null || sheetname.length() == 0) return;
			String[] titleArrs = base.getCTableTitle();
			for (int i = 0; i < titleArrs.length; i++)
				if (titleArrs[i].equals(sheetname)) return;
			CTabItem tbtmExcel = new CTabItem(base, SWT.NONE);
			tbtmExcel.setText(sheetname);
			ResultTable table = new ResultTable(base, ResultTable.ACC_ResultTableState,base.getProperties(),sheetname);
			table.setHeaderVisible(base.isViewTableHead());
			table.setLinesVisible(true);
			tbtmExcel.setControl(table);
			try {
				Workbook workbook = new XSSFWorkbook(url);
				Sheet sheet=workbook.getSheet(sheetname);
				if(sheet==null) {
					workbook.close();
					return;
				}
				int cols=UtilsPOI.getWidthMax(sheet);
				for (int i = 0; i < cols; i++)
					table.setTableColumn(SWT.LEFT,i + "", 180);
				table.add(sheet);
				
				
				
				
				
				
				
				workbook.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/*
			for (int i = 0; i < this.cols; i++)
				table.setTableColumn(i + "", 180);
			String[] arrs = new String[cols];
			Arrays.fill(arrs, "");
			for (int i = 0; i < this.rows; i++)
				UtilsSWTTableSQL.add(table, arrs);
				*/
		}
	}
}
