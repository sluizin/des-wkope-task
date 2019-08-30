package des.wangku.operate.standard.desktop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import des.wangku.operate.standard.swtComposite.SWTSearch;
import des.wangku.operate.standard.task.AbstractTask;

/**
 * 扩展对象
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class TaskObjectClass {
	Map<String, Object> map = new HashMap<>();
	/** 菜单编号 */
	int id = 0;
	/** 主菜单，顶部 */
	Menu menu = null;
	/** 是否自动加载 */
	boolean autoLoad = false;
	/** jar包时间戳 */
	long jarTimestamp = 0l;
	String classFile = null;
	Class<?> myClass = null;
	AbstractTask task = null;
	String identifier = null;
	String name = null;
	String menuText = null;
	/** 是否过期 */
	boolean expire = false;
	/** 组名 */
	String group = "";
	/** 通过日期判断是否有效 */
	boolean effective = true;

	/**
	 * 得到项目是否自动装载
	 * @return boolean
	 */
	public final boolean getAnnoAutoLoad() {
		if (!map.containsKey(ACC_annoAutoLoad)) return false;
		Object obj = map.get(ACC_annoAutoLoad);
		return obj == null ? false : Boolean.parseBoolean(obj.toString());
	}

	/**
	 * 得到项目是否过期
	 * @return boolean
	 */
	public final boolean getAnnoExpire() {
		if (!map.containsKey(ACC_annoExpire)) return false;
		Object obj = map.get(ACC_annoExpire);
		return obj == null ? false : Boolean.parseBoolean(obj.toString());
	}

	/**
	 * 得到组名
	 * @return String
	 */
	public final String getAnnoGroup() {
		if (!map.containsKey(ACC_annoGroup)) return "";
		Object obj = map.get(ACC_annoGroup);
		return obj == null ? "" : obj.toString();
	}

	/**
	 * 得到项目编号
	 * @return String
	 */
	public final String getAnnoIdentifier() {
		if (!map.containsKey(ACC_annoIdentifier)) return null;
		Object obj = map.get(ACC_annoIdentifier);
		return obj == null ? null : obj.toString();
	}

	/**
	 * 得到名称
	 * @return String
	 */
	public final String getAnnoName() {
		if (!map.containsKey(ACC_annoName)) return null;
		Object obj = map.get(ACC_annoName);
		return obj == null ? null : obj.toString();
	}

	public final String getClassFile() {
		return classFile;
	}

	public final String getGroup() {
		return group;
	}

	public final int getId() {
		return id;
	}

	public final String getIdentifier() {
		return identifier;
	}

	public final long getJarTimestamp() {
		return jarTimestamp;
	}

	public final Map<String, Object> getMap() {
		return map;
	}

	/**
	 * 返回名称，如没设置名称，则返回null
	 * @return String
	 */
	public final String getMenuText() {
		if (menuText != null) return menuText;
		return getAnnoName();
	}

	public final Class<?> getMyClass() {
		return myClass;
	}

	public final String getName() {
		return name;
	}

	public final boolean isAutoLoad() {
		return autoLoad;
	}

	public final boolean isEffective() {
		return effective;
	}

	public final boolean isExpire() {
		return expire;
	}

	public final void setAutoLoad(boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	public final void setClassFile(String classFile) {
		this.classFile = classFile;
	}

	public final void setEffective(boolean effective) {
		this.effective = effective;
	}

	public final void setExpire(boolean expire) {
		this.expire = expire;
	}

	public final void setGroup(String group) {
		this.group = group;
	}

	public final void setId(int id) {
		this.id = id;
	}

	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public final void setJarTimestamp(long jarTimestamp) {
		this.jarTimestamp = jarTimestamp;
	}

	public final void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public final void setMenuText(String menuText) {
		this.menuText = menuText;
	}

	public final void setMyClass(Class<?> myClass) {
		this.myClass = myClass;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "TaskObjectClass [map=" + map + ", autoLoad=" + autoLoad + ", jarTimestamp=" + jarTimestamp + ", classFile=" + classFile + ", myClass=" + myClass + ", identifier=" + identifier + ", name=" + name + ", menuText=" + menuText
				+ ", expire=" + expire + ", group=" + group + ", effective=" + effective + "]";
	}

	public static final String ACC_annoExpire = "expire";
	public static final String ACC_annoAutoLoad = "autoLoad";
	public static final String ACC_annoIdentifier = "identifier";

	public static final String ACC_annoName = "name";

	public static final String ACC_annoGroup = "group";

	public static final String getAccAnnoautoload() {
		return ACC_annoAutoLoad;
	}

	public static final String getAccAnnoexpire() {
		return ACC_annoExpire;
	}

	public static final String getAccAnnogroup() {
		return ACC_annoGroup;
	}

	public static final String getAccAnnoidentifier() {
		return ACC_annoIdentifier;
	}

	public static final String getAccAnnoname() {
		return ACC_annoName;
	}

	public final Menu getMenu() {
		return menu;
	}

	public final void setMenu(Menu menu) {
		this.menu = menu;
	}
	/**
	 * 得到所有工程顶部菜单
	 * @return List&lt;MenuItem&gt;
	 */
	public List<MenuItem> getTaskMenuItemAll() {
		List<MenuItem> list= SWTSearch.menuItemSearch(menu);
		List<MenuItem> list2=list.stream().filter(x->x.getID()>=DesktopConst.ACC_FistTaskID).collect(Collectors.toList());
		return list2;
	}

}
