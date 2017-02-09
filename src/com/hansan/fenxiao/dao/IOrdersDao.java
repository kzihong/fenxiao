package com.hansan.fenxiao.dao;

import java.util.List;

import com.hansan.fenxiao.entities.OrderItem;
import com.hansan.fenxiao.entities.Orders;
import com.hansan.fenxiao.entities.Term;

public abstract interface IOrdersDao extends IBaseDao<Orders> {
	public abstract Orders findByNo(String paramString);

	public abstract void savaOrderItems(List<OrderItem> orderItems);

	public abstract List<OrderItem> getOrderItemsByOrderId(Integer id);

	public abstract int getCurrentTerm();

	public abstract int insertNewTerm();

	public abstract int stopTerm();

	public abstract int StartTerm();

	public abstract List<OrderItem> getOrderItemsByTerm(int currentTerm);

	public abstract Term getTermByID(int id);

	public abstract void setFortuna(Integer id, Integer fortuna, String pingCodes);

	public abstract List<OrderItem> updateandGetFortunateOrderItems(List<Integer> fortunates, Integer term);

	public abstract Term getLastUnsettleTerm();

	public abstract void setSettled(Integer id);

	public abstract int getTermCount();

	public abstract List<Term> list2(int start, int pageSize);

	public abstract void saveOrUpdate(Term findTerm);

	public abstract void saveOrUpdate(OrderItem orderItem);
}
