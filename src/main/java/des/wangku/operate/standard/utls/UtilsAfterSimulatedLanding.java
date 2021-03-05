package des.wangku.operate.standard.utls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 模拟ie登陆后的html
 * 通过Browser得到之后的html代码
 * 只是嵌入监听器中
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsAfterSimulatedLanding {
	public static abstract class AbstractSimulateClass implements ProgressListener{
		/** 日志 */
		Logger logger = LoggerFactory.getLogger(UtilsAfterSimulatedLanding.class);
		protected String url="";
		protected Browser browser=null;
		protected Document doc=null;
		/**
		 * 开始操作
		 */
		public void start(Composite parent,String url) {
			this.url=url;
			browser=new Browser(parent, SWT.NONE  );
			browser.setVisible(false);
			browser.setBounds(0, 0, 2, 2);
			browser.addProgressListener(this);
			browser.setUrl(url);
		}
		@Override
		public void changed(ProgressEvent event) {
			
		}

		@Override
		public void completed(ProgressEvent event) {
			logger.debug("Browser 提取网址完成，进行操作:"+url);
			String content=browser.getText();
			doc=UtilsJsoupExt.getDocument(content);
			work();
			//browser.dispose();
		}
		/**
		 * 对Document doc进行操作即可<br>
		 * doc里含有登陆后的数据<br>
		 */
		public abstract void work();
	}
}
