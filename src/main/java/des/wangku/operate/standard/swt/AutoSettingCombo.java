package des.wangku.operate.standard.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsSWTTools;

/**
 * 自动设置系统<br>
 * 下拉，自动选择套餐，分别向各个控件输入内容
 * json key:autosetting
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class AutoSettingCombo extends Combo {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(AutoSettingCombo.class);
	/** 父容器 */
	Composite parent;
	AutoSettingCombo base = null;
	String jsonkey = "autosetting";
	String name = null;
	List<Plan> val = new ArrayList<>();

	public AutoSettingCombo(Composite parent, int style) {
		this(parent, style, null);
	}

	public AutoSettingCombo(Composite parent, int style, String name) {
		this(parent, style, "autosetting", name);
	}

	public AutoSettingCombo(Composite parent, int style, String jsonke, String name) {
		super(parent, style | SWT.DROP_DOWN | SWT.READ_ONLY);
		this.parent = parent;
		this.jsonkey = jsonke;
		this.name = name;
		base = this;
	}

	/**
	 * 构造初始化
	 */
	public void initialization() {
		this.add("未选..");
		this.select(0);
		AbstractTask t = UtilsSWTTools.getParentObjSuperclass(parent, AbstractTask.class);
		if (t == null) return;
		if (name == null) return;
		String json = t.getProJsonValue(jsonkey);//坐标a
		List<Project> projectlist = JSON.parseArray(json, Project.class);
		for (Project e : projectlist) {
			if (name.equalsIgnoreCase(e.name)) {
				this.val = e.val;
				break;
			}
		}
		for (int i = 0, len = val.size(); i < len; i++) {
			Plan e = val.get(i);
			this.add(e.name);
		}

		this.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selected = getSelectionIndex();
				if (selected == 0) return;
				Plan p = val.get(selected - 1);
				Item t = p.val;
				Control[] controls = base.getParent().getChildren();
				t.setControlVal(controls);
			}
		});
	}

	/**
	 * {"name":"坐标a","val":[ {"name": "第一方案","val": {"text": [{"no": 1,"val": "金银花"}]}}]}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class Project {
		String name = "";
		List<Plan> val = new ArrayList<>();

		public Project() {

		}

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final List<Plan> getVal() {
			return val;
		}

		public final void setVal(List<Plan> val) {
			this.val = val;
		}

	}

	/**
	 * {"name": "第一方案","val": {"text": [{"no": 1,"val": "金银花"}]}}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class Plan {
		String name = "";
		Item val = new Item();

		public Plan() {

		}

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final Item getVal() {
			return val;
		}

		public final void setVal(Item val) {
			this.val = val;
		}

	}

	/**
	 * {"text": [{"no": 1,"val": "金银花"}]}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class Item {
		public Item() {

		}

		List<ItemText> text = new ArrayList<>();

		/**
		 * 向control中设置值
		 * @param controls Control[]
		 */
		public void setControlVal(Control[] controls) {
			int pointText = 0;
			for (int i = 0; i < controls.length; i++) {
				Control c = controls[i];
				if (c instanceof Text) {
					Text text = (Text) c;
					String val = getTextVal(pointText++);
					if (val != null) text.setText(val);
				}
			}

		}

		/**
		 * 得到某个下标的值，如果没有找到，则返回null
		 * @param no int
		 * @return String
		 */
		public String getTextVal(int no) {
			for (ItemText e : text) {
				if (e.no == no) return e.val;
			}
			return null;
		}

		public final List<ItemText> getText() {
			return text;
		}

		public final void setText(List<ItemText> text) {
			this.text = text;
		}

	}

	/**
	 * {"no": 1,"val": "金银花"}
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ItemText {
		int no = -1;
		String val = "";

		public ItemText() {

		}

		public final int getNo() {
			return no;
		}

		public final void setNo(int no) {
			this.no = no;
		}

		public final String getVal() {
			return val;
		}

		public final void setVal(String val) {
			this.val = val;
		}
	}

	protected void checkSubclass() {

	}
}
