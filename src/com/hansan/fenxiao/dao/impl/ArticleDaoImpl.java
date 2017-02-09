package com.hansan.fenxiao.dao.impl;

import com.hansan.fenxiao.dao.IArticleDao;
import com.hansan.fenxiao.entities.Article;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("articleDao")
@Scope("prototype")
public class ArticleDaoImpl<T extends Article> extends BaseDaoImpl<T> implements IArticleDao<T> {
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Article> getTopArticles() {
		return getSession().createQuery("select bean from Article bean where bean.status = 1 order by bean.views ").list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends Article> getUnTopArticles() {
		return getSession().createQuery("select bean from Article bean where bean.status = 0 order by bean.createDate ").list();
	}
}