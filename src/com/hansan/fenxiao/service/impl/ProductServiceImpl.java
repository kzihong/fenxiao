package com.hansan.fenxiao.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.hansan.fenxiao.dao.IProductDao;
import com.hansan.fenxiao.entities.Product;
import com.hansan.fenxiao.service.IProductService;

@Repository("productService")
@Scope("prototype")
public class ProductServiceImpl<T extends Product> extends BaseServiceImpl<T> implements IProductService<T> {

	@Autowired
	private IProductDao<Product> productDao;

	public boolean delete(T baseBean) {
		return this.baseDao.delete(baseBean);
	}

	public List<Product> getListByCategory(Integer categoryId) {
		return productDao.getProductListByCategory(categoryId);
	}
}
