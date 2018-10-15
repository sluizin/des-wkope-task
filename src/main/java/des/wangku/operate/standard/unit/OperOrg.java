package des.wangku.operate.standard.unit;

import java.util.ArrayList;
import java.util.List;


/**
 * 整个运营中心
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class OperOrg {
	String name = "";

	List<ClassZone> operatezone = new ArrayList<>();

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final List<ClassZone> getOperatezone() {
		return operatezone;
	}

	public final void setOperatezone(List<ClassZone> operatezone) {
		this.operatezone = operatezone;
	}

	/**
	 * 片区
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ClassZone {
		String name = "";
		/** 支持 */
		List<ClassManager> support = new ArrayList<>();
		/** 负责人 */
		List<ClassManager> responsible = new ArrayList<>();
		List<ClassDistrict> district = new ArrayList<>();

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final List<ClassDistrict> getDistrict() {
			return district;
		}

		public final void setDistrict(List<ClassDistrict> district) {
			this.district = district;
		}

		public final List<ClassManager> getSupport() {
			return support;
		}

		public final void setSupport(List<ClassManager> support) {
			this.support = support;
		}

		public final List<ClassManager> getResponsible() {
			return responsible;
		}

		public final void setResponsible(List<ClassManager> responsible) {
			this.responsible = responsible;
		}

	}

	/**
	 * 大区
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ClassDistrict {
		String name = "";
		/** 负责人 */
		List<ClassManager> responsible = new ArrayList<>();
		List<ClassBase> base = new ArrayList<>();

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final List<ClassBase> getBase() {
			return base;
		}

		public final void setBase(List<ClassBase> base1) {
			base = base1;
		}

		public final String getResponsibleString() {
			return getClassManagerList2String(responsible);
		}

		public final List<ClassManager> getResponsible() {
			return responsible;
		}

		public final void setResponsible(List<ClassManager> responsible) {
			this.responsible = responsible;
		}
	}

	/**
	 * 基地
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static final class ClassBase {
		/** 基地名称 */
		String name = "";
		/** 核心单品 */
		String csproduct = "";
		/** 产业网编号 */
		int siteId = 0;
		/** 产业网名称 */
		String cywname = "";
		/** 二级域名 依据于网库 */
		String domain99114 = "";
		/** 一级域名 */
		List<ClassDomain> domain = new ArrayList<>();
		/** 基地负责人 */
		String baseman = "";
		/** 基地负责人联系方式 */
		String basemancontact = "";
		/** 运营对接人 */
		String opreceiver = "";
		/** 运营对接人联系方式 */
		String opreceivercontact = "";

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final String getCsproduct() {
			return csproduct;
		}

		public final void setCsproduct(String csproduct) {
			this.csproduct = csproduct;
		}

		public final String getBaseman() {
			return baseman;
		}

		public final void setBaseman(String baseman) {
			this.baseman = baseman;
		}

		public final String getBasemancontact() {
			return basemancontact;
		}

		public final void setBasemancontact(String basemancontact) {
			this.basemancontact = basemancontact;
		}

		public final String getOpreceiver() {
			return opreceiver;
		}

		public final void setOpreceiver(String opreceiver) {
			this.opreceiver = opreceiver;
		}

		public final String getOpreceivercontact() {
			return opreceivercontact;
		}

		public final void setOpreceivercontact(String opreceivercontact) {
			this.opreceivercontact = opreceivercontact;
		}

		public final int getSiteId() {
			return siteId;
		}

		public final void setSiteId(int siteId) {
			this.siteId = siteId;
		}

		public final String getCywname() {
			return cywname;
		}

		public final void setCywname(String cywname) {
			this.cywname = cywname;
		}

		public final String getDomain99114() {
			return domain99114;
		}

		public final void setDomain99114(String domain99114) {
			this.domain99114 = domain99114;
		}

		public final List<ClassDomain> getDomain() {
			return domain;
		}

		public final void setDomain(List<ClassDomain> domain) {
			this.domain = domain;
		}

	}

	/**
	 * 一级域名
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class ClassDomain {
		String url = "";

		public final String getUrl() {
			return url;
		}

		public final void setUrl(String url) {
			this.url = url;
		}

	}

	/**
	 * 管理员
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class ClassManager {
		String name = "";

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

	}

	static final String getClassManagerList2String(List<ClassManager> list) {
		StringBuilder sb = new StringBuilder();
		int len = list.size();
		if (len == 0) return "";
		if (len == 1) return list.get(0).getName();
		sb.append('[');
		for (int i = 0; i < len; i++) {
			if (i > 0) sb.append(',');
			sb.append(list.get(i).name);
		}
		sb.append(']');
		return sb.toString();
	}
}
