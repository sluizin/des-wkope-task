package des.wangku.operate.standard.task;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 
 * 设置项目基础信息注解
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE})
public @interface AnnoProjectTask {
	/**
	 * 得到菜单名称
	 * @return String
	 */
	public String MenuName() default "";
	/**
	 * 得到菜单名称前缀 如 输入:P02 则菜单为:[P02]XXXX
	 * @return String
	 */
	public String MenuNameHead() default "";
}
