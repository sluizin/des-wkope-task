package des.wangku.operate.standard.unit;

import java.util.ArrayList;
import java.util.List;

/**
 * {"key":"落地总平台","value":["www.99114.com","XXXX.99114.cn"]}
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UnitKV {
	String key = "";
	List<String> value = new ArrayList<>();

	public final String getKey() {
		return key;
	}

	public final void setKey(String key) {
		this.key = key;
	}

	public final List<String> getValue() {
		return value;
	}

	public final void setValue(List<String> value) {
		this.value = value;
	}
	public final void add(String v) {
		this.value.add(v);
	}

	@Override
	public String toString() {
		return "UnitKV [" + (key != null ? "key=" + key + ", " : "") + (value != null ? "value=" + value : "") + "]";
	}

}
