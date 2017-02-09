package com.hansan.fenxiao.service;

import java.util.List;

import com.hansan.fenxiao.entities.CartItem;
import com.hansan.fenxiao.entities.Config;
import com.hansan.fenxiao.entities.OrderItem;
import com.hansan.fenxiao.entities.Orders;
import com.hansan.fenxiao.entities.Term;
import com.hansan.fenxiao.entities.User;

public abstract interface IOrdersService<T extends Orders> extends IBaseService<T> {
	public abstract Orders findByNo(String paramString);

	public abstract void savaOrderItems(List<OrderItem> orderItems);

	public abstract List<OrderItem> getOrderItemsByOrderId(Integer id);

	public abstract int getCurrentTerm();
	
	Term getLastUnsettleTerm();
	
	int insertNewTerm();
	
	int stopTerm();
	
	int StartTerm();

	public abstract List<OrderItem> getOrderItemsByTerm(int currentTerm);

	public abstract Term getTermByID(int id);

	public abstract void setFortuna(Integer id, Integer fortuna, String string);

	public abstract List<OrderItem> updateandGetFortunateOrderItems(List<Integer> fortunates, Integer integer);

	public abstract void setSettled(Integer id);

	public abstract List<OrderItem> getOrderItemlist(String hql, int start, int pageSize, Object[] objects);

	public abstract int getTermCount();

	public abstract List<Term> list2(int start, int pageSize);

	public abstract void saveOrUpdate(Term findTerm);

	public abstract void mergeBatch(List<OrderItem> standOff);

	public abstract void pay(Integer integer, Integer integer2,Config config);

}