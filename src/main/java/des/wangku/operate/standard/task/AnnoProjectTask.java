package des.wangku.operate.standard.task;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 
 * 设置项目基础信息注解<br>
 * <code>group:组名		name:项目名称	identifier:项目编号
 * expire:是否过期<br>
 * dateStart:开始日期	dateEnd:结束日期<br>
 * autoLoad:是否自动装载(平台打开时自动调取最新保存的允许自动装载的工程[懒人操作])
 * </code>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE})
public @interface AnnoProjectTask {
	/**
	 * 是否自动装载
	 * @return boolean
	 */
	public boolean autoLoad() default false;
	/**
	 * 项目所在的组
	 * @return String
	 */
	public String group() default "";
	/**
	 * 得到项目名称
	 * @return String
	 */
	public String name() default "";
	/**
	 * 得到项目编号 如 输入:P02 则菜单为:[P02]XXXX
	 * @return String
	 */
	public String identifier() default "";
	/**
	 * 是否过期
	 * @return boolean
	 */
	public boolean expire() default false;
	
	
	/**
	 * 开始日期 格式:"2019-08-12 00:00:00"
	 * @return String
	 */
	public String dateStart() default "";
	
	/**
	 * 结束日期 格式:"2019-08-12 00:00:00"
	 * @return String
	 */
	public String dateEnd() default "";
}
