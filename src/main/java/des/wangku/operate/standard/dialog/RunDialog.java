package des.wangku.operate.standard.dialog;

import org.eclipse.swt.widgets.Canvas;

//import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.task.InterfaceRunDialog;
import des.wangku.operate.standard.utls.UtilsDate;
import des.wangku.operate.standard.utls.UtilsDialogState;
import des.wangku.operate.standard.utls.UtilsSWTTools;

import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * 多线程运行时弹出的窗口
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class RunDialog extends Dialog {
	/** 日志 */
	static Logger logger = Logger.getLogger(RunDialog.class);

	protected Object result;
	Shell parent;
	protected Shell shell;
	Canvas canvas = null;
	Label jdlabel = null;
	Label mclabel = null;
	final Display display = Display.getDefault();
	ProgressBar progressBar = null;
	/** 转圈图片的间隔 */
	private static final long millis = 500;
	/** 开始计时 */
	long timeFirst = System.currentTimeMillis();;
	Label label_Time = null;
	InterfaceRunDialog parentObj = null;
	/** 线程池 */
	ThreadPoolExecutor threadPool=null;
	/** 线程停止 */
	public volatile AtomicBoolean isBreak = new AtomicBoolean(false);

	/**
	 * 设置值
	 * @param maxNum int
	 * @param num int
	 * @param mc String
	 */
	public synchronized void setValue(int maxNum, int num, String mc) {
		if (isBreak.get()) return;
		progressBar.setMaximum(maxNum);
		progressBar.setSelection(num);
		jdlabel.setText("[" + num + "/" + maxNum + "]");
		mclabel.setText(mc);
		autoChange();
	}

	/**
	 * 自动加1
	 * @param mc String
	 */
	public synchronized void setValueAdd(String mc) {
		if (isBreak.get()) return;
		int num = progressBar.getSelection();
		int maxNum = progressBar.getMaximum();
		num++;
		progressBar.setSelection(num);
		jdlabel.setText("[" + num + "/" + maxNum + "]");
		mclabel.setText(mc);
		mclabel.setToolTipText(mc);
		autoChange();
	}

	/**
	 * 自动改变
	 */
	synchronized void autoChange() {
		switch (progressBar.getSelection() % 5) {
		case 0:
			shell.setText("进行中.");
			break;
		case 1:
			shell.setText("进行中..");
			break;
		case 2:
			shell.setText("进行中...");
			break;
		case 3:
			shell.setText("进行中....");
			break;
		default:
			shell.setText("进行中.....");
		}
	}

	/**
	 * Create the dialog.
	 * @param parent Shell
	 * @param style int
	 * @param obj AbstractTask
	 * @param maxNum int
	 * @wbp.parser.constructor
	 */
	public RunDialog(Shell parent, int style, InterfaceRunDialog obj, int maxNum) {
		super(parent, style);
		this.parent = parent;
		this.shell = new Shell(parent);
		parentObj = obj;
		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setMaximum(maxNum);
		Initialization();
	}

	/**
	 * Create the dialog.
	 * @param parent Shell
	 * @param style int
	 * @param obj AbstractTask
	 */
	public RunDialog(Shell parent, int style, AbstractTask obj) {
		super(parent, style);
		this.parent = parent;
		this.shell = new Shell(parent);
		parentObj = obj;
		progressBar = new ProgressBar(shell, SWT.NONE);
		Initialization();
	}

	/**
	 * 初始化
	 */
	private void Initialization() {
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				event.doit = false;
			}
		});
		setText("SWT Dialog");
		canvas = new Canvas(shell, SWT.NONE);
		canvas.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				logger.debug("break Thread!!!!!!!!!");
				mclabel.setText("进程停止中....[线程依次退出]");
				if (parentObj != null) parentObj.setIsBreakChange(true);//isBreak.compareAndSet(false, true);
				isBreak.compareAndSet(false, true);
				logger.debug("break Thread!!!!!!!!!:" + isBreak.get());
			}
		});
		jdlabel = new Label(shell, SWT.NONE);
		jdlabel.setAlignment(SWT.RIGHT);
		mclabel = new Label(shell, SWT.NONE);

		Label label = new Label(shell, SWT.NONE);
		label.setBounds(10, 10, 30, 12);
		label.setText("用时:");

		label_Time = new Label(shell, SWT.NONE);
		label_Time.setAlignment(SWT.LEFT);
		label_Time.setBounds(46, 10, 183, 12);
		label_Time.setText("");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		timeFirst = System.currentTimeMillis();
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		shell.addDisposeListener(new DisposeListener () {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				long nowNow = System.currentTimeMillis();
				long betweentime = nowNow - timeFirst;
				String useTime = UtilsDate.formatDuring(betweentime);
				logger.debug("线程运行:线程数量["+getThreadSort()+"]\t单元数量:["+ (progressBar.getSelection() - progressBar.getMinimum()) +"]\t用时:"+useTime);
			}
			
		});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	/**
	 * 得到线程数量，总数
	 * @return int
	 */
	public int getThreadSort() {
		if(threadPool!=null) {
			return threadPool.getMaximumPoolSize();
		}
		return 1;
	}
	/** 自转图片线程 */
	private Thread canvasAutoThread;

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		//shell = new Shell(getParent(), getStyle());
		shell.setSize(242, 209);
		shell.setText("进行中......");
		UtilsDialogState.changeDialogCenter(parent, shell);

		canvas.setBounds(71, 40, 90, 101);

		progressBar.setBounds(10, 164, 148, 10);

		jdlabel.setBounds(164, 162, 65, 12);
		jdlabel.setText("初始化...");

		mclabel.setBounds(10, 146, 216, 12);
		canvasAutoThread = new Thread() {
			public void run() {
				while (!isBreak.get()) {
					try {
						sleep(millis);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					canvasAutoChange();
				}
			}
		};
		canvasAutoThread.start();

	}

	static final PaintListener getPaintListener(Image image) {
		PaintListener t = new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (image != null) e.gc.drawImage(image, 0, 0);
			}
		};
		return t;
	}

	public void closeThread() {
		isBreak.compareAndSet(false, true);
	}

	public final Shell getShell() {
		return shell;
	}

	public final void setShell(Shell shell) {
		this.shell = shell;
	}

	private static final PaintListener[] paintArray = { getPaintListener(SWTResourceManager.getImage(RunDialog.class, "/images/wk-Nimg01.png")), getPaintListener(SWTResourceManager.getImage(RunDialog.class, "/images/wk-Nimg02.png")),
			getPaintListener(SWTResourceManager.getImage(RunDialog.class, "/images/wk-Nimg03.png")), getPaintListener(SWTResourceManager.getImage(RunDialog.class, "/images/wk-Nimg04.png")) };

	/**
	 * canvas自动更换图片，进行动画描述
	 */
	synchronized void canvasAutoChange() {
		if (isBreak.get()) return;
		shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (isBreak.get()) return;
				UtilsSWTTools.changePaintListener(paintArray, canvas);
				long nowNow = System.currentTimeMillis();
				long betweentime = nowNow - timeFirst;
				String useTime = UtilsDate.formatDuring(betweentime);
				String allTime = "";
				if (progressBar.getSelection() > progressBar.getMinimum()) {
					long pertime = betweentime / (long) (progressBar.getSelection() - progressBar.getMinimum());
					long alltime = pertime * (long) (progressBar.getMaximum() - progressBar.getMinimum());
					allTime = "/" + UtilsDate.formatDuring(alltime);
				}
				label_Time.setText(useTime + allTime);//已定义的用于显示时钟的label
			}
		});
	}

	public final void setThreadPool(ThreadPoolExecutor threadPool) {
		this.threadPool = threadPool;
	}
	
}
