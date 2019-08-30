package des.wangku.operate.wk;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.Test;

import des.wangku.operate.wk.Consts.LinkAccess;
import des.wangku.operate.wk.Consts.LinkMySQL;

public class Quarter {

	@Test
	public void test() {
		try {
			LinkAccess linkAccess=new LinkAccess();
			Statement state=linkAccess.dbconn();
			if(state==null) {
				System.out.println("state:null");
				return;
			}
			Connection connectAccess = state.getConnection();
			LinkMySQL linkMySQL = new LinkMySQL();
			Connection connect = linkMySQL.dbconn();
			String sql;
			sql="select "
					+ "a.member_id as '编号',"
					+ "b.corporation_name as '公司名称',"
					+ "a.telephone as '电话',"
					+ "a.mobile as '手机',"
					+ "a.link_man as '联系人姓名', "
					+ "(select name from wk_trade_shard1.sys_area where area_code= SUBSTRING(a.area_code,1,6) limit 1) as '省',"
					+ "(select name from wk_trade_shard1.sys_area where length(area_code)=9 and area_code= SUBSTRING(a.area_code,1,9) limit 1) as '市',"
					+ "(select name from wk_trade_shard1.sys_area where length(area_code)=12 and area_code= SUBSTRING(a.area_code,1,12) limit 1) as '区',"
					+ "a.email as '邮件' "
					+ " from "
					+ "wk_member_shard1.member a,"
					+ "wk_member_shard1.member_basic b,"
					+ "wk_member_shard1.member_detail e "
					+ "where "
					+ "a.member_id=b.member_id and "
					+ "a.member_id=e.member_id and "
					+ "a.area_code like '101103%' and "
					+ "a.mobile is not null and "
					+ "a.telephone is not null and "
					+ "a.telephone!='固定电话' and "
					+ "a.email is not null and "
					+ "a.email!='' and LOCATE('@',a.email)>0 and "
					+ "a.link_man is not null and "
					+ "a.status>-1 and "
					+ "a.add_time BETWEEN  '2017-10-01 00:00:00' and '2020-06-01 00:00:00' limit 60;";
			System.out.println(""+sql);
			Consts.viewColumnCount(connectAccess,connect,sql,"Quarter");
			
			
			

			linkMySQL.dbclose();
			linkAccess.dbclose();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
