package com.hansan.fenxiao.dao;

import java.util.Collection;
import java.util.List;

import com.hansan.fenxiao.entities.Article;

public abstract interface IArticleDao<T extends Article> extends IBaseDao<T> {

	List<Article> getTopArticles();

	Collection<? extends Article> getUnTopArticles();
}
