package des.wangku.operate.standard.utls;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.PV;
import des.wangku.operate.standard.dialog.InputValueDialog;
import des.wangku.operate.standard.dialog.SearchDialog;
import des.wangku.operate.standard.dialog.TableOutputExcelParaDialog;
import des.wangku.operate.standard.dialog.TableOutputExcelParaDialog.ExcelParaClass;
import des.wangku.operate.standard.swt.ExcelCTabFolder;
import des.wangku.operate.standard.swt.ResultTable;
import des.wangku.operate.standard.task.AbstractTablesEditDialog;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.task.InterfaceExcelChange;
import des.wangku.operate.standard.task.InterfaceTablesDialog;

/**
 * 针对Table的一些监听器
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsSWTTableListener {
	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(UtilsSWTTableListener.class);

	/**
	 * 添加一行信息
	 * @param table ResultTable
	 * @return SelectionListener
	 */
	public static final SelectionListener getListenerAddLine(ResultTable table) {
		SelectionListener t = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table.getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						int point = table.getSelectionIndex();
						int cols = table.getColumnCount();
						String[] arr = new String[cols];
						UtilsSWTTableSQL.insert(table, point, arr);
					}
				});
				table.redraw();
			}
		};
		return t;

	}

	/**
	 * 清空
	 * @param table ResultTable
	 * @return SelectionListener
	 */
	public static final SelectionListener getListenerCleanAll(ResultTable table) {
		SelectionListener t = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("CleanALL!!!");
				if (UtilsSWTMessageBox.Confirm(table.getShell(), "是否要清空数据?")) {
					logger.debug("清空数据\"CleanALL!!!");
					table.removeAll();
					UtilsSWTTable.collectTable(table);
				}
			}
		};
		return t;
	}

	/**
	 * 清空某列数据
	 * @param table ResultTable
	 * @return SelectionListener
	 */
	public static final SelectionListener getListenerAddColumn(ResultTable table) {
		SelectionListener t = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputValueDialog inputNameDialog = new InputValueDialog(table.getShell(), 0, "设置列名", "名称", "", false);
				Object obj = inputNameDialog.open();
				if (obj == null) return;
				String newValue = (String) obj;
				table.getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						TableColumn e = ResultTable.getDefaultTableColumn(table,SWT.LEFT, 150, newValue);
						ResultTable.setDefaultTableColumnPos(table, e);
					}
				});
			}
		};
		return t;
	}

	/**
	 * 清空某列数据
	 * @param table ResultTable
	 * @return SelectionListener
	 */
	public static final SelectionListener getListenerCleanColumn(ResultTable table) {
		SelectionListener t = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int column = table.getSelectColumn();
				logger.debug("CleanColALL!!!:" + column);
				if (!UtilsSWTMessageBox.Confirm(table.getShell(), "是否要清空整列?")) return;
				if (column == -1) {
					UtilsSWTMessageBox.Alert(table.getShell(), "请选择要清空的列(鼠标所在的列)");
					return;
				}
				table.getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						TableItem[] arrs = table.getItems();
						for (int p = 0; p < arrs.length; p++) {
							arrs[p].setText(column, "");
						}
						table.redraw();
					}
				});
				UtilsSWTTable.collectTable(table);
			}
		};
		return t;
	}

	/**
	 * 移除某列数据
	 * @param table ResultTable
	 * @return SelectionListener
	 */
	public static final SelectionListener getListenerRemoveColumn(ResultTable table) {
		SelectionListener t = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int column = table.getSelectColumn();
				logger.debug("RevmoveColALL!!!:" + column);
				if (!UtilsSWTMessageBox.Confirm(table.getShell(), "是否要移除整列?")) return;
				if (column == -1) {
					UtilsSWTMessageBox.Alert(table.getShell(), "请选择要移除的列(鼠标所在的列)");
					return;
				}
				table.getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						table.getColumn(column).dispose();
						table.redraw();
					}
				});
				UtilsSWTTable.collectTable(table);
			}
		};
		return t;
	}

	/**
	 * 选中所有当前某列值相同的值 注意：null和空，相同
	 * @param table SelectionListener
	 * @return SelectionListener
	 */
	public static final SelectionListener getListenerTableSameValueSelectionAll(ResultTable table) {
		SelectionListener t = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int line = table.getSelectLine();
				int column = table.getSelectColumn();
				if (!UtilsSWTMessageBox.Confirm(table.getShell(), "是否要选择某列相同值的行?")) return;
				if (line == -1 || column == -1) {
					UtilsSWTMessageBox.Alert(table.getShell(), "请选择要参考的列(鼠标所在的列)");
					return;
				}
				table.getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						String value = table.getString(line, column);
						for (TableItem e : table.getItems()) {
							String v = e.getText(column);
							if ((value == null || value.length() == 0) && (v == null || v.length() == 0)) e.setChecked(true);
							if (value != null && value.equals(v)) {
								e.setChecked(true);
							}
						}
					}
				});
				UtilsSWTTable.collectTable(table);
			}
		};
		return t;
	}

	/**
	 * 选中所有含有当前某列值相同的值 注意：null和空，相同
	 * @param table SelectionListener
	 * @return SelectionListener
	 */
	public static final SelectionListener getListenerTableLikeValueSelectionAll(ResultTable table) {
		SelectionListener t = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int line = table.getSelectLine();
				int column = table.getSelectColumn();
				if (!UtilsSWTMessageBox.Confirm(table.getShell(), "是否要选择含有某列相同值的行?")) return;
				if (line == -1 || column == -1) {
					UtilsSWTMessageBox.Alert(table.getShell(), "请选择要参考的列(鼠标所在的列)");
					return;
				}
				table.getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						String value = table.getString(line, column);
						for (TableItem e : table.getItems()) {
							String v = e.getText(column);
							if ((value == null || value.length() == 0) && (v == null || v.length() == 0)) e.setChecked(true);
							if (v != null && v.indexOf(value) > -1) e.setChecked(true);
						}
					}
				});
				UtilsSWTTable.collectTable(table);
			}
		};
		return t;
	}

	/**
	 * 复制行至粘贴板 多行格式
	 * @param table ResultTable
	 * @return Listener
	 */
	public static final Listener getListenerCopyLine(ResultTable table) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				logger.debug("copyMultiline!!!");
				if (!UtilsSWTTableUtils.copyMultiLineToClipboard(table)) return;
				UtilsSWTMessageBox.Alert(table.getShell(), "导入多行格式到粘贴板完成");
			}
		};
		return t;
	}

	/**
	 * 复制行至粘贴板 Json格式
	 * @param table Table
	 * @return Listener
	 */
	public static final Listener getListenerCopyJson(ResultTable table) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				logger.debug("copyJson!!!");
				TableItem[] arr = UtilsSWTTable.getSelectCheckTableItemAllArray(table);
				if (arr.length == 0) return;
				List<List<String>> list = UtilsSWTTable.getTableItemList(false, table, arr);
				String itemString = JSON.toJSONString(list);
				UtilsClipboard.copy(itemString);
				UtilsSWTMessageBox.Alert(table.getShell(), "导入Json格式到粘贴板完成");
			}
		};
		return t;
	}

	/**
	 * 选中某行
	 * @param table ResultTable
	 * @return Listener
	 */
	public static final Listener getListenerSelectedLine(ResultTable table) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				UtilsSWTTable.setSelectedLine(table.getDisplay(), table);
			}
		};
		return t;
	}

	/**
	 * Table全选 / 全取消
	 * @param table ResultTable
	 * @param check boolean
	 * @return SelectionListener
	 */
	public static final SelectionListener getListenerTableSelectAll(ResultTable table, boolean check) {
		SelectionListener t = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table == null) return;
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem ee = table.getItem(i);
					ee.setChecked(check);
				}
			}
		};
		return t;
	}

	/**
	 * 反选
	 * @param table ResultTable
	 * @return SelectionListener
	 */
	public static final SelectionListener getListenerTableRevSelectionAll(ResultTable table) {
		SelectionListener t = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table == null) return;
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem ee = table.getItem(i);
					ee.setChecked(!ee.getChecked());
				}
			}
		};
		return t;
	}

	/**
	 * 把选中的多行删除
	 * @param isConfirm boolean
	 * @param table ResultTable
	 * @return Listener
	 */
	public static final Listener getListenerRemoveSelectedLine(boolean isConfirm, ResultTable table) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				logger.debug("RemoveSelectedLine!!!");
				UtilsSWTTable.removeSelectedLine(table.getDisplay(), table.getShell(), isConfirm, table);
			}
		};
		return t;
	}

	/**
	 * 把table中选中的记录保存到excel中
	 * @param table Table
	 * @param isCheck boolean
	 * @return Listener
	 */
	public static final Listener getListenerCopyToExcel(ResultTable table, boolean isCheck) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				TableItem[] arr = {};
				if (isCheck) arr = UtilsSWTTable.getSelectCheckTableItemAllArray(table);
				else arr = table.getItems();
				if (arr.length == 0) return;
				boolean isHead = false;
				ExcelParaClass epc = null;
				if (table.getHeaderVisible()) {
					TableOutputExcelParaDialog ale = new TableOutputExcelParaDialog(table.getShell(), 0, arr);
					Object obj = ale.open();
					if (obj == null) return;
					epc = (ExcelParaClass) obj;
					isHead = epc.isHead();
				}
				List<List<String>> list = UtilsSWTTable.getTableItemList(isHead, table, arr);
				String path = PV.getJarBasicPath() + "/" + PV.ACC_OutputCatalog;
				String filename = UtilsRnd.getNewFilenameNow(4, 1) + ".xlsx";
				File file = new File(path);
				if (!file.exists()) file.mkdirs();
				String excelFilename = path + "/" + filename;
				InterfaceExcelChange change = UtilsSWTTools.getParentInterfaceObj(table, InterfaceExcelChange.class);
				String sheetName = ExcelCTabFolder.getSheetName(table, "信息");
				boolean t = UtilsSWTPOI.makeExcel(excelFilename, sheetName, list, change, epc);
				logger.debug("copyToexcel:" + excelFilename);
				if (t) UtilsSWTMessageBox.Alert(table.getShell(), filename + "生成成功");
			}
		};
		return t;
	}

	/**
	 * 把选中的多行的某列放入粘贴板 Listener
	 * @param table ResultTable
	 * @param index int[]
	 * @return Listener
	 */
	public static final Listener getListenerSelectedLineCopy(ResultTable table, int... index) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				UtilsSWTTable.setSelectClipboardColumn(table.getDisplay(), table, index);
			}
		};
		return t;
	}

	/**
	 * 单击列头时进行排序
	 * @param table ResultTable
	 * @param index int
	 * @return Listener
	 */
	public static final Listener getListenerColumnDoubleClick(ResultTable table, int index) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				if (table == null) return;
				logger.debug("ColumnDoubleClick!!!:" + index);
				TableColumn column = table.getColumn(index);
				table.setSortColumn(column);
				boolean newisAscend = table.getIsAscend();
				table.setSortDirection((newisAscend ? SWT.UP : SWT.DOWN));
				UtilsSWTTable.StringItemsSorter(table, index, newisAscend);
				table.setIsAscend(!newisAscend);
			}
		};
		return t;
	}

	/**
	 * 双击table中的某行，弹出相对应该的窗体
	 * @param parent AbstractTask
	 * @param table ResultTable
	 * @param clazz AbstractTablesEditDialog
	 * @return Listener
	 */
	public static Listener getListenerTableOpenEdit(AbstractTask parent, ResultTable table, Class<? extends AbstractTablesEditDialog> clazz) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				logger.debug("getTableListenerOpenEdit!!!");
				TableItem item = table.getItem(new Point(e.x, e.y));
				if (item == null) return;
				if (parent.getEditDialog() != null) parent.getEditDialog().close();
				//parent.setEditDialog(obj);
				try {
					@SuppressWarnings("rawtypes")
					Constructor c1 = clazz.getDeclaredConstructor(new Class[] { Shell.class, int.class, TableItem.class });
					AbstractTablesEditDialog obj = (AbstractTablesEditDialog) c1.newInstance(new Object[] { parent.getShell(), 0, item });
					parent.setEditDialog(obj);
					parent.getEditDialog().open();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		};
		return t;
	}

	/**
	 * 给table表格加上快捷键
	 * @param table ResultTable
	 * @return KeyListener
	 */
	public static final KeyListener addKeyListenerTable(ResultTable table) {
		KeyListener t = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				/*
				 * if (e.stateMask == SWT.CTRL && (e.keyCode >= '0' && e.keyCode <= '9')) {
				 * // int c = e.keyCode - 48;
				 * UtilsSWTTable.setSelectClipboardColumn(display, table, e.keyCode - 48);
				 * }
				 */
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'f')) {
					InterfaceTablesDialog parent = UtilsSWTTools.getParentInterfaceObj(table, InterfaceTablesDialog.class);
					parent.setSearchDialog(new SearchDialog(table.getShell(), 0, table));
					parent.getSearchDialog().open();
				} /*
					 * if (e.keyCode == SWT.DEL) {
					 * UtilsSWTTable.removeSelectedLine(display, shell, true, table);
					 * }
					 */
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		};
		return t;
	}
}
