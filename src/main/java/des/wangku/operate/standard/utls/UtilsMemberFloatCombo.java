package des.wangku.operate.standard.utls;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.alibaba.fastjson.JSON;

import des.wangku.operate.standard.desktop.DesktopConst;
import des.wangku.operate.standard.task.AbstractTask;
import des.wangku.operate.standard.task.InterfaceExtRemember.RemClass;

/**
 * 针对移动下拉框
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsMemberFloatCombo {

	/**
	 * 针对Text记忆下拉内容的监听
	 * @param base AbstractTask
	 * @param text Text
	 * @param id String
	 * @return Listener
	 */
	public static final Listener getListenerTextKeyUpRemember(AbstractTask base, Text text, String id) {
		Listener t = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.keyCode == SWT.ARROW_DOWN || event.keyCode == SWT.ARROW_UP) return;
				if (!DesktopConst.isRemember_Input) return;
				Combo c = base.remFloatOutCombo;
				if (c == null) return;
				try {
					Thread.sleep(500);// 添加延迟
				} catch (Exception e) {
					e.printStackTrace();
				}
				Point point = UtilsSWTLocation.getParentLocation(text);
				Point textPoint = text.getLocation();
				int x = textPoint.x;
				int y = textPoint.y;
				Point location = new Point(point.x + x, point.y + y + 2);
				c.setLocation(location);
				c.setSize(new Point(text.getSize().x, 50));
				String[] arr = base.getRemMultiValueArray(id);//base.getRemValueArray(id);
				c.removeAll();
				c.setItems(arr);
				base.remFloatOutText = text;
				c.setListVisible(true);
			}
		};
		return t;
	}

	/**
	 * 记忆id的设置，通过 text_12格式进行保存
	 * @param i int
	 * @return String
	 */
	public static final String getRememberID(int i) {
		return "text_" + i;
	}

	/**
	 * 把AbstractTask内的浮动下拉框和目的框进行关联
	 * @param base AbstractTask
	 */
	public static final void make_FloatCombo(AbstractTask base) {
		base.parentShell.setLayout(new GridLayout(3, false));
		base.remFloatOutCombo = new Combo(base.parentShell, SWT.BORDER);
		base.remFloatOutCombo.setBounds(10, 10, 100, 50);
		GridData com_data = new GridData();
		com_data.widthHint = 100;
		com_data.horizontalAlignment = GridData.FILL;
		com_data.grabExcessVerticalSpace = true;
		base.remFloatOutCombo.setLayoutData(com_data);
		base.remFloatOutCombo.setVisibleItemCount(5);
		base.remFloatOutCombo.clearSelection();
		base.remFloatOutCombo.setVisible(false);
		if (base.remFloatOutCombo.isListening(SWT.Selection)) return;
		base.remFloatOutCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!DesktopConst.isRemember_Input) return;
				int index = base.remFloatOutCombo.getSelectionIndex();
				if (index < 0) return;
				String value = base.remFloatOutCombo.getItem(index);
				if (value != null && base.remFloatOutText != null) {
					String title=UtilsString.splitWordMiddleBrackets(value);
					if(title==null)return;
					base.remFloatOutText.setText(title.trim());
				}
			}

		});
	}

	/**
	 * 向Text添加监听器，设置调出id的历史记录
	 * @param base AbstractTask
	 * @param text Text
	 * @param id String
	 */
	public static final void make_TextCombo(AbstractTask base, Text text, String id) {
		if (base == null || text == null || id == null) return;
		Listener t = UtilsMemberFloatCombo.getListenerTextKeyUpRemember(base, text, id);
		if (t == null) return;
		if (UtilsSWTListener.isListenerExist(text, SWT.KeyUp, t)) return;
		text.addListener(SWT.KeyUp, t);
	}

	/**
	 * 为Button按扭加上监听，用于记忆记录
	 * @param base AbstractTask
	 * @return SelectionAdapter
	 */
	public static final SelectionAdapter setListenerButton(AbstractTask base) {
		if (base == null) return null;
		SelectionAdapter t = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!DesktopConst.isRemember_Input) return;
				List<Text> list = UtilsSWTComposite.getCompositeSearchChildrenControl(Text.class, base.parentComposite);
				for (int i = 0, len = list.size(); i < len; i++) {
					Text f = list.get(i);
					RemClass g = new RemClass(getRememberID(i), f.getText());
					System.out.println("add:" + g.toString());
					base.addRemLine(JSON.toJSONString(g));
				}
			}
		};
		return t;
	}

	/**
	 * 向容器中的每个button按扭加载点击监听器，用于记录所有Text的内容，并保存进入到记忆文档中
	 * @param base AbstractTask
	 */
	public static final void setListenerbutton_remember(AbstractTask base) {
		if (base == null) return;
		/** 给所有的Button加上监听 */
		List<Button> list = UtilsSWTComposite.getCompositeSearchChildrenControl(Button.class, base.parentComposite);
		SelectionAdapter t = UtilsMemberFloatCombo.setListenerButton(base);
		if (t == null) return;
		for (Button e : list) {
			//if (e.isListening(SWT.Selection)) continue;
			e.addSelectionListener(t);
		}
		/** 给所有的Text加上监听 */
		List<Text> listText = UtilsSWTComposite.getCompositeSearchChildrenControl(Text.class, base.parentComposite);
		for (int i = 0, len = listText.size(); i < len; i++) {
			String id = UtilsMemberFloatCombo.getRememberID(i);
			Text f = listText.get(i);
			UtilsMemberFloatCombo.make_TextCombo(base, f, id);
		}
	}
}
