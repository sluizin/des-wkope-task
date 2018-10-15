package des.wangku.operate.standard.dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.utls.UtilsDialogState;
import des.wangku.operate.standard.utls.UtilsSWTMessageBox;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;

public class SearchDialog extends Dialog {
	/** 日志 */
	static Logger logger = Logger.getLogger(SearchDialog.class);
	Shell parent;
	protected Shell shell = null;
	private Text searchText = null;
	Button button_search = null;
	Button button_cleanbgcolor = null;
	Combo combo = null;
	ResultTable table = null;

	public SearchDialog(Shell parent, int style, ResultTable table) {
		super(parent, style);
		this.parent = parent;
		this.table = table;
		this.shell = new Shell(parent);
		shell.setSize(224, 115);
		shell.setText("从表中检索关键字");
		searchText = new Text(shell, SWT.BORDER);
		combo = new Combo(shell, SWT.READ_ONLY);

		searchText.setBounds(10, 10, 198, 18);

		combo.add("所有列");
		if (table != null) {
			TableColumn[] arrs = table.getColumns();
			for (int i = 0, len = arrs.length; i < len; i++) {
				TableColumn ee = arrs[i];
				combo.add(ee.getText());
			}
		}
		combo.setToolTipText("从某列中查找");
		combo.setBounds(10, 34, 86, 20);
		combo.select(0);

		button_search = new Button(shell, SWT.NONE);
		button_search.setBounds(136, 58, 72, 22);
		button_search.setText("查找");

		button_cleanbgcolor = new Button(shell, SWT.CHECK);
		button_cleanbgcolor.setSelection(true);
		button_cleanbgcolor.setBounds(104, 38, 72, 16);
		button_cleanbgcolor.setText("清空背景");
		button_cleanbgcolor.setToolTipText("不清空背景的话，可以合并检索内容");
		button_search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table == null) return;
				String value = searchText.getText().trim();
				if (value.length() == 0) {
					UtilsSWTMessageBox.Alert(shell, "请输入关键字!");
					searchText.setText("");
					searchText.setFocus();
					return;
				}
				renderingResult(value);
			}
		});
	}
	static final Color ColorOld=SWTResourceManager.getColor(SWT.COLOR_WHITE);
	static final Color ColorSearchLine=SWTResourceManager.getColor(SWT.COLOR_GREEN);
	static final Color ColorSearchUnit=SWTResourceManager.getColor(SWT.COLOR_RED);
	/**
	 * 渲染结果
	 */
	void renderingResult(String key) {
		int index = combo.getSelectionIndex();
		TableItem first=null;
		//UtilsSWTTable.CleanTableBGColorAll(table, ColorOld, 2);
		if(button_cleanbgcolor.getSelection()) {
			//UtilsSWTTable.CleanTableBGColorAll(table, ColorOld, 2+4);
		}
		List<List<Integer>> list=getTableSearch(table,key,index);
		for(int i=0,len=list.size();i<len;i++) {
			List<Integer> li=list.get(i);
			if(li.size()>0) {
				TableItem  e=table.getItem(i);
				if(first==null)first=e;
				e.setBackground(ColorSearchLine);
				for(int ii=0;ii<li.size();ii++) {
					e.setBackground(li.get(ii), ColorSearchUnit);
				}
			}
		}
		if(first!=null) {
			
		}
		
	}
	/**
	 * 得到检索出来的列的列表
	 * @param table ResultTable
	 * @param key String
	 * @param colArrs int[]
	 * @return List<List<Integer>>
	 */
	static final List<List<Integer>> getTableSearch(ResultTable table, String key, int... colArrs) {
		List<List<Integer>> list=new ArrayList<>();
		TableItem[] items = table.getItems();
		for (int i = 0, len = items.length; i < len; i++) {
			list.add(getTableSearch(items[i],key,colArrs));
		}
		return list;
	}

	/**
	 * 判断行中各列是否含有此关键字的列，并得到列号(0开始)
	 * @param e TableItem
	 * @param key String
	 * @param colArrs int[]
	 * @return List<Integer>
	 */
	static final List<Integer> getTableSearch(TableItem e, String key, int... colArrs) {
		if (e == null || key == null) return new ArrayList<>(0);
		int colSort = e.getParent().getColumnCount();
		List<Integer> list = new ArrayList<>(colSort);
		boolean isSearchAll = isSearchAll(colArrs);
		for (int i = 0; i < colSort; i++) {
			if (isSearchAll || isExist(i, colArrs)) {
				if (e.getText(i).indexOf(key) > -1) list.add(i);
			}
		}
		return list;
	}
	/**
	 * 判断下标是否有此下标数组中
	 * @param index int
	 * @param colArrs int[]
	 * @return boolean
	 */
	static boolean isExist(int index, int... colArrs) {
		for (int i = 0, len = colArrs.length; i < len; i++)
			if (index == colArrs[i]-1) return true;
		return false;
	}

	/**
	 * 判断是否为全部检索，如果里面含有编号为0的，则为全部检索，否则为多列检索
	 * @param colArrs int[]
	 * @return boolean
	 */
	static final boolean isSearchAll(int... colArrs) {
		for (int i = 0, len = colArrs.length; i < len; i++)
			if (colArrs[i] == 0) return true;
		return false;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		UtilsDialogState.changeDialogCenter(parent, shell);

	}
}
