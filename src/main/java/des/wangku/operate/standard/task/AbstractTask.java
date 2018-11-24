package des.wangku.operate.standard.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;

import des.wangku.operate.standard.PV;
import des.wangku.operate.standard.database.DatabaseProperties;
import des.wangku.operate.standard.dialog.RunDialog;
import des.wangku.operate.standard.dialog.SearchDialog;
import des.wangku.operate.standard.dialog.ThreadRun;
import des.wangku.operate.standard.swt.AbstractCTabFolder.ParaClass;
import des.wangku.operate.standard.utls.UtilsShiftCompare;
import des.wangku.operate.standard.utls.UtilsFile;
import des.wangku.operate.standard.utls.UtilsJar;
import des.wangku.operate.standard.utls.UtilsList;
import des.wangku.operate.standard.utls.UtilsPathFile;
import des.wangku.operate.standard.utls.UtilsSWTComposite;
import des.wangku.operate.standard.utls.UtilsSWTListener;
import des.wangku.operate.standard.utls.UtilsSWTMenu;

/**
 * 容器任务主父类<br>
 * 使用线程池。具体的池化没有做。运行一次，建立一次线程池，同线程组
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public abstract class AbstractTask extends Composite implements InterfaceRunDialog, InterfaceChanged, InterfaceCollect, InterfaceTablesDialog, InterfaceProperties, InterfaceVersionFile {
	/** 日志 */
	static Logger logger = Logger.getLogger(AbstractTask.class);
	/**
	 * 项目文件头部如：<br>
	 * {des-wkope-task-}XXXX.accdb<br>
	 * {des-wkope-task-}XXXX.properties<br>
	 * {des-wkope-task-}XXXX.xlsx
	 */
	public static final String ACC_PROHead = "des-wkope-task-";
	/** 容器宽度 */
	public static final int ACC_CpsWidth = 900;
	/** 容器高度 */
	public static final int ACC_CpsHeight = 600;
	/** 父容器 */
	public Composite parentComposite;
	/** 父视图 */
	public Display parentDisplay = null;

	/** 搜索时弹出的窗口 */
	SearchDialog searchDialog = null;

	@Override
	public void setSearchDialog(SearchDialog e) {
		searchDialog = e;
	}

	@Override
	public SearchDialog getSearchDialog() {
		return searchDialog;

	}

	/** 双击某行，进行二次修改弹出的窗口 */
	AbstractTablesEditDialog editDialog = null;

	@Override
	public void setEditDialog(AbstractTablesEditDialog obj) {
		editDialog = obj;
	}

	@Override
	public AbstractTablesEditDialog getEditDialog() {
		return editDialog;
	}

	/**
	 * 得到菜单名称
	 * @return String
	 */
	public abstract String getMenuName();

	/**
	 * 得到菜单名称前缀 如 输入:P02 则菜单为:[P02]XXXX
	 * @return String
	 */
	public abstract String getMenuNameHead();
	/**
	 * 得到pX，把前缀最小写
	 * @return String
	 */
	public String getMenuNameHeadLowerCase() {
		if(getMenuNameHead()==null)return "";
		return getMenuNameHead().toLowerCase();
	}
	/**
	 * 得到完整项目名称 [P02]XXXXXXXXXXXX
	 * @return String
	 */
	public String getMenuText() {
		String menuName = getMenuName();
		String menuNameHead = getMenuNameHead();
		if (menuNameHead != null && menuNameHead.length() > 0) menuName = "[" + menuNameHead + "]" + menuName;
		return menuName;
	}

	/**
	 * 得到项目输出绝对路径<br>
	 * c:/XXXXX/output/P10
	 * @return String
	 */
	public String getOutputPath() {
		String proFolder = getMenuNameHead();
		return PV.getJarBasicPath() + "/" + PV.ACC_OutputCatalog + ((proFolder == null || proFolder.length() == 0) ? "" : "/" + proFolder);
	}

	/**
	 * 空的构造函数。如果调用此构造函数，则不初始化内部方法，只生成本Bean
	 * @param parent Composite
	 */
	public AbstractTask(Composite parent) {
		super(parent, 0);
	}

	/** ExcelCTabFolder参数 */
	protected final ParaClass pc = new ParaClass();

	/**
	 * 构造函数
	 * @param parent 父容器
	 * @param style 状态
	 * @param basicClass 子类
	 * @param abstractMenuValue 显示鼠标右键的功能
	 */
	public AbstractTask(Composite parent, int style, Class<? extends AbstractTask> basicClass, int abstractMenuValue) {
		super(parent, style);
		pc.setSaveFolder(this.getMenuNameHead()).init(this.getProProperties());
		this.basicClass = basicClass;
		this.abstractMenuValue = abstractMenuValue;
		parent.setSize(ACC_CpsWidth, ACC_CpsHeight);
		parentComposite = parent;
		parentDisplay = parent.getDisplay();
		setMenu(abstractMenu);
		initCompositeMenu();
		initMenu();
		this.setToolTipText(getMenuName());
		if (parent != null) {
			Composite p = parent.getParent();
			if (p != null) {
				p.getShell().setText(getMenuText());
			}
		}
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				logger.debug("DisposeListener 被调用 任务完成，进行回收资源！");
				disposeResources();
			}
		});
	}

	public final ParaClass getPc() {
		return pc;
	}
	/**
	 * 任务关闭时，关闭相应的资源信息
	 */
	public abstract void disposeResources();
	/**
	 * 让主工作线程停止
	 */
	public abstract void mainWorkThreadBreak();

	/**
	 * 得到线程状态 所有线程线束为true
	 * @return boolean
	 */
	public abstract boolean getMainWorkThreadState();

	/** 主框架中的鼠标右键是否显示相关操作值 */
	protected int abstractMenuValue = 0;
	/** 主框架中的鼠标右键 */
	protected Menu abstractMenu = new Menu(this);
	Class<? extends AbstractTask> basicClass = null;

	/**
	 * 得到版本文件在jar包的位置 如:"/update.info"
	 * @return String
	 *         public abstract String getVersionFileJarFullPath();
	 */

	/**
	 * 针对子窗口进行设置右键菜单<br>
	 * 父菜单为 abstractMenu<br>
	 * 这是菜单自定义前面菜单项，后面设置抽象类的定义的通用功能菜单
	 */
	public abstract void initCompositeMenu();

	/**
	 * 初始化
	 * 1:添加鼠标右键菜单，属于通用属性
	 */
	protected void initMenu() {
		if (!UtilsShiftCompare.isCompare(abstractMenuValue, MVver, MVsysQuit, MVmodQuit)) return;
		new MenuItem(abstractMenu, SWT.SEPARATOR);
		if (UtilsShiftCompare.isCompare(abstractMenuValue, MVver)) {
			MenuItem miversion = new MenuItem(abstractMenu, SWT.NONE);
			miversion.setText("版本说明");
			miversion.addListener(SWT.Selection, UtilsSWTListener.getListenerShowVersion(basicClass, getVersionFileJarFullPath()));
		}
		if (UtilsShiftCompare.isCompare(abstractMenuValue, MVmodQuit)) {
			UtilsSWTMenu.addMenuModelExist(parentComposite, abstractMenu);
		}
		if (UtilsShiftCompare.isCompare(abstractMenuValue, MVsysQuit)) {
			UtilsSWTMenu.addMenuSystemExist(abstractMenu);
		}
	}

	/** 显示版本 右键菜单 */
	public static final int MVver = 1;
	/** 显示模块退出 右键菜单 */
	public static final int MVmodQuit = 2;
	/** 显示系统退出 右键菜单 */
	public static final int MVsysQuit = 4;

	/** 运行时弹出的窗口 */
	public RunDialog ThreadRunDialog = null;
	/**
	 * 主窗体中的已经打开的单元，用于运行时暂时关闭单元，运行完成后，自动打开，已关闭的单元不在此列
	 */
	protected Control[] parentControlThreadClose = {};

	/** 运行时暂时关闭，除关闭已经打开的单元外，可以额外控制(可以打开已经关闭的单元) */
	public abstract void multiThreadOnRun();

	/** 运行完成后打开有效性,除打开已经关闭的单元外。可以额外控制 */
	public abstract void multiThreadOnRunEnd();

	/** 线程池 */
	//protected ThreadPoolExecutor ThreadPool =Executors.newFixedThreadPool(5);// Executors.newFixedThreadPool(10);
	protected ThreadPoolExecutor ThreadPool = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.CallerRunsPolicy());

	/**
	 * 关闭运行对话框
	 */
	protected void ThreadRunDialogClose() {
		parentDisplay.asyncExec(new Runnable() {
			@Override
			public void run() {
				ThreadRunDialog.closeThread();
				ThreadRunDialog.getShell().dispose();
				for (int i = 0; i < parentControlThreadClose.length; i++)
					parentControlThreadClose[i].setEnabled(true);
				multiThreadOnRunEnd();
				collect();
			}
		});
	}

	protected void ThreadMainWorkThreadBreak() {
		ThreadPool.shutdown();
		ThreadRunDialogClose();
	}

	protected boolean getThreadMainWorkThreadState() {
		return ThreadPool.isTerminated();
	}

	/**
	 * 检测线程组状态线程，在运行线程组后启动此线程，
	 * 用于判断此线程组是否已经运行完成，如果运行完成
	 * 则关闭运行对话框
	 * @return Thread
	 */
	protected Thread getCommonThreadCheckGroup() {
		Thread threadCheckGroup = new Thread() {
			public void run() {
				while (!getThreadMainWorkThreadState()) {
					try {
						sleep(800);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				ThreadRunDialogClose();
			}
		};
		return threadCheckGroup;
	}

	/**
	 * 弹出运行线程窗口
	 * @param obj InterfaceRunDialog
	 * @param max int
	 */
	protected void ThreadStart(InterfaceRunDialog obj, int max) {
		parentDisplay.asyncExec(new Runnable() {
			public void run() {
				ThreadRunDialog = new RunDialog(parentComposite.getShell(), 0, obj, max);
				ThreadRunDialog.setThreadPool(ThreadPool);
				ThreadRunDialog.open();
			}
		});
	}

	/**
	 * 以线程池的形式运行 ExcelCTabFolder组件内部的信息列表 自定义线程数
	 * @param base AbstractTask
	 * @param spinner_maxthread Spinner
	 * @param workList List<InterfaceThreadRunUnit>
	 */
	protected void startECTFRunThread(AbstractTask base, Spinner spinner_maxthread, List<InterfaceThreadRunUnit> workList) {
		if (workList == null || workList.size() == 0) return;
		int selectNum = 1;
		if (spinner_maxthread != null) {
			selectNum = spinner_maxthread.getSelection();
			if (selectNum <= 0) selectNum = 2;
		}
		startECTFRunThread(base, selectNum, workList);
	}

	/**
	 * 以线程池的形式运行 ExcelCTabFolder组件内部的信息列表 list总数为线程数
	 * @param base AbstractTask
	 * @param workList List<InterfaceThreadRunUnit>
	 */
	protected void startECTFRunThread(AbstractTask base, List<InterfaceThreadRunUnit> workList) {
		if (workList == null || workList.size() == 0) return;
		int selectNum = workList.size();
		startECTFRunThread(base, selectNum, workList);
	}

	/**
	 * 以线程池的形式运行 ExcelCTabFolder组件内部的信息列表
	 * @param base AbstractTask
	 * @param selectNum int
	 * @param workList List<InterfaceThreadRunUnit>
	 */
	protected void startECTFRunThread(AbstractTask base, int selectNum, List<InterfaceThreadRunUnit> workList) {
		if (workList == null || workList.size() == 0) return;
		if (selectNum <= 0) selectNum = 2;
		int threadNum = UtilsList.getMaxThreadPoolCount(workList.size(), selectNum);
		List<InterfaceThreadRunUnit> newList = new ArrayList<>(workList.size());
		for (InterfaceThreadRunUnit ee : workList) {
			newList.add(ee);
		}
		startECTFRunThreadWork(base, newList, threadNum);
	}

	/**
	 * 以线程池的形式运行工作 ExcelCTabFolder组件组件内部的信息列表
	 * @param base AbstractTask
	 * @param workList List<InterfaceThreadRunUnit>
	 * @param maxThreadNum int
	 */
	private void startECTFRunThreadWork(AbstractTask base, List<InterfaceThreadRunUnit> workList, int maxThreadNum) {
		setIsBreakChange(false);
		allControlEnabledChange(false);
		/*
		parentControlThreadClose = UtilsSWTComposite.getCompositeChildrenEnable(base, true);
		for (int i = 0; i < parentControlThreadClose.length; i++)
			parentControlThreadClose[i].setEnabled(false);
		 */
		multiThreadOnRun();
		ThreadStart(base, workList.size());
		//ThreadPool = Executors.newFixedThreadPool(maxThreadNum);
		ThreadPool = new ThreadPoolExecutor(maxThreadNum, maxThreadNum, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.CallerRunsPolicy());
		ThreadPool.allowCoreThreadTimeOut(true);

		List<List<InterfaceThreadRunUnit>> list2 = UtilsList.averageAssign(workList, maxThreadNum);
		for (int i = 0; i < maxThreadNum; i++) {
			Runnable task = new ThreadRun(base, list2.get(i));
			ThreadPool.submit(task);
		}
		ThreadPool.shutdown();
		getCommonThreadCheckGroup().start();

	}
	/**
	 * 设置所有对象关闭或打开
	 * @param enabled boolean
	 */
	protected void allControlEnabledChange(boolean enabled) {
		parentControlThreadClose = UtilsSWTComposite.getCompositeChildrenEnable(this, !enabled);
		for (int i = 0; i < parentControlThreadClose.length; i++)
			parentControlThreadClose[i].setEnabled(enabled);
	}

	@Override
	public Properties getProProperties() {
		Properties properties = new Properties();
		try {
			String filename = UtilsPathFile.getModelJarBasicPath() + "/des-wkope-task-" + getMenuNameHead() + ".properties";
			File file = new File(filename);
			if (!file.exists()) return properties;
			if (!file.isFile()) return properties;
			InputStream is2 = new FileInputStream(file);
			properties.load(new InputStreamReader(is2, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * 从Taskjar中读取配置文件 用于保护配置信息，直接封装到exe文件中
	 * @param filename String
	 * @return Properties
	 */
	public static final Properties getProPropertiesTaskJar(String filename) {
		Properties properties = new Properties();
		try {
			InputStream is2 = UtilsJar.getJarInputStreamBase(DatabaseProperties.class, filename);
			properties.load(new InputStreamReader(is2, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * 得到此项目相关不同文件 des-wkope-task-XXXX.YYY
	 * @param fileExt String
	 * @return String
	 */
	public final String getNewModelFile(String fileExt) {
		return AbstractTask.ACC_PROHead + getMenuNameHead().toLowerCase() + "." + fileExt;
	}

	/**
	 * 得到本地jar包中resources目录里的文件
	 * @param filename String
	 * @return String
	 */
	protected final String getSourcesFileContent(String filename) {
		if (basicClass == null) return null;
		try {
			URL url = UtilsJar.getJarSourceURL(basicClass, filename);/* "/update.info" */
			if (url == null) return null;
			InputStream is = url.openStream();
			String content = UtilsFile.readFile(is).toString();
			return content;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
	/**
	 * 得到与model同目录的资源文件{des-wkope-task-}XXXX.xlsx
	 * @param fileExt String
	 * @return String
	 */
	public final String getBaseSourceFile(String fileExt) {
		String filename = AbstractTask.ACC_PROHead + getMenuNameHead().toLowerCase() + "."+fileExt;
		filename = UtilsPathFile.getModelJarBasicPath() + "/" + filename;
		return filename;
	}
	/**
	 * 得到与model目录下的具体项目里的资源文件 如"d:/XXXXX/model/Px/XXX"
	 * @param filename String
	 * @return String
	 */
	public final String getSubSourceFile(String filename) {
		filename = UtilsPathFile.getModelJarBasicPath() + "/"+getMenuNameHead()+"/" + filename;
		return filename;
	}

}
