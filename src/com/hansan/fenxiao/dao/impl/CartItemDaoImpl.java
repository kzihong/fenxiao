package com.hansan.fenxiao.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.hansan.fenxiao.dao.CartDao;
import com.hansan.fenxiao.entities.CartItem;
import com.hansan.fenxiao.entities.Product;

@Repository("cartDao")
@Scope("prototype")
public class CartItemDaoImpl implements CartDao {

	@Resource(name = "sessionFactory")
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public int getCartItemQuantity(int userId) {
		List<CartItem> cartItems = this.getCartItems(userId);
		int count = 0;
		for (CartItem cartItem : cartItems) {
			count += cartItem.getQuantity();
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CartItem> getCartItems(int userId) {
		return getSession().createQuery("select bean from CartItem bean where bean.userId = :userId")
				.setInteger("userId", userId).list();
	}

	@Override
	public CartItem getCartItemById(int productId, int userId) {
		CartItem cartItem = (CartItem) getSession()
				.createQuery("select bean from CartItem bean where bean.products = :id and bean.userId = :userId")
				.setInteger("id", productId).setInteger("userId", userId).uniqueResult();

		return cartItem;
	}

	@Override
	public void updateCartItem(int id, int update) {
		CartItem cartItem = (CartItem) getSession().get(CartItem.class, id);
		cartItem.setQuantity(update);
		cartItem.setFigure(cartItem.getQuantity()*10f);
		if (cartItem.getQuantity() == 0) {
			deleteCartItem(cartItem.getId());
		}

	}

	@Override
	public void deleteCartItem(int id) {
		CartItem cartItem = (CartItem) getSession().get(CartItem.class, id);
		if (cartItem != null)
			getSession().delete(cartItem);
	}

	@Override
	public void addCartItem(Integer productId, Integer id) {
		CartItem cartItem = getCartItemById(productId, id);
		if (cartItem == null) {
			cartItem = new CartItem();
			cartItem.setProducts("" + productId);
			cartItem.setUserId(id);
			cartItem.setQuantity(1);
			cartItem.setFigure(cartItem.getQuantity()*10);
			getSession().persist(cartItem);
		} else {
			cartItem.setQuantity(cartItem.getQuantity() + 1);
			cartItem.setFigure(cartItem.getQuantity()*10);
		}
	}

	@Override
	public void reduceCartItem(int parseInt, Integer id) {
		CartItem cartItem = getCartItemById(parseInt, id);
		cartItem.setQuantity(cartItem.getQuantity() - 1);
		cartItem.setFigure(cartItem.getQuantity()*10);
		if (cartItem.getQuantity() <= 0) {
			deleteCartItem(cartItem.getId());
		}
	}

	@Override
	public CartItem findById(int parseInt) {
		return (CartItem) getSession().get(CartItem.class, parseInt);
	}

	@Override
	public void empty(Integer userId) {
		getSession().createSQLQuery("delete from cartitem where user_id = :id").setInteger("id", userId)
				.executeUpdate();
	}

	@Override
	public void saveOrUpdate(CartItem cartItem) {
		getSession().saveOrUpdate(cartItem);
	}

	@Override
	public CartItem getCartItemById(String p, Integer id) {
		CartItem cartItem = (CartItem) getSession()
				.createQuery("select bean from CartItem bean where bean.products = :id and bean.userId = :userId")
				.setString("id", p).setInteger("userId", id).uniqueResult();

		return cartItem;
	}

}
