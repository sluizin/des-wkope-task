package des.wangku.operate.standard.utls;

import java.io.IOException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 模拟登陆后提出的Html()
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@Deprecated
public class UtilsSimulateLogin {

	/**
	 * 网址中含有此域名者都要用ie进行Agent
	 */
	public static final String[] ACC_IECore_URLs= {"aizhan.com"};
	

	/**
	 * 域名是否要用ie进行Agent
	 * @param url URL
	 */
	public static final boolean isIECore(URL url) {
		if(url==null)return false;
		String domain=url.getHost();
		for(String e:ACC_IECore_URLs) {
			if(domain.indexOf(e)>-1) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 域名是否要用ie进行Agent
	 * @param url URL
	 */
	public static final boolean isIECore(String url) {
		if(url==null)return false;
		try {
			URL urla=new URL(url);
			return isIECore(urla);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public interface ProgressListenerExt extends ProgressListener{
		public default void workExt() {
			
		};
	}
	public static abstract class SimulateClass implements ProgressListenerExt{
		Document doc=null;
		Browser browser=null;
		public SimulateClass(Composite parent,String url) {
			browser=new Browser(parent, SWT.NONE);
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
			String content=browser.getText();
			this.doc=UtilsJsoupExt.getDocument(content);
			work();
			workExt();
		}
		public abstract void work() ;
		public final Document getDoc() {
			return doc;
		}
		
		
		
	}
	public static final void getBrowserDoc(URL url,Composite parent,SimulateClass listener) {
		if(!isIECore(url))return;
		Browser browser=new Browser(parent, SWT.NONE);
		browser.setVisible(false);
		browser.setBounds(0, 0, 2, 2);
		browser.addProgressListener(listener);
		browser.setUrl(url.toString());
		
	}
	public static final Browser getBrowser(URL url,Composite parent,ProgressListener listener) {
		if(!isIECore(url))return null;
		Browser browser=new Browser(parent, SWT.NONE);
		browser.setVisible(false);
		browser.setBounds(0, 0, 2, 2);
		browser.addProgressListener(listener);
		browser.setUrl(url.toString());
		return browser;
	}
	
	
	
	
	public static final String getHtml(Browser browser,URL url) {
		/*
		Browser browser=new Browser(parent, SWT.NONE);
		browser.setVisible(false);
		browser.setBounds(0, 0, 2, 2);*/
		//browser=new Browser(parent, SWT.NONE);
		System.out.println("url:"+url);
		String urlpath=url.toString();
		browser.setUrl(urlpath);
		browser.refresh();
		browser.update();
		browser.redraw();
		String content=browser.getText();
		System.out.println("content:"+content);
		return content;
		//return "";
	}
	static final WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);

	static {
		webClient.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true  
		webClient.getOptions().setCssEnabled(false); //禁用css支持  
		webClient.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常  
		webClient.getOptions().setTimeout(10000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待  
		webClient.waitForBackgroundJavaScript(8000);
		webClient.getOptions().setAppletEnabled(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.setCssErrorHandler(new SilentCssErrorHandler());
		webClient.getOptions().setPopupBlockerEnabled(true);
		webClient.getOptions().setRedirectEnabled(true);		
	}
	static WebClient webClient22 = new WebClient(BrowserVersion.INTERNET_EXPLORER);
    static {
        webClient22.getOptions().setTimeout(20000);
        // webClient.getCookieManager().setCookiesEnabled(true);
        webClient22.getOptions().setThrowExceptionOnFailingStatusCode(true);
        webClient22.getOptions().setThrowExceptionOnScriptError(false);
        webClient22.getOptions().setCssEnabled(true);
        webClient22.getOptions().setJavaScriptEnabled(true);
        webClient22.addRequestHeader("Accept", "textml,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        webClient22.addRequestHeader("Accept-Encoding", "gzip, deflate");
        webClient22.addRequestHeader("Accept-Language", "en-US,en;q=0.5");
        webClient22.addRequestHeader("Cache-Control", "max-age=0");
        webClient22.addRequestHeader("Connection", "keep-alive");
        webClient22.addRequestHeader("Host", "baidurank.aizhan.com");
        webClient22.addRequestHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
    	
    }
	public static final String getHtml3(URL url) {
        try {
        	HtmlPage page =webClient22.getPage(url);
			String pageXml = page.asXml(); //以xml的形式获取响应文本 
			System.out.println("pageXml:"+pageXml);
			return pageXml;
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
        
	}
	public static final String getHtml2(URL url) {
		try {
			System.out.println("url:"+url.toString());
			HtmlPage page = webClient.getPage(url);
			String pageXml = page.asXml(); //以xml的形式获取响应文本 
			System.out.println("pageXml:"+pageXml);
			//wc.close();
			return pageXml;
		} catch (Exception e) {
			//if (wc != null) wc.close();
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	
}
