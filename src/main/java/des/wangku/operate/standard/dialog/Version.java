package des.wangku.operate.standard.dialog;

import java.io.InputStream;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import des.wangku.operate.standard.utls.UtilsDialogState;
import des.wangku.operate.standard.utls.UtilsFile;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;

/**
 * 显示版本信息包括新版本更新内容
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class Version extends Dialog {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(Version.class);
	Shell parent;
	protected Shell shell = null;
	private Text text;

	/**
	 * 构造函数
	 * @param parent Shell
	 * @param style int
	 */
	public Version(Shell parent, int style) {
		super(parent, style);
		this.parent = parent;
		shell = new Shell(parent);
		initialization();
	}
	/**
	 * 构造函数
	 * @param parent Shell
	 * @param style int
	 * @param is InputStream
	 */
	public Version(Shell parent, int style,InputStream is) {
		super(parent, style);
		this.parent = parent;
		shell = new Shell(parent);
		initialization();
		setTextContent(is);
	}
	/**
	 * 初始化
	 */
	void initialization() {
		shell.setText("制作：孙健");
		shell.setSize(442, 266);
		text = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text.setBounds(10, 10, 424, 219);
	}
	
	public void setTextContent(String filename) {
		String Content = UtilsFile.readFile(filename).toString();
		text.setText(Content);
	}

	public void setTextContent(InputStream is) {
		String Content = UtilsFile.readFile(is).toString();
		text.setText(Content);
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
