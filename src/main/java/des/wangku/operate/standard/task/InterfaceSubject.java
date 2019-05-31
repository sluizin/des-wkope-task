package des.wangku.operate.standard.task;
/**
 * 
 * 项目信息的非必须项目信息<br>
 * 项目所在的组 getGroup()
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceSubject {
	/**
	 * 得到项目所在的组，默认为空、null
	 * @return String
	 */
	public default String getGroup() {
		return "";
	}
}
