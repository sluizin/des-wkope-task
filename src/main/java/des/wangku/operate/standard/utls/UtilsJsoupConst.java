package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class UtilsJsoupConst {
	/** jsoup关键字规则，间断关键字 */
	public static final String ACC_JsoupRuleCutIntervalSplit = "\\|[Tt]{1}[oO]{1}\\|";
	/** jsoup关键字规则，最高等级间隔 */
	public static final String ACC_JsoupRuleInterval = "|;|";
	public static final String ACC_JsoupRuleIntervalSplit = "\\|\\;\\|";
	/** jsoup关键字规则，用于页内精确定位 主要用于通过与id、class等相似的规则，进行定位 */
	public static final String ACC_JsoupRulePrecisePositioningInPage = "|->|";
	/** jsoup关键字规则，用于页内精确定位 主要用于通过与id、class等相似的规则，进行定位 */
	public static final String ACC_InPage = ACC_JsoupRulePrecisePositioningInPage;
	public static final String ACC_JsoupRulePrecisePositioningInPageSplit = "\\|->\\|";
	/** jsoup关键字规则，用于页外精确定位 主要用于通过链接的连锁，定位节点 */
	public static final String ACC_JsoupRulePrecisePositioningOutPage = "|=>|";
	/** jsoup关键字规则，用于页外精确定位 主要用于通过链接的连锁，定位节点 */
	public static final String ACC_OutPage = ACC_JsoupRulePrecisePositioningOutPage;
	public static final String ACC_JsoupRulePrecisePositioningOutPageSplit = "\\|=>\\|";

	/**
	 * 多个关键字进行精确定位a1 a2 a3
	 * @param source Element
	 * @param arrs String[]
	 * @return Elements
	 */
	public static final Elements forward(Element source, String... arrs) {
		if (arrs.length == 0) return new Elements();
		List<String> list = Arrays.asList(arrs);
		Elements es = new Elements();
		forwardPrivate(es, source, list, 0);
		return es;
	}
	/**
	 * 多个关键字进行精确定位 a1 |->| a2 |->| a3
	 * @param source Element
	 * @param key String
	 * @return Elements
	 */
	public static final Elements forwardKey(Element source, String key) {
		if (key.indexOf(ACC_JsoupRulePrecisePositioningInPage) == -1) return new Elements();
		String[] arr = key.split(ACC_JsoupRulePrecisePositioningInPageSplit);
		return forward(source, arr);
	}


	/**
	 * 递归循环调取精确定位
	 * @param es Elements
	 * @param source Element
	 * @param list List&lt;String&gt;
	 * @param index int
	 */
	private static final void forwardPrivate(Elements es, Element source, List<String> list, int index) {
		if (index < 0 || index >= list.size()) return;
		String key = list.get(index);
		Elements ess = UtilsJsoup.getElementAll(source, key);
		if (ess.size() == 0) return;
		if (index == list.size() - 1) {
			es.addAll(ess);
			return;
		}
		for (Element ee : ess)
			forwardPrivate(es, ee, list, index + 1);
	}
	/**
	 * 把规则关键字放入数组中，经过处理后的最终结果
	 * @param arr String[]
	 * @return String[]
	 */
	public static final String[] getJsoupRuleKey(String... arr) {
		String[] arrs = {};
		List<String> list = new ArrayList<>();
		for (String e : arr) {
			if (e == null || e.length() == 0) continue;
			jsoupRuleSingleKey(list, e);
		}
		list = UtilsList.distinct(list);
		return list.toArray(arrs);
	}
	/**
	 * 判断是否含有间断关键字以|to|为间隔，如果有多个间隔，以前2个字符串为关键字
	 * @param key String
	 * @return boolean
	 */
	public static final boolean isCutInterval(String key) {
		if(key==null || key.length()==0)return false;
		String[] arr=key.split(ACC_JsoupRuleCutIntervalSplit);
		if(arr.length<2)return false;
		return true;
	}


	/**
	 * 把规则关键字放入list中，是最终数组，已经完成分组
	 * @param list List&lt;String&gt;
	 * @param key String
	 * @param arr String[]
	 */
	private static final void jsoupRuleSingleKey(List<String> list, String key) {
			if (key == null) return;
			key=key.trim();
			if (key.length() == 0) return;
			if (key.indexOf(ACC_JsoupRuleInterval) > -1) {
				String[] arr = key.split(ACC_JsoupRuleIntervalSplit);
				for(String e:arr)jsoupRuleSingleKey(list,e);
				return;
			}
			if (key.indexOf(ACC_JsoupRulePrecisePositioningOutPage) > -1) {
				list.add(key);
				return;
			}
			if (key.indexOf(ACC_JsoupRulePrecisePositioningInPage) > -1) {
				list.add(key);
				return;
			}
			if (key.indexOf("||") > -1) {
				String[] arr = key.split("\\|\\|");
				for(String f:arr) {
					f=f.trim();
					if(f.length()>0)
					list.add(f);
				}
				return;
			}
			if (key.indexOf("|") > -1) {
				String[] arr = key.split("\\|");
				for(String f:arr) {
					f=f.trim();
					if(f.length()>0)
					list.add(f);
				}
				return;
			}
			String[] arrs = key.split(";");
			for (String e : arrs) {
				if (e == null) continue;
				e = e.trim();
				if (e.length() == 0) continue;
				list.add(e);
			}
	}
	/**
	 * UtilsJsoup工具读取多个关键时，只支持关键字，以分号进行分格 ";"
	 * @param arr String[]
	 * @return String[]
	 */
	public static final String[] jsoupSingleKeyArray(String... arr) {
		List<String> list = new ArrayList<>();
		for (String e : arr) {
			if (e == null || (e = e.trim()).length() == 0) continue;
			String[] arrs = e.split("[;]");
			for (String f : arrs) {
				if (f == null || (f = f.trim()).length() == 0) continue;
				list.add(f);
			}
		}
		String[] arrs = {};
		if (list.size() == 0) return arrs;
		return list.toArray(arrs);
	}

	/**
	 * 单独关键字串等级<br>
	 * 结果:-1 则关键字为null或过滤掉空格后长度为0<br>
	 * 结果:1 则含有|;|<br>
	 * 结果:2 则含有|=>|<br>
	 * 结果:3 则含有|->|<br>
	 * 结果:4 则含有|to|<br>
	 * 默认返回0
	 * @param key String
	 * @return int
	 */
	public static final int keyLevel(String key) {
		if(key==null)return -1;
		key = key.trim();
		if (key.length() == 0) return -1;
		if(key.indexOf(ACC_JsoupRuleInterval)>-1)return 1;
		if (key.indexOf(ACC_JsoupRulePrecisePositioningOutPage) > -1) return 2;
		if (key.indexOf(ACC_JsoupRulePrecisePositioningInPage) > -1) return 3;
		if (isCutInterval(key))return 4;
		return 0;
	}

	public static void main(String[] args) 
	{
		String line="(ss)vv(vv)abc;{cd,d}tt[0,2,d],cc[c,3]";
		String[] arr=line.split("\\(.*?\\)|(,)");
		for(String e:arr)
			System.out.println("e:"+e);
		//int[] arrs= {0,-5,-1,5,3,-7,4,-10};
		//Arrays.sort(arrs);
		//for(int e:arrs)
			//System.out.println("e:"+e);
	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点
	 * @param e Element
	 * @param keyarr String[]
	 */
	public static final Elements relation(Element e, String... keyarr) {
		Elements summary = new Elements();
		if (e == null || keyarr.length == 0) return summary;
		relation(summary, e, keyarr, 0);
		return summary;
	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点
	 * @param summary Elements
	 * @param e Element
	 * @param keyarr String[]
	 * @param index int
	 */
	private static final void relation(Elements summary, Element e, String[] keyarr, int index) {
		if (e == null) return;
		int len = keyarr.length;
		if (index >= len) return;
		String key = keyarr[index];
		if (key == null) return;
		key = key.trim();
		if (key.length() == 0) return;
		Elements es = UtilsJsoupLink.getHrefElementsAll(e, key);
		if (index == len - 1) {
			summary.addAll(es);
			return;
		}
		for (Element f : es) {
			List<String> list = UtilsJsoupLink.getHrefAll(f);
			list = UtilsList.distinct(list);
			for (String url : list) {
				Element t = UtilsJsoupExt.getDoc(url);
				if (t == null) continue;
				relation(summary, t, keyarr, index + 1);
			}
		}

	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点<br>
	 * @param url String
	 * @param keyarr String[]
	 */
	public static final Elements relation(String url, String... keyarr) {
		Elements all = new Elements();
		if (url == null || url.length() == 0) return all;
		if (keyarr.length == 0) return all;
		Elements es = UtilsJsoup.getElementAll(url, keyarr[0]);
		if (keyarr.length == 1) return es;
		for (Element e : es) {
			Elements ess = new Elements();
			relation(ess, e, keyarr, 1);
			all.addAll(ess);
		}
		return all;
	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点<br>
	 * 以"|=&gt;|"为间隔
	 * @param e Element
	 * @param key String
	 */
	public static final Elements relationKey(Element e, String key) {
		if (e == null || key == null || key.length() == 0) return new Elements();
		if (key.indexOf(ACC_JsoupRulePrecisePositioningOutPage) == -1) return new Elements();
		String[] keyarr = key.split(ACC_JsoupRulePrecisePositioningOutPageSplit);
		return relation(e, keyarr);
	}

	/**
	 * 递归调用，找到多个最后一个关键字的节点，保存到summary中进行汇总，可能会涉及到多个网页的节点<br>
	 * 以"|=&gt;|"为间隔
	 * @param key String
	 */
	public static final Elements relationKey(String url, String key) {
		if (url == null || url.length() == 0 || key == null || key.length() == 0) return new Elements();
		if (key.indexOf(ACC_JsoupRulePrecisePositioningOutPage) == -1) return new Elements();
		String[] keyarr = key.split(ACC_JsoupRulePrecisePositioningOutPageSplit);
		if (keyarr.length == 0) return new Elements();
		return relation(url, keyarr);
	}
	/**
	 * 设置主关键字范围，或减去多条。还是只选多条，如没空，则全部
	 * @param els Elements
	 * @param surplus int[]
	 * @return Elements
	 */
	static final Elements setMainKeyRange(Elements els,int...surplus) {
		if(els==null ||els.size()==0)return new Elements(0);
		int len = els.size();
		Elements es=new Elements(len);
		if (surplus.length == 0) {
			es.addAll(els);
			return es;
		}
		int state=UtilsArrays.isStateArraysInt(surplus);
		if(state==-1) {
			/* 删除多条记录，只保存留下的记录 但范围内必须都为负值，-1是指删除第一条记录 */
			Arrays.sort(surplus);/* 负数组从小到大排序 */
			for (int vv : surplus) {
				int vvv=Math.abs(vv);
				vvv--;
				if (vvv >= 0 && vvv < len) {
					els.remove(vvv);
				}
				es.addAll(els);
			}
			return es;
		}
		/* 只提取指定的下标记录 */
		for (int vv : surplus) {
			if (vv >= 0 && vv < len) {
				es.add(els.get(vv));
			}
		}
		return es;
		
	}
}
