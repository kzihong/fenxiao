package com.hansan.fenxiao.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.hansan.fenxiao.dao.IProductDao;
import com.hansan.fenxiao.entities.Product;

@Repository("productDao")
@Scope("prototype")
public class ProductDaoImpl<T extends Product> extends BaseDaoImpl<T>
  implements IProductDao<T>{
	
	@SuppressWarnings("unchecked")
	public List<Product> getProductListByCategory(Integer categoryId){
		String jpql = "select bean from Product bean where bean.productCate.id = :id";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(jpql);
		query.setParameter("id", categoryId);
		return query.list();
	}
}
