package des.wangku.operate.standard.desktop;

import static des.wangku.operate.standard.desktop.DesktopConst.ExtendTaskMap;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsPathFile;

/**
 * 桌面调动时需要的一些方法
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class DesktopUtils {
	/**
	 * 判断是否有任务ID
	 * @param taskID int
	 * @return boolean
	 */
	public static final boolean isExist(int taskID) {
		for (String key : ExtendTaskMap.keySet()) {
			TaskObjectClass ff = ExtendTaskMap.get(key);
			if (ff.getId() == taskID) return true;
		}
		return false;
	}

	/**
	 * 退出系统<br>
	 * 项目退出时，要从堆栈轨迹查看是哪个工作调用，再因收资源
	 */
	public static final void existProject() {
		AbstractTask task = LoadTaskUtils.getStackTracebstractTask();
		existTask(task);
		System.exit(0);
	}

	/**
	 * 项目退出时，因收任务资源
	 * @param task AbstractTask
	 */
	public static final void existTask(AbstractTask task) {
		if (task == null) return;
		System.out.println("Quit Task :" + task.getProjectNameAll());
		task.disposeAll();
	}

	/**
	 * 退出系统<br>
	 * 从某个容器中提取任务，并回收任务资源
	 * @param main Composite
	 */
	public static final void existProject(Composite main) {
		Control[] arrs = main.getChildren();
		for (Control f : arrs) {
			if (f instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) f;
				existTask(task);
			}
		}
		main.dispose();
		System.exit(0);
	}

	/**
	 * 得到桌面包中图片 /images/
	 * @param filename String
	 * @return Image
	 */
	public static final Image getImages(String filename) {
		if (filename == null) return null;
		return SWTResourceManager.getImage(DesktopConst.classzz, DesktopConst.ACC_Images + "/" + filename);
	}
	/**
	 * 得到桌面包中图标图片 /images/icon/
	 * @param filename String
	 * @return Image
	 */
	public static final Image getImagesIcon(String filename) {
		if (filename == null) return null;
		return SWTResourceManager.getImage(DesktopConst.classzz, DesktopConst.ACC_ImagesIcon + "/" + filename);
	}

	/**
	 * 得到model绝对目录
	 * @return String
	 */
	public static final String getJarBasicPathmodel() {
		return UtilsPathFile.getJarBasicPathCatalog(DesktopConst.ACC_ModelCatalog, DesktopConst.DEVWorkSpaceModel);
	}
	/**
	 * 得到config绝对目录
	 * @return String
	 */
	public static final String getJarBasicPathconfig() {
		return UtilsPathFile.getJarBasicPathCatalog(DesktopConst.ACC_ConfigCatalog, DesktopConst.DEVWorkSpaceConfig);
	}
	/**
	 * 得到扩展库绝对目录
	 * @return String
	 */
	public static final String getJarBasicPathExtLibs() {
		return UtilsPathFile.getJarBasicPathCatalog(DesktopConst.ACC_ExtLibsCatalog, DesktopConst.DEVWorkSpaceExtLibs);
	}
}
