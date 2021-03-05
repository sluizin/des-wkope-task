package des.wangku.operate.standard.subengineering.rank;

import java.util.List;
/**
 * 
 * 得到权重，包括url,type[pc/moblie],list
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public interface RankInterface {

	/**
	 * 得到权重
	 * @return RankInfor
	 */
	public RankInfor getRankInfor();

	/**
	 * 通过aizhan地址，得到此页的结果列表
	 * @param urlpage  String
	 * @return List &lt;  AIInforUnit &gt; 
	 */
	public List<RankInforUnit> getInforList(String urlpage);
}
