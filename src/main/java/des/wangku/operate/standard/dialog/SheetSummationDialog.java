package des.wangku.operate.standard.dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.swt.AbstractCTabFolder;
import des.wangku.operate.standard.swt.MultiTree;
import des.wangku.operate.standard.utls.UtilsDialogState;
import des.wangku.operate.standard.utls.UtilsSWTMessageBox;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
/**
 * 多个sheet累加调序的对话框
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class SheetSummationDialog extends Dialog {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(SheetSummationDialog.class);
	protected Shell sheet;
	AbstractCTabFolder parentExcel;
	String[] result= {};
	Group group = null;
	Tree parent=null;
	MultiTree tree = null;
	public SheetSummationDialog(Shell parent, int style, AbstractCTabFolder parentExce) {
		super(parent, style);
		this.parentExcel = parentExce;
		this.sheet = new Shell(parent);
		String[] arrs=parentExce.getCTableTitle();
		sheet.setText("选择要累加的表格");
		sheet.setSize(220, 244);
		UtilsDialogState.changeDialogCenter(parent, sheet);
		
		group = new Group(sheet, SWT.NONE);
		group.setLocation(0, 0);
		group.setSize(210,169);
		group.setText("多选与调序");
		group.setToolTipText("勾选即选中");
		
		tree = new MultiTree(group, 0,false);//(new MultiTree(group, 0,false)).formatID();
		tree.setBounds(10, 20, 10, 10);
		tree.singleAutoAddArrs(arrs);
		tree.setSize(190, 140);
		
		

		Button btnNewButton = new Button(sheet, SWT.NONE);
		btnNewButton.setBounds(62, 186, 93, 23);
		btnNewButton.setText("确认");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] titleArrs=tree.getArrayTitleChecked();
				if(titleArrs.length==0) {
					UtilsSWTMessageBox.Alert(sheet, "至少选择一项sheet页");
					return;
				}
				result=titleArrs;
				sheet.dispose();
			}
		});
	}
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String[] open() {
		sheet.open();
		sheet.layout();
		Display display = getParent().getDisplay();
		while (!sheet.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
}
