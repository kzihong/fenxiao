package com.hansan.fenxiao.service;

import java.util.List;

import com.hansan.fenxiao.entities.CartItem;

public interface CartService {

	List<CartItem> getCartItems(int parseInt);

	Object getCartItemQuantity(Integer userId);

	CartItem getCartItemById(Integer productId,Integer userId);

	void updateCartItem(Integer id, Integer quantity);

	void deleteCartItem(List<CartItem> cartItems);
	
	void deleteCartItem(int parseInt);

	int addCartItem(Integer productId, Integer id,boolean hasfx);

	int reduceCartItem(int parseInt, Integer id);

	CartItem findById(int parseInt);

	void empty(Integer userId);

	void saveOrUpdate(CartItem cartItem);

	CartItem getCartItemById(String p, Integer id);
	

}
