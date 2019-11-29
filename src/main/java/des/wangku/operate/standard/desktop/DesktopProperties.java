package des.wangku.operate.standard.desktop;

import java.util.Properties;

import des.wangku.operate.standard.task.InterfaceProperties;
import des.wangku.operate.standard.utls.UtilsPathFile;
import des.wangku.operate.standard.utls.UtilsProperties;
/**
 * 桌面程序中的配置信息
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public final class DesktopProperties implements InterfaceProperties {
	@Override
	public Properties getProProperties() {
		Properties properties = new Properties();
		String filename = UtilsPathFile.getJarBasicPathconfig() + "/"+DesktopConst.ACC_DesktopProperties;
		UtilsProperties.loadProperties(properties, filename);
		return properties;
	}
}
