package com.hansan.fenxiao.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 项目名称：springmvc_hibernate 
 * 类名称：EmailUtil 
 * 类描述：发送邮件工具类 
 * 创建人：Cherish
 * 联系方式：785427346@qq.com 
 * 创建时间：2016年4月23日 上午9:48:21
 * @version 1.0
 */
public class EmailUtil {

	// properties配置文件地址
	//private static final String PROPERTIES_PATH = "standard_data.properties";

	private static Session session;
	private static Properties props = null;
	private static final String HOST = "smtp.qq.com";
	private static int PORT = 587;
	private static final String isAUTH = "true";
	private static String FROM = "823382133@qq.com";

	private static String USERNAME = "823382133@qq.com";
	private static String PASSWORD = "gpogqyuyqjlubfbg";
	
	private static final String TIMEOUT = "25000";
	private static final String DEBUG = "true";

	// 初始化session
	static {
		props = new Properties();
		props.put("fromer", FROM);
		props.put("username", USERNAME);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("password", PASSWORD);
		props.put("mail.smtp.timeout", TIMEOUT);
		props.put("mail.debug", DEBUG);
		props.put("mail.host", "smtp.qq.com");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.port", 465);
		props.put("mail.smtp.socketFactory.port", 465);
		
		session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
			}
		});
	}

	public static void main(String[] args) {
		/*try {
			String html = "";
			sendFileEmail("823382133@qq.com","gpogqyuyqjlubfbg","823382133@qq.com", "yeah", html, new File("F:/excel/2016年06月21日第6期.xls"));
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		/*try {
			EmailUtil.sendFileEmail("838800811@qq.com", "lyktyevzcasubcfb",
					"823382133@qq.com", "test", "", new File(EmailUtil.class.getResource("../").getFile()+"1.png"));
			//EmailUtil.sendTextEmail("823382133@qq.com", "823382133@qq.com", "BUG", "asdf");
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
			try {
				EmailUtil.sendTextEmail("823382133@qq.com", "823382133@qq.com", "BUG", e.getMessage(),"gpogqyuyqjlubfbg");
			} catch (IOException | MessagingException e1) {
				e1.printStackTrace();
			}
		}*/
		String aString = "1231231231";
		System.out.println(aString.substring(1,aString.length()-4));
	}


	/**
	 * 
	 * @Title sendEmail
	 * @Description 通过isHtml判断发送的邮件的内容
	 * @param to 邮件接收者
	 * @param content 邮件内容
	 * @param isHtml 是否发送html
	 * @throws MessagingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws EmailException
	 */
	public static void sendEmail(String to, String title, String content, boolean isHtml)
			throws FileNotFoundException, IOException, MessagingException {
		String fromer = props.getProperty("fromer");
		if (isHtml) {
			sendHtmlEmail(fromer, to, title, content);
		} else {
			sendTextEmail(fromer, to, title, content,"gpogqyuyqjlubfbg");
		}
	}

	// 发送纯文字邮件
	public static void sendTextEmail(String from, String to, String subject, String content,String password)
			throws FileNotFoundException, IOException, MessagingException {
		props.setProperty("fromer", from);
		props.setProperty("username", from);
		props.setProperty("password", password);
		session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipient(RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setText(content);
		message.setSentDate(new Date());
		Transport.send(message);
	}

	// 发送有HTML格式邮件
	public static void sendHtmlEmail(String from, String to, String subject, String htmlConent)
			throws FileNotFoundException, IOException, MessagingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipient(RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setSentDate(new Date());
		
		Multipart multi = new MimeMultipart();
		BodyPart html = new MimeBodyPart();
		html.setContent(htmlConent, "text/html; charset=utf-8");
		multi.addBodyPart(html);
		message.setContent(multi);
		Transport.send(message);
	}

	// 发送带附件的邮件
	public static void sendFileEmail(String from , String password ,String to, String subject, String htmlConent, File attachment)
			throws FileNotFoundException, IOException, MessagingException {
		props.setProperty("fromer", from);
		props.setProperty("username", from);
		props.setProperty("password", password);
		System.out.println("props:"+props);
		session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});
		Message message = new MimeMessage(session);
		String fromer = props.getProperty("fromer");
		message.setFrom(new InternetAddress(fromer));
		message.setRecipient(RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setSentDate(new Date());
		// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
		Multipart multipart = new MimeMultipart();
		// 添加邮件正文
		BodyPart contentPart = new MimeBodyPart();
		contentPart.setContent(htmlConent, "text/html;charset=UTF-8");
		multipart.addBodyPart(contentPart);
		// 添加附件的内容
		if (attachment != null) {
			BodyPart attachmentBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attachment);
			attachmentBodyPart.setDataHandler(new DataHandler(source));

			// 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
			// 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
			// sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
			// messageBodyPart.setFileName("=?GBK?B?" +
			// enc.encode(attachment.getName().getBytes()) + "?=");
			// MimeUtility.encodeWord可以避免文件名乱码
			attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
			multipart.addBodyPart(attachmentBodyPart);
		}

		message.setContent(multipart);
		Transport.send(message);
	}


}
