package com.hansan.fenxiao.service;

import java.util.List;

import com.hansan.fenxiao.entities.Product;

public abstract interface IProductService<T extends Product> extends IBaseService<T> {
	List<Product> getListByCategory(Integer categoryId);
}