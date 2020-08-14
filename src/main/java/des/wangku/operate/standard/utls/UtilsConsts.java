package des.wangku.operate.standard.utls;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量池
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsConsts {
	public static final String ACC_ENTER = System.getProperty("line.separator");
	private static final Map<String, String> header = new HashMap<>();

	private static final Map<String, String> header_a = new HashMap<>();
	
	private static final Map<String, String> header_b = new HashMap<>();
	
	private static final Map<String, String> header_c = new HashMap<>();
	
	private static final Map<String, String> header_d = new HashMap<>();
	
	private static final Map<String, String> header_e = new HashMap<>();
	static {
		//设置请求头
		header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
		header.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
		header.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		header.put("Accept-Encoding", "gzip, deflate");
		header.put("X-Requested-With", "XMLHttpRequest");
		header.put("Content-Type", "text/*, application/xml");
		header.put("Connection", "keep-alive");

		header_a.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.22 Safari/537.36 SE 2.X MetaSr 1.0");
		header_a.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header_a.put("Accept-Language", "zh-CN,zh;q=0.8");
		header_a.put("Accept-Encoding", "gzip, deflate, sdch");
		header_a.put("Content-Type", "application/octet-stream");
		header_a.put("Connection", "keep-alive");
		header_a.put("Upgrade-Insecure-Requests", "1");
		
		
		header_b.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header_b.put("Accept-Encoding", "gzip, deflate, br");
		header_b.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
		header_b.put("Connection", "keep-alive");
		header_b.put("Host", "fanyi.baidu.com");
		header_b.put("Content-Type", "application/json;charset=UTF-8");
		//header_c.put("Referer", "http://www.seodo.cn/Quote");
		header_b.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3610.2 Safari/537.36");
		header_b.put("X-Requested-With", "XMLHttpRequest");
		//header_b.put("Upgrade-Insecure-Requests", "1");
		header_b.put("Cookie", "yunsuo_session_verify=383809ce78db30d2ad2ff0d3c4686578; Hm_lvt_294353148bfd9c1037daea2fa1c2c537=1542008107; __root_domain_v=.seodo.cn; _qddaz=QD.xz9ou4.cqswqk.joe0qg19; _qdda=3-1.1; _qddab=3-az79v8.joe2tf02; _qddamta_2852061168=3-0; __RequestVerificationToken=xtnqVeX50c5pHpZyf-nVFDWp93sNArwOh7VfA7fJrTg479Do5ZVpKDv8XmqUtWWzdDcm8QhTd6LJk9GjBgWMtKU6d-16hOYeY1TC3Aj5Yk1_a76m8ghy6x_gzwuw4vt-hNHTrRC3vpeOPEusUqG5Sg2; _qddac=3-2-1.1.az79v8.joe2tf02; Hm_lpvt_294353148bfd9c1037daea2fa1c2c537=1542015029");
	

		header_c.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.9 Safari/537.36");
		header_c.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		header_c.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
		header_c.put("Accept-Encoding", "gzip, deflate, br");
		header_c.put("Cache-Control", "max-age=0");
		header_c.put("Connection", "keep-alive");
		header_c.put("Host", "m.baidu.com");
		header_c.put("Sec-Fetch-Dest", "document");
		header_c.put("Sec-Fetch-Mode", "navigate");
		header_c.put("Sec-Fetch-Site", "none");
		header_c.put("Sec-Fetch-User", "?1");
		header_c.put("Upgrade-Insecure-Requests", "1");
		header_c.put("Cookie", "PSTM=1542011390; BIDUPSID=EF309A51D38CDB56B8B7B8F0A9BF85E2; BAIDUID=A08623F1A622B2887A4BF8B272C81214:FG=1; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; yjs_js_security_passport=a28e592603241ea4ac5e5d689d84ee2235d537e7_1585099816_js; BDUSS=GdORjZ-TXhpOU0wS2daVmR3eC1vbG1NekN2TUZwTW9kVkdpUUVIUUFlaENRYUplRUFBQUFBJCQAAAAAAAAAAAEAAABtjdJKX7H5t-LHp8TqvP1fAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEK0el5CtHpec; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; delPer=0; BDRCVFR[dG2JNJb_ajR]=mk3SLVN4HKm; BDRCVFR[-pGxjrCMryR]=mk3SLVN4HKm; BDPASSGATE=IlPT2AEptyoA_yiU4VKD3kIN8efjSvCA1fe2SCRXQlW4fCaWmhH3BrUrWz0HSieXBDP6wZTXebZda5XKXlVXa_EqnBsZp5pQeUPcxvqOucPTKtB88b1yPafDX8UasgqbhKlOcAYgRNE8Ay5DjQPRp3EEdh_l73JQb4r55EDCmMrrFzqOD942ymyqOXVfZnXcKsiNwhjXnEpKKSmBUuL2LTPPiFcMXWcu8d39gs5Q0w4mpj9yHQjSRvAa1G85Jppg0ha43haelMLSAF5UvY2YVlI6-EOIzqnxSSxM2rvx5K; ASUV=1.2.126; SE_LAUNCH=5%3A26418679; ysm=7922|7922; MSA_WH=1364_636; MSA_PBT=146; MSA_ZOOM=1056; COOKIE_SESSION=0_0_0_1_0_w3_0_1_0_0_0_1_4_1585122047%7C1%230_0_0_0_0_0_0_0_1585122047%7C1; wpr=3; FC_MODEL=-1_14_3_0_0.25_0_6_0_0_0_1.71_-1_8_0_8_56_0_1585122059742_1585122047800%7C9%230.25_-1_-1_9_9_1585122059742_1585122047800%7C9; H_WISE_SIDS=141911_143845_122159_140843_143435_141129_143861_142113_143879_142357_139057_141746_143161_143788_143448_142071_142780_131247_137746_138165_138883_140259_141941_127969_140066_143997_140593_143058_141807_140235_140350_138426_141009_143469_131423_144238_143522_107315_138595_144106_143478_142912_140312_138663_136751_110085; __bsi=11523301866440624390_00_10_R_R_21_0303_c02f_Y");
		
		header_d.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4181.9 Safari/537.36");
		header_d.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		header_d.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
		header_d.put("Accept-Encoding", "gzip, deflate, br");
		header_d.put("Cache-Control", "max-age=0");
		header_d.put("Connection", "keep-alive");
		header_d.put("Host", "m.baidu.com");
		header_d.put("Sec-Fetch-Dest", "document");
		header_d.put("Sec-Fetch-Mode", "navigate");
		header_d.put("Sec-Fetch-Site", "cross-site");
		header_d.put("Sec-Fetch-User", "?1");
		header_d.put("Upgrade-Insecure-Requests", "1");
		header_d.put("Cookie", "_ga=GA1.2.988265835.1542072590; __gads=ID=c78675f14ac190bd:T=1542072645:S=ALNI_Mb5JbmY1kfK6IcUZxLOReW3k7mj8g; Hm_lvt_d8d668bc92ee885787caab7ba4aa77ec=1564737435; _dx_uzZo5y=d083ff246315cddd3e5c53a925b0ef9c81eb8f691808cdc7c4075fd3b9714290a9a14694; Hm_lvt_10eee3a589e4747cd2e9b98300655039=1579486561,1579486951; CNZZDATA1263762152=2116773890-1583200699-https%253A%252F%252Fwww.baidu.com%252F%7C1583200699; CNZZDATA2554769=cnzz_eid%3D767279222-1586250317-https%253A%252F%252Fwww.baidu.com%252F%26ntime%3D1586250317; Hm_lvt_cc17b07fc9529e3d80b4482c9ce09086=1586251574; CNZZDATA1259255955=72674-1589261354-https%253A%252F%252Fwww.baidu.com%252F%7C1589261354; CNZZDATA1277383466=1992895375-1592198448-https%253A%252F%252Fwww.baidu.com%252F%7C1592198448; CNZZDATA1274331442=1258411351-1594718642-https%253A%252F%252Fwww.baidu.com%252F%7C1594718642; UM_distinctid=1736b17a4cb13b-05fbe6a9cfcf9d-15117b52-100200-1736b17a4cc597; CNZZDATA5897703=cnzz_eid%3D1519376888-1592458189-https%253A%252F%252Fwww.baidu.com%252F%26ntime%3D1595228084; CNZZDATA1261691463=1297589882-1592457029-https%253A%252F%252Fwww.baidu.com%252F%7C1595228932; _gid=GA1.2.120995078.1595831096; _gat=1");
				

		header_e.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header_e.put("Accept-Encoding", "gzip, deflate, br");
		header_e.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
		header_e.put("Cache-Control", "max-age=0");
		header_e.put("Connection", "keep-alive");
		header_e.put("Cookie", "_ga=GA1.2.260221633.1596016850; _gid=GA1.2.1230793865.1596016850; _gat=1");
		header_e.put("DNT", "1");
		header_e.put("Upgrade-Insecure-Requests", "1");
		header_e.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0");
		

	}
	/**
	 * 得到随机头部文件
	 * @return Map&lt;String, String&gt;
	 */
	public static final Map<String, String> getRndHeadMap1(){
		int v=UtilsRnd.getRndInt(0, 5);
		switch(v) {
		case 0:return header;
		case 1:return header_a;
		case 2:return header_b;
		case 3:return header_c;
		case 4:return header_d;
		case 5:return header_e;
		default:
			return header;
		}
	}
	/**
	 * 得到随机头部文件
	 * @return Map&lt;String, String&gt;
	 */
	public static final Map<String, String> getRndHeadMap(){
		return UtilsConstsRequestHeader.getRndHeadMap();
	}
}
