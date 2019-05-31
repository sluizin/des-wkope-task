package des.wangku.operate.standard.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.PV;
import des.wangku.operate.standard.database.DatabaseProperties;
import des.wangku.operate.standard.dialog.AbstractSearch;
import des.wangku.operate.standard.dialog.RunDialog;
import des.wangku.operate.standard.dialog.ThreadRun;
import des.wangku.operate.standard.swt.AbstractCTabFolder.ParaClass;
import des.wangku.operate.standard.swt.InterfaceMultiSave;
import des.wangku.operate.standard.swt.InterfaceMultiTreeExtend;
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
public abstract class AbstractTask extends Composite implements InterfaceProject, InterfaceRunDialog, InterfaceChanged, InterfaceCollect, InterfaceTablesDialog, InterfaceProperties, InterfaceVersionFile, InterfaceMultiSave, InterfaceJson,
		InterfaceMultiThreadOnRun, InterfaceCompositeMenu, InterfaceAnnoProjectTaskAnalysis, InterfaceMultiTreeExtend, InterfaceSubject {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AbstractTask.class);
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
	/** 父Shell */
	public Shell parentShell = null;
	/** 父容器 */
	public Composite parentComposite;
	/** 父视图 */
	public Display parentDisplay = null;
	/** 本父类对象 */
	protected AbstractTask abstractTask = this;
	/** 搜索时弹出的窗口 */
	AbstractSearch searchDialog = null;

	@Override
	public void setSearchDialog(AbstractSearch e) {
		searchDialog = e;
	}

	@Override
	public AbstractSearch getSearchDialog() {
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
	 * 得到项目名称<br>
	 * 可以使用@AnnoProjectTask注解中的 name 进行设置
	 * @return String
	 */
	public abstract String getProjectName();

	/**
	 * 得到项目名称，通过方法与注解
	 * @return String
	 */
	public String getProjectNameAll() {
		String projectName = getProjectName();
		if (projectName != null) return projectName;
		projectName = this.getAnnoName();
		if (projectName == null) return null;
		return projectName.trim();
	}

	/**
	 * 得到项目编号 如 输入:P02 则菜单为:[P02]XXXX<br>
	 * 如果在model中出现同名的前缀，则只保留其中一个。<br>
	 * 即：项目前缀唯一<br>
	 * 不区别大小写<br>
	 * 可以使用@AnnoProjectTask注解中的 identifier 进行设置
	 * @return String
	 */
	public abstract String getIdentifier();

	/**
	 * 通过方法还是注解得到PXX
	 * @return String
	 */
	public String getIdentifierAll() {
		String identifier = getIdentifier();
		if (identifier != null && identifier.trim().length() > 0) return getIdentifier().trim();
		identifier = getAnnoIdentifier();
		if (identifier == null) return null;
		return identifier.trim();
	}

	/**
	 * 得到pX，把前缀最小写
	 * @return String
	 */
	public String getIdentifierLowerCase() {
		String identifier = getIdentifierAll();
		if (identifier == null) return "";
		return identifier.toLowerCase();
	}

	/**
	 * 得到完整项目名称 [P02]XXXXXXXXXXXX<br>
	 * 为null，则不允许加载
	 * @return String
	 */
	public String getMenuText() {
		String name = getProjectNameAll();
		String identifier = getIdentifierAll();
		if (identifier != null && identifier.length() > 0) name = "[" + identifier + "]" + name;
		return name;
	}

	/**
	 * 通过注解得到是否过期
	 * @return boolean
	 */
	public boolean getExpireAll() {
		boolean expire = this.getAnnoExpire();
		return expire;
	}
	/**
	 * 通过注解得到是否在有效期之内
	 * @return boolean
	 */
	public boolean getEffective() {
		boolean effective=this.isEffective();
		return effective;
	}

	/**
	 * 通过注解得到组名<br>
	 * 默认为空
	 * @return String
	 */
	public String getGroupAll() {
		String group = this.getAnnoGroup();
		return group == null ? "" : group;
	}

	/**
	 * 得到项目输出绝对路径<br>
	 * c:/XXXXX/output/P10
	 * @return String
	 */
	public String getOutputPath() {
		String proFolder = getIdentifierAll();
		return PV.getOutpoutCatalog() + ((proFolder == null || proFolder.length() == 0) ? "" : "/" + proFolder);
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
	 */
	public AbstractTask(Composite parent, int style) {
		super(parent, style);
		Class<? extends AbstractTask> basicClass = this.getClass();
		init(parent, style, basicClass, MVver | MVmodQuit | MVsysQuit);
	}

	/**
	 * 构造函数
	 * @param parent 父容器
	 * @param style 状态
	 * @param basicClass 子类
	 */
	public AbstractTask(Composite parent, int style, Class<? extends AbstractTask> basicClass) {
		super(parent, style);
		init(parent, style, basicClass, MVver | MVmodQuit | MVsysQuit);
	}

	/**
	 * 构造函数
	 * @param parent 父容器
	 * @param style 状态
	 * @param basicClass 子类
	 * @param abstractMenuValue 显示鼠标右键的功能
	 */
	public AbstractTask(Composite parent, int style, Class<? extends AbstractTask> basicClass, int abstractMenuValue) {
		super(parent, style);
		init(parent, style, basicClass, abstractMenuValue);
	}

	/**
	 * 初始化
	 * @param parent 父容器
	 * @param style 状态
	 * @param basicClass 子类
	 * @param abstractMenuValue int 显示鼠标右键的功能
	 */
	private final void init(Composite parent, int style, Class<? extends AbstractTask> basicClass, int abstractMenuValue) {
		pc.setSaveFolder(this.getIdentifierAll()).init(this.getProProperties());
		this.basicClass = basicClass;
		this.abstractMenuValue = abstractMenuValue;
		parent.setSize(ACC_CpsWidth, ACC_CpsHeight);
		parentComposite = parent;
		parentDisplay = parent.getDisplay();
		parentShell = parent.getShell();
		setMenu(abstractMenu);
		initCompositeMenu();
		initMenu();
		if (getProjectNameAll() != null) this.setToolTipText(getProjectNameAll());
		if (parent != null) {
			Composite p = parent.getParent();
			if (p != null && getMenuText() != null) p.getShell().setText(getMenuText());
		}
		initListener();
	}

	public final ParaClass getPc() {
		return pc;
	}

	/** 主框架中的鼠标右键是否显示相关操作值 */
	protected int abstractMenuValue = 0;
	/** 主框架中的鼠标右键 */
	protected Menu abstractMenu = new Menu(this);
	/** 具体的模块的类 */
	protected Class<? extends AbstractTask> basicClass = null;

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

	/**
	 * 初始化<br>
	 * 添加监听器
	 */
	protected void initListener() {
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				logger.debug("DisposeListener 被调用 任务完成，进行回收资源！");
				disposeResources();
			}
		});
		addHelpListener(new HelpListener() {
			@Override
			public void helpRequested(HelpEvent e) {
				UtilsSWTListener.showVersion(basicClass, getVersionFileJarFullPath());
			}
		});
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

	/** 运行时暂时关闭，除关闭已经打开的单元外，可以额外控制(可以打开已经关闭的单元)，运行此方法前所有对象已关闭，可以额外打开 */
	//public abstract void multiThreadOnRun();

	/** 运行完成后打开有效性,除打开已经关闭的单元外。可以额外控制 */
	//public abstract void multiThreadOnRunEnd();

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

	/**
	 * 中断线程
	 */
	protected void ThreadMainWorkThreadBreak() {
		ThreadPool.shutdown();
		ThreadRunDialogClose();
	}

	/**
	 * 得到线程池的状态，是否关闭
	 * @return boolean
	 */
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
	 * @param max int
	 */
	protected void ThreadStart(int max) {
		parentDisplay.asyncExec(new Runnable() {
			public void run() {
				ThreadRunDialog = new RunDialog(parentComposite.getShell(), 0, abstractTask, max);
				ThreadRunDialog.setThreadPool(ThreadPool);
				ThreadRunDialog.open();
			}
		});
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
	 * 以线程池的形式运行 ExcelCTabFolder组件内部的信息列表 自定义线程数<br>
	 * 调取接口函数getECTFThreadRunUnitList
	 * @param spinner_maxthread Spinner
	 */
	protected void startECTFRunThread(Spinner spinner_maxthread) {
		List<InterfaceThreadRunUnit> workList = getECTFThreadRunUnitList();
		startECTFRunThread(spinner_maxthread, workList);
	}

	/**
	 * 以线程池的形式运行 ExcelCTabFolder组件内部的信息列表 自定义线程数
	 * @param spinner_maxthread Spinner
	 * @param workList List<InterfaceThreadRunUnit>
	 */
	protected void startECTFRunThread(Spinner spinner_maxthread, List<InterfaceThreadRunUnit> workList) {
		if (workList == null || workList.size() == 0) return;
		int selectNum = 1;
		if (spinner_maxthread != null) {
			selectNum = spinner_maxthread.getSelection();
			if (selectNum <= 0) selectNum = 2;
		}
		startECTFRunThread(selectNum, workList);
	}

	/**
	 * 以线程池的形式运行 ExcelCTabFolder组件内部的信息列表 list总数为线程数<br>
	 * 调取接口函数getECTFThreadRunUnitList
	 * @param base AbstractTask
	 */
	protected void startECTFRunThread() {
		List<InterfaceThreadRunUnit> workList = getECTFThreadRunUnitList();
		startECTFRunThread(workList);
	}

	/**
	 * 以线程池的形式运行 ExcelCTabFolder组件内部的信息列表 list总数为线程数
	 * @param workList List<InterfaceThreadRunUnit>
	 */
	protected void startECTFRunThread(List<InterfaceThreadRunUnit> workList) {
		if (workList == null || workList.size() == 0) return;
		int selectNum = workList.size();
		startECTFRunThread(selectNum, workList);
	}

	/**
	 * 以线程池的形式运行 ExcelCTabFolder组件内部的信息列表
	 * @param selectNum int
	 * @param workList List<InterfaceThreadRunUnit>
	 */
	protected void startECTFRunThread(int selectNum, List<InterfaceThreadRunUnit> workList) {
		if (workList == null) return;
		int size = workList.size();
		if (size == 0) return;
		if (selectNum <= 0) selectNum = 2;
		int threadNum = UtilsList.getMaxThreadPoolCount(size, selectNum);
		if (threadNum > InterfaceMultiThreadOnRun.ACC_MAXThreadCount) threadNum = InterfaceMultiThreadOnRun.ACC_MAXThreadCount;
		List<InterfaceThreadRunUnit> newList = new ArrayList<>(size);
		for (InterfaceThreadRunUnit e : workList)
			newList.add(e);
		startECTFRunThreadWork(newList, threadNum);
	}

	/**
	 * -
	 * 以线程池的形式运行工作 ExcelCTabFolder组件组件内部的信息列表
	 * @param workList List<InterfaceThreadRunUnit>
	 * @param maxThreadNum int
	 */
	private void startECTFRunThreadWork(List<InterfaceThreadRunUnit> workList, int maxThreadNum) {
		if (maxThreadNum <= 0) return;
		setIsBreakChange(false);
		allControlEnabledChange(false);
		/*
		 * parentControlThreadClose = UtilsSWTComposite.getCompositeChildrenEnable(base, true);
		 * for (int i = 0; i < parentControlThreadClose.length; i++)
		 * parentControlThreadClose[i].setEnabled(false);
		 */
		multiThreadOnRun();
		ThreadStart(workList.size());
		//ThreadPool = Executors.newFixedThreadPool(maxThreadNum);
		ThreadPool = new ThreadPoolExecutor(maxThreadNum, maxThreadNum, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.CallerRunsPolicy());
		ThreadPool.allowCoreThreadTimeOut(true);

		List<List<InterfaceThreadRunUnit>> list2 = UtilsList.averageAssign(workList, maxThreadNum);
		for (int i = 0; i < maxThreadNum; i++) {
			Runnable task = new ThreadRun(abstractTask, list2.get(i));
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

	/**
	 * 得到d:/XXX/XXX/model/des-wkope-task-P101<br>
	 * 后面加上扩展名
	 * @return String
	 */
	String getModelProjectFileLeft() {
		return UtilsPathFile.getModelJarBasicPath() + "/" + AbstractTask.ACC_PROHead + getIdentifierLowerCase();
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
		return AbstractTask.ACC_PROHead + getIdentifierAll().toLowerCase() + "." + fileExt;
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
		String filename = AbstractTask.ACC_PROHead + getIdentifierAll().toLowerCase() + "." + fileExt;
		filename = UtilsPathFile.getModelJarBasicPath() + "/" + filename;
		return filename;
	}

	/**
	 * 得到与model目录下的具体项目里的资源文件 如"d:/XXXXX/model/Px/XXX"
	 * @param filename String
	 * @return String
	 */
	public final String getSubSourceFile(String filename) {
		return UtilsPathFile.getModelJarBasicPath() + "/" + getIdentifierAll() + "/" + filename;
	}

	/** 设置线程运算量进行回收 */
	protected int skipGC = 0;

	public int getSkipGC() {
		return skipGC;
	}

}
