package des.wangku.operate.standard.utls;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 发送邮件 commons-email 组件
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class UtilsEmail {
	/** 日志 */
	static Logger logger = LoggerFactory.getLogger(UtilsEmail.class);

	/**
	 * 发送 邮件方法 (Html格式，支持附件)
	 * @return void
	 */
	public static void sendEmail(MailInfo mailInfo) {

		try {
			HtmlEmail email = new HtmlEmail();
			// 配置信息
			email.setHostName(mailInfo.senderServerHost);
			email.setFrom(mailInfo.senderAddress, mailInfo.senderNick);
			email.setAuthentication(mailInfo.senderUsername, mailInfo.senderPassword);
			email.setCharset("UTF-8");
			email.setSubject(mailInfo.getSubject());
			email.setHtmlMsg(mailInfo.getContent());

			// 添加附件
			List<EmailAttachment> attachments = mailInfo.attachments;
			for (EmailAttachment e : attachments)
				email.attach(e);
			// 收件人
			List<String> toAddress = mailInfo.toAddress;
			for (String e : toAddress)
				email.addTo(e);
			// 抄送人
			List<String> ccAddress = mailInfo.ccAddress;
			for (String e : ccAddress)
				email.addCc(e);
			//邮件模板 密送人
			List<String> bccAddress = mailInfo.bccAddress;
			for (String e : bccAddress)
				email.addBcc(e);
			email.send();
			System.out.println("邮件发送成功！");
		} catch (EmailException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送邮件内容
	 * @author Sunjian
	 * @version 1.0
	 * @since jdk1.8
	 */
	public static class MailInfo {
		String senderServerHost = "smtp.99114.com";
		String senderAddress = "";
		String senderNick = "";
		String senderUsername = "";
		String senderPassword = "";
		// 收件人
		List<String> toAddress = new ArrayList<>();
		// 抄送人地址
		List<String> ccAddress = new ArrayList<>();
		// 密送人
		List<String> bccAddress = new ArrayList<>();
		// 附件信息
		List<EmailAttachment> attachments = new ArrayList<>();
		// 邮件主题
		String subject = null;
		// 邮件的文本内容
		String content = "";

		/**
		 * senderServerHost<br>
		 * senderAddress<br>
		 * senderNick<br>
		 * senderUsername<br>
		 * senderPassword<br>
		 * @param arrs String[]
		 */
		public final MailInfo set(String... arrs) {
			if (arrs.length > 0) senderServerHost = arrs[0];
			if (arrs.length > 1) senderAddress = arrs[1];
			if (arrs.length > 2) senderNick = arrs[2];
			if (arrs.length > 3) senderUsername = arrs[3];
			if (arrs.length > 4) senderPassword = arrs[4];
			return this;
		}

		public final String getSubject() {
			return subject;
		}

		public final void setSubject(String subject) {
			this.subject = subject;
		}

		public final String getContent() {
			return content;
		}

		public final void setContent(String content) {
			this.content = content;
		}

		/**
		 * 收件人
		 * @param arrs String[]
		 */
		public void addToAddress(String... arrs) {
			for (String e : arrs) {
				if (e == null || e.trim().length() == 0) continue;
				if (toAddress.contains(e)) continue;
				toAddress.add(e);
			}
		}

		/**
		 * 抄送人地址
		 * @param arrs String[]
		 */
		public void addCcAddress(String... arrs) {
			for (String e : arrs) {
				if (e == null || e.trim().length() == 0) continue;
				if (ccAddress.contains(e)) continue;
				ccAddress.add(e);
			}
		}

		/**
		 * 密送人
		 * @param arrs String[]
		 */
		public void addBccAddress(String... arrs) {
			for (String e : arrs) {
				if (e == null || e.trim().length() == 0) continue;
				if (bccAddress.contains(e)) continue;
				bccAddress.add(e);
			}
		}

		/**
		 * 附件
		 * @param arrs String[]
		 */
		public final void addAttachmentsPath(String... arrs) {
			for (String e : arrs) {
				if (e == null || e.trim().length() == 0) continue;
				EmailAttachment att = new EmailAttachment();
				att.setPath(e);
				addAttachments(att);
			}
		}

		/**
		 * 附件
		 * @param arrs EmailAttachment[]
		 */
		public final void addAttachments(EmailAttachment... arrs) {
			for (EmailAttachment e : arrs) {
				if (e == null) continue;
				if (attachments.contains(e)) continue;
				attachments.add(e);
			}
		}

		public final String getSenderServerHost() {
			return senderServerHost;
		}

		public final void setSenderServerHost(String senderServerHost) {
			this.senderServerHost = senderServerHost;
		}

		public final String getSenderAddress() {
			return senderAddress;
		}

		public final void setSenderAddress(String senderAddress) {
			this.senderAddress = senderAddress;
		}

		public final String getSenderNick() {
			return senderNick;
		}

		public final void setSenderNick(String senderNick) {
			this.senderNick = senderNick;
		}

		public final String getSenderUsername() {
			return senderUsername;
		}

		public final void setSenderUsername(String senderUsername) {
			this.senderUsername = senderUsername;
		}

		public final String getSenderPassword() {
			return senderPassword;
		}

		public final void setSenderPassword(String senderPassword) {
			this.senderPassword = senderPassword;
		}
	}

	public static void main(String[] args) {
		MailInfo mailInfo = (new MailInfo()).set("smtp.99114.com", "sunjian@99114.com", "用户发来信息", "sunjian@99114.com", "Sunjian1978");
		mailInfo.addToAddress("sunjian@99114.com");
		//添加附件
		mailInfo.addAttachmentsPath("G:/sunjian/calendar1.gif");
		mailInfo.setSubject("某个网站来的主题");
		mailInfo.setContent("内容：<h1>test,测试</h1>");

		UtilsEmail.sendEmail(mailInfo);

	}

}
