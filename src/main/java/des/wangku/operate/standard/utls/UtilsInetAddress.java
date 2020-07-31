package des.wangku.operate.standard.utls;

import java.net.InetAddress;
/**
 * 得到本地的一些信息
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsInetAddress {
	/**
	 * 得到本机名称 默认为Unknown
	 * @return String
	 */
	public static final String getBasicName() {
		String hostname = "Unknown";
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch (Exception ex)
        {
            System.out.println("Hostname can not be resolved");
        }
        return hostname;
	}
}
