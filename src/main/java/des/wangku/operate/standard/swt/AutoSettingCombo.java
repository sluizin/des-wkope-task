package des.wangku.operate.standard.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.utls.UtilsComposite;
import des.wangku.operate.standard.utls.UtilsSWTTools;
import static des.wangku.operate.standard.swt.AutoSettingComboItems.*;
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
	static final String JsonkeyDef = "autosetting";
	String jsonkey = JsonkeyDef;
	String name = null;
	List<Plan> val = new ArrayList<>();

	public AutoSettingCombo(Composite parent, int style) {
		this(parent, style, "autoset");
	}

	public AutoSettingCombo(Composite parent, int style, String name) {
		this(parent, style, JsonkeyDef, name);
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
		if(json==null || json.length()==0)return;
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
		this.add("清理所有控件..");
		this.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selected = getSelectionIndex();
				if (selected == 0) return;
				Control[] controls = base.getParent().getChildren();
				if(controls.length==0)return;
				/* 当选择最后一个，则需要清空所有控件 */
				if(selected==base.getItemCount()-1) {
					UtilsComposite.cleanControls(controls);
					return;
				}
				Plan p = val.get(selected - 1);
				if(p==null)return;
				Item t = p.val;
				t.setControlVal(controls);
			}
		});
	}
	protected void checkSubclass() {

	}
}
