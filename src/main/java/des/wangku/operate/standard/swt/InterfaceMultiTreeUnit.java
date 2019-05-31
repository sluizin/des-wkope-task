package des.wangku.operate.standard.swt;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public interface InterfaceMultiTreeUnit {
	/**
	 *  是否选中状态
	 * @return boolean
	 */
	public boolean isCheck();
	/**
	 * 编号 唯一
	 * @return String
	 */
	public String getID();
	/**
	 * 名称
	 * @return String
	 */
	public String getName();
	/** 下级子列 */
	List<InterfaceMultiTreeUnit> list = new ArrayList<>();
	
}
