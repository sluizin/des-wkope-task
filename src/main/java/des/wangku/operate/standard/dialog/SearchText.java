package des.wangku.operate.standard.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import des.wangku.operate.standard.utls.UtilsSWTMessageBox;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class SearchText extends AbstractSearch {
	/** 父容器 */
	protected Shell parentShell;
	/** 子容器 */
	protected Shell shell;

	protected Text searchText = null;
	protected Button button_search = null;

	public SearchText(Shell parent, int style) {
		super(parent, style);
		this.parentShell = parent;
		this.shell = new Shell(parent);
		shell.setText("检索关键字");
		shell.setSize(268, 63);

		searchText = new Text(shell, SWT.BORDER);
		searchText.setBounds(10, 10, 184, 18);
		searchText.setFocus();
		searchText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					search();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});
		button_search = new Button(shell, SWT.NONE);
		button_search.setBounds(200, 8, 52, 22);
		button_search.setText("查找");
		button_search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search();
			}
		});
	}
	/**
	 * 设置shell的setText值
	 * @param text String
	 * @return SearchText
	 */
	public SearchText setTextHead(String text) {
		shell.setText(text);		
		return this;
	}
	/**
	 * 检索信息
	 */
	void search() {
		textValue = searchText.getText().trim();
		if (textValue.length() == 0) {
			UtilsSWTMessageBox.Alert(shell, "请输入关键字!");
			searchText.setText("");
			searchText.setFocus();
			return;
		}
		setReturn();
		shell.dispose();
	}

	/**
	 * Open the dialog.
	 * @return Object
	 */
	public Object open() {
		return super.open(parentShell, shell);
	}

	@Override
	public Object getReturn() {
		return returnObj;
	}

	@Override
	public void setReturn() {
		returnObj = searchText.getText().trim();
	}

}
