package des.wangku.operate.standard.task;
/**
 * 项目总父接口
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceProjectTop {
	/**
	 * 得到本地项目对象
	 * @return AbstractTask
	 */
	public default AbstractTask getProejectBase() {
		Object obj = this;
		if(!(obj instanceof AbstractTask)) return null;
		AbstractTask t = (AbstractTask) obj;
		return t;
	}
}
