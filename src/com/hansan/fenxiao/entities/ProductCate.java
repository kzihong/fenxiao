package com.hansan.fenxiao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "product_cate")
public class ProductCate extends BaseBean {
	
	private String name;
	
	private int fatherId;
	
	private double firstLevel;
	
	private double secoundLevel;
	
	private double thirdLevel;

	private double rebate;
	
	private String info;
	
	private int least =1;
	
	@Transient
	private ProductCate productCate;
	
	public ProductCate getProductCate() {
		if(this.productCate == null){
		}
		return productCate;
	}

	public double getRebate() {
		return rebate;
	}

	public void setRebate(double rebate) {
		this.rebate = rebate;
	}

	public void setProductCate(ProductCate productCate) {
		this.productCate = productCate;
	}

	public int getLeast() {
		return least;
	}

	public void setLeast(int least) {
		this.least = least;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFatherId() {
		return this.fatherId;
	}

	public void setFatherId(int fatherId) {
		this.fatherId = fatherId;
	}

	public double getFirstLevel() {
		return firstLevel;
	}

	public void setFirstLevel(double firstLevel) {
		this.firstLevel = firstLevel;
	}

	public double getSecoundLevel() {
		return secoundLevel;
	}

	public void setSecoundLevel(double secoundLevel) {
		this.secoundLevel = secoundLevel;
	}

	public double getThirdLevel() {
		return thirdLevel;
	}

	public void setThirdLevel(double thirdLevel) {
		this.thirdLevel = thirdLevel;
	}
	
	
}