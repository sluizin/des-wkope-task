package des.wangku.operate.standard.swt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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

import static des.wangku.operate.standard.Constants.UNSAFE;
import static des.wangku.operate.standard.Constants.TreeItemsOffset;

import des.wangku.operate.standard.desktop.LoadTaskUtils;
import des.wangku.operate.standard.dialog.HelpDialog;
import des.wangku.operate.standard.dialog.SearchText;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsArrays;
import des.wangku.operate.standard.utls.UtilsClipboard;
import des.wangku.operate.standard.utls.UtilsSWTMessageBox;
import des.wangku.operate.standard.utls.UtilsSWTTools;
import des.wangku.operate.standard.utls.UtilsSWTTree;
import des.wangku.operate.standard.utls.UtilsString;
import des.wangku.operate.standard.utls.UtilsVerification;

/**
 * 重构Tree UI组件<br>
 * 更改顺序时，因为没有找到强制刷新的方法，则使用输出、修改、输入的方式进行修改
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings({ "restriction", "unchecked" })
public class MultiTree extends Tree {
	/** 父窗口 */
	Composite parent;
	/** 子容器 */
	Shell shell;
	Display display = null;
	/** 本对象 */
	MultiTree base = this;
	/** 是否格式化id 默认不格式化 */
	boolean isFormatID = false;
	/** id最长宽度 */
	int maxIDLen = 0;
	Menu menu = null;
	/** 附加对象，用于特殊携带值 */
	Object additional = null;
	/** 列表排序 如果设置排序，则主列表同级不可修改 */
	boolean sorting = false;
	/** 允许上下移动，默认为不允许上下移动 */
	boolean allowMoveUD = false;
	/** 允许左右移动，默认为不允许左右移动 */
	boolean allowMoveLR = false;

	/**
	 * 构造函数 只允许选择一个项目
	 * @param parent Composite
	 */
	public MultiTree(Composite parent) {
		super(parent, ACC_styleAlone);
		init(parent, SWT.None);
	}

	/**
	 * 构造函数 允许选择多个项目
	 * @param parent Composite
	 * @param style int
	 */
	public MultiTree(Composite parent, int style) {
		super(parent, style | ACC_style);
		init(parent, style);
	}

	/**
	 * 构造函数
	 * @param parent Composite
	 * @param style int
	 * @param isMULTI boolean
	 */
	public MultiTree(Composite parent, int style, boolean isMULTI) {
		super(parent, isMULTI ? (style | ACC_style) : (style | ACC_styleAlone));
		init(parent, style);
	}

	/**
	 * 添加多个字符串
	 * @param arrs int[]
	 * @param textarrs String[]
	 */
	public final void add(int[] arrs, String... textarrs) {
		for (String e : textarrs) {
			add(e, arrs);
		}
	}

	/**
	 * 添加字符串到指定位置
	 * @param text String
	 * @param arrs int[]
	 */
	public final void add(String text, int... arrs) {
		if (text == null) return;
		TreeItem e = getTreeItem(arrs);
		if (e == null) {
			TreeItem t = new TreeItem(this, SWT.NONE);
			privateMKItem(t, text);
			return;
		}
		TreeItem t = new TreeItem(e, SWT.NONE);
		privateMKItem(t, text);
	}

	/**
	 * 添加多个字符串
	 * @param textarrs String[]
	 * @param arrs int[]
	 */
	public final void add(String[] textarrs, int... arrs) {
		add(arrs, textarrs);
	}

	/**
	 * 添加监听器 针对键盘
	 * @return KeyListener
	 */
	private final KeyListener addKeyListener() {
		KeyListener t = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				/* 移除项目 */
				if (e.keyCode == SWT.DEL) {
					MultiTree b = (MultiTree) e.getSource();
					TreeItem[] arrs = b.getSelection();
					for (TreeItem ee : arrs) {
						if (ee == null || ee.isDisposed()) continue;
						ee.removeAll();
						ee.dispose();
					}
				}

				if (e.stateMask == SWT.CTRL && (e.keyCode == 'f' || e.keyCode == 'F')) {
					//InterfaceTablesDialog parent = UtilsSWTTools.getParentInterfaceObj(base, InterfaceTablesDialog.class);
					//parent.setSearchDialog(new SearchDialog(table.getShell(), 0, table));
					//parent.setSearchDialog((new SearchText(shell, 0)).setTextHead("搜索关键字并选中"));
					SearchText st = (new SearchText(shell, 0)).setTextHead("搜索关键字并选中");
					Object obj = st.open();
					if (obj == null) return;
					String value = (String) obj;
					List<TreeItem> list = base.getTreeItemListByChecked(null);
					for (TreeItem ff : list) {
						if (ff.getText().indexOf(value) > -1) {
							ff.setChecked(true);
							expandedParent(ff, true);
							UtilsSWTTree.changeSelectTreeCheck(ff, true);
						}
					}
				}
				if (e.stateMask == SWT.CTRL && (e.keyCode == 'c' || e.keyCode == 'C')) {
					MultiTree b = (MultiTree) e.getSource();
					TreeItem[] arrs = b.getSelection();
					List<UnitClass> list = new ArrayList<>();
					for (TreeItem ff : arrs) {
						list.add(new UnitClass(ff, false));
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
	private final void addListener() {
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
		this.addHelpListener(getHelpListener());
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
		this.addKeyListener(addKeyListener());
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
					logger.info("MultiTree MouseDoubleClickListener working:" + toStringTreeItem(item));
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
	 * 按键盘F1时弹出窗体
	 * @return HelpListener
	 */
	HelpListener getHelpListener() {
		HelpListener t = new HelpListener() {
			@Override
			public void helpRequested(HelpEvent e) {
				String content = "自定义树型控件结构：\n" + "\t允许上下移动\t[allowMoveUD方法]\n" + "\t左右移动\t[allowMoveLR方法]\n" + "\t删除\t[Delete键]\n" + "\t检索关键字并选中\t[Ctrl+f]";
				HelpDialog ver = new HelpDialog(shell, 0, content);
				ver.open();
			}
		};
		return t;
	}

	/**
	 * 添加一个单级项目
	 * @param id String
	 * @param title String
	 * @return TreeItem
	 */
	public final TreeItem addTo(String id, String title) {
		return singleAdd(id, title);
	}

	/**
	 * 添加一个单级项目
	 * @param id String
	 * @param title String
	 * @return TreeItem
	 */
	public final TreeItem addTo(String parentID, String id, String title) {
		TreeItem f = this.getTreeItByID(parentID);
		if (f == null) return null;
		return mkItem(f, id, title, id);
	}

	protected void checkSubclass() {

	}

	/**
	 * 格式化ID<br>
	 * 格式化即把所有id的长度固定，以最长的id串为标准长度，不足处以0为前置
	 * @return MultiTree
	 */
	public final MultiTree formatID() {
		this.isFormatID = false;
		return this;
	}

	/**
	 * 允许左右移动
	 * @return MultiTree
	 */
	public final MultiTree allowMoveLR() {
		this.allowMoveLR = true;
		menuGradediffMove.setEnabled(allowMoveLR);
		return this;
	}

	/**
	 * 允许上下移动
	 * @return MultiTree
	 */
	public final MultiTree allowMoveUD() {
		this.allowMoveUD = true;
		menuGradeSameMove.setEnabled(allowMoveUD);
		return this;
	}

	/**
	 * 得到所有选中的项目的ID
	 * @return String[]
	 */
	public final String[] getArrayIDChecked() {
		List<UnitClass> list = getUCListChecked();
		List<String> relist = list.stream().map(x -> x.id).distinct().collect(Collectors.toList());
		return UtilsArrays.toArray(String.class, relist);
	}

	/**
	 * 得到所有选中的项目的ID，并且含有关键字符串
	 * @param indexStr String
	 * @return String[]
	 */
	public final String[] getArrayIDChecked(String indexStr) {
		if (indexStr == null || indexStr.length() == 0) return getArrayIDChecked();
		List<UnitClass> list = getUCListChecked();
		List<String> relist = list.stream().filter(x -> x.id.indexOf(indexStr) > 0).map(x -> x.id).distinct().collect(Collectors.toList());
		return UtilsArrays.toArray(String.class, relist);
	}

	/**
	 * 得到所有选中的项目的Title
	 * @return String[]
	 */
	public final String[] getArrayTitleChecked() {
		List<UnitClass> list = getUCListChecked();
		List<String> relist = list.stream().map(x -> x.name).distinct().collect(Collectors.toList());
		return UtilsArrays.toArray(String.class, relist);
	}

	/**
	 * 得到所有选中的项目的Value
	 * @return Object[]
	 */
	public final Object[] getArrayValueChecked() {
		List<UnitClass> list = getUCListChecked();
		List<Object> relist = list.stream().filter(Objects::nonNull).filter(x -> Objects.nonNull(x)).map(x -> x).distinct().collect(Collectors.toList());
		return UtilsArrays.toArray(Object.class, relist);
	}

	/**
	 * 得到顺序随机id以sj0001以例<br>
	 * 数字范围: 1--9999之间
	 * @return String
	 */
	public final String getAutoID() {
		for (int i = 1; i < ACC_AutoIDNumMax; i++) {
			String newid = format(i + "", ACC_AutoIDNumLen);
			String id = ACC_AutoIDPrefix + newid;
			if (!isExist(id)) return id;
		}
		return null;
	}

	/**
	 * 自动得到id，先在text中提取[XXXX]的id,如果没有提出，则直接赋与顺序编号sj0001
	 * @param text String
	 * @return String
	 */
	public final String getAutoID(String text) {
		String id = getID(text);
		if (id == null) return getAutoID();
		return null;
	}

	/**
	 * 得到同级的所有TreeItem
	 * @param a TreeItem
	 * @return TreeItem[]
	 */
	public final TreeItem[] getBrothers(TreeItem a) {
		TreeItem[] arr = {};
		if (a == null) return arr;
		boolean isFirst = a.getParentItem() == null;
		if (isFirst) {
			return a.getParent().getItems();
		} else {
			return a.getParentItem().getItems();
		}
	}

	/**
	 * 得到TreeItem所在的下标
	 * @param a TreeItem
	 * @return int
	 */
	public final int getBrothersIndex(TreeItem a) {
		if (a == null) return -1;
		TreeItem[] arrs = getBrothers(a);
		for (int i = 0, len = arrs.length; i < len; i++)
			if (arrs[i].equals(a)) return i;
		return -1;
	}

	/**
	 * 得到同级某个下标的 TreeItem
	 * @param a TreeItem
	 * @param index int
	 * @return TreeItem
	 */
	public final TreeItem getBrotherTreeItem(TreeItem a, int index) {
		if (a == null || index < 0) return null;
		TreeItem[] arrs = getBrothers(a);
		if (index >= arrs.length) return null;
		return arrs[index];
	}

	/**
	 * 得到选中的词条数组
	 * @param filterArr int[]
	 * @return List&lt;TreeItem&gt;
	 */
	public final List<TreeItem> getCheckedList(int... filterArr) {
		TreeItem[] arrs = getItems();
		DeepClass dc = new DeepClass(0);
		for (TreeItem e : arrs) {
			if (UtilsArrays.isfilterArr(0, filterArr) && e.getChecked()) {
				dc.list.add(e);
			}
			getTreeItemCheckedList(dc, 0, e, filterArr);
		}
		return dc.list;
	}

	/**
	 * 得到所有选中的项目
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<TreeItem> getCheckedTreeItemList() {
		return getTreeItemListByChecked(true);
	}

	/**
	 * 得到选择的ID list集
	 * @return List&lt;String&gt;
	 */
	public final List<Object> getDataList() {
		List<TreeItem> selectList = getCheckedTreeItemList();
		List<Object> dataList = new ArrayList<>();
		for (TreeItem e : selectList) {
			Object obj = e.getData();
			if (obj == null) continue;
			dataList.add(obj);
		}
		return dataList;
	}

	/**
	 * Tree的最深度 -1:没有深度。0:一级深度
	 * @return int
	 */
	public final int getDeepAll() {
		TreeItem[] arss = getItems();
		if (arss.length == 0) return -1;
		int deepCount = 0;
		for (TreeItem e : arss) {
			int deep = getDeepAll(e);
			if (deep > deepCount) deepCount = deep;
		}
		return deepCount;
	}

	/**
	 * 得到某个栏目的深度 0即没有深度
	 * @param e TreeItem
	 * @return int
	 */
	public final int getDeepAll(TreeItem e) {
		if (e == null) return 0;
		DeepClass dc = new DeepClass(1);
		getDeepsAllPrivate(dc, 0, e);
		return dc.deep;
	}

	/**
	 * 得到选择的ID list集
	 * @return List&lt;String&gt;
	 */
	@Deprecated
	public final List<String> getIDList() {
		List<TreeItem> selectList = getCheckedTreeItemList();
		List<String> idList = new ArrayList<>();
		for (TreeItem e : selectList) {
			String id = getID(e.getText());
			if (id == null) continue;
			idList.add(id);
		}
		return idList;
	}

	/**
	 * 得到选择的ID list集
	 * @return List&lt;String&gt;
	 */
	@Deprecated
	public final List<String> getIDListUnitClass() {
		List<UnitClass> selectList = getUCListChecked();
		List<String> idList = new ArrayList<>();
		for (UnitClass e : selectList) {
			if (e.id == null) continue;
			idList.add(e.id);
		}
		return idList;
	}

	/**
	 * 得到选择的ID list集
	 * @return List&lt;String&gt;
	 */
	public final List<String> getIDsList() {
		List<Object> list = getDataList();
		List<String> idList = new ArrayList<>(list.size());
		for (Object e : list) {
			if (e == null) continue;
			idList.add(e.toString());
		}
		return idList;
	}

	/**
	 * @param arrs TreeItem[]
	 * @param index int
	 * @return TreeItem
	 */
	public final TreeItem getItems(TreeItem[] arrs, int index) {
		int len = arrs.length;
		if (len == 0) return null;
		if (index < 0 || index >= len) return null;
		return arrs[index];
	}

	/**
	 * 移动选中的sheet
	 * @param isUP boolean
	 * @return Listener
	 */
	private final Listener getListenerControlMove(boolean isUP) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				if (sorting) {
					UtilsSWTMessageBox.Alert(shell, "移位操作失败", "已设置为排序功能，则按编号进行排序");
					return;
				}
				TreeItem[] arrs = base.getSelection();
				List<String> list = getTreeItemID(arrs);
				TreeItemChangeUp(isUP, list);
			}
		};
		return t;
	}

	/**
	 * 移动选中的sheet
	 * @param isLeft boolean
	 * @return Listener
	 */
	final Listener getListenerControlMoveLeft(boolean isLeft) {
		Listener t = new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] arrs = base.getSelection();
				List<String> list = getTreeItemID(arrs);
				TreeItemChangeLeft(isLeft, list);
			}
		};
		return t;
	}

	/**
	 * 按顺序查找id所在的深度。多级深度展示 输出ID
	 * @param id String
	 * @return String[]
	 */
	public final String[] getOrderDeepID(String id) {
		List<TreeItem> list = getOrderDeepListByID(id);
		List<String> relist = list.stream().map(x -> getID(x)).distinct().collect(Collectors.toList());
		return relist.toArray(new String[relist.size()]);
	}

	/**
	 * 按顺序查找id所在的深度，多级深度展示
	 * @param id String
	 * @return List&lt;TreeItem&gt;
	 */
	public final List<TreeItem> getOrderDeepListByID(String id) {
		List<TreeItem> list = new ArrayList<>();
		TreeItem target = getTreeItByID(id);
		if (target == null) return list;
		privateReverseParentDeep(list, target);
		return list;
	}

	/**
	 * 按顺序查找id所在的深度。多级深度展示 输出title
	 * @param id String
	 * @return String[]
	 */
	public final String[] getOrderDeepTitle(String id) {
		List<TreeItem> list = getOrderDeepListByID(id);
		List<String> relist = list.stream().map(x -> getTitle(x)).distinct().collect(Collectors.toList());
		return relist.toArray(new String[relist.size()]);
	}

	/**
	 * 按顺序查找id所在的深度。多级深度展示 输出value
	 * @param id String
	 * @return String[]
	 */
	public final String[] getOrderDeepValue(String id) {
		List<TreeItem> list = getOrderDeepListByID(id);
		List<String> relist = list.stream().filter(Objects::nonNull).filter(x -> Objects.nonNull(x.getData())).map(x -> x.getData().toString()).distinct().collect(Collectors.toList());
		return relist.toArray(new String[relist.size()]);
	}

	/**
	 * 按照等级数字串得到对象
	 * @param arrs int[]
	 * @return TreeItem
	 */
	public final TreeItem getTreeItem(int... arrs) {
		if (arrs.length == 0) return null;
		if (arrs.length == 1) {
			if (arrs[0] < 0) return null;
			return getItems(getItems(), arrs[0]);
		}
		TreeItem f = this.getItem(arrs[0]);
		TreeItem e = null;
		for (int i = 1; i < arrs.length; i++) {
			e = getItems(f.getItems(), arrs[i]);
			if (e == null) return null;
			f = e;
		}
		return e;
	}

	/**
	 * 按id查找 TreeItem
	 * @param id String
	 * @return TreeItem
	 */
	public final TreeItem getTreeItByID(String id) {
		return getTreeIt(id, 0);
	}

	/**
	 * 在树中查看是否含有value值<br>
	 * type:0 按id查<br>
	 * type:1 按名称查[排队id]<br>
	 * type:2 按value查<br>
	 * @param value String
	 * @param type int
	 * @return TreeItem
	 */
	public final TreeItem getTreeIt(String value, int type) {
		if (value == null) return null;
		TreeItem[] arrs = this.getItems();
		for (TreeItem e : arrs) {
			TreeItem f = getSearchTreeItem(e, value, type);
			if (f != null) return f;
		}
		return null;
	}

	/**
	 * 把TreeItem数组中所有的id提出
	 * @param arrs TreeItem[]
	 * @return List&lt;String&gt;
	 */
	public List<String> getTreeItemID(TreeItem... arrs) {
		List<String> list = new ArrayList<>(arrs.length);
		for (TreeItem e : arrs) {
			String id = getID(e);
			if (id == null) continue;
			if (list.contains(id)) continue;
			list.add(id);
		}
		return list;
	}

	/**
	 * 得到TreeItem所在Tree中的下标
	 * @param e TreeItem
	 * @return int
	 */
	int getTreeItemInsideIndex(TreeItem e) {
		if (e == null) return -1;
		Object obj = UNSAFE.getObject(base, TreeItemsOffset);
		TreeItem[] item = (TreeItem[]) obj;
		for (int i = 0, len = item.length; i < len; i++) {
			TreeItem f = item[i];
			if (f == null) continue;
			if (f.equals(e)) return i;
		}
		return -1;
	}

	/**
	 * 得到选中的词条数组
	 * @param filterArr int[]
	 * @return List&lt;TreeItem&gt;
	 */
	@Deprecated
	public final List<TreeItem> getTreeItemList(int... filterArr) {
		TreeItem[] arrs = getItems();
		DeepClass dc = new DeepClass(0);
		for (TreeItem e : arrs) {
			if (UtilsArrays.isfilterArr(0, filterArr)) {
				dc.list.add(e);
			}
			getTreeItemList(dc, 0, e, filterArr);
		}
		return dc.list;
	}

	/**
	 * 得取树型的对象集 所有对象
	 * @param isChecked Boolean
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<TreeItem> getTreeItemListByChecked(Boolean isChecked) {
		List<TreeItem> list = new ArrayList<>();
		TreeItem[] arrs = getItems();
		for (TreeItem e : arrs) {
			if (isChecked == null || e.getChecked() == isChecked) list.add(e);
			treeItemList(list, e, isChecked);
		}
		return list;
	}

	/**
	 * 得取树型的对象集 指定某个对象
	 * @param e TreeItem
	 * @return list&lt;TreeItem&gt;
	 */
	public final List<TreeItem> getTreeItemListByTreeItem(TreeItem e) {
		List<TreeItem> list = new ArrayList<>();
		treeItemList(list, e, null);
		return list;
	}

	/**
	 * 得到指定级数位置的标签内容
	 * @param arrs int[]
	 * @return String
	 */
	public final String getTreeItemString(int... arrs) {
		TreeItem e = getTreeItem(arrs);
		if (e == null) return null;
		return e.getText();
	}

	/**
	 * 把TreeItem数组中所有的id提出
	 * @param arrs TreeItem[]
	 * @return List&lt;String&gt;
	 */
	public List<String> getTreeItemTitle(TreeItem... arrs) {
		List<String> list = new ArrayList<>(arrs.length);
		for (TreeItem e : arrs) {
			String id = getTitle(e);
			if (id == null) continue;
			if (list.contains(id)) continue;
			list.add(id);
		}
		return list;
	}

	/**
	 * 通过id得到单元
	 * @param id String
	 * @return UnitClass
	 */
	public final UnitClass getUCByID(String id) {
		if (id == null) return null;
		List<UnitClass> sourceList = getUCListAll();
		id = id.trim();
		for (UnitClass e : sourceList) {
			if (e.id.equals(id)) return e;
		}
		return null;
	}

	/**
	 * 结点总数
	 * @return int
	 */
	public final int getUCCount() {
		List<UnitClass> list = getUCListAll();
		return list.size();
	}

	/**
	 * 得到所有单元列表，没有深度 但子列表存在
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<UnitClass> getUCListAll() {
		List<UnitClass> sourceList = getUCWholeList();
		List<UnitClass> list = new ArrayList<>();
		for (UnitClass e : sourceList) {
			list.add(e);
			privateGetUCList(list, e);
		}
		return list;
	}

	/**
	 * 得到所有选中的项目
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<UnitClass> getUCListChecked() {
		List<UnitClass> sourceList = getUCListAll();
		List<UnitClass> list = new ArrayList<>(sourceList.size());
		for (UnitClass e : sourceList) {
			if (e.checked) list.add(e);
		}
		return list;
	}

	/**
	 * 得到某个id的父节点
	 * @param id String
	 * @return UnitClass
	 */
	public final UnitClass getUCParentByID(String id) {
		if (!isExist(id)) return null;
		UnitClass t = getUCByID(id);
		String parentID = t.getTargetid();
		if (parentID == null || parentID.trim().length() == 0) return null;
		return getUCByID(parentID);
	}

	/**
	 * 得取树型的对象集 完整的数据树
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<UnitClass> getUCWholeList() {
		List<UnitClass> list = new ArrayList<>();
		TreeItem[] arrs = getItems();
		for (TreeItem e : arrs) {
			list.add(new UnitClass(e));
		}
		return list;
	}

	/**
	 * 初始化
	 * @param parent Composite
	 * @param style int
	 */
	void init(Composite parent, int style) {
		checkSubclass();
		this.parent = parent;
		this.shell = new Shell(base.getShell());
		shell.setText("检索关键字");
		this.display = parent.getDisplay();
		addListener();
		menu = new Menu(this);
		setMenu(menu);
		setMoveMenuItem();
	}

	/**
	 * 从MultiTree查找编号id，是否存在
	 * @param id String
	 * @return boolean
	 */
	public final boolean isExist(String id) {
		if (id == null || id.trim().length() == 0) return false;
		TreeItem[] arrs = this.getItems();
		for (TreeItem e : arrs) {
			if (isExist(id, e)) return true;
		}
		return false;
	}

	/**
	 * 批量导入单元 列表中允许乱序
	 * @param list List&lt;UnitClass&gt;
	 * @return int
	 */
	public final int mkItem(List<UnitClass> list2) {
		/* list按targetid从大到小排序 另生成新的list，不影响源list */
		//List<UnitClass> list2 = list.stream().sorted(Comparator.comparing(UnitClass::getTargetid).reversed()).collect(Collectors.toList());
		int count = 0;
		/* 先试着添加没有target的节点，即无父类的节点 */
		for (int i = list2.size() - 1; i >= 0; i--) {
			UnitClass e = list2.get(i);
			String targetid = e.getTargetid();
			//System.out.println("singleAdd:" + e.id + "\t" + (targetid == null ? "null" : targetid));
			if ((targetid != null && targetid.trim().length() > 0)) continue;
			TreeItem f = mkItem(e);
			if (f == null) continue;
			count++;
			list2.remove(i);
		}
		/* 添加有上级的节点 如果排序为乱序，要求多级检索 */
		int size = list2.size();
		loop: for (int p = 0; p < size; p++) {
			for (int i = list2.size() - 1; i >= 0; i--) {
				UnitClass e = list2.get(i);
				String targetid = e.getTargetid();
				if (!isExist(targetid)) continue;
				TreeItem f = mkItem(e);
				if (f == null) continue;
				count++;
				list2.remove(i);
				if (list2.size() == 0) break loop;
				continue loop;
			}
			break;
		}
		return count;
	}

	/**
	 * 建立item text:[XXXX]ABCD
	 * @param text String
	 * @return TreeItem
	 */
	public final TreeItem mkItem(String text) {
		String id = getID(text);
		String title = getTitle(text);
		return mkItem(id, title);
	}

	/**
	 * 建立item
	 * @param id String
	 * @param name String
	 * @return TreeItem
	 */
	public final TreeItem mkItem(String id, String name) {
		if (id == null) return null;
		if (name == null) return null;
		id = id.trim();
		name = name.trim();
		if (isExist(id)) return null;
		return mkItem(id, name, false, false, false);
	}

	/**
	 * 建立item
	 * @param id String
	 * @param name String
	 * @param isCheck boolean
	 * @param isExpanded boolean
	 * @param isGrayed boolean
	 * @return TreeItem
	 */
	public final TreeItem mkItem(String id, String name, boolean isCheck, boolean isExpanded, boolean isGrayed) {
		return privateMKItem(null, id, name, isCheck, isExpanded, isGrayed);
	}

	/**
	 * 建立item
	 * @param parent TreeItem
	 * @param title String
	 * @return TreeItem
	 */
	public final TreeItem mkItem(TreeItem parent, String title) {
		return mkItem(parent, title, false, false);
	}

	/**
	 * 建立item
	 * @param parent TreeItem
	 * @param text String
	 * @param isCheck boolean
	 * @param isExpanded boolean
	 * @return TreeItem
	 */
	public final TreeItem mkItem(TreeItem parent, String text, boolean isCheck, boolean isExpanded) {
		if (text == null) return null;
		String id = getID(text);
		if (isExist(id)) return null;
		String title = getTitle(text);
		return mkItem(parent, id, title, isCheck, isExpanded, false);
	}

	/**
	 * 建立item
	 * @param parent TreeItem
	 * @param id String
	 * @param name String
	 * @param isCheck boolean
	 * @param isExpanded boolean
	 * @return TreeItem
	 */
	public final TreeItem mkItem(TreeItem parent, String id, String name, boolean isCheck, boolean isExpanded) {
		return mkItem(parent, id, name, false, false, false);
	}

	/**
	 * 建立item
	 * @param parent TreeItem
	 * @param id String
	 * @param name String
	 * @param isCheck boolean
	 * @param isExpanded boolean
	 * @param isGrayed boolean
	 * @return TreeItem
	 */
	public final TreeItem mkItem(TreeItem parent, String id, String name, boolean isCheck, boolean isExpanded, boolean isGrayed) {
		return privateMKItem(parent, id, name, isCheck, isExpanded, isGrayed);
	}

	/**
	 * 建立item
	 * @param parent TreeItem
	 * @param id String
	 * @param name String
	 * @param value String
	 * @return TreeItem
	 */
	public final TreeItem mkItem(TreeItem parent, String id, String name, String value) {
		return mkItem(parent, id, name, false, false, false);
	}

	/**
	 * 建立item<br>
	 * 如果parent为null,则添加顶级单元
	 * @param e UnitClass
	 * @return TreeItem
	 */
	public final TreeItem mkItem(UnitClass e) {
		if (e == null) return null;
		String targetid = e.getTargetid();
		if (targetid == null || targetid.trim().length() == 0) return singleAdd(e);
		TreeItem f = this.getTreeItByID(targetid);
		if (f == null) return singleAdd(e);
		return mkItem(f, e.id, e.name, e.checked, e.expanded, e.grayed);
	}

	/**
	 * 通过id值查找name值
	 * @param id String
	 * @return String
	 */
	public final String getNameByID(String id) {
		if (id == null || id.length() == 0) return null;
		TreeItem e = this.getTreeItByID(id);
		if (e == null) return null;
		return getTitle(e);
	}

	/**
	 * 添加TreeItem的内容 text为[XXXX]AAAAA
	 * @param t TreeItem
	 * @param text title
	 */
	private final void privateMKItem(TreeItem t, String text) {
		String id = getID(text);
		if (id == null) return;
		String title = getTitle(text);//title.replaceAll("\\[" + id + "\\]", "");
		if (title == null) return;
		privateMKItem(t, id, title);
	}

	/**
	 * 添加TreeItem的内容 text为[XXXX]AAAAA
	 * @param t TreeItem
	 * @param id String
	 * @param name String
	 */
	private final void privateMKItem(TreeItem t, String id, String name) {
		privateMKItem(t, id, name, new DataClass());
	}

	/**
	 * 添加新的节点 parent为null时，则添加新的主节点
	 * @param parent TreeItem
	 * @param id String
	 * @param name String
	 * @param isCheck boolean
	 * @param isExpanded boolean
	 * @param isGrayed boolean
	 * @return TreeItem
	 */
	private final TreeItem privateMKItem(TreeItem parent, String id, String name, boolean isCheck, boolean isExpanded, boolean isGrayed) {
		if (id == null || name == null) return null;
		id = id.trim();
		name = name.trim();
		if (id.length() == 0 || name.length() == 0) return null;
		if (isExist(id)) return null;
		TreeItem t = parent == null ? new TreeItem(base, SWT.NONE) : new TreeItem(parent, SWT.NONE);
		privateMKItem(t, id, name);
		t.setChecked(isCheck);
		t.setExpanded(isExpanded);
		t.setGrayed(isGrayed);
		return t;
	}

	/**
	 * 添加TreeItem的内容 text为[XXXX]AAAAA
	 * @param t TreeItem
	 * @param id String
	 * @param name String
	 */
	private final void privateMKItem(TreeItem t, String id, String name, DataClass dc) {
		if (id == null) return;
		if (name == null) return;
		String textAll = getTagTitle(id, name);
		privateMKItem(t, textAll, dc);
	}

	/**
	 * 通过项目中从包外读取项目json文件中提出字符串并导入数据<br>
	 * json文件名:des-wkope-task-XXXX.json
	 * @param task AbstractTask
	 * @param jsonkey String
	 */
	public final void putAbstractTaskJson(AbstractTask task, String jsonkey) {
		if (task == null || jsonkey == null) return;
		putUCWhole(task.getProJsonValue(jsonkey));
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
	 * 导入二级Item
	 * @param map Map&lt;String,List&lt;String&gt;&gt;
	 */
	public final void putItemLevel2(Map<String, List<String>> map) {
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			String key = entry.getKey();
			List<String> list = entry.getValue();
			TreeItem[] arrs = singleAutoAddArrays(key);
			if (arrs.length == 0) continue;
			TreeItem parent = arrs[0];
			for (String e : list) {
				singleAutoAdd(parent, e);
			}
		}
	}

	/**
	 * 导入单独单元，按targetid,自动寻找父节点
	 * @param json String
	 */
	public final void putUCJson(String json) {
		if (json == null || json.length() == 0) return;
		UnitClass e = JSON.parseObject(json, UnitClass.class);
		if (e == null) return;
		mkItem(e);
	}

	/**
	 * 导入多个对象节点，允许乱序
	 * @param json String
	 */
	public final void putUCJsonArray(String json) {
		if (json == null || json.length() == 0) return;
		List<UnitClass> list = JSON.parseArray(json, UnitClass.class);
		if (list == null || list.size() == 0) return;
		mkItem(list);
	}

	/**
	 * 通过UnitClass 数组导入数据 完整导入
	 * @param list List&lt;UnitClass&gt;
	 */
	public final void putUCWhole(List<UnitClass> list) {
		if (isFormatID) maxIDLen = maxIDLen(list, 0);
		if (sorting) Collections.sort(list);
		for (UnitClass e : list) {
			TreeItem t = e.toTreeItem(base, this);
			if (t == null) continue;
			if (sorting) Collections.sort(e.list);
			for (UnitClass f : e.list) {
				putUCWhole(f, t);
			}
			setAttrib(t, e);
		}
	}

	/**
	 * 通过json字符串导入数据 完整导入
	 * @param json String
	 */
	public final void putUCWhole(String json) {
		if (json == null || json.length() == 0) return;
		List<UnitClass> list = JSON.parseArray(json, UnitClass.class);
		putUCWhole(list);
	}

	/**
	 * 按等级导入标签单元 完整导入
	 * @param e UnitClass
	 * @param t TreeItem
	 */
	public final void putUCWhole(UnitClass e, TreeItem t) {
		if (e == null || t == null) return;
		TreeItem ti = e.toTreeItem(this, t);//mkItem(t, e.getNameAll(), e.isCheck);
		if (ti == null) return;
		if (sorting) Collections.sort(e.list);
		for (UnitClass f : e.list) {
			putUCWhole(f, ti);
		}
		setAttrib(ti, e);
	}

	/**
	 * 导入数据，未重组数据
	 * @param list List&lt;UnitClass&gt;
	 */
	public final void putWhole(List<UnitClass> list) {
		UnitClass result = new UnitClass();
		recombination(list, result, sorting);
		putUCWhole(result.list);
	}

	/**
	 * 导入数据
	 * @param rs ResultSet
	 */
	public final void putWhole(ResultSet rs) {
		if (rs == null) return;
		List<UnitClass> list = resultSetChange(rs);
		putWhole(list);
	}

	/**
	 * 强制刷新，用于树型建立后，再添加节点，则会发生编号混乱<br>
	 * 如果设置了排序功能，则进行二次排序<br>
	 * 防止每次添加节点时都要刷新一下树型
	 */
	public final void refresh() {
		List<UnitClass> elist = getUCWholeList();
		this.removeAll();
		this.putUCWhole(elist);
	}

	/**
	 * 把UnitClass中的属性赋予给TreeItem
	 * @param t TreeItem
	 * @param e UnitClass
	 */
	final void setAttrib(TreeItem t, UnitClass e) {
		if (t == null || e == null) return;
		t.setChecked(e.checked);
		t.setExpanded(e.expanded);
		t.setGrayed(e.grayed);
		//Image image=SWTResourceManager.getImage("G:\\zg99114com\\zt.9911.com\\dpt\\app_v2.0\\images\\weixin_icon.jpg");
		//System.out.println("image:"+image.toString());
		//t.setImage(image);
		setTreeItemListener(t, e);
	}

	/**
	 * 设置向上向下向左向右移动的功能菜单
	 */
	final void setMoveMenuItem() {
		setMoveMenuItemUp();
		setMoveMenuItemLeft();
	}

	/** 异级移动 */
	MenuItem menuGradediffMove = null;
	/** 同级移动 */
	MenuItem menuGradeSameMove = null;

	/**
	 * 左右移动右键菜单
	 */
	final void setMoveMenuItemLeft() {
		menuGradediffMove = new MenuItem(menu, SWT.CASCADE);
		menuGradediffMove.setEnabled(allowMoveLR);
		menuGradediffMove.setText("异级移动");
		Menu b = new Menu(this.getShell(), SWT.DROP_DOWN);
		menuGradediffMove.setMenu(b);
		MenuItem c = new MenuItem(b, SWT.NONE);
		c.setText("左移");
		c.addListener(SWT.Selection, getListenerControlMoveLeft(true));
		MenuItem d = new MenuItem(b, SWT.NONE);
		d.setText("右移");
		d.addListener(SWT.Selection, getListenerControlMoveLeft(false));
	}

	/**
	 * 上下移动右键菜单
	 */
	final void setMoveMenuItemUp() {
		menuGradeSameMove = new MenuItem(menu, SWT.CASCADE);
		menuGradeSameMove.setText("同级移动");
		menuGradeSameMove.setEnabled(allowMoveUD);
		Menu b = new Menu(this.getShell(), SWT.DROP_DOWN);
		menuGradeSameMove.setMenu(b);
		MenuItem c = new MenuItem(b, SWT.NONE);
		c.setText("上移");
		c.addListener(SWT.Selection, getListenerControlMove(true));
		MenuItem d = new MenuItem(b, SWT.NONE);
		d.setText("下移");
		d.addListener(SWT.Selection, getListenerControlMove(false));
	}

	/**
	 * 向TreeItem添加监听器
	 * @param t TreeItem
	 * @param e UnitClass
	 */
	final void setTreeItemListener(TreeItem t, UnitClass e) {
		if (t == null || e == null) return;
		t.addListener(SWT.FocusOut, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				if (item == null) return;
				System.out.println("setTreeItemListener:" + item.getText());
			}
		});
	}

	/**
	 * 添加一个单级项目
	 * @param id String
	 * @param title String
	 * @return TreeItem
	 */
	public final TreeItem singleAdd(String id, String title) {
		return mkItem(id, title);
	}

	/**
	 * 添加一个单级项目
	 * @param e UnitClass
	 * @return TreeItem
	 */
	public final TreeItem singleAdd(UnitClass e) {
		TreeItem f = singleAdd(e.id, e.name);
		setAttrib(f, e);
		return f;
	}

	/**
	 * 添加多个单级字符串，如果没有查到id 样式为[XX]xxx格式，则不添加
	 * @param arrs String[]
	 * @return List&lt;TreeItem&gt;
	 */
	public final List<TreeItem> singleAddArrs(String... arrs) {
		List<TreeItem> list = new ArrayList<>();
		for (String e : arrs) {
			TreeItem f = mkItem(e);
			if (f != null) list.add(f);
		}
		return list;
	}

	/**
	 * 添加多个单级字符串<br>
	 * id号优先级：自带id([id]xxx) &gt;自动随机编号 &gt;text
	 * @param arrs String[]
	 * @return TreeItem[]
	 */
	public final TreeItem[] singleAutoAddArrays(String... arrs) {
		List<TreeItem> list = new ArrayList<>();
		for (String e : arrs) {
			TreeItem f = singleAutoAddOne(e);
			if (f != null) list.add(f);
		}
		return UtilsArrays.toArray(TreeItem.class, list);
	}
	/**
	 * 添加多个节点，自动编号
	 * @param parent TreeItem
	 * @param arrs String[]
	 * @return TreeItem[]
	 */
	public final TreeItem[] singleAutoAdd(TreeItem parent, String... arrs) {
		List<TreeItem> list = new ArrayList<>();
		for (String title : arrs) {
			String id = this.getAutoID();
			TreeItem f = mkItem(parent, id, title, id);
			if (f != null) list.add(f);
		}
		return UtilsArrays.toArray(TreeItem.class, list);
	}
	/**
	 * 给某个节点添加下属节点
	 * @param parentid String
	 * @param arrs String[]
	 * @return TreeItem[]
	 */
	public final TreeItem[] singleAutoAdd(String parentid, String... arrs) {
		TreeItem e = this.getTreeItByID(parentid);
		return singleAutoAdd(e, arrs);
	}

	/**
	 * 添加单级字符串<br>
	 * id号优先级：自带id([id]xxx) &gt;自动随机编号 &gt;text
	 * @param text String
	 * @return TreeItem
	 */
	public final TreeItem singleAutoAddOne(String text) {
		if (text == null) return null;
		String id = getAutoID(text);
		if (id == null) return null;
		String title = getTitle(text);
		if (title == null) title = text;
		return mkItem(id, title);
	}

	/**
	 * 进行排序，从小到大，按id，同级进行排序<br>
	 * 树型节点将不可移动
	 * @return MultiTree
	 */
	public final MultiTree sorting() {
		this.sorting = true;
		return this;
	}

	/**
	 * 输出json 单元列表输出
	 * @return String
	 */
	public final String toJson() {
		List<UnitClass> list = getUCListAll();
		return JSON.toJSONString(list);
	}

	/**
	 * 把TreeItem以下的所有节点输出成json串，不含子类
	 * @param e TreeItem
	 * @return String
	 */
	public final String toJson(TreeItem e) {
		if (e == null) return null;
		List<UnitClass> list = toUCList(e);
		return JSON.toJSONString(list);
	}

	/**
	 * 输出json 完整输出
	 * @return String
	 */
	public final String toJsonWhole() {
		List<UnitClass> list = getUCWholeList();
		return JSON.toJSONString(list);
	}

	/**
	 * 获取TreeItem下所有的UnitClass单元，包括自己，不含子类
	 * @param e TreeItem
	 * @return List&lt;UnitClass&gt;
	 */
	public final List<UnitClass> toUCList(TreeItem e) {
		if (e == null) return new ArrayList<>();
		List<TreeItem> list = getTreeItemListByTreeItem(e);
		List<UnitClass> list2 = list.stream().map(x -> new UnitClass(x, false)).collect(Collectors.toList());
		return list2;
	}

	/**
	 * 按id查找位置，并依次左移[升一级]或右移一位[降一级]
	 * @param isLeft boolean
	 * @param list List&lt;String&gt;
	 * @return boolean
	 */
	public final boolean TreeItemChangeLeft(boolean isLeft, List<String> list) {
		String arrs[] = list.toArray((new String[list.size()]));
		return TreeItemChangeLeft(isLeft, arrs);
	}

	/**
	 * 按id查找位置，并依次左移[升一级]或右移一位[降一级]
	 * @param isLeft boolean
	 * @param idArrs String[]
	 * @return boolean
	 */
	public final boolean TreeItemChangeLeft(boolean isLeft, String... idArrs) {
		List<UnitClass> elist = getUCWholeList();
		boolean ischange = false;/* 是否进行了修改 */
		ischange = TreeItemChangeLeft(elist, isLeft, idArrs);
		if (!ischange) return false;
		this.removeAll();
		this.putUCWhole(elist);
		return true;
	}

	/**
	 * 按id查找位置，并依次上移或下移一位
	 * @param isUP boolean
	 * @param list List&lt;String&gt;
	 * @return boolean
	 */
	public final boolean TreeItemChangeUp(boolean isUP, List<String> list) {
		String arrs[] = list.toArray((new String[list.size()]));
		return TreeItemChangeUp(isUP, arrs);
	}

	/**
	 * 按id查找位置，并依次同级上移或下移一位
	 * @param isUP boolean
	 * @param idArrs String[]
	 * @return boolean
	 */
	public final boolean TreeItemChangeUp(boolean isUP, String... idArrs) {
		List<UnitClass> elist = getUCWholeList();
		boolean ischange = false;/* 是否进行了修改 */
		ischange = TreeItemChangeUp(elist, isUP ? -1 : 1, idArrs);
		if (!ischange) return false;
		this.removeAll();
		this.putUCWhole(elist);
		/*
		 * 想办法设置所有id的栏目获得焦点
		 * for(String id:idArrs) {
		 * TreeItem e=this.getTreeItemByID(id);
		 * if(e==null)continue;
		 * }
		 */
		return true;
	}

	/**
	 * 第一参数 为总记录，step移动数量或为负数，负数为上移,arrs为id数组
	 * @param elist List&lt;String&gt;
	 * @param step int
	 * @param idArrs String[]
	 * @return boolean
	 */

	public final boolean TreeItemChangeUp(List<UnitClass> elist, int step, String... idArrs) {
		boolean ischange = false;
		for (String id : idArrs) {
			UnitClass f = UnitClass.getUCSearch(elist, id, 0);/* 查找到当前的单元位置 */
			if (f == null) continue;
			List<UnitClass> findlist = UnitClass.getUCParentBaseList(elist, f);
			int len = findlist.size();
			if (len == 0) continue;
			int index = UnitClass.getIndex(findlist, f);
			if (index == -1) continue;
			int to = index + step;
			if (!exchange(findlist, index, to)) continue;
			ischange = true;
		}
		return ischange;
	}

	/**
	 * TreeItem中data所携带的值
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class DataClass {
		UnitClass uc = new UnitClass();

		public DataClass() {

		}

		public DataClass(UnitClass uc) {
			this.uc = uc;
		}

		@Override
		public String toString() {
			return "DataClass [uc=" + uc + "]";
		}

	}

	/**
	 * 另类对象
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	private static final class DeepClass {
		int deep = 0;
		List<TreeItem> list = new ArrayList<>();

		public DeepClass(int deep) {
			this.deep = deep;
		}
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
		/** 名称 */
		String name = null;
		/** 关联字段 */
		String targetid = null;
		/** 下级子列 */
		List<UnitClass> list = new ArrayList<>();

		public UnitClass() {

		}

		/**
		 * id,name,targetid
		 * @param arrs Object[]
		 */
		public UnitClass(Object... arrs) {
			if (arrs.length < 4) return;
			if (arrs[0] != null) id = arrs[0].toString();
			if (arrs[1] != null) name = arrs[1].toString();
			if (arrs[2] != null) targetid = arrs[3].toString();
		}

		/**
		 * 构造函数
		 * @param name String
		 */
		public UnitClass(String name) {
			this.name = name;
		}

		/**
		 * 构造函数
		 * @param id String
		 * @param name String
		 * @param targetid String
		 */
		public UnitClass(String id, String name, String targetid) {
			this(id, name, targetid, false);
		}

		/**
		 * 构造函数
		 * @param id String
		 * @param name String
		 * @param value String
		 * @param targetid String
		 * @param checked boolean
		 */
		public UnitClass(String id, String name, String targetid, boolean checked) {
			this(id, name, targetid, checked, false);
		}

		/**
		 * 构造函数
		 * @param id String
		 * @param name String
		 * @param value String
		 * @param targetid String
		 * @param checked boolean
		 * @param expanded boolean
		 */
		public UnitClass(String id, String name, String targetid, boolean checked, boolean expanded) {
			this(id, name, targetid, checked, expanded, false);
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
		 * 把TreeItem转成UnitClass
		 * @param e TreeItem
		 */
		public UnitClass(TreeItem e) {
			this(e, true);
		}

		/**
		 * 把TreeItem转成UnitClass<br>
		 * 是否提取子类
		 * @param e TreeItem
		 * @param isSubclass boolean
		 */
		public UnitClass(TreeItem e, boolean isSubclass) {
			boolean issuccess = putTreeItem(e);
			if (!issuccess) return;
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

		public final String getId() {
			return id;
		}

		/**
		 * 通过id,name得到具体显示内容
		 * @param base MultiTree
		 * @return String
		 */
		private String getIDNameText(MultiTree base) {
			if (name == null) return null;
			if (id == null || id.length() == 0) return name;
			if (base.isFormatID) return getTagTitle(base.maxIDLen, id, name);
			return getTagTitle(id, name);
		}

		public final List<UnitClass> getList() {
			return list;
		}

		public final String getName() {
			return name;
		}

		/**
		 * 返回含有id号的字符串名称
		 * @return String
		 */
		public final String getNameAll() {
			if (name == null) return null;
			if (id == null || id.length() == 0) return name;
			return getTagTitle(id, name);
		}

		public String getTargetid() {
			if (targetid == null) return null;
			if (targetid.trim().length() == 0) return null;
			return targetid.trim();
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

		public final boolean isChecked() {
			return checked;
		}

		public final boolean isExpanded() {
			return expanded;
		}

		public final boolean isGrayed() {
			return grayed;
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
			String id = getID(text);
			if (id == null) return false;
			this.id = id;
			this.name = name;
			this.checked = e.getChecked();
			this.expanded = e.getExpanded();
			this.grayed = e.getGrayed();
			this.targetid = getID(e.getParentItem());
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
				list.add(new UnitClass(f));
			}
		}

		public final void setChecked(boolean checked) {
			this.checked = checked;
		}

		public final void setExpanded(boolean expanded) {
			this.expanded = expanded;
		}

		public final void setGrayed(boolean grayed) {
			this.grayed = grayed;
		}

		public final void setId(String id) {
			this.id = id;
		}

		public final void setList(List<UnitClass> list) {
			this.list = list;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public void setTargetid(String targetid) {
			this.targetid = targetid;
		}

		@Override
		public String toString() {
			return "UnitClass [checked=" + checked + ", expanded=" + expanded + ", grayed=" + grayed + ", id=" + id + ", name=" + name + ",targetid=" + targetid + ", list=" + list + "]";
		}

		public TreeItem toTreeItem(MultiTree base, Tree parent) {
			TreeItem t = new TreeItem(parent, SWT.NONE);
			return toTreeItemPrivate(base, t);
		}

		public TreeItem toTreeItem(MultiTree base, TreeItem parent) {
			TreeItem t = new TreeItem(parent, SWT.NONE);
			return toTreeItemPrivate(base, t);
		}

		/**
		 * 拼接TreeItem
		 * @param base MultiTree
		 * @param t TreeItem
		 * @return TreeItem
		 */
		private final TreeItem toTreeItemPrivate(MultiTree base, TreeItem t) {
			if (base.isExist(id)) return null;
			String newname = getIDNameText(base);
			//if (newname != null) t.setText(newname);
			//t.setData(value != null ? value : id);
			privateMKItem(t, newname, new DataClass(this));
			return t;
		}

		/**
		 * 得到UnitClass所在list的下标
		 * @param list List&lt;UnitClass&gt;
		 * @param e UnitClass
		 * @return int
		 */
		static final int getIndex(List<UnitClass> list, UnitClass e) {
			if (e == null) return -1;
			for (int i = 0, len = list.size(); i < len; i++)
				if (list.get(i).equals(e)) return i;
			return -1;
		}

		/**
		 * 判断当前UnitClass在列表中的下标
		 * @param list List&lt;UnitClass&gt;
		 * @param e UnitClass
		 * @return int
		 */
		static final int getListIndex(final List<UnitClass> list, UnitClass e) {
			if (e == null) return -1;
			for (int i = 0, len = list.size(); i < len; i++)
				if (e.equals(list.get(i))) return i;
			return -1;
		}

		/**
		 * 从list查找单元所在的上级节点
		 * @param list List&lt;UnitClass&gt;
		 * @param targetid String
		 * @return UnitClass;
		 */
		static final UnitClass getUCParent(final List<UnitClass> list, String targetid) {
			if (list.size() == 0 || targetid == null) return null;
			for (UnitClass f : list) {
				if (f.id.equals(targetid)) return f;
				UnitClass uc = getUCParent(f.list, targetid);
				if (uc != null) return uc;
			}
			return null;
		}

		/**
		 * 从list查找单元所在的上级节点
		 * @param list List&lt;UnitClass&gt;
		 * @param e UnitClass
		 * @return UnitClass;
		 */
		static final UnitClass getUCParent(final List<UnitClass> list, UnitClass e) {
			if (list.size() == 0 || e == null) return null;
			UnitClass f = getUCParent(list, e.targetid);
			return f;
		}

		/**
		 * 从list查找单元所在的list
		 * @param list List&lt;UnitClass&gt;
		 * @param e UnitClass
		 * @return List&lt;UnitClass&gt;
		 */
		static final List<UnitClass> getUCParentBaseList(List<UnitClass> list, UnitClass e) {
			UnitClass f = getUCParent(list, e);
			if (f != null) return f.list;
			if (e.targetid == null) return list;/* 如果在第一列，则返回当前list */
			return new ArrayList<>();
		}

		/**
		 * 嵌套调用e中查看是否含有value值<br>
		 * type:0 按id查<br>
		 * type:1 按名称查[排队id]<br>
		 * type:2 按value查
		 * @param list List&lt;UnitClass&gt;
		 * @param value Object
		 * @param type int
		 * @return UnitClass
		 */
		public static final UnitClass getUCSearch(List<UnitClass> list, Object value, int type) {
			if (value == null) return null;
			for (UnitClass e : list) {
				UnitClass f = getUCSearch(e, value, type);
				if (f != null) return f;
			}
			return null;
		}

		/**
		 * 嵌套调用e中查看是否含有value值<br>
		 * type:0 按id查<br>
		 * type:1 按名称查[排队id]<br>
		 * type:2 按value查
		 * @param e UnitClass
		 * @param value Object
		 * @param type int
		 * @return UnitClass
		 */
		public static final UnitClass getUCSearch(UnitClass e, Object value, int type) {
			if (e == null || value == null) return null;
			if (isSearch(e, value, type)) return e;
			for (UnitClass f : e.list) {
				UnitClass t = getUCSearch(f, value, type);
				if (t != null) return t;
			}
			return null;
		}

		static boolean isSearch(UnitClass e, Object obj, int type) {
			switch (type) {
			case 0:/* id */
				if (obj.equals(e.id)) return true;
				break;
			case 1:/* title */
				if (obj.equals(e.name)) return true;
				break;
			default:
				if (obj.equals(e)) return true;
			}
			return false;
		}
	}

	private static int ACC_style = SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI;

	private static int ACC_styleAlone = SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION;

	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(MultiTree.class);

	public static final String ACC_AutoIDPrefix = "sj";

	public static final int ACC_AutoIDNumMax = 1000;

	public static final int ACC_AutoIDNumLen = 4;

	/**
	 * 合并多个list，并过滤重复单元
	 * @param lists List&lt;UnitClass&gt;[]
	 * @return List&lt;UnitClass&gt;
	 */
	public static final List<UnitClass> combine(List<UnitClass>... lists) {
		List<UnitClass> list = new ArrayList<>();
		for (List<UnitClass> l : lists) {
			list.addAll(l);
		}
		return list.stream().distinct().collect(Collectors.toList());
	}

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
	 * 展开或关闭父类
	 * @param e TreeItem
	 * @param isExpanded boolean
	 */
	public static final void expandedParent(TreeItem e, boolean isExpanded) {
		if (e == null) return;
		TreeItem parent = e.getParentItem();
		if (parent == null) return;
		parent.setExpanded(isExpanded);
		expandedParent(parent, isExpanded);
	}

	/**
	 * 格式化字符串，先判断是否为数字。如果为数字，则前面补0，如果为字符串，则左对齐
	 * @param id String
	 * @param maxlen int
	 * @return String
	 */
	static final String format(String id, int maxlen) {
		if (UtilsString.isNumber(id)) return String.format("%0" + maxlen + "d", Integer.parseInt(id));
		return String.format("%1$-" + maxlen + "s", id);
	}

	/**
	 * 得到深度的静态方法
	 * @param dc DeepClass
	 * @param deep int
	 * @param e TreeItem
	 */
	private static final void getDeepsAllPrivate(DeepClass dc, int deep, TreeItem e) {
		if (e == null) return;
		TreeItem[] arrs = e.getItems();
		if (arrs.length > 0) deep++;
		if (dc.deep < deep) dc.deep = deep;
		for (TreeItem f : arrs) {
			getDeepsAllPrivate(dc, deep, f);
		}
	}

	/**
	 * 通过内容得到id
	 * @param title String
	 * @return String
	 */
	static final String getID(String title) {
		return UtilsVerification.getStringID(title);
	}

	/**
	 * 通过内容得到id
	 * @param e TreeItem
	 * @return String
	 */
	static final String getID(TreeItem e) {
		if (e == null) return null;
		return getID(e.getText());
	}

	/**
	 * 嵌套调用e中查看是否含有value值<br>
	 * type:0 按id查<br>
	 * type:1 按名称查[排队id]<br>
	 * type:2 按value查
	 * @param e TreeItem
	 * @param value String
	 * @param type int
	 * @return TreeItem
	 */
	public static final TreeItem getSearchTreeItem(TreeItem e, String value, int type) {
		if (e == null || value == null) return null;
		String name = e.getText();
		switch (type) {
		case 0:
			String id1 = getID(name);
			if (value.equals(id1)) return e;
			break;
		case 1:
			String title = getTitle(name);
			if (value.equals(title)) return e;
			break;
		default:
			Object val = e.getData();
			if (val != null && value.equals(val)) return e;
		}
		TreeItem[] arrs = e.getItems();
		for (TreeItem f : arrs) {
			TreeItem t = getSearchTreeItem(f, value, type);
			if (t != null) return t;
		}
		return null;
	}

	/**
	 * 嵌套调出选中的单元选项 list
	 * @param list List&lt;UnitClass&gt;
	 * @param e UnitClass
	 */
	static final void getSelectList(List<TreeItem> list, TreeItem e) {
		for (TreeItem f : e.getItems()) {
			if (f.getChecked()) list.add(f);
			getSelectList(list, f);
		}
	}

	/**
	 * 得到标签名称
	 * @param id String
	 * @param name String
	 * @return String
	 */
	static final String getTagTitle(int maxlen, String id, String name) {
		if (id == null) return name;
		String newid = format(id, maxlen);//String.format("%1$4s", id);
		return "[" + newid + "]" + name;
	}

	/**
	 * 得到标签名称
	 * @param id String
	 * @param name String
	 * @return String
	 */
	static final String getTagTitle(String id, String name) {
		if (id == null) return name;
		return "[" + id + "]" + name;
	}

	/**
	 * 得到名称(排除id)
	 * @param id String
	 * @param name String
	 * @return String
	 */
	static final String getTitle(String name) {
		return UtilsVerification.getStringName(name);
	}

	/**
	 * 得到名称(排除id)
	 * @param id String
	 * @param e TreeItem
	 * @return String
	 */
	static final String getTitle(TreeItem e) {
		return getTitle(e.getText());
	}

	/**
	 * @param dc DeepClass
	 * @param deep int
	 * @param e TreeItem
	 * @param arr int[]
	 */
	private static final void getTreeItemCheckedList(DeepClass dc, int deep, TreeItem e, int... arr) {
		if (e == null) return;
		deep++;
		TreeItem[] arrs = e.getItems();
		for (TreeItem f : arrs) {
			if (UtilsArrays.isfilterArr(deep, arr) && f.getChecked()) {
				dc.list.add(f);
			}
			getTreeItemCheckedList(dc, deep, f, arr);
		}
	}

	/**
	 * @param dc DeepClass
	 * @param deep int
	 * @param e TreeItem
	 * @param arr int[]
	 */
	static final void getTreeItemList(DeepClass dc, int deep, TreeItem e, int... arr) {
		if (e == null) return;
		deep++;
		TreeItem[] arrs = e.getItems();
		for (TreeItem f : arrs) {
			if (UtilsArrays.isfilterArr(deep, arr)) {
				dc.list.add(f);
			}
			getTreeItemList(dc, deep, f, arr);
		}
	}

	/**
	 * 在TreeItem中查找编号ID，是否相同
	 * @param id String
	 * @param f TreeItem
	 * @return boolean
	 */
	private static final boolean isExist(String id, TreeItem f) {
		if (id == null || f == null) return false;
		id = id.trim();
		if (id.equals(getID(f.getText()))) return true;
		if (id.equals(f.getData())) return true;
		TreeItem[] arrs = f.getItems();
		for (TreeItem e : arrs) {
			if (isExist(id, e)) return true;
		}
		return false;
	}

	/**
	 * 得到节点列表中id字符串的最长长度
	 * @param list List&lt;UnitClass&gt;
	 * @param max int
	 * @return int
	 */
	static final int maxIDLen(List<UnitClass> list, int max) {
		for (UnitClass e : list) {
			int sort = e.id.length();
			if (sort > max) max = sort;
			int sort2 = maxIDLen(e.list, max);
			if (sort2 > max) max = sort2;
		}
		return max;
	}

	/**
	 * 嵌套调出选中的单元选项 list
	 * @param list List&lt;UnitClass&gt;
	 * @param e UnitClass
	 */
	private static final void privateGetUCList(List<UnitClass> list, UnitClass e) {
		for (UnitClass f : e.list) {
			list.add(f);
			privateGetUCList(list, f);
		}
	}

	/**
	 * 添加TreeItem的内容
	 * @param t TreeItem
	 * @param text title
	 * @param dc DataClass
	 */
	private static final void privateMKItem(TreeItem t, String text, DataClass dc) {
		if (text != null) t.setText(text);
		if (dc != null) t.setData(dc);
		t.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.button == 1) { //按键不是左键跳出. 1左键,2中键,3右键
					System.out.println("t:" + t.getText());
					return;
				}
			}

		});
	}

	/**
	 * 添加TreeItem的内容
	 * @param t TreeItem
	 * @param e UnitClass
	 */
	static final void privateMKItem(TreeItem t, UnitClass e) {
		privateMKItem(t, e.name, new DataClass(e));
	}

	/**
	 * 反序查找e所在的路径，即从此节点向父查找
	 * @param list List&lt;TreeItem&gt;
	 * @param e TreeItem
	 */
	static final void privateReverseParentDeep(List<TreeItem> list, TreeItem e) {
		if (e == null) return;
		list.add(0, e);
		privateReverseParentDeep(list, e.getParentItem());
	}

	/**
	 * 重组对象组 ，并是否进行排序
	 * @param list List&lt;UnitClass&gt;
	 * @param result UnitClass
	 * @param isSorting boolean
	 */
	static final void recombination(List<UnitClass> list, UnitClass result, boolean isSorting) {
		String targetid = result.id;
		for (int i = list.size(); i >= 0; i--) {
			if (list.size() <= i) continue;
			UnitClass e = list.get(i);
			String target = e.getTargetid();
			if ((targetid == target) || (targetid != null && targetid.equals(target))) {
				list.remove(i);
				result.list.add(e);
				if (isSorting) Collections.sort(result.list);
				recombination(list, e, isSorting);
			}
		}
	}

	/**
	 * 把ResultSet转成List&lt;UnitClass&gt;<br>
	 * ResultSet : id,name,value,[targetid]
	 * @param rs ResultSet
	 * @return List&lt;UnitClass&gt;
	 */
	static final List<UnitClass> resultSetChange(ResultSet rs) {
		List<UnitClass> list = new ArrayList<>();
		try {
			int len = rs.getMetaData().getColumnCount();
			if (len < 4) return list;
			while (rs.next()) {
				UnitClass f = new UnitClass(rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4));
				list.add(f);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static final String toJson(List<UnitClass> list) {
		String json = JSON.toJSONString(list);
		System.out.println("json" + json);
		return json;
	}

	/**
	 * TreeItem转成字符串，用于输出显示使用
	 * @param f TreeItem
	 * @return String
	 */
	private static final String toStringTreeItem(TreeItem f) {
		if (f == null) return "null";
		return f.getText() + "[" + f.getData() + "]";
	}

	/**
	 * 得到TreeItem中data的值，并转成DataClass
	 * @param f TreeItem
	 * @return DataClass
	 */
	public static final DataClass treeItem2DC(final TreeItem f) {
		if (f == null) return null;
		Object obj = f.getData();
		if (obj == null) return null;
		if (obj instanceof DataClass) return (DataClass) obj;
		return null;
	}

	/**
	 * 第一参数 为总记录，isLeft是左移还是右移,arrs为id数组
	 * @param elist List&lt;String&gt;
	 * @param isLeft boolean
	 * @param idArrs String[]
	 * @return boolean
	 */
	public static final boolean TreeItemChangeLeft(List<UnitClass> elist, boolean isLeft, String... idArrs) {
		boolean ischange = false;
		for (String id : idArrs) {
			ischange = ischange | TreeItemChangeToLeft(elist, isLeft, id);
		}
		return ischange;
	}

	/**
	 * 单元向左移动，并返回是否成功
	 * @param elist List&lt;String&gt;
	 * @param e UnitClass
	 * @return boolean
	 */
	private static final boolean TreeItemChangeLeft(List<UnitClass> elist, UnitClass e) {
		if (e.targetid == null) return false;/* 在第一列中，不允许左移 */
		UnitClass parent = UnitClass.getUCParent(elist, e);
		if (parent == null) return false;
		e.targetid = parent.targetid;/* 把本节点的targetid改成父节点的targetid */
		/* 从父节点的列表中删除节点 */
		for (int i = parent.list.size() - 1; i >= 0; i--) {
			if (parent.list.get(i).equals(e)) {
				parent.list.remove(i);
				break;
			}
		}
		/* 在父节点所在的列中插入本节点 */
		List<UnitClass> newlist = UnitClass.getUCParentBaseList(elist, parent);
		int index = UnitClass.getIndex(newlist, parent);
		if (index < 0) return false;
		newlist.add(index, e);
		return true;
	}

	/**
	 * 单元向右移动，并返回是否成功
	 * @param elist List&lt;String&gt;
	 * @param e UnitClass
	 * @return boolean
	 */
	private static final boolean TreeItemChangeRight(List<UnitClass> elist, UnitClass e) {
		List<UnitClass> newlist = UnitClass.getUCParentBaseList(elist, e);
		int index = UnitClass.getIndex(newlist, e);
		if (index <= 0) return false;
		int p = index - 1;
		UnitClass t = newlist.get(p);
		t.list.add(e);
		t.expanded = true;
		e.targetid = t.id;
		newlist.remove(index);
		return true;
	}

	/**
	 * 第一参数 为总记录，isLeft是左移还是右移,id编号
	 * @param elist List&lt;String&gt;
	 * @param isLeft boolean
	 * @param id String
	 * @return boolean
	 */
	private static final boolean TreeItemChangeToLeft(List<UnitClass> elist, boolean isLeft, String id) {
		if (id == null) return false;
		UnitClass e = UnitClass.getUCSearch(elist, id, 0);/* 查找到当前的单元位置 */
		if (e == null) return false;
		if (isLeft) {/* 向左移动，上升一级 */
			if (!TreeItemChangeLeft(elist, e)) return false;
			return true;
		} else {/* 向右移动，下降一级 */
			if (!TreeItemChangeRight(elist, e)) return false;
			return true;
		}
	}

	/**
	 * 嵌套调出所有此treeitem下的所有子标签，是否选中，如为null，则不判断
	 * @param list &lt;TreeItem&gt;
	 * @param e TreeItem
	 * @param isChecked Boolean
	 */
	private static final void treeItemList(List<TreeItem> list, TreeItem e, Boolean isChecked) {
		if (list == null || e == null) return;
		for (TreeItem f : e.getItems()) {
			if (isChecked == null || f.getChecked() == isChecked) {
				list.add(f);
			}
			treeItemList(list, f, isChecked);
		}
	}
}
