package des.wangku.operate.standard.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import des.wangku.operate.standard.desktop.LoadTaskUtils;
import des.wangku.operate.standard.dialog.HelpDialog;
import des.wangku.operate.standard.dialog.SearchText;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsClipboard;
import des.wangku.operate.standard.utls.UtilsSWTMessageBox;
import des.wangku.operate.standard.utls.UtilsSWTTools;
import des.wangku.operate.standard.utls.UtilsSWTTree;
import des.wangku.operate.standard.utls.UtilsString;
import des.wangku.operate.standard.utls.UtilsVerification;

/**
 * 节点的data为隐藏的编号，唯一<br>
 * id与名称自动过滤两侧空格<br>
 * 名称不能含有[XXX]
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MQTree extends Tree {
	/** 本对象 */
	MQTree base = this;
	/** 显示对象 */
	Display display = null;
	/** 鼠标右键 */
	Menu menu = null;
	/** 异级移动 */
	MenuItem menuMoveLR = null;
	/** 同级移动 */
	MenuItem menuMoveUD = null;
	/** 父窗口 */
	Composite parent;
	/** 子容器 */
	Shell shell;
	/** 标题中是否含有id 如:[XXXX]ABCD */
	boolean showID = false;
	/** 列表排序 如果设置排序，则主列表同级不可修改 */
	boolean sorting = false;
	/** 是否忽略id大小写 */
	boolean ignoreCase = false;
	/** 短记忆id，即输入数据节点时，自动保存最后的id，用于程序手动批量导入时，通过特殊方法，直接录入到上一个节点的子节点 */
	String memoryid=null;
	/** 附加对象 */
	Map<String,Object> additObjMap=new HashMap<>();
	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 */
	public MQTree(Composite parent) {
		this(parent, 0, false);
	}

	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 */
	public MQTree(Composite parent, int style) {
		this(parent, style, false);
	}

	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 * @param isMulti boolean
	 */
	public MQTree(Composite parent, int style, boolean isMulti) {
		super(parent, isMulti ? (style | ACC_styleMulti) : (style | ACC_styleAlone));
		p_init(parent, style);
	}

	/**
	 * 通过项目中从包外读取项目json文件中提出字符串并导入数据<br>
	 * json文件名:des-wkope-task-XXXX.json
	 * @param task AbstractTask
	 * @param jsonkey String
	 */
	public final void putAbstractTaskJson(AbstractTask task, String jsonkey) {
		if (task == null || jsonkey == null) return;
		this.mkItemByUC_Json(task.getProJsonValue(jsonkey));
	}

	/**
	 * 从堆栈轨迹查找工程，并得到此工程下的json文件<br>
	 * json文件名:des-wkope-task-XXXX.json
	 * @param jsonkey String
	 */
	public final void putAutoAbstractJson(String jsonkey) {
		if (jsonkey == null) return;
		AbstractTask task = LoadTaskUtils.getStackTracebstractTask();
		if (task == null) return;
		putAbstractTaskJson(task, jsonkey);
	}

	/**
	 * 允许左右移动
	 * @return MultiTree
	 */
	public final MQTree allowMoveLR() {
		menuMoveLR.setEnabled(true);
		return this;
	}

	/**
	 * 允许上下移动
	 * @return MultiTree
	 */
	public final MQTree allowMoveUD() {
		menuMoveUD.setEnabled(true);
		return this;
	}

	/**
	 * 多个id转成name
	 * @param idArrs String[]
	 * @return String[]
	 */
	public final String[] changeId2Name(String... idArrs) {
		String[] arr = {};
		if (idArrs.length == 0) return arr;
		List<String> list = new ArrayList<>(idArrs.length);
		for (String id : idArrs) {
			String name = getName(id);
			if (name != null) list.add(name);
		}
		return list.toArray(arr);
	}

	protected void checkSubclass() {

	}

	/**
	 * 得到顺序随机id以sj1,sj12以例<br>
	 * 数字范围: 1--9999之间<br>
	 * @return String
	 */
	public final String getAutoID() {
		String[] arr = getIDAll();
		for (int i = 1; i < ACC_AutoIDNumMax; i++) {
			String id = ACC_AutoIDHead + i;
			if (!UtilsString.isExist(id, arr)) return id;
		}
		return null;
	}

	/**
	 * 得树型的所有头部节点id
	 * @return String[]
	 */
	public final String[] getHeadID() {
		return Utils.getTreeItemID(base.getItems());
	}

	/**
	 * 得到此id的同亲节点即同父节点<br>
	 * @param id String
	 * @return String[]
	 */
	public final String[] getBrothersID(String id) {
		String[] arr = {};
		if (id == null) return arr;
		TreeItem e = Utils.getSearchTreeItem(base, id, 0);
		if (e == null) return arr;
		TreeItem parent = e.getParentItem();
		if (parent == null) return Utils.getTreeItemID(base.getItems());
		return Utils.getTreeItemID(parent.getItems());
	}

	/**
	 * 得到此id下的亲子节点<br>
	 * @param id String
	 * @return String[]
	 */
	public final String[] getChildId(String id) {
		String[] arr = {};
		if (id == null) return arr;
		TreeItem e = Utils.getSearchTreeItem(base, id, 0);
		if (e == null) return arr;
		return Utils.getTreeItemID(e.getItems());
	}

	/**
	 * 得到此id下的所有子节点 多级汇总
	 * @param id String
	 * @return String[]
	 */
	public final String[] getChildIdDeep(String id) {
		String[] arr = {};
		if (id == null) return arr;
		TreeItem e = Utils.getSearchTreeItem(base, id, 0);
		if (e == null) return arr;
		List<TreeItem> list = p_getTreeItemListAll(e);
		List<String> list2 = list.stream().map(t -> Utils.getID(t)).filter(Objects::nonNull).collect(Collectors.toList());
		return list2.toArray(arr);
	}

	/**
	 * 树型结构节点总数
	 * @return int
	 */
	public final int getCount() {
		return p_getTreeItemListAll(null).size();
	}

	/**
	 * 通过嵌套得到所有的节点<br>
	 * @return String[]
	 */
	public final String[] getIDAll() {
		return getIDAll(null);
	}

	/**
	 * 通过嵌套得到所有的节点<br>
	 * 如id为null，则返回Tree所有节点<br>
	 * 如id没有此节点，则返回空数组<br>
	 * 如有此节点，即指此节点下的所有节点
	 * @param id String
	 * @return String[]
	 */
	public final String[] getIDAll(String id) {
		List<TreeItem> list = getTreeItemAll(id);
		List<String> list2 = list.stream().map(t -> Utils.getID(t)).filter(Objects::nonNull).collect(Collectors.toList());
		String[] arr = {};
		return list2.toArray(arr);
	}

	/**
	 * 查找全树型结构中选中状态的子节点<br>
	 * @return String[]
	 */
	public final String[] getIDAllByChecked() {
		return getIDAllByChecked(true);
	}

	/**
	 * 查找选中状态或未选中的子节点<br>
	 * 如arrs为空，则返回Tree所有节点<br>
	 * 如arrs中含有null，则返回Tree所有节点<br>
	 * 如arrs中含有的所有id下的所有的节点<br>
	 * @param isChecked boolean
	 * @param idArrs String[]
	 * @return String[]
	 */

	public final String[] getIDAllByChecked(boolean isChecked, String... idArrs) {
		List<TreeItem> list = p_getTreeItemByIDArray(idArrs);
		String[] arr = {};
		if (list.size() == 0) return arr;
		List<String> list2 = list.stream().filter(x -> x.getChecked() == isChecked).map(t -> Utils.getID(t)).filter(Objects::nonNull).collect(Collectors.toList());
		return list2.toArray(arr);
	}

	/**
	 * 查找全树型结构中展开状态的节点<br>
	 * @return String[]
	 */
	public final String[] getIDAllByExpanded() {
		return getIDAllByExpanded(true);
	}

	/**
	 * 查找展开状态或未展开的子节点<br>
	 * 如arrs为空，则返回Tree所有节点<br>
	 * 如arrs中含有null，则返回Tree所有节点<br>
	 * 如arrs中含有的所有id下的所有的节点<br>
	 * @param isExpanded boolean
	 * @param idArrs String[]
	 * @return String[]
	 */
	public final String[] getIDAllByExpanded(boolean isExpanded, String... idArrs) {
		List<TreeItem> list = p_getTreeItemByIDArray(idArrs);
		String[] arr = {};
		if (list.size() == 0) return arr;
		List<String> list2 = list.stream().filter(x -> x.getExpanded() == isExpanded).map(t -> Utils.getID(t)).filter(Objects::nonNull).collect(Collectors.toList());
		return list2.toArray(arr);
	}

	/**
	 * 通过id得到名称
	 * @param id String
	 * @return String
	 */
	public final String getName(String id) {
		if (id == null) return null;
		TreeItem e = Utils.getSearchTreeItem(base, id, 0);
		if (e == null) return null;
		return Utils.getName(e);
	}

	/**
	 * 通过名称查找所有含此名称的名称 全等
	 * @param key String
	 * @param type int
	 * @return String[]
	 */
	public final String[] getNamesByEqualsKey(String key) {
		String[] arr = {};
		if (key == null) return arr;
		List<TreeItem> list = Utils.searchTreeItemList(this, key, 1);
		List<String> list2 = list.stream().map(t -> Utils.getName(t)).filter(Objects::nonNull).collect(Collectors.toList());
		return list2.toArray(arr);
	}

	/**
	 * 从数型中得到某个id的父id<br>
	 * 没有找到，则返回null
	 * @param id String
	 * @return String
	 */
	public final String getParentID(String id) {
		if (id == null) return null;
		TreeItem e = Utils.getSearchTreeItem(base, id, 0);
		if (e == null) return null;
		TreeItem f = e.getParentItem();
		if (f == null) return null;
		return Utils.getID(f);
	}

	/**
	 * 判断id是否为头部节点
	 * @param id String
	 * @return boolean
	 */
	public final boolean isHeadID(String id) {
		if (id == null) return false;
		TreeItem e = Utils.getSearchTreeItem(base, id, 0);
		if (e == null) return false;
		TreeItem f = e.getParentItem();
		if (f == null) return true;
		return false;

	}

	/**
	 * 得到树中某个id的父id串
	 * @param id String
	 * @return String[]
	 */
	public final String[] getParentIDArray(String id) {
		String[] arr = {};
		if (id == null) return arr;
		List<String> list = new ArrayList<>();
		p_getParentID(list, id);
		return list.toArray(arr);
	}

	/**
	 * 通过嵌套得到所有的节点<br>
	 * @return List&lt;TreeItem&gt;
	 */
	public final List<TreeItem> getTreeItemAll() {
		return getTreeItemAll(null);
	}

	/**
	 * 通过嵌套得到所有的节点<br>
	 * 如id为null，则返回Tree所有节点<br>
	 * 如id没有此节点，则返回空数组<br>
	 * 如有此节点，即指此节点下的所有节点
	 * @param id String
	 * @return List&lt;TreeItem&gt;
	 */
	public final List<TreeItem> getTreeItemAll(String id) {
		List<TreeItem> list = new ArrayList<>();
		if (id == null) list = p_getTreeItemListAll(null);
		else {
			TreeItem e = Utils.getSearchTreeItem(base, id, 0);
			if (e == null) return new ArrayList<>();
			list = p_getTreeItemListAll(e);
		}
		return list;
	}

	/**
	 * 判断id是否是parentID的子节点
	 * @param parentID String
	 * @param id String
	 * @return boolean
	 */
	public final boolean isChild(String parentID, String id) {
		if (parentID == null || id == null) return false;
		String[] arr = this.getChildIdDeep(parentID);
		for (String e : arr) {
			if (id.equals(e)) return true;
		}
		return false;
	}

	/**
	 * 判断编号是否存在 null或空，则返回false
	 * @param id String
	 * @return boolean
	 */
	public final boolean isExistID(String id) {
		return p_isExistValue(id, 0);
	}

	/**
	 * 判断名称是否存在 null或空，则返回false<br>
	 * @param name String
	 * @return boolean
	 */
	public final boolean isExistName(String name) {
		return p_isExistValue(name, 1);
	}

	/**
	 * 建立头节点 id为自动编号[sj1,sj12]
	 * @param name String
	 * @return String
	 */
	public final String mkAutoItem(String name) {
		String id = getAutoID();
		return mkItem(id, name);
	}

	/**
	 * 建立节点 使用短记忆id为父节点
	 * @param id String
	 * @param name String
	 * @return String
	 */
	public final String mkMemoItem(String id, String name) {
		TreeItem e=Utils.testingIDParent(base,id,memoryid);
		if (e == null) return null;
		Utils.putTreeItemValue(base,false, e, id, name);
		return id;
	}
	/**
	 * 建立头节点
	 * @param id String
	 * @param name String
	 * @return String
	 */
	public final String mkItem(String id, String name) {
		return mkItem(null, id, name);
	}

	/**
	 * 建立节点<br>
	 * 如果parentid=null 则为头部节点<br>
	 * 如果没有找到parentid节点，则返回null<br>
	 * @param parentid String
	 * @param id String
	 * @param name String
	 * @return String
	 */
	public final String mkItem(String parentid, String id, String name) {
		TreeItem e=Utils.testingIDParent(base,id,parentid);
		if (e == null) return null;
		Utils.putTreeItemValue(base,e, id, name);
		return id;
	}

	/**
	 * 导入unitclass节点，并嵌套导入子节点，最后保存状态
	 * @param e UnitClass
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<UnitClass> mkItem(UnitClass e) {
		List<UnitClass> uclist = new ArrayList<>();
		Utils.putUnitClass(this, uclist, e);
		Utils.changeTreeItemState(this, uclist);/* 从unitClass中更新状态 导入数据后，再更新状态 */
		return uclist;
	}

	/**
	 * 把unitclass列表导入，导入之前把多等级节点转成同级节点，依次进行导入<br>
	 * 返回导入成功的unitclass列表
	 * @param sourcelist List&lt;UnitClass&gt;
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<UnitClass> mkItemByUCList(List<UnitClass> sourcelist) {
		List<UnitClass> uclist = Utils.summUnitClass(base, sourcelist);
		List<UnitClass> list = new ArrayList<>();
		for (UnitClass e : uclist)
			if (Utils.putUCValue(this, e)) list.add(e);
		Utils.changeTreeItemState(this, list);/* 从unitClass中更新状态 导入数据后，再更新状态 */
		return list;
	}

	/**
	 * 导入unitclass节点<br>
	 * 数据为json字符串以list并列保存的数据源<br>
	 * 或为单个unitclass节点
	 * 返回保存成功的id表
	 * @param json String
	 * @return List&lt;String&gt;
	 */
	public final List<String> mkItemByUC_Json(String json) {
		Object obj = JSON.parse(json);
		if (obj instanceof JSONArray) {
			List<UnitClass> sourcelist = JSON.parseArray(json, UnitClass.class);
			List<UnitClass> list = mkItemByUCList(sourcelist);
			return list.stream().map(t -> t.id).filter(Objects::nonNull).collect(Collectors.toList());
		}
		if (obj instanceof JSONObject) {
			UnitClass e = JSON.parseObject(json, UnitClass.class);
			List<UnitClass> uclist = Utils.summUnitClass(base, e);
			return uclist.stream().map(t -> t.id).filter(Objects::nonNull).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	/**
	 * 添加监听器 针对键盘
	 * @return KeyListener
	 */
	private final KeyListener p_addKeyListener() {
		KeyListener t = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				/* 移除项目 */
				if (e.keyCode == SWT.DEL) {
					MQTree b = (MQTree) e.getSource();
					TreeItem[] arrs = b.getSelection();
					for (TreeItem ee : arrs) {
						if (ee == null || ee.isDisposed()) continue;
						ee.removeAll();
						ee.dispose();
					}
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'f' || e.keyCode == 'F')) {
					SearchText st = (new SearchText(shell, 0)).setTextHead("搜索关键字并选中");
					Object obj = st.open();
					if (obj == null) return;
					String value = (String) obj;
					List<TreeItem> list = base.p_getTreeItemListAll(null);
					for (TreeItem ff : list) {
						if (ff.getText().indexOf(value) > -1) {
							ff.setChecked(true);
							Utils.expandedParent(ff, true);
							UtilsSWTTree.changeSelectTreeCheck(ff, true);
						}
					}
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'c' || e.keyCode == 'C')) {
					MQTree b = (MQTree) e.getSource();
					TreeItem[] arrs = b.getSelection();
					List<UnitClass> list = new ArrayList<>();
					for (TreeItem f : arrs) {
						boolean isExpanded = f.getExpanded();
						list.add(new UnitClass(f, !isExpanded));
					}
					if (list.size() == 0) return;
					String content = JSON.toJSONString(list);
					UtilsClipboard.copy(content);
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

		};
		return t;
	}

	/**
	 * 添加监听器
	 */
	private final void p_addListener() {
		this.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					if (item == null) return;
					boolean checked = item.getChecked();
					UtilsSWTTree.changeSelectTreeCheck(item, checked);
				}
			}
		});
		this.addHelpListener(p_listenerHelp());
		this.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem f = (TreeItem) e.item;
				InterfaceMultiTreeExtend after = UtilsSWTTools.getParentInterfaceObj(base, InterfaceMultiTreeExtend.class);
				if (f != null && after != null) {
					after.multiTreeSelectedAfter(f);
				}
			}
		});
		this.addKeyListener(p_addKeyListener());
		this.addListener(SWT.FocusIn, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				if (item == null) return;
			}
		});

		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Point point = new Point(e.x, e.y);
				TreeItem item = base.getItem(point);
				if (item == null) return;
				InterfaceMultiTreeExtend after = UtilsSWTTools.getParentInterfaceObj(base, InterfaceMultiTreeExtend.class);
				if (item != null && after != null) {
					after.multiTreeMouseDoubleClick(item);
					logger.info("MultiTree MouseDoubleClickListener working:" + Utils.toStringTreeItem(item));
					//logger.info("MultiTree SelectionListener working:" + isafter);
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseUp(MouseEvent e) {

			}

		});
	}

	/**
	 * 组合标题，如果显示id号，则使用[]进行显示
	 * @param id String
	 * @param name String
	 * @return String
	 */
	private final String p_combName(String id, String name) {
		return showID ? "[" + id + "]" + name : name;
	}

	/**
	 * 嵌套调取父id，并保存在list中
	 * @param base MQTree
	 * @param list List&lt;String&gt;
	 * @param id String
	 */
	private final void p_getParentID(List<String> list, String id) {
		String parentid = getParentID(id);
		if (parentid == null) return;
		list.add(parentid);
		p_getParentID(list, parentid);
	}

	/**
	 * 从id数组中提取TreeItem表，并排除重复<br>
	 * 如arrs为空，则返回Tree所有节点<br>
	 * 如arrs中含有null，则返回Tree所有节点<br>
	 * 如arrs中含有的所有id下的所有的节点<br>
	 * @param idArrs String[]
	 * @return List&lt;TreeItem&gt;
	 */
	private final List<TreeItem> p_getTreeItemByIDArray(String... idArrs) {
		List<TreeItem> list = new ArrayList<>();
		if (Utils.isFullTree(idArrs)) list = getTreeItemAll(null);
		else {
			for (String e : idArrs)
				list.addAll(getTreeItemAll(e));
			list = new ArrayList<>(new HashSet<>(list));/* 去重 */
		}
		return list;
	}

	/**
	 * 通过嵌套得到所有的节点<br>
	 * 如e为null，则返回Tree所有节点<br>
	 * 如e不为null，即指此节点下的所有节点
	 * @param e TreeItem
	 * @return List &lt;TreeItem&gt;
	 */
	private final List<TreeItem> p_getTreeItemListAll(TreeItem e) {
		if (e == null) return Utils.childTreeItem(this);
		return Utils.childTreeItem(e);
	}

	/**
	 * 得取树型的对象集所有节点
	 * @param isChecked boolean
	 * @return List&lt;TreeItem&gt;
	 */
	@SuppressWarnings("unused")
	private final List<TreeItem> p_getTreeItemListByChecked(boolean isChecked) {
		return p_getTreeItemListByChecked(null, isChecked);
	}

	/**
	 * 如果id为null，则查找所有节点<br>
	 * 如果id不为null,则查找某个节点下的所有节点
	 * @param id String
	 * @param isChecked boolean
	 * @return List&lt;TreeItem&gt;
	 */
	private final List<TreeItem> p_getTreeItemListByChecked(String id, boolean isChecked) {
		TreeItem e = Utils.getSearchTreeItem(base, id, 0);
		List<TreeItem> list = p_getTreeItemListAll(e == null ? null : e);
		return list.stream().filter(x -> x.getChecked() == isChecked).collect(Collectors.toList());
	}

	/**
	 * 初始化
	 * @param parent Composite
	 * @param style int
	 */
	private final void p_init(Composite parent, int style) {
		checkSubclass();
		this.parent = parent;
		this.shell = new Shell(base.getShell());
		this.shell.setText("检索关键字");
		this.display = parent.getDisplay();
		p_addListener();
		menu = new Menu(this);
		setMenu(menu);
		p_setMoveMenuItem();
	}

	/**
	 * 通过type查找字符串是否存在
	 * @param value String
	 * @param type int
	 * @return boolean
	 */
	private final boolean p_isExistValue(String value, int type) {
		if (value == null || value.trim().length() == 0) return false;
		TreeItem e = Utils.getSearchTreeItem(base, value, type);
		return e != null;

	}

	/**
	 * 按键盘F1时弹出窗体
	 * @return HelpListener
	 */
	private final HelpListener p_listenerHelp() {
		HelpListener t = new HelpListener() {
			@Override
			public void helpRequested(HelpEvent e) {
				String content = "新版自定义树型控件结构[MQTree]：\n" + "\t上下移动\t[allowMoveUD方法]\n" + "\t左右移动\t[allowMoveLR方法]\n" + "\t删除\t[Delete键]\n" + "\t检索关键字并选中\t[Ctrl+f]";
				HelpDialog ver = new HelpDialog(shell, 0, content);
				ver.open();
			}
		};
		return t;
	}

	/**
	 * 设置向上向下向左向右移动的功能菜单
	 */
	private final void p_setMoveMenuItem() {
		p_setMoveMenuItemUp();
		p_setMoveMenuItemLeft();
	}

	/**
	 * 左右移动右键菜单
	 */
	private final void p_setMoveMenuItemLeft() {
		menuMoveLR = new MenuItem(menu, SWT.CASCADE);
		menuMoveLR.setEnabled(false);
		menuMoveLR.setText("异级移动");
		Menu b = new Menu(this.getShell(), SWT.DROP_DOWN);
		menuMoveLR.setMenu(b);
		MenuItem c = new MenuItem(b, SWT.NONE);
		c.setText("左移");
		c.addListener(SWT.Selection, p_listenerControlMoveLeft(true));
		MenuItem d = new MenuItem(b, SWT.NONE);
		d.setText("右移");
		d.addListener(SWT.Selection, p_listenerControlMoveLeft(false));
	}

	/**
	 * 上下移动右键菜单
	 */
	private final void p_setMoveMenuItemUp() {
		menuMoveUD = new MenuItem(menu, SWT.CASCADE);
		menuMoveUD.setText("同级移动");
		menuMoveUD.setEnabled(false);
		Menu b = new Menu(this.getShell(), SWT.DROP_DOWN);
		menuMoveUD.setMenu(b);
		MenuItem c = new MenuItem(b, SWT.NONE);
		c.setText("上移");
		c.addListener(SWT.Selection, p_listenerControlMoveUp(true));
		MenuItem d = new MenuItem(b, SWT.NONE);
		d.setText("下移");
		d.addListener(SWT.Selection, p_listenerControlMoveUp(false));
	}

	/**
	 * 上下移动选中的节点
	 * @param isUP boolean
	 * @return Listener
	 */
	private final Listener p_listenerControlMoveUp(boolean isUP) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				if (sorting) {
					UtilsSWTMessageBox.Alert(shell, "移位操作失败", "已设置为排序功能，则按编号进行排序");
					return;
				}
				TreeItem[] arrs = base.getSelection();
				if (arrs.length == 0) return;
				String idSelectedArr[] = Utils.getTreeItemID(arrs);
				Utils.changeTreeItem(base, isUP ? -1 : 1, idSelectedArr);
			}
		};
		return t;
	}

	/**
	 * 左右移动选中的节点
	 * @param isLeft boolean
	 * @return Listener
	 */
	private final Listener p_listenerControlMoveLeft(boolean isLeft) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] arrs = base.getSelection();
				String[] idSelectedArr=Utils.getIDArray(arrs);
				Utils.changeTreeItem(base, isLeft, idSelectedArr);
			}
		};
		return t;
	}
	/**
	 * 显示ID
	 * @return MultiTree
	 */
	public final MQTree ShowID() {
		showID = true;
		return this;
	}

	/**
	 * 进行排序，从小到大，按id，同级进行排序<br>
	 * 树型节点将不可移动
	 * @return MultiTree
	 */
	public final MQTree Sorting() {
		sorting = true;
		return this;
	}

	/**
	 * 是否输入与输出时忽略大小写 默认为不忽略大小写
	 * @return MultiTree
	 */
	public final MQTree IgnoreCase() {
		ignoreCase = true;
		return this;
	}

	/**
	 * 得到单数组形态节点汇总
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<UnitClass> toUCList() {
		return Utils.summUnitClass(base, toUCListWhole());
	}

	/**
	 * 得到完整的树型结构
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<UnitClass> toUCListWhole() {
		return Utils.getWholeUnitClassAll(base);
	}

	/**
	 * 所有节点输出成json字符串
	 * @return String
	 */
	public final String toJson() {
		List<UnitClass> list = toUCList();
		return JSON.toJSONString(list);
	}

	/**
	 * 强制刷新，用于树型建立后，再添加节点，则会发生编号混乱<br>
	 * 如果设置了排序功能，则进行二次排序<br>
	 * 防止每次添加节点时都要刷新一下树型<br>
	 */
	public final void refresh() {
		List<UnitClass> list = toUCList();
		refreshData(list);
	}

	/**
	 * 强制展开所有单元
	 */
	public final void expandedAll() {
		List<UnitClass> list = toUCList();
		for (UnitClass e : list)
			e.expanded = true;
		refreshData(list);

	}

	/**
	 * 强制刷新，把list导入
	 * @param list List&lt;UnitClass&gt;
	 */
	private final void refreshData(List<UnitClass> list) {
		this.removeAll();
		if (sorting) Utils.sorting(list);
		this.mkItemByUCList(list);
	}

	/**
	 * 树形结构单元
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class UnitClass implements Comparable<UnitClass> {
		/** 是否选中 */
		boolean checked = false;
		/** 是否展开 */
		boolean expanded = false;
		/** 是否灰色 */
		boolean grayed = false;
		/** 编号 唯一 */
		String id = null;
		/** 下级子列 */
		List<UnitClass> list = new ArrayList<>();
		/** 名称 */
		String name = null;
		/** 关联字段 */
		String targetid = null;

		public UnitClass() {

		}

		/**
		 * 构造函数
		 * @param id String
		 * @param name String
		 * @param value String
		 * @param targetid String
		 * @param checked boolean
		 * @param expanded boolean
		 * @param grayed boolean
		 */
		public UnitClass(String id, String name, String targetid, boolean checked, boolean expanded, boolean grayed) {
			this.id = id;
			this.name = name;
			this.targetid = targetid;
			this.checked = checked;
			this.expanded = expanded;
			this.grayed = grayed;
		}

		/**
		 * 把TreeItem转成UnitClass<br>
		 * 提取子类
		 * @param e TreeItem
		 */
		public UnitClass(TreeItem e) {
			this(e, true);
		}

		public final boolean isChecked() {
			return checked;
		}

		public final void setChecked(boolean checked) {
			this.checked = checked;
		}

		public final boolean isExpanded() {
			return expanded;
		}

		public final void setExpanded(boolean expanded) {
			this.expanded = expanded;
		}

		public final boolean isGrayed() {
			return grayed;
		}

		public final void setGrayed(boolean grayed) {
			this.grayed = grayed;
		}

		public final String getId() {
			return id;
		}

		public final void setId(String id) {
			this.id = id;
		}

		public final List<UnitClass> getList() {
			return list;
		}

		public final void setList(List<UnitClass> list) {
			this.list = list;
		}

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final String getTargetid() {
			return targetid;
		}

		public final void setTargetid(String targetid) {
			this.targetid = targetid;
		}

		/**
		 * 把TreeItem转成UnitClass<br>
		 * 是否提取子类
		 * @param e TreeItem
		 * @param isSubclass boolean
		 */
		public UnitClass(TreeItem e, boolean isSubclass) {
			boolean isSuccess = putTreeItem(e);
			if (!isSuccess) return;
			if (!isSubclass) return;
			putTreeItemSubclass(e);
		}

		@Override
		public int compareTo(UnitClass arg0) {
			return this.id.compareTo(arg0.id);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			UnitClass other = (UnitClass) obj;
			if (checked != other.checked) return false;
			if (expanded != other.expanded) return false;
			if (grayed != other.grayed) return false;
			if (id == null) {
				if (other.id != null) return false;
			} else if (!id.equals(other.id)) return false;
			if (name == null) {
				if (other.name != null) return false;
			} else if (!name.equals(other.name)) return false;
			if (targetid == null) {
				if (other.targetid != null) return false;
			} else if (!targetid.equals(other.targetid)) return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (checked ? 1231 : 1237);
			result = prime * result + (expanded ? 1231 : 1237);
			result = prime * result + (grayed ? 1231 : 1237);
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((targetid == null) ? 0 : targetid.hashCode());
			return result;
		}

		/**
		 * 导入状态
		 * @param base MQTree
		 */
		final void inputState(MQTree base) {
			TreeItem e = Utils.getSearchTreeItem(base, id, 0);
			if (e == null) return;
			inputState(e);
		}

		/**
		 * 导入状态
		 * @param e TreeItem
		 */
		final void inputState(TreeItem e) {
			if (e == null) return;
			e.setChecked(checked);
			e.setExpanded(expanded);
			e.setGrayed(grayed);
		}

		/**
		 * 导入TreeItem信息
		 * @param e TreeItem
		 * @return boolean
		 */
		public boolean putTreeItem(TreeItem e) {
			if (e == null) return false;
			String text = e.getText();
			if (text == null) return false;
			String name = UtilsVerification.getStringName(text);
			if (name == null) return false;
			String id = Utils.getID(e);
			if (id == null) return false;
			this.id = id;
			this.name = name;
			this.checked = e.getChecked();
			this.expanded = e.getExpanded();
			this.grayed = e.getGrayed();
			this.targetid = Utils.getID(e.getParentItem());
			return true;
		}

		/**
		 * 导入TreeItem中的子类信息
		 * @param e TreeItem
		 */
		public void putTreeItemSubclass(TreeItem e) {
			if (e == null) return;
			TreeItem[] arrs = e.getItems();
			for (TreeItem f : arrs) {
				list.add(new UnitClass(f, true));
			}
		}

		@Override
		public String toString() {
			return "UnitClass [checked=" + checked + ", expanded=" + expanded + ", grayed=" + grayed + ", id=" + id + ", list=" + list + ", name=" + name + ", targetid=" + targetid + "]";
		}

		
		
		
	}

	/**
	 * 工具类
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	static final class Utils {
		/**
		 * 先判断此id是否存在，如果存在则返回null<br>
		 * 最后返回parentid下的新建TreeItem
		 * @param base MQTree
		 * @param id String
		 * @param parentid String
		 * @return TreeItem
		 */
		static final TreeItem testingIDParent(MQTree base,String id,String parentid) {
			if (base.isExistID(id)) return null;
			TreeItem e = Utils.getNewTreeItem(base, parentid);
			return e;
		}
		/**
		 * 把TreeItem数组中所有的id提出
		 * @param arrs TreeItem[]
		 * @return String[]
		 */
		static String[] getTreeItemID(TreeItem... arrs) {
			String[] arr = {};
			if (arrs.length == 0) return arr;
			List<String> list = new ArrayList<>(arrs.length);
			for (TreeItem e : arrs) {
				String id = getID(e);
				if (id == null) continue;
				if (list.contains(id)) continue;
				list.add(id);
			}
			if (list.size() == 0) return arr;
			return list.toArray(arr);
		}

		/**
		 * 排序重组
		 * @param list List&lt;UnitClass&gt;
		 */
		static final void sorting(List<UnitClass> list) {
			if (list.size() < 2) return;
			Collections.sort(list);
			for (UnitClass e : list)
				sorting(e.list);
		}

		/**
		 * 排序重组
		 * @param e UnitClass
		 */
		static final void sorting(UnitClass e) {
			sorting(e.list);
		}

		/**
		 * 整理id，过滤两侧空格
		 * @param id String
		 * @return String
		 */
		static final String arrangeID(String id) {
			if (id == null || id.length() == 0) return id;
			return id.trim();
		}

		/**
		 * 更新状态 用于赋值后，再批量修改状态
		 * @param base MQTree
		 * @param list List&lt;UnitClass&gt;
		 */
		static final void changeTreeItemState(MQTree base, List<UnitClass> list) {
			for (UnitClass f : list) {
				f.inputState(base);
			}
		}

		/**
		 * 嵌套调出所有此treeitem下的所有子标签，是否选中，如为null，则不判断<br>
		 * 含本TreeItem
		 * @param list List&lt;TreeItem&gt;
		 * @param e TreeItem
		 */
		private static final void childTreeItem(List<TreeItem> list, TreeItem e) {
			if (list == null || e == null) return;
			list.add(e);
			for (TreeItem f : e.getItems()) {
				childTreeItem(list, f);
			}
		}

		/**
		 * 嵌套调出所有此tree下的所有子标签
		 * @param e Tree
		 * @return list List&lt;TreeItem&gt;
		 */
		static final List<TreeItem> childTreeItem(Tree e) {
			List<TreeItem> list = new ArrayList<>();
			TreeItem[] arrs = e.getItems();
			for (TreeItem f : arrs) {
				Utils.childTreeItem(list, f);
			}
			return list;
		}

		/**
		 * 嵌套调出所有此treeitem下的所有子节点
		 * @param e TreeItem
		 * @return list List&lt;TreeItem&gt;
		 */
		static final List<TreeItem> childTreeItem(TreeItem e) {
			List<TreeItem> list = new ArrayList<>();
			if (e == null) return list;
			TreeItem[] arrs = e.getItems();
			for (TreeItem f : arrs) {
				Utils.childTreeItem(list, f);
			}
			return list;
		}

		/**
		 * 展开或关闭父类
		 * @param e TreeItem
		 * @param isExpanded boolean
		 */
		static final void expandedParent(TreeItem e, boolean isExpanded) {
			if (e == null) return;
			TreeItem parent = e.getParentItem();
			if (parent == null) return;
			parent.setExpanded(isExpanded);
			expandedParent(parent, isExpanded);
		}
		/**
		 * 展开或关闭节点以及所有子节点
		 * @param e UnitClass
		 * @param isExpanded boolean
		 */
		static final void expandedUC(UnitClass e, boolean isExpanded) {
			if (e == null) return;
			e.expanded=isExpanded;
			for(UnitClass f:e.list) {
				expandedUC(f,isExpanded);
			}
		}
		/**
		 * 多个TreeItem转成id数组
		 * @param arrs TreeItem[]
		 * @return String[]
		 */
		static final String[] getIDArray(TreeItem... arrs) {
			String[] arr= {};
			if(arrs.length==0)return arr;
			List<String> list=new ArrayList<>(arrs.length);
			for(TreeItem e:arrs) {
				String id=getID(e);
				if(id!=null)list.add(id);
			}			
			return list.toArray(arr);
		}

		/**
		 * 得到名称<br>
		 * 从data中提取
		 * @param e TreeItem
		 * @return String
		 */
		static final String getID(TreeItem e) {
			if (e == null) return null;
			Object obj = e.getData();
			if (obj == null) return null;
			return obj.toString();
		}

		/**
		 * 得到名称(排除id) [XXX]
		 * @param title String
		 * @return String
		 */
		static final String getName(String title) {
			return UtilsVerification.getStringName(title);
		}

		/**
		 * 得到名称(排除id) [XXX]
		 * @param e TreeItem
		 * @return String
		 */
		static final String getName(TreeItem e) {
			if (e == null) return null;
			return getName(e.getText());
		}

		/**
		 * 新建TreeItem<br>
		 * parentid:null 第一列<br>
		 * parentid:不存在 返回null<br>
		 * parentid:存在 指定节点下<br>
		 * @param tree MQTree
		 * @param parentid String
		 * @return TreeItem
		 */
		static final TreeItem getNewTreeItem(MQTree tree, String parentid) {
			if (parentid == null) return new TreeItem(tree, SWT.NONE);
			parentid = Utils.arrangeID(parentid);
			TreeItem parent = Utils.getSearchTreeItem(tree, parentid, 0);
			if (parent == null) return null;
			TreeItem e = new TreeItem(parent, SWT.NONE);
			return e;
		}

		/**
		 * 嵌套调用base中查看是否含有value值<br>
		 * type:0 按id查<br>
		 * type:1 按名称查[排除id] 全等<br>
		 * type:2 按名称查[排除id] indexof>-1<br>
		 * @param base MQTree
		 * @param value String
		 * @param type int
		 * @return TreeItem
		 */
		static final TreeItem getSearchTreeItem(MQTree base, String value, int type) {
			value = Utils.arrangeID(value);
			if (base == null || value == null) return null;
			TreeItem[] arr = base.getItems();
			for (TreeItem e : arr) {
				TreeItem f = getSearchTreeItem(base, e, value, type);
				if (f != null) return f;
			}
			return null;
		}

		/**
		 * 嵌套调用e中查看是否含有value值<br>
		 * type:0 按id查<br>
		 * type:1 按名称查[排除id] 全等<br>
		 * type:2 按名称查[排除id] indexof>-1<br>
		 * @param base MQTree
		 * @param e TreeItem
		 * @param value String
		 * @param type int
		 * @return TreeItem
		 */
		static final TreeItem getSearchTreeItem(MQTree base, TreeItem e, String value, int type) {
			if (e == null || value == null) return null;
			if (isEquals(base, e, value, type)) return e;
			TreeItem[] arrs = e.getItems();
			for (TreeItem f : arrs) {
				TreeItem t = getSearchTreeItem(base, f, value, type);
				if (t != null) return t;
			}
			return null;
		}

		/**
		 * 从list查找id所在的uc
		 * @param base MQTree
		 * @param list List&lt;UnitClass&gt;
		 * @param id String
		 * @return UnitClass
		 */
		static final UnitClass getUC(MQTree base, List<UnitClass> list, String id) {
			return getUC(base, list, id, 0);
		}

		/**
		 * 从MQTree树型中查找符合条件的uc
		 * @param base MQTree
		 * @param value String
		 * @param type int
		 * @return UnitClass
		 */
		static final UnitClass getUC(MQTree base, String value, int type) {
			List<UnitClass> list = base.toUCListWhole();
			return getUC(base, list, value, type);
		}

		/**
		 * 从list中查找符合条件的uc
		 * @param base MQTree
		 * @param list List&lt;UnitClass&gt;
		 * @param value String
		 * @param type int
		 * @return UnitClass
		 */
		static final UnitClass getUC(MQTree base, List<UnitClass> list, String value, int type) {
			for (UnitClass e : list) {
				if (isEquals(value, type, e.id, e.name, base.ignoreCase)) return e;
				UnitClass f = getUC(base, e.list, value, type);
				if (f != null) return f;
			}
			return null;
		}

		/**
		 * 得到全树结构节点
		 * @param base MQTree
		 * @return List&lt;UnitClass&gt;
		 */
		static final List<UnitClass> getWholeUnitClassAll(MQTree base) {
			List<UnitClass> list = new ArrayList<>();
			TreeItem[] arr = base.getItems();
			for (TreeItem e : arr) {
				list.add(new UnitClass(e, true));
			}
			return list;
		}

		/**
		 * 通过type判断节点是否与value相同<br>
		 * type:0 按id查<br>
		 * type:1 按名称查[排除id] 全等<br>
		 * type:2 按名称查[排除id] indexof>-1<br>
		 * @param base MQTree
		 * @param e TreeItem
		 * @param value String
		 * @param type int
		 * @return boolean
		 */
		static final boolean isEquals(MQTree base, TreeItem e, String value, int type) {
			if (e == null) return false;
			return isEquals(value, type, getID(e), getName(e), base.ignoreCase);
		}

		/**
		 * 通过type判断节点是否与value相同<br>
		 * type:0 按id查 默认<br>
		 * type:1 按名称查[排除id] 全等<br>
		 * type:2 按名称查[排除id] indexof>-1<br>
		 * ignoreCase为是否忽略大小写
		 * @param value String
		 * @param type int
		 * @param id String
		 * @param name String
		 * @param ignoreCase boolean
		 * @return boolean
		 */
		static final boolean isEquals(String value, int type, String id, String name, boolean ignoreCase) {
			if (value == null) return false;
			switch (type) {
			case 1: {
				if (name == null) return false;
				if (ignoreCase) {/* 忽略大小写 */
					name=name.toLowerCase();
					value=value.toLowerCase();
				}
				if (value.equals(name)) return true;
			}
				break;
			case 2: {
				if (name == null) return false;
				if (ignoreCase) {/* 忽略大小写 */
					name=name.toLowerCase();
					value=value.toLowerCase();
				}
				if (name.indexOf(value) > -1) return true;
			}
				break;
			default:/* 按照id进行查找 */
				if (id == null) return false;
				if (ignoreCase) {/* 忽略大小写 */
					if (value.equalsIgnoreCase(id)) return true;
					return false;
				}
				if (value.equals(id)) return true;
			}
			return false;
		}

		/**
		 * 判断id数组是否包括全部<br>
		 * 如idArrs为空，则返回True<br>
		 * 如idArrs中某个值含有null,则返回True<br>
		 * 否则返回false
		 * @param idArrs String[]
		 * @return boolean
		 */
		static final boolean isFullTree(String... idArrs) {
			if (idArrs.length == 0) return true;
			for (String e : idArrs)
				if (e == null) return true;
			return false;
		}

		static final TreeItem privateNewTreeItem(MQTree base, Tree parent) {
			TreeItem t = new TreeItem(parent, SWT.NONE);
			return t;
		}

		static final TreeItem privateNewTreeItem(MQTree base, TreeItem parent) {
			TreeItem t = new TreeItem(parent, SWT.NONE);
			return t;
		}
		/**
		 * 导入数据值 默认保存memory ID
		 * @param base MQTree
		 * @param e TreeItem
		 * @param id String
		 * @param name String
		 */
		static final void putTreeItemValue(MQTree base, TreeItem e, String id, String name) {
			putTreeItemValue(base,true,e,id,name);
		}
		/**
		 * 导入数据值
		 * @param base MQTree
		 * @param ismemoryid boolean
		 * @param e TreeItem
		 * @param id String
		 * @param name String
		 */
		static final void putTreeItemValue(MQTree base,boolean ismemoryid, TreeItem e, String id, String name) {
			if (base == null || e == null) return;
			if (id == null || id.trim().length() == 0) return;
			if (name == null || name.trim().length() == 0) return;
			id = Utils.arrangeID(id);
			e.setData(id);
			if(ismemoryid)base.memoryid=id;
			name = Utils.arrangeID(name);
			e.setText(base.p_combName(id, name));
		}

		/**
		 * 导入UC中的数据到MQTree中
		 * @param base MQTree
		 * @param e UnitClass
		 * @return boolean
		 */
		static final boolean putUCValue(MQTree base, UnitClass e) {
			if (base == null || e == null) return false;
			String id = base.mkItem(e.targetid, e.id, e.name);
			if (id == null) return false;
			return true;
		}

		/**
		 * 把e导入base中，并记录保存成功的unitclass
		 * @param base MQTree
		 * @param list List&lt;UnitClass&gt;
		 * @param e UnitClass
		 */
		static final void putUnitClass(MQTree base, List<UnitClass> list, UnitClass e) {
			if (base == null || e == null) return;
			String id = base.mkItem(e.targetid, e.id, e.name);
			if (id == null) return;/* 没有找到targetid，则无法插入 */
			list.add(e);
			for (UnitClass f : e.list)
				putUnitClass(base, list, f);
		}

		/**
		 * 搜索子节点，查到节点则放入到list中
		 * @param base MQTree
		 * @param list List&lt;TreeItem&gt;
		 * @param f TreeItem
		 * @param value String
		 * @param type int
		 */
		private static final void searchChildTreeItemList(MQTree base, List<TreeItem> list, TreeItem f, String value, int type) {
			if (f == null) return;
			TreeItem[] arr = f.getItems();
			for (TreeItem e : arr) {
				if (isEquals(base, e, value, type)) list.add(e);
				searchChildTreeItemList(base, list, e, value, type);
			}
		}

		/**
		 * 搜索树型下所有符合条件的子节点
		 * @param base MQTree
		 * @param value String
		 * @param type int
		 * @return List&lt;TreeItem&gt;
		 */
		static final List<TreeItem> searchChildTreeItemList(MQTree base, String value, int type) {
			List<TreeItem> list = new ArrayList<>();
			TreeItem[] arr = base.getItems();
			for (TreeItem e : arr) {
				if (isEquals(base, e, value, type)) list.add(e);
				searchChildTreeItemList(base, list, e, value, type);
			}
			return list;
		}

		/**
		 * 搜索节点下所有符合条件的子节点
		 * @param base MQTree
		 * @param f TreeItem
		 * @param value String
		 * @param type int
		 * @return List&lt;TreeItem&gt;
		 */
		static final List<TreeItem> searchChildTreeItemList(MQTree base, TreeItem f, String value, int type) {
			List<TreeItem> list = new ArrayList<>();
			searchChildTreeItemList(base, list, f, value, type);
			return list;
		}

		/**
		 * 嵌套调用e中查看所有含有value值<br>
		 * type:0 按id查<br>
		 * type:1 按名称查[排除id]<br>
		 * @param base MQTree
		 * @param e TreeItem
		 * @param value String
		 * @param type int
		 * @return list List&lt;TreeItem&gt;
		 */
		static final List<TreeItem> searchTreeItemList(MQTree base, String value, int type) {
			List<TreeItem> list = new ArrayList<>();
			TreeItem[] arr = base.getItems();
			for (TreeItem e : arr) {
				if (isEquals(base, e, value, type)) list.add(e);
				searchTreeItemList(base, e, list, value, type);
			}
			return list;
		}

		/**
		 * 嵌套调用e中查看所有含有value值<br>
		 * type:0 按id查<br>
		 * type:1 按名称查[排除id]<br>
		 * @param base MQTree
		 * @param e TreeItem
		 * @param list List&lt;TreeItem&gt;
		 * @param value String
		 * @param type int
		 */
		private static final void searchTreeItemList(MQTree base, TreeItem e, List<TreeItem> list, String value, int type) {
			if (e == null || value == null) return;
			TreeItem[] arrs = e.getItems();
			for (TreeItem f : arrs) {
				if (isEquals(base, f, value, type)) list.add(e);
				searchTreeItemList(base, f, list, value, type);
			}
		}

		/**
		 * 嵌套调用e中查看所有含有value值<br>
		 * type:0 按id查<br>
		 * type:1 按名称查[排除id]<br>
		 * @param base MQTree
		 * @param e TreeItem
		 * @param value String
		 * @param type int
		 * @return list List&lt;TreeItem&gt;
		 */
		static final List<TreeItem> searchTreeItemList(MQTree base, TreeItem e, String value, int type) {
			List<TreeItem> list = new ArrayList<>();
			searchTreeItemList(base, e, list, value, type);
			return list;
		}

		/**
		 * 把多级节点转成同级节点list
		 * @param base MQTree
		 * @param list List&lt;UnitClass&gt;
		 * @return List&lt;UnitClass&gt;
		 */
		static final List<UnitClass> summUnitClass(MQTree base, List<UnitClass> list) {
			List<UnitClass> uclist = new ArrayList<>();
			for (UnitClass e : list) {
				Utils.summUnitClass(base, uclist, e);
			}
			return uclist;
		}

		/**
		 * 把多级节点转成同级节点list
		 * @param base MQTree
		 * @param e UnitClass
		 * @return List&lt;UnitClass&gt;
		 */
		static final List<UnitClass> summUnitClass(MQTree base, UnitClass e) {
			List<UnitClass> uclist = new ArrayList<>();
			if (e == null) return uclist;
			Utils.summUnitClass(base, uclist, e);
			return uclist;
		}

		/**
		 * 把e中嵌套调出所有节点放在一个list中
		 * @param base MQTree
		 * @param list List&lt;UnitClass&gt;
		 * @param e UnitClass
		 */
		private static final void summUnitClass(MQTree base, List<UnitClass> list, UnitClass e) {
			if (base == null || e == null) return;
			list.add(e);
			for (UnitClass f : e.list)
				summUnitClass(base, list, f);
		}

		/**
		 * step移动数量或为负数，负数为上移,arrs为id数组
		 * @param base MQTree
		 * @param step int
		 * @param idArrs String[]
		 * @return boolean
		 */
		static final boolean changeTreeItem(MQTree base, int step, String... idArrs) {
			boolean ischange = false;
			List<UnitClass> elist = base.toUCListWhole();
			for (String id : idArrs) {
				List<UnitClass> baselist = getUCBaselist(base, elist, id);
				if (baselist.size() == 0) continue;
				int index = getUCListIndex(baselist, id);
				if (index == -1) continue;
				int to = index + step;
				if (!exchange(baselist, index, to)) continue;
				ischange = true;
			}
			if (ischange) base.refreshData(elist);
			return ischange;
		}
		/**
		 * 把多个id进行左右移动，升级或降级
		 * @param base MQTree
		 * @param isLeft boolean
		 * @param idArrs String[]
		 * @return boolean
		 */
		static final boolean changeTreeItem(MQTree base, boolean isLeft, String... idArrs) {
			boolean ischange = false;
			List<UnitClass> elist = base.toUCListWhole();
			for (String id : idArrs) {
				UnitClass f = getUC(base, elist, id);
				if(f==null)continue;
				if(isLeft) {/* 左移，即向上一级提升 */
					if(base.isHeadID(id))continue;/* 头部节点不能左移 */
					String parentid=base.getParentID(id);
					/* 移起源组 */
					List<UnitClass> baselist = getUCBaselist(base, elist, id);
					int index=getUCListIndex(baselist, id);
					baselist.remove(index);
					/* 加入上一级组中 */
					UnitClass parent = getUC(base, elist, parentid);					
					List<UnitClass> tolist = getUCBaselist(base, elist, parentid);
					int pindex=getUCListIndex(tolist, parentid);
					tolist.add(pindex,f);
					f.targetid=parent.targetid;
				}else {
					List<UnitClass> baselist = getUCBaselist(base, elist, id);
					int index = getUCListIndex(baselist, id);
					if (index == -1) continue;
					if(index==0)continue;/* 右移时，如果没有前面的节点，则无效操作 */
					UnitClass first=baselist.get(index-1);
					baselist.remove(index);
					f.targetid=first.id;
					first.list.add(f);
					first.expanded=true;
				}
				ischange = true;
			}
			if (ischange) base.refreshData(elist);
			return ischange;
		}

		/**
		 * 从elist中提取id所在的list
		 * @param base MQTree
		 * @param elist List&lt;UnitClass&gt;
		 * @param id String
		 * @return List&lt;UnitClass&gt;
		 */
		static final List<UnitClass> getUCBaselist(MQTree base, List<UnitClass> elist, String id) {
			UnitClass e = Utils.getUC(base, elist, id);
			if (e == null) return new ArrayList<>();
			String parentid = base.getParentID(id);
			if (parentid == null) return elist;/* 头部节点 */
			/* 节点 */
			UnitClass p = Utils.getUC(base, elist, parentid);
			return p.list;
		}

		/**
		 * 从list查id所在list的下标
		 * @param list List&lt;UnitClass&gt;
		 * @param id String
		 * @return int
		 */
		static final int getUCListIndex(List<UnitClass> list, String id) {
			if (id == null) return -1;
			for (int i = 0, len = list.size(); i < len; i++)
				if (id.equals(list.get(i).id)) return i;
			return -1;
		}

		/**
		 * 交换list中两个下标的位置
		 * @param list List&lt;UnitClass&gt;
		 * @param index int
		 * @param to int
		 * @return boolean
		 */
		static final boolean exchange(List<UnitClass> list, int index, int to) {
			if (index == to) return false;
			int len = list.size();
			if (index < 0 || index >= len) return false;
			if (to < 0 || to >= len) return false;
			UnitClass k = list.get(index);
			list.set(index, list.get(to));
			list.set(to, k);
			return true;
		}

		/**
		 * TreeItem转成字符串，用于输出显示使用
		 * @param f TreeItem
		 * @return String
		 */
		static final String toStringTreeItem(TreeItem f) {
			if (f == null) return "null";
			return f.getText() + "[" + f.getData() + "]";
		}
	}

	private static final String ACC_AutoIDHead = "sj";

	private static final int ACC_AutoIDNumMax = 9999;

	private static final int ACC_styleAlone = SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION;

	private static final int ACC_styleMulti = ACC_styleAlone | SWT.MULTI;

	/** 日志 */
	static final Logger logger = LoggerFactory.getLogger(MQTree.class);

	public static void aaa_inputDefaultData(MQTree mqtree) {
		mqtree.mkAutoItem("两国政治");
		mqtree.mkAutoItem("世界大战");
		mqtree.mkItem("B01", "欧洲人");
		mqtree.mkItem("A01", "A01-12", "中美经济");
		mqtree.mkItem(" B01 ", " B12 ", " 武汉 长江大桥 ");
		mqtree.mkItem("A01-12", " B13 ", " 经济结构 ");
		mqtree.mkItem("A01-12", " B14 ", "埃塞俄比亚");
		mqtree.mkItem("A01-12", " B15 ", "孟加拉");
		mqtree.mkItem("B01", "B01--1", "经济量");
		mqtree.mkItem("B01", "B01--2", "面向世界");
		mqtree.mkItem("B01", "B01--3", "30万亿美元");
		mqtree.mkItem("B01--2", "B01-01", "发展过程");
		mqtree.mkItem("B01--2", "B01-02", "空气污染");
		mqtree.mkItem("B01--2", "B01-03", "农业社会");
		mqtree.mkItem("B01--2", "B01-05", "社会转型");
		mqtree.mkItem("B01--2", "B01-04", "空气污染");

		mqtree.expandedAll();
	}
}
