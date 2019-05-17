package des.wangku.operate.standard.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.utls.UtilsSWTMessageBox;

public class SearchResultTable extends AbstractSearch {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(SearchResultTable.class);
	/** 父容器 */
	private Shell parentShell;
	/** 子容器 */
	private Shell shell;

	private Text searchText = null;
	private Button button_search = null;
	private Button button_cleanbgcolor = null;
	private Combo combo = null;

	private ResultTable table = null;

	public SearchResultTable(Shell parent, int style) {
		super(parent, style);
		this.parentShell = parent;
		this.shell = new Shell(parent);
		shell.setText("检索关键字");
		shell.setSize(268, 123);

		searchText = new Text(shell, SWT.BORDER);
		searchText.setBounds(10, 10, 184, 18);
		combo = new Combo(shell, SWT.READ_ONLY);
		combo.setToolTipText("从某列中查找");
		combo.setBounds(10, 34, 86, 20);
		combo.add("所有列");
		combo.select(0);

		button_cleanbgcolor = new Button(shell, SWT.CHECK);
		button_cleanbgcolor.setSelection(true);
		button_cleanbgcolor.setBounds(104, 38, 72, 16);
		button_cleanbgcolor.setText("清空背景");
		button_cleanbgcolor.setToolTipText("不清空背景的话，可以合并检索内容");

		button_search = new Button(shell, SWT.NONE);
		button_search.setBounds(200, 8, 52, 22);
		button_search.setText("查找");
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
	/**
	 * 把ResultTable添加到combo的列表中
	 * @param table ResultTable
	 * @return SearchResultTable
	 */
	public final SearchResultTable putCombo(ResultTable table) {
		if (table == null) return this;
		this.table = table;
		TableColumn[] arrs = table.getColumns();
		for (int i = 0, len = arrs.length; i < len; i++) {
			TableColumn ee = arrs[i];
			combo.add(ee.getText());
		}
		return this;
	}

	/**
	 * Open the dialog.
	 * @return Object
	 */
	public Object open() {
		return super.open(parentShell, shell);
	}

	private static final Color ColorSearchLine = SWTResourceManager.getColor(SWT.COLOR_GREEN);
	private static final Color ColorSearchUnit = SWTResourceManager.getColor(SWT.COLOR_RED);


	/**
	 *  渲染结果
	 * @param key String
	 */
	private void renderingResult(String key) {
		int index = combo.getSelectionIndex();
		TableItem first = null;
		List<List<Integer>> list = getTableSearch(table, key, index);
		for (int i = 0, len = list.size(); i < len; i++) {
			List<Integer> li = list.get(i);
			if (li.size() == 0) continue;
			TableItem e = table.getItem(i);
			if (first == null) first = e;
			e.setBackground(ColorSearchLine);
			for (int ii = 0; ii < li.size(); ii++) {
				e.setBackground(li.get(ii), ColorSearchUnit);
			}
		}
	}

	/**
	 * 得到检索出来的列的列表
	 * @param table ResultTable
	 * @param key String
	 * @param colArrs int[]
	 * @return List<List<Integer>>
	 */
	private static final List<List<Integer>> getTableSearch(ResultTable table, String key, int... colArrs) {
		List<List<Integer>> list = new ArrayList<>();
		TableItem[] items = table.getItems();
		for (int i = 0, len = items.length; i < len; i++) {
			list.add(getTableSearch(items[i], key, colArrs));
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
	private static final List<Integer> getTableSearch(TableItem e, String key, int... colArrs) {
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
	private static boolean isExist(int index, int... colArrs) {
		for (int i = 0, len = colArrs.length; i < len; i++)
			if (index == colArrs[i] - 1) return true;
		return false;
	}

	/**
	 * 判断是否为全部检索，如果里面含有编号为0的，则为全部检索，否则为多列检索
	 * @param colArrs int[]
	 * @return boolean
	 */
	private static final boolean isSearchAll(int... colArrs) {
		for (int i = 0, len = colArrs.length; i < len; i++)
			if (colArrs[i] == 0) return true;
		return false;
	}

	@Override
	public Object getReturn() {
		return null;
	}

	@Override
	public void setReturn() {

	}

}
