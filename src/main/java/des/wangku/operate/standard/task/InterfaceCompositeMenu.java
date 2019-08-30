/**
 * 
 */
package des.wangku.operate.standard.task;

/**
 * 项目初始化时对鼠标右键子菜单进行设置<br>
	 * 父菜单为 abstractMenu<br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceCompositeMenu {
	/**
	 * 针对子窗口进行设置右键菜单<br>
	 * 父菜单为 abstractMenu<br>
	 * 这是菜单自定义前面菜单项，后面设置抽象类的定义的通用功能菜单
	 */
	public default void initCompositeMenu() {
		
	}
}
