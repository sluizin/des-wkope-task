package des.wangku.operate.standard.dialog;

//import java.io.InputStream;
//import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.graphics.ImageData;
//import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

//import des.wangku.operate.standard.composite.ImageViewer;
//import des.wangku.operate.standard.database.DatabaseProperties;
import des.wangku.operate.standard.utls.UtilsDialogState;
//import des.wangku.operate.standard.utls.UtilsJar;

import org.eclipse.swt.widgets.Label;

/**
 * 系统初始化时需要的进度提示条
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class LoadingProgressBar extends Dialog implements Runnable, Callable<Boolean> {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(LoadingProgressBar.class);

	protected Object result;
	Shell parent;
	protected Shell shell;
	ProgressBar progressBar = null;
	Label label = null;

	public LoadingProgressBar(Shell parent, int style) {
		super(parent, style);
		this.parent = parent;
		this.shell = new Shell(parent);
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				event.doit = false;
			}
		});
		shell.setSize(264, 62);
		shell.setText("启动中...");
		UtilsDialogState.changeDialogCenter(parent, shell);

		label = new Label(shell, SWT.NONE);
		label.setBounds(22, 10, 118, 17);
		label.setText("模块加载中....稍等");
		/*
		String string="/images/loading.gif";
        ImageViewer ic  =   new  ImageViewer(shell); 
        ic.setBounds(180, 10, 200, 200);
        ImageLoader loader  =   new  ImageLoader();  
		InputStream is2=UtilsJar.getJarInputStreamBase(LoadingProgressBar.class, string);
		//InputStreamReader is=new InputStreamReader(is2, "UTF-8");
		 ImageData[] imageDatas  =loader.load(is2);
       // ImageData[] imageDatas  =  loader.load(string);  

        ic.setImage(imageDatas[ 0 ]);
        ic.pack();
        */
        
		shell.open();
		shell.layout();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	Object open() {
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	@Override
	public Boolean call() throws Exception {
		return null;
	}

	@Override
	public void run() {
	}

	public final Shell getShell() {
		return shell;
	}

	public final void setShell(Shell shell) {
		this.shell = shell;
	}
}
