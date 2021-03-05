package des.wangku.operate.standard.subengineering.rank;

import java.util.ArrayList;
import java.util.List;
/**
 * 权重<br>
 * url网址<br>
 * type类型，如pc或mobile
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */

public class RankInfor {
	String url;
	String type;
	List<RankInforUnit> list = new ArrayList<>();
	public final String getUrl() {
		return url;
	}
	public final void setUrl(String url) {
		this.url = url;
	}
	public final String getType() {
		return type;
	}
	public final void setType(String type) {
		this.type = type;
	}
	public final List<RankInforUnit> getList() {
		return list;
	}
	public final void setList(List<RankInforUnit> list) {
		this.list = list;
	}
	public final String getPosid(String key) {
		for(RankInforUnit e:list) {
			if(e.key.equals(key))return e.posid;
		}
		return "--";
	}

}
