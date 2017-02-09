package com.hansan.fenxiao.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "term")
public class Term implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer tid;
	
	private Integer id;
	
	private Integer fortuna;
	
	private boolean sent;
	
	private boolean end;
	
	private boolean settled; 
	
	private String pingCodes;
	
	private long rollTime;
	
	@Transient
	private String rollTimeString;
	
	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	@Override
	public String toString() {
		return "Term [id=" + id + ", fortuna=" + fortuna + ", sent=" + sent + ", end=" + end + ", settled=" + settled
				+ ", pingCodes=" + pingCodes + ", createTime=" + getCreateTime() + "rolltime "+getRollTime()+"]";
	}

	public String getPingCodes() {
		return pingCodes;
	}

	public void setPingCodes(String pingCodes) {
		this.pingCodes = pingCodes;
	}

	public boolean isSettled() {
		return settled;
	}

	public void setSettled(boolean settled) {
		this.settled = settled;
	}

	@Column(name="create_time")
	private long createTime;
	
	@Transient
	private String createTimeString;
	
	@Transient
	private List<String> products;
	
	public long getRollTime() {
		return rollTime;
	}

	public void setRollTime(long rollTime) {
		this.rollTime = rollTime;
	}

	public String getRollTimeString() {
		if(this.rollTime != 0){
			setRollTimeString(new SimpleDateFormat("yyyy-MM-dd").format(new Date(rollTime)));
		}
		return rollTimeString;
	}

	public void setRollTimeString(String rollTimeString) {
		this.rollTimeString = rollTimeString;
	}

	public List<String> getProducts() {
		return products;
	}

	public void setProducts(List<String> products) {
		this.products = products;
	}

	public String getCreateTimeString() {
		if(this.createTime != 0){
			setCreateTimeString(new SimpleDateFormat().format(new Date(createTime)));
		}
		return createTimeString;
	}

	public void setCreateTimeString(String createTimeString) {
		this.createTimeString = createTimeString;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFortuna() {
		return fortuna;
	}

	public void setFortuna(Integer fortuna) {
		this.fortuna = fortuna;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
