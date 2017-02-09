package com.hansan.fenxiao.service;

import com.hansan.fenxiao.entities.OrderItem;

public interface OrderItemService {
	void sava(OrderItem orderItem);
	void delete(long id);
}
