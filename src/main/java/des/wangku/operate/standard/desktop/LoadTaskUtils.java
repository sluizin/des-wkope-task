package des.wangku.operate.standard.desktop;

import static des.wangku.operate.standard.desktop.DesktopConst.ExtendTaskMap;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.swt.widgets.Composite;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import des.wangku.operate.standard.asm.MQClassVisitor;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsPathFile;

/**
 * 加载工程方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class LoadTaskUtils {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(LoadTaskUtils.class);

	/**
	 * 查找到最新的允许自动装载的工程
	 * @return TaskObjectClass
	 */
	public static final TaskObjectClass getNewestAutoLoadTask() {
		TaskObjectClass result = null;
		for (String key : ExtendTaskMap.keySet()) {
			TaskObjectClass t = ExtendTaskMap.get(key);
			if (!t.isAutoLoad()) continue;
			if (result == null) {
				result = t;
				continue;
			}
			if (t.getJarTimestamp() > result.getJarTimestamp()) result = t;
		}
		return result;
	}

	/**
	 * 过滤失效的任务,并保存到ExtendTaskMap中
	 * @param mini Composite
	 */
	public static final void filterTaskObject(Composite mini) {
		try {
			Map<String, TaskObjectClass> ExtendTaskMapOrder = new TreeMap<>();
			int id = DesktopConst.ACC_FistTaskID;
			for (String key : ExtendTaskMap.keySet()) {
				TaskObjectClass ff = ExtendTaskMap.get(key);
				Constructor<?> c1 = ff.getMyClass().getDeclaredConstructor(new Class[] { Composite.class });
				AbstractTask a1 = (AbstractTask) c1.newInstance(new Object[] { mini });
				String menuText=a1.getMenuText();
				if (menuText== null || menuText.length() == 0) continue;
				if (ExtendTaskMapOrder.containsKey(menuText)) {
					TaskObjectClass old=ExtendTaskMapOrder.get(menuText);
					if(ff.jarTimestamp<=old.jarTimestamp)continue;// 发现同项目前缀，并且更新时间为旧时，则过滤
				}
				ff.task = a1;
				ff.identifier = a1.getIdentifierAll();
				ff.name = a1.getProjectNameAll();
				ff.menuText = menuText;
				ff.expire = a1.getExpireAll();
				ff.group = a1.getGroupAll();
				ff.effective = a1.getEffective();
				ff.autoLoad = ff.getAnnoAutoLoad();
				ff.id = id++;
				ExtendTaskMapOrder.put(menuText, ff);
			}
			ExtendTaskMap.clear();
			ExtendTaskMap.putAll(ExtendTaskMapOrder);

		} catch (Exception excep) {
			excep.printStackTrace();
		}
	}


	/**
	 * 得到某个目录里所有的jar文件
	 * @param path String
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getJarList(String path) {
		List<String> jarList = new ArrayList<String>();
		UtilsPathFile.getJarList(jarList, path);
		return jarList;
	}

	/**
	 * 得到model目录中所有的jar包
	 * @return List&lt;String&gt;
	 */
	public static final List<String> getModelJarList() {
		String path = UtilsPathFile.getJarBasicPathmodel();
		File file = new File(path);
		if (!file.exists()) file.mkdirs();
		List<String> jarList = getJarList(path);
		for (int i = 0; i < jarList.size(); i++) {
			String jarPath = jarList.get(i);
			LoadTaskUtils.isSearchJar(jarPath);
		}
		return jarList;
	}

	/**
	 * @param jarPath String
	 * @return boolean
	 */
	@SuppressWarnings("resource")
	public static final boolean isSearchJar(String jarPath) {
		try {
			JarFile jarFile = new JarFile(jarPath);
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				if (jarEntry.isDirectory() || (!jarEntry.getName().endsWith(".class"))) continue;
				InputStream input = jarFile.getInputStream(jarEntry);
				ClassReader cr = new ClassReader(input);
				MQClassVisitor mqcv = new MQClassVisitor(Opcodes.ASM6);
				cr.accept(mqcv, 0);
				cr = null;
				Map<String, Object> map = mqcv.getMap();
				if (mqcv.getClassFile() == null) continue;
				if (!mqcv.isStructure1()) {
					logger.debug("[" + jarEntry.getName() + "]缺少构造函数 参数:(Lorg/eclipse/swt/widgets/Composite;)");
					continue;
				}
				if (!mqcv.isStructure2()) {
					logger.debug("[" + jarEntry.getName() + "]缺少构造函数 参数:(Lorg/eclipse/swt/widgets/Composite;I)");
					continue;
				}
				long jarTimestamp = jarEntry.getTime();
				//logger.debug("jarEntry.getName>>:" + jarEntry.getName());
				//logger.debug("jarPath>>:" + jarPath);
				URL url1 = new URL("file:" + jarPath);
				URLClassLoader myClassLoader = new URLClassLoader(new URL[] { url1 }, Thread.currentThread().getContextClassLoader());
				Class<?> myClass1 = myClassLoader.loadClass(mqcv.getClassFile());

				TaskObjectClass ff = new TaskObjectClass();
				ff.classFile = mqcv.getClassFile();
				ff.map = map;
				ff.myClass = myClass1;
				ff.name = ff.getAnnoName();
				ff.expire = ff.getAnnoExpire();
				ff.autoLoad = ff.getAnnoAutoLoad();
				ff.jarTimestamp = jarTimestamp;
				ff.group = ff.getAnnoGroup();
				ExtendTaskMap.put(mqcv.getClassFile(), ff);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ExtendTaskMap.isEmpty()) return false;
		return true;
	}

	/**
	 * 按classFile，查找总任务池。是否存在此任务
	 * @param classFile String
	 * @return AbstractTask
	 */
	public static final AbstractTask getSearchAbstractTask(String classFile) {
		TaskObjectClass e = getSearchTaskObject(classFile);
		if (e == null) return null;
		return e.task;
	}

	/**
	 * 按classFile，查找总任务池。是否存在此任务
	 * @param classFile String
	 * @return TaskObjectClass
	 */
	public static final TaskObjectClass getSearchTaskObject(String classFile) {
		for (String key : ExtendTaskMap.keySet()) {
			TaskObjectClass ff = ExtendTaskMap.get(key);
			if (ff.classFile.equals(classFile)) return ff;
		}
		return null;
	}

	/**
	 * 从堆栈轨迹查看是哪个工作调用
	 * @return AbstractTask
	 */
	public static final AbstractTask getStackTracebstractTask() {
		Throwable throwable = new Throwable();
		StackTraceElement[] stacks = throwable.getStackTrace();
		for (StackTraceElement stack : stacks) {
			String classfile = stack.getClassName();
			AbstractTask e = getSearchAbstractTask(classfile);
			if (e != null) return e;
		}
		return null;
	}
}
