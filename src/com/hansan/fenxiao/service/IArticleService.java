package com.hansan.fenxiao.service;

import java.util.Collection;
import java.util.List;

import com.hansan.fenxiao.entities.Article;

public abstract interface IArticleService<T extends Article> extends IBaseService<T> {

	List<Article> getTopArticles();

	Collection<? extends Article> getUnTopArticles();
}