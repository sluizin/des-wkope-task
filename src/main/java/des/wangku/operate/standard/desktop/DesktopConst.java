package des.wangku.operate.standard.desktop;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.desktop.TaskObjectClass;
/**
 * 桌面程序中的常量池
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class DesktopConst {
	/* 日志 */
	static Logger logger = LoggerFactory.getLogger(DesktopConst.class);
	/** 任务列表 即对上面的Map进行了过滤，过滤了一些非法无效的对象 */
	public static final Map<String, TaskObjectClass> ExtendTaskMap = new TreeMap<>();
	/** 每个任务一个id号，此为最小的id值 大于此值的都是任务*/
	public static final int ACC_FistTaskID = 1000;
	public static final String DEVWorkSpaceMainProject = "D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope";

	/** 配置目录 */
	public static final String ACC_ConfigCatalog = "config";
	/** 项目目录 */
	public static final String ACC_ModelCatalog = "model";
	/** 文件输出目录名 */
	public static final String ACC_OutputCatalog = "output";

	public static final String ACC_DesktopProperties="desktop.properties";
	

	/** D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope/build/libs */
	public static final String DEVWorkSpaceLib = DEVWorkSpaceMainProject+"/build/libs";
	
	/** D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope/build/libs/model */
	public static final String DEVWorkSpaceModel = DEVWorkSpaceLib+"/"+ACC_ModelCatalog;
	
	/** D:/Eclipse/eclipse-oxygen/Workspaces/des-wkope/build/libs/config */
	public static final String DEVWorkSpaceConfig = DEVWorkSpaceLib+"/"+ACC_ConfigCatalog;
	/*
	 * 
	 */
	/** 桌面程序的资源配置信息 */
	public static final DesktopProperties dprop=new DesktopProperties();
}
