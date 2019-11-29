package des.wangku.operate.standard.desktop;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.desktop.TaskObjectClass;

/**
 * 桌面程序中的常量池
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class DesktopConst {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(DesktopConst.class);
	
	/** 项目平台 原始标题名称 换项目时更改标题名称，模块退出，则显示此字符串 */
	public static final String ACC_ProjectTitleDefault="程序运行平台 SWT Application";
	
	/** 任务列表 即对上面的Map进行了过滤，过滤了一些非法无效的对象 */
	public static final Map<String, TaskObjectClass> ExtendTaskMap = new TreeMap<>();
	
	/** 每个任务一个id号，此为最小的id值 大于此值的都是任务 */
	public static final int ACC_FistTaskID = 1000;
	
	public static final String DEVWorkSpaceMainProject = "D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope";

	/** 配置目录 */
	public static final String ACC_ConfigCatalog = "config";
	/** 项目目录 */
	public static final String ACC_ModelCatalog = "model";
	/** 文件输出目录名 */
	public static final String ACC_OutputCatalog = "output";

	public static final String ACC_DesktopProperties = "desktop.properties";

	/** D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope/build/libs */
	public static final String DEVWorkSpaceLib = DEVWorkSpaceMainProject + "/build/libs";

	/** D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope/build/libs/model */
	public static final String DEVWorkSpaceModel = DEVWorkSpaceLib + "/" + ACC_ModelCatalog;

	/** D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope/build/libs/config */
	public static final String DEVWorkSpaceConfig = DEVWorkSpaceLib + "/" + ACC_ConfigCatalog;

	/** 桌面程序的资源配置信息 */
	public static final DesktopProperties DesktopPro = new DesktopProperties();
	/** images目录 */
	public static final String ACC_Images = "/images";
	/** images/icon目录 */
	public static final String ACC_ImagesIcon = ACC_Images+"/icon";

	public static final Class<?> classzz = DesktopConst.class;
	
	
	
	
	
	
	/** 主窗口的图标 */
	public static final Image ACC_Shell = DesktopUtils.getImagesIcon("favicon.ico");
	/** 任务菜单前的图标 */
	public static final Image ACC_M0task_taskImage = DesktopUtils.getImagesIcon("star.gif");
	/** 任务数组前的图标 */
	public static final Image ACC_M0task_taskGroupImage = DesktopUtils.getImagesIcon("key_list.gif");
	/** 主菜单中第一行中主任务 */
	public static final Image ACC_M0task = DesktopUtils.getImagesIcon("task.gif");
	/** 主菜单中第一行中设置 */
	public static final Image ACC_M0set = DesktopUtils.getImagesIcon("set.gif");
	/** 主菜单中第一行中版本 */
	public static final Image ACC_M0ver = DesktopUtils.getImagesIcon("ver.gif");
	/** 主菜单中第一行中退出 */
	public static final Image ACC_M0exit = DesktopUtils.getImagesIcon("exist.gif");

	
	
	
	
	public static final String ACC_AuthorTitle = "Java桌面程序开发 综合平台";
	/*
	 * 从配置信息提取配置
	 */
	/** 系统自动装载 */
	public static final boolean isAutoLoad=DesktopConst.DesktopPro.getProPropBoolean("sys_autoload", true);
	public static final boolean isSysMSVwarning=DesktopConst.DesktopPro.getProPropBoolean("sys_menu_set_voice_warning", false);
	public static final boolean isSysMSVconfirm=DesktopConst.DesktopPro.getProPropBoolean("sys_menu_set_voice_confirm", false);
	public static final boolean isSysMSVthread=DesktopConst.DesktopPro.getProPropBoolean("sys_menu_set_voice_thread", false);

	public static final boolean isSysMSRInput=DesktopConst.DesktopPro.getProPropBoolean("sys_menu_set_rem_input", false);
	/** 是否记忆控件值 */
	public static boolean Remember_Input = false;
}
