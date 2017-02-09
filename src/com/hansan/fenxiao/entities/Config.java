package com.hansan.fenxiao.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "config")
public class Config extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String logo;
	private String siteName;
	private String siteUrl;
	private String siteKeys;
	private String siteDescription;
	private String address;
	private String phone;
	private String qq;
	private String weixin;
	private String weibo;
	private String email;
	private String sendEmail;
	private String sendEmailPass;
	private String sendEmailSmtp;
	private String foldername;
	private Double firstLevel;
//	private Double secondLevel;
//	private Double thirdLevel;
	private String downloadUrl;
	private String alipayPartner;
	private String alipaySellerEmail;
	private String alipayKey;
	private Integer onlinePayIsOpen;
	private Integer rechargeCardIsOpen;

	private String acceptExcelEmail; // 接受每期订单的email
	private String emailStmpCode;
	private Integer currentYeas;
	private String bannerUrl;
	private String newsUrl;
	
	private Integer nextTerm;
	
	private String msgAPIUsername;
	private String msgAPIPassword;
	private String msgAPITemplate;
	
	private double eztzze; //二中特之中二
	private double szezzs; //三中二之中三

	public String getMsgAPIUsername() {
		return msgAPIUsername;
	}
	
	

	public String getFoldername() {
		return foldername;
	}



	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}

	public Double getFirstLevel() {
		return firstLevel;
	}

	public void setFirstLevel(Double firstLevel) {
		this.firstLevel = firstLevel;
	}

	public void setMsgAPIUsername(String msgAPIUsername) {
		this.msgAPIUsername = msgAPIUsername;
	}

	
	public String getMsgAPIPassword() {
		return msgAPIPassword;
	}

	public void setMsgAPIPassword(String msgAPIPassword) {
		this.msgAPIPassword = msgAPIPassword;
	}

	public String getMsgAPITemplate() {
		return msgAPITemplate;
	}

	public void setMsgAPITemplate(String msgAPITemplate) {
		this.msgAPITemplate = msgAPITemplate;
	}

	public Integer getNextTerm() {
		return nextTerm;
	}

	public void setNextTerm(Integer nextTerm) {
		this.nextTerm = nextTerm;
	}

	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public String getEmailStmpCode() {
		return emailStmpCode;
	}

	public void setEmailStmpCode(String emailStmpCode) {
		this.emailStmpCode = emailStmpCode;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getSiteName() {
		return this.siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteUrl() {
		return this.siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getSiteKeys() {
		return this.siteKeys;
	}

	public void setSiteKeys(String siteKeys) {
		this.siteKeys = siteKeys;
	}

	public String getSiteDescription() {
		return this.siteDescription;
	}

	public void setSiteDescription(String siteDescription) {
		this.siteDescription = siteDescription;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getQq() {
		return this.qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWeixin() {
		return this.weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getWeibo() {
		return this.weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSendEmail() {
		return this.sendEmail;
	}

	public void setSendEmail(String sendEmail) {
		this.sendEmail = sendEmail;
	}

	public String getSendEmailPass() {
		return this.sendEmailPass;
	}

	public void setSendEmailPass(String sendEmailPass) {
		this.sendEmailPass = sendEmailPass;
	}

	public String getSendEmailSmtp() {
		return this.sendEmailSmtp;
	}

	public void setSendEmailSmtp(String sendEmailSmtp) {
		this.sendEmailSmtp = sendEmailSmtp;
	}


	public String getDownloadUrl() {
		return this.downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getAlipayPartner() {
		return this.alipayPartner;
	}

	public void setAlipayPartner(String alipayPartner) {
		this.alipayPartner = alipayPartner;
	}

	public String getAlipaySellerEmail() {
		return this.alipaySellerEmail;
	}

	public void setAlipaySellerEmail(String alipaySellerEmail) {
		this.alipaySellerEmail = alipaySellerEmail;
	}

	public String getAlipayKey() {
		return this.alipayKey;
	}

	public void setAlipayKey(String alipayKey) {
		this.alipayKey = alipayKey;
	}

	public Integer getOnlinePayIsOpen() {
		return this.onlinePayIsOpen;
	}

	public void setOnlinePayIsOpen(Integer onlinePayIsOpen) {
		this.onlinePayIsOpen = onlinePayIsOpen;
	}

	public Integer getRechargeCardIsOpen() {
		return this.rechargeCardIsOpen;
	}

	public void setRechargeCardIsOpen(Integer rechargeCardIsOpen) {
		this.rechargeCardIsOpen = rechargeCardIsOpen;
	}

	public String getAcceptExcelEmail() {
		return acceptExcelEmail;
	}

	public void setAcceptExcelEmail(String acceptExcelEmail) {
		this.acceptExcelEmail = acceptExcelEmail;
	}

	public Integer getCurrentYeas() {
		return currentYeas;
	}

	public void setCurrentYeas(Integer currentYeas) {
		this.currentYeas = currentYeas;
	}

	public double getEztzze() {
		return eztzze;
	}

	public void setEztzze(double eztzze) {
		this.eztzze = eztzze;
	}

	public double getSzezzs() {
		return szezzs;
	}

	public void setSzezzs(double szezzs) {
		this.szezzs = szezzs;
	}
	
}
