package des.wangku.operate.standard.subengineering.rank;

import des.wangku.operate.standard.utls.UtilsString;

public class RankInforUnit {

	String key=null;
	String posid="";
	public RankInforUnit() {
		
	}
	public RankInforUnit(String key,String posid) {
		this.key=key;
		this.posid=posid;
	}
	public final String getKey() {
		return key;
	}

	public final void setKey(String key) {
		this.key = key;
	}

	public final String getPosid() {
		return posid;
	}

	/**
	 * 数据格式 "第1页 第2位"<br>
	 * @return int
	 */
	public final int getPosidInteger() {
		//getNumbersIntRankPosid(content, "第", "页", "第", "位");
		return UtilsString.getNumbersIntRankPosid(posid);
	}
	/**
	 * 数据格式 "第1页 第2位"<br>
	 * "第", "页", "第", "位"
	 * @param pageFirst String
	 * @param pageEnd String
	 * @param pointFist String
	 * @param pointEnd String
	 * @return int
	 */
	public final int getPosidInteger(String pageFirst, String pageEnd, String pointFist, String pointEnd) {
		return UtilsString.getNumbersIntRankPosid(posid,  pageFirst, pageEnd, pointFist, pointEnd);
	}
	 
	public final void setPosid(String posid) {
		this.posid = posid;
	}

}
