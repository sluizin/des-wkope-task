package des.wangku.operate.standard.swt;

/**
 * 修改弹出窗口后返回父类进一步进行修改
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface InterfaceResultTableUpdate {
	/**
	 * 修改弹出窗口后返回父类进一步进行修改<br>
	 * 返回为true，table内的值才会修改
	 * @param base ResultTable
	 * @param x int
	 * @param y int
	 * @param oldValue String
	 * @param newValue String
	 * @return boolean
	 */
	public boolean resultTableUpdate(ResultTable base,int x, int y,String oldValue, String newValue);
}
