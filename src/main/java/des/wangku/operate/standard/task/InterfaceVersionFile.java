package des.wangku.operate.standard.task;
/**
 * 设置版本信息文件所在的位置
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public interface InterfaceVersionFile {

	/**
	 * 得到版本文件在jar包的位置 如:"/update.info"
	 * @return String
	 */
	public default String getVersionFileJarFullPath() {
		return "/update.info";
	}
}
