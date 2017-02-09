package com.hansan.fenxiao.dao;

import java.util.List;

import com.hansan.fenxiao.entities.Product;

public abstract interface IProductDao<T extends Product> extends IBaseDao<T> {
	List<Product> getProductListByCategory(Integer categoryId);
}
