package com.hansan.fenxiao.entities;

import freemarker.template.utility.StringUtil;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "user")
public class User extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String no; // 所谓的分销编号，直接取id不行吗蠢
	private String name; // 名字,可选
	private String phone; // 电话号码，用作登录名
	private String password; // 密码
	private Double balance; // 脑残吧？balance都可以用来做余额的表示，真是蠢到至极
	private Double commission; // 佣金
	private String superior; // 所有的上级推荐人的no
	private Integer status; // 是否开通分销功能 --由管理员指定
	@Column(name = "bank_account")
	private String bankAccount;

	@Temporal(TemporalType.TIMESTAMP)
	private Date statusDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginTime;
	private String lastLoginIp;
	private String registerIp;
	private Integer loginCount;

	public boolean hasFx(){
		return this.no != null && !"".equals(this.no.trim());
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getLastLoginTime() {
		return this.lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginIp() {
		return this.lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public Integer getLoginCount() {
		return this.loginCount;
	}

	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Double getBalance() {
		return this.balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Double getCommission() {
		return this.commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public String getRegisterIp() {
		return this.registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	public String getSuperior() {
		return this.superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public String getNo() {
		return this.no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public Date getStatusDate() {
		return this.statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

}