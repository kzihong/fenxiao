package com.hansan.fenxiao.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hansan.fenxiao.dao.CartDao;
import com.hansan.fenxiao.dao.IProductDao;
import com.hansan.fenxiao.entities.CartItem;
import com.hansan.fenxiao.entities.Product;
import com.hansan.fenxiao.service.CartService;

@Repository("cartService")
public class CartServiceImpl implements CartService{
	
	@Autowired
	private IProductDao<Product> productDao;
	
	@Resource(name = "cartDao")
	private CartDao cartDao;

	@Override
	public List<CartItem> getCartItems(int parseInt) {
		// TODO Auto-generated method stub
		return cartDao.getCartItems(parseInt);
	}

	@Override
	public Object getCartItemQuantity(Integer userId) {
		// TODO Auto-generated method stub
		return cartDao.getCartItemQuantity(userId);
	}

	@Override
	public CartItem getCartItemById(Integer productId,Integer userId) {
		// TODO Auto-generated method stub
		return cartDao.getCartItemById(productId,userId);
	}

	@Override
	public void updateCartItem(Integer id, Integer quantity) {
		// TODO Auto-generated method stub
		cartDao.updateCartItem(id, quantity);
	}

	@Override
	public void deleteCartItem(int parseInt) {
		cartDao.deleteCartItem(parseInt);
	}

	@Override
	public int addCartItem(Integer productId, Integer id,boolean hasfx) {
		CartItem cartItem = cartDao.getCartItemById(productId, id);
		Product single = productDao.findById(Product.class, productId);
		if(cartItem == null){
			cartItem = new CartItem();
			cartItem.setFigure(10);
			cartItem.setMutli(false);
			cartItem.setProductTitle(single.getPicture().substring(19));
			cartItem.setCategory(single.getProductCate());
			cartItem.setOdds(hasfx?single.getOddsFx():single.getOdds());
			cartItem.setProducts(productId+"");
			cartItem.setQuantity(1);
			cartItem.setUserId(id);
			cartDao.saveOrUpdate(cartItem);
		}else {
			cartItem.setFigure(cartItem.getFigure()+10);
			cartItem.setQuantity(cartItem.getQuantity()+1);
			cartDao.saveOrUpdate(cartItem);
		}
		return cartDao.getCartItemQuantity(id);
	}

	@Override
	public int reduceCartItem(int parseInt, Integer id) {
		cartDao.reduceCartItem(parseInt,id);
		return cartDao.getCartItemQuantity(id);
	}

	@Override
	public CartItem findById(int parseInt) {
		return cartDao.findById(parseInt);
	}

	@Override
	public void empty(Integer userId) {
		cartDao.empty(userId);
		
	}

	@Override
	public void saveOrUpdate(CartItem cartItem) {
		cartDao.saveOrUpdate(cartItem);
	}

	@Override
	public CartItem getCartItemById(String p, Integer id) {
		return cartDao.getCartItemById(p,id);
	}

	@Override
	public void deleteCartItem(List<CartItem> cartItems) {
		for(CartItem cartItem : cartItems){
			cartDao.deleteCartItem(cartItem.getId());
		}
	}
	

}
