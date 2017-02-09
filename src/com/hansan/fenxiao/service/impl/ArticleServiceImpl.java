package com.hansan.fenxiao.service.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.hansan.fenxiao.dao.IArticleDao;
import com.hansan.fenxiao.entities.Article;
import com.hansan.fenxiao.service.IArticleService;

@Repository("articleService")
@Scope("prototype")
public class ArticleServiceImpl<T extends Article> extends BaseServiceImpl<T> implements IArticleService<T> {
	@Resource(name="articleDao")
	IArticleDao<Article> articleDao;
	@Override
	public List<Article> getTopArticles() {
		return articleDao.getTopArticles();
	}
	@Override
	public Collection<? extends Article> getUnTopArticles() {
		return articleDao.getUnTopArticles();
	}
}