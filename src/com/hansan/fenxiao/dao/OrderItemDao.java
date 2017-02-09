package com.hansan.fenxiao.dao;

import com.hansan.fenxiao.entities.OrderItem;

public interface OrderItemDao {
	void persist(OrderItem orderItem);
	void delete(long id);
	
}
