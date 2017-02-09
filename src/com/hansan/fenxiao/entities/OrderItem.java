package com.hansan.fenxiao.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="order_item")
public class OrderItem implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="order_id")
	private Orders orders;
	
	private Integer quantity;
	
	private Boolean fortunate = false;
	
	private Float figure;
	
	private String products;
	
	private String productTitle;
	
	private boolean isMutli;
	
	private float odds;
	
	@JoinColumn(name="category")
	@ManyToOne
	private ProductCate category;
	
	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public ProductCate getCategory() {
		return category;
	}

	public void setCategory(ProductCate category) {
		this.category = category;
	}

	public float getOdds() {
		return odds;
	}

	public void setOdds(float odds) {
		this.odds = odds;
	}

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}

	public boolean isMutli() {
		return isMutli;
	}

	public void setMutli(boolean isMutli) {
		this.isMutli = isMutli;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Orders getOrders() {
		return orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Boolean getFortunate() {
		return fortunate;
	}

	public void setFortunate(Boolean fortunate) {
		this.fortunate = fortunate;
	}

	public Float getFigure() {
		return figure;
	}

	public void setFigure(Float figure) {
		this.figure = figure;
	}
	
	
}
