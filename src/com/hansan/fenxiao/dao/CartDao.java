package com.hansan.fenxiao.dao;

import java.util.List;

import com.hansan.fenxiao.entities.CartItem;

public interface CartDao {
	int getCartItemQuantity(int userId);
	List<CartItem> getCartItems(int userId);
	CartItem getCartItemById(int productId,int userId);
	void updateCartItem(int id,int update);
	void deleteCartItem(int id);
	void addCartItem(Integer productId, Integer id);
	void reduceCartItem(int parseInt, Integer id);
	CartItem findById(int parseInt);
	void empty(Integer userId);
	void saveOrUpdate(CartItem cartItem);
	CartItem getCartItemById(String p, Integer id);
}
