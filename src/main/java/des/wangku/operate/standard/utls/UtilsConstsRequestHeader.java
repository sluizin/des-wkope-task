package des.wangku.operate.standard.utls;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求头部
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class UtilsConstsRequestHeader {
	
	/**
	 * 浏览器可接受的MIME类型	
	 */
	private static final String[] Accept = {
			"text/plain,text/html",
			"text/html,application/xhtml+xml,*/*",
			"text/html,application/xhtml+xml,application/xml;q=0.9,**",
			"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
			"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
			"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
			"application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"
	};
	
	/**
	 * 请求头用来告知（服务器）客户端可以处理的字符集类型
	 */
	private static final String[] Accept_Charset = {
			"utf-8, iso-8859-1;",
			"utf-8, iso-8859-1;q=0.5",
			"utf-8, iso-8859-1;q=0.5, *;q=0.1"
	};
	/**
	 * 浏览器能够进行解码的数据编码方式，比如gzip。Servlet能够向支持gzip的浏览器返
	 * 回经gzip编码的HTML页面。许多情形下这可以减少5到10倍的下载时间 
	 */
	private static final String[] Accept_Encoding = {
			"compress, gzip",
			"gzip, deflate",
			"gzip, deflate, br",
			"gzip, deflate, sdch"
	};
	
	/**
	 * 浏览器所希望的语言种类，当服务器能够提供一种以上的语言版本时要用到 
	 */
	private static final String[] Accept_Language = {
			"zh-CN",
			"zh-CN,zh;q=0.8",
			"zh-CN,zh;q=0.9,en;q=0.8",
			"zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3",
			"zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2"
	};
	
	/**
	 * 
	 */
	private static final String[] Content_Type = {
			"application/xml",
			"application/json",
			"application/json;charset=UTF-8;",
			"application/x-www-form-urlencoded",
			"application/octet-stream",
			"text/*, application/xml",
			"text/html;charset:UTF-8;",
			"text/plain;charset:UTF-8;",
			"text/xml;charset:UTF-8;",
			"multipart/form-data"
	};
	
	/**
	 * Cookie
	 */
	private static final String[] Cookie = {
			"_ga=GA1.2.260221633.1596016850; _gid=GA1.2.1230793865.1596016850; _gat=1",
			"Hm_lvt_f160a8c6f9e75fcc0c45d38950c1b318=1596086031; Hm_lpvt_f160a8c6f9e75fcc0c45d38950c1b318=1596086031",
			"Hm_lvt_f160a8c6f9e75fcc0c45d38950c1b318=1596077172; Hm_lpvt_f160a8c6f9e75fcc0c45d38950c1b318=1596077172",
			"Hm_lvt_f160a8c6f9e75fcc0c45d38950c1b318=1596086031; Hm_lpvt_f160a8c6f9e75fcc0c45d38950c1b318=1596086053",
			"UM_distinctid=172c13dd48f13b-05752d3f1d3dc2-6b131b7e-100200-172c13dd490410; Hm_lvt_92381f24aff7a81d183aae59846dcd7e=1594193827,1594345888,1594715548,1594863863; Hm_lvt_d4d56ed0532c01d0b8ba1211350e2182=1595470941,1595901173,1596000075,1596079645; Hm_lpvt_d4d56ed0532c01d0b8ba1211350e2182=1596079645",
			"UM_distinctid=173742ac56def-0829086d1d0ef9-15117b52-100200-173742ac56e225; CNZZDATA4201020=cnzz_eid%3D1888328995-1595380551-%26ntime%3D1595380551; Hm_lvt_92381f24aff7a81d183aae59846dcd7e=1595381828; ASPSESSIONIDQQBAQARR=POJJHDBBCFIFOOPIMNLHILMA; ASPSESSIONIDQQCARAQR=MPJLHDBBPFLBHICMGLPIGNDB; CookieCode=2Q1F",
			"_ga=GA1.2.1331343529.1542942914; pgv_pvi=7563932672; UM_distinctid=171052756c31c5-0a15715177b43c-141d7e56-100200-171052756c4575; gr_user_id=7b1d1915-299a-4aad-8f3e-81228582eaed; grwng_uid=c84ccf19-d2ee-433d-824c-324192341d53; Hm_lvt_f160a8c6f9e75fcc0c45d38950c1b318=1595473625; ASPSESSIONIDAAQRDARB=MLENOOBBCCLJGIDODILABIIK; CookieCode=9UN4",
			"UM_distinctid=172c13dd48f13b-05752d3f1d3dc2-6b131b7e-100200-172c13dd490410; Hm_lvt_92381f24aff7a81d183aae59846dcd7e=1594193827,1594345888,1594715548,1594863863; Hm_lvt_d4d56ed0532c01d0b8ba1211350e2182=1595470941,1595901173,1596000075,1596079645; Hm_lpvt_d4d56ed0532c01d0b8ba1211350e2182=1596079645; ASPSESSIONIDAQTCCQTT=GPFNBPBBJGOHKGCGOHLIGGFO",
			"pgv_pvid=1475993176; pgv_pvi=2068243456; RK=ETilnJmQEO; ptcz=7ab91a7e1ddbdd78d562445d8d36b8370828c0d6485e77e0a2898c8dc55a2173; o_cookie=75583378; pac_uid=1_75583378; bid=bd1a61ed-eb68-4f1b-9805-c95a8e4602a3; tvfe_boss_uuid=781dea2e78830a2d; XWINDEXGREY=0; ied_qq=o0075583378; pgv_si=s7516335104; _qpsvr_localtk=0.07781598395854683; bidjs=58c786fa-ba38-4b16-8135-ab60e3829e9c; wxuin=96090485889220",
			"_ga=GA1.2.1331343529.1542942914; Hm_lvt_e6b45e1a03c715ceee8117ed66d9b93d=1565769001; pgv_pvi=7563932672; Hm_lvt_f9bbf3ae1268183c96aeb7b7cbd01955=1571968391; Hm_lvt_68ac391f0593b7b1b76c8324474cdcbc=1571987192; Hm_lvt_459e698678e58b2571825553c124ef0b=1574132199; Hm_lvt_23859c6bc63ba37de8d5080c2c7829d2=1575530319; Hm_lvt_db29d8d5dccd6c6cb1a89b09490ca24e=1575533466,1575609176,1575858034; Hm_lvt_d13b130ff6031f9f3f568f498ff99df7=1575004429,1575253740,1577089903; Hm_lvt_88da7b228c1555b621997eaf5259c0dd=1577699558; UM_distinctid=171052756c31c5-0a15715177b43c-141d7e56-100200-171052756c4575; Hm_lvt_8f545a3daa4f8f448e7a1da4f38033b9=1586400938; Hm_lvt_d1b4903484be6bae625c908e35fc1d9e=1587955274; Hm_lvt_95a4e97b8cde3be6f484b2b366eaa244=1591003492; Hm_lvt_f96f409593ef7039b9910fb4c5a873aa=1591003492; Hm_lvt_aa653a55944c0506c2de09685a56934a=1591343445; Hm_lvt_5426d8df1c982209375c4d159d597a95=1591318968,1591596348; Hm_lvt_2522e90d9641c4c4e73b375985a5a033=1593478080; Hm_lvt_9184928c9ebfb7e43fa8af01baefa0a6=1595225196; gr_user_id=7b1d1915-299a-4aad-8f3e-81228582eaed; grwng_uid=c84ccf19-d2ee-433d-824c-324192341d53; Hm_lvt_f160a8c6f9e75fcc0c45d38950c1b318=1595473625; Hm_lvt_00b90e2ce8410db9b4b865c479a897b7=1594343523,1595225196,1595325964,1596010052; Hm_lvt_2647523f610f049a950094cf25d5e77b=1594189646,1594343523,1596010052",
			"_ga=GA1.2.1331343529.1542942914; Hm_lvt_e6b45e1a03c715ceee8117ed66d9b93d=1565769001; pgv_pvi=7563932672; Hm_lvt_f9bbf3ae1268183c96aeb7b7cbd01955=1571968391; Hm_lvt_68ac391f0593b7b1b76c8324474cdcbc=1571987192; Hm_lvt_459e698678e58b2571825553c124ef0b=1574132199; Hm_lvt_23859c6bc63ba37de8d5080c2c7829d2=1575530319; Hm_lvt_db29d8d5dccd6c6cb1a89b09490ca24e=1575533466,1575609176,1575858034; Hm_lvt_d13b130ff6031f9f3f568f498ff99df7=1575004429,1575253740,1577089903; Hm_lvt_88da7b228c1555b621997eaf5259c0dd=1577699558; UM_distinctid=171052756c31c5-0a15715177b43c-141d7e56-100200-171052756c4575; Hm_lvt_8f545a3daa4f8f448e7a1da4f38033b9=1586400938; Hm_lvt_d1b4903484be6bae625c908e35fc1d9e=1587955274; Hm_lvt_95a4e97b8cde3be6f484b2b366eaa244=1591003492; Hm_lvt_f96f409593ef7039b9910fb4c5a873aa=1591003492; Hm_lvt_aa653a55944c0506c2de09685a56934a=1591343445; Hm_lvt_5426d8df1c982209375c4d159d597a95=1591318968,1591596348; Hm_lvt_2522e90d9641c4c4e73b375985a5a033=1593478080; Hm_lvt_9184928c9ebfb7e43fa8af01baefa0a6=1595225196; gr_user_id=7b1d1915-299a-4aad-8f3e-81228582eaed; grwng_uid=c84ccf19-d2ee-433d-824c-324192341d53; Hm_lvt_f160a8c6f9e75fcc0c45d38950c1b318=1595473625; Hm_lvt_2647523f610f049a950094cf25d5e77b=1594189646,1594343523,1596010052; Hm_lvt_00b90e2ce8410db9b4b865c479a897b7=1595225196,1595325964,1596010052,1596079811; Hm_lpvt_00b90e2ce8410db9b4b865c479a897b7=1596079811",
			"PSTM=1542011390; BIDUPSID=EF309A51D38CDB56B8B7B8F0A9BF85E2; MSA_WH=1364_636; COOKIE_SESSION=0_11_1_1_1_b2_0_1_5_0_0_1_28_1588066233%7C1%230_0_0_0_0_0_0_0_1588066233%7C1; FC_MODEL=-1_23_3_0_0.57_0_6_0_0_0_27.49_-1_8_0_8_67_0_1588066261_1588066233%7C9%230.57_-1_-1_9_9_1588066261_1588066233%7C9%230_adg_5_0_1_0_201_1588066233; MCITY=-%3A; BAIDUID=2D7173F7666730C3F3AE794EE03CD6D3:FG=1; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; BDUSS=E93Q0RTflJic1ZlMnh2ZEFQaHU1T09lM0VvOFFJbVJ5cW5MREVmdTRzdXpxVWxmRUFBQUFBJCQAAAAAAAAAAAEAAABtjdJKX7H5t-LHp8TqvP1fAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALMcIl-zHCJfa; delPer=0; PSINO=7; ZD_ENTRY=baidu; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; H_PS_PSSID=32293_1433_32360_32348_32045_32398_32404_32429_32116_32297_26350_32436_31640; H_WISE_SIDS=148078_152521_147937_147209_152354_150686_150077_147089_151494_150086_148867_151311_148713_150745_147279_152309_150647_150165_151561_148303_148523_127969_148795_149719_146652_151319_151954_146732_145788_150437_131423_152017_144659_107313_151497_151579_152275_149253_150907_152301_152153_144966_152272_152513_146785_150341_149807_152247_147547_148868_151703_110085; FEED_SIDS=3000066_2; __bsi=11129386015480856026_00_17_N_N_0_0303_c02f_Y; plus_lsv=e1339ee5f098ff6b; plus_cv=1::m:49a3f4a6; Hm_lvt_12423ecbc0e2ca965d84259063d35238=1596101933; SE_LAUNCH=5%3A26601698; rsv_i=f38aZnkUPhSq9A%2BkrzXTZlxRWgtSNVGEH50aulOtNAfQkJwAjn%2B5iIvkdGtPnKg8KISKFeBwLwbf2CDTdd6ZfDH3O5FrzI8; BDSVRTM=485; Hm_lpvt_12423ecbc0e2ca965d84259063d35238=1596101947; BDSVRBFE=Go; wise_tj_ub=ci%40-1_-1_-1_-1_-1_-1_-1%7Ciq%4012_1_8_87%7Ccb%40-1_-1_-1_-1_-1_-1_-1%7Cce%401%7Ctse%401; BDICON=10123156"
	};
	
	/**
	 * 浏览器类型，如果Servlet返回的内容与浏览器类型有关则该值非常有用
	 */
	public static final String[] User_Agent = {
			"UCWEB7.0.2.37/28/999",
			"Mozilla/5.0 (Linux; X11)",
			"Openwave/ UCWEB7.0.2.37/28/999",
			"NOKIA5700/ UCWEB7.0.2.37/28/999",
			"Mozilla/4.0(compatible;MSIE5.01;Window NT5.0)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; The World)",
			"Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11",
			"Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/999",
			"Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0",
			"Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB7.0)",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0",
			"Mozilla/5.0 (Androdi; Linux armv7l; rv:5.0) Gecko/ Firefox/5.0 fennec/5.0",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)",
			"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:79.0) Gecko/20100101 Firefox/79.0",
			"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0; HTC; Titan)",
			"Opera/9.80 (Android 2.3.4; Linux; Opera mobi/adr-1107051709; U; zh-cn) Presto/2.8.149 Version/11.10",
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; ) AppleWebKit/534.12 (KHTML, like Gecko) Maxthon/3.0 Safari/534.12",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4181.9 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.9 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3610.2 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36",
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; InfoPath.2; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; 360SE)",
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1 QQBrowser/6.9.11079.201",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.22 Safari/537.36 SE 2.X MetaSr 1.0",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36 Maxthon/5.3.8.2000",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)",
			"Mozilla/5.0 (Linux; U; Android 2.2.1; zh-cn; HTC_Wildfire_A3333 Build/FRG83D) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1",
			"Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Win64; x64; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; .NET4.0E)",
			"MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.3)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; Tablet PC 2.0; .NET4.0E)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E) QQBrowser/6.9.11079.201"
	};
	/**
	 * 设置随机默认值
	 * @param map Map&lt;String, String&gt;
	 * @param arr String[]
	 * @param key String
	 */
	private static final void setRndVal(Map<String, String> map,String[] arr,String key) {
		int len = arr.length;
		if(len == 0)return;
		if(len == 1) {
			map.put(key, arr[0]);
			return;
		}
		int v = UtilsRnd.getRndInt(1, len);
		map.put(key, arr[v-1]);
	}
	static final Map<String,String> StaticBasicMap = new HashMap<>(10);
	static {
		StaticBasicMap.put("Connection", "keep-alive");
		StaticBasicMap.put("Cache-Control", "max-age=0");
		
		StaticBasicMap.put("Sec-Fetch-Dest", "document");
		StaticBasicMap.put("Sec-Fetch-Mode", "navigate");
		StaticBasicMap.put("Sec-Fetch-Site", "cross-site");
		StaticBasicMap.put("Sec-Fetch-User", "?1");
		
		StaticBasicMap.put("Upgrade-Insecure-Requests", "1");
		StaticBasicMap.put("DNT", "1");
	}
	/**
	 * 得到随机头部文件
	 * @return Map&lt;String, String&gt;
	 */
	public static final Map<String, String> getRndHeadMap(){
		Map<String, String> map=new HashMap<>(20);
		map.putAll(StaticBasicMap);
		setRndVal(map,Accept,"Accept");
		setRndVal(map,Accept_Charset,"Accept-Charset");
		setRndVal(map,Accept_Encoding,"Accept-Encoding");
		setRndVal(map,Accept_Language,"Accept-Language");
		setRndVal(map,Cookie,"Cookie");
		setRndVal(map,Content_Type,"Content-Type");
		setRndVal(map,User_Agent,"User-Agent");

		return map;
	}
	/**
	 * 得到随机头部文件
	 * @param host String
	 * @return Map&lt;String, String&gt
	 */
	public static final Map<String, String> getRndHeadMap(String host){
		Map<String, String> map=getRndHeadMap();
		if(host==null || host.length()==0)return map;
		String value=UtilsReadURL.getHost(host);
		map.put("Host", value);
		return map;
	}
	public static final String getRndHeadMapString(String host){
		Map<String, String> map=getRndHeadMap(host);
		StringBuilder sb=new StringBuilder(100);
		for(String key : map.keySet()){
		    String value = map.get(key);
		    sb.append(key+":"+value+"\r\n");
		}
		return sb.toString();
	}
}
