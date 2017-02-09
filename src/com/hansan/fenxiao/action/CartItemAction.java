package com.hansan.fenxiao.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;

import org.json.JSONException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hansan.fenxiao.entities.CartItem;
import com.hansan.fenxiao.entities.Product;
import com.hansan.fenxiao.entities.ProductCate;
import com.hansan.fenxiao.entities.User;
import com.hansan.fenxiao.service.CartService;
import com.hansan.fenxiao.service.IProductCateService;
import com.hansan.fenxiao.service.IProductService;
import com.hansan.fenxiao.utils.BjuiJson;

@Controller("cartItemAction")
@Scope("prototype")
public class CartItemAction extends BaseAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Resource(name="cartService")
	private CartService cartService;
	
	@Resource(name = "productService")
	private IProductService<Product> productService;
	
	private CartItem cartItem ;
	
	@Resource(name = "productCateService")
	private IProductCateService<ProductCate> productCateService;

	public void getCartItems() {
		String callbackData = "";
		String userId = request.getParameter("u");
		if(userId == null || "".equals(userId)){
			try {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			List<CartItem> cartItems = cartService.getCartItems(Integer.parseInt(userId));
			callbackData = JSONArray.toJSONString(cartItems);
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}
	
	
	
	public void updateCartItem() {
		String callbackData ="";
		String p = request.getParameter("p");
		String quantity = request.getParameter("q");
		User user = (User) request.getSession().getAttribute("loginUser");
		if((p == null || "".equals(p))){
			try {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			CartItem cartItem = null;
			if(p != null || !"".equals(p)){
				cartItem = cartService.getCartItemById(Integer.parseInt(p), user.getId());
				if(cartItem == null){
					cartItem = new CartItem();
					Product product = productService.findById(Product.class, Integer.parseInt(p));
					cartItem.setCategory(product.getProductCate());
					cartItem.setFigure(10f*Integer.parseInt(quantity));
					cartItem.setQuantity(Integer.parseInt(quantity));
					cartItem.setMutli(false);
					cartItem.setProducts(p);
					cartItem.setOdds(user.hasFx()?product.getOddsFx():product.getOdds());
					cartItem.setUserId(user.getId());
					cartItem.setProductTitle(product.getPicture().substring(19));
					cartService.saveOrUpdate(cartItem);
					if(cartItem.getQuantity() == 0){
						cartService.deleteCartItem(cartItem.getId());
					}
				}else {
					cartService.updateCartItem(cartItem.getId(), Integer.parseInt(quantity));
				}
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("number", cartService.getCartItemQuantity(user.getId()));
			jsonObject.put("cart", cartItem);
			callbackData = jsonObject.toJSONString();
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}
	
	public void addCartItem() {
		String callbackData = "";
		String pid = request.getParameter("p");
		User user = (User) request.getSession().getAttribute("loginUser");
		if(pid == null||user ==null){
			try {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			int number = cartService.addCartItem(Integer.parseInt(pid), user.getId(),user.hasFx());
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("number", number);
//			jsonObject.put("cart", cartItem);
			callbackData = jsonObject.toJSONString();
		}
		
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}

	public void reduceCartItem() {
		String callbackData = "";
		String productId = request.getParameter("p");
		User user = (User) request.getSession().getAttribute("loginUser");
		if(productId == null || "".equals(productId)){
			try {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			int number = cartService.reduceCartItem(Integer.parseInt(productId),user.getId());
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("number", number);
			jsonObject.put("cart", cartItem);
			callbackData = jsonObject.toJSONString();
		}

		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}
	
	public void empty() {
		User user = (User) request.getSession().getAttribute("loginUser");
		Integer userId = user.getId();
		this.cartService.empty(userId);
	}
	public void getCartItemQuantity(){
		String callbackData = "";
		User user = (User) request.getSession().getAttribute("loginUser");
		Integer userId = user.getId();
		if(userId == null || userId == 0){
			try {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("number", cartService.getCartItemQuantity(userId));
			callbackData = jsonObject.toJSONString();
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}
	public void updateByCid(){
		String callbackData ="";
		PrintWriter out = null;
		String cid = request.getParameter("c");
		int q = Integer.parseInt(request.getParameter("q"));
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		CartItem cartItem = cartService.findById(Integer.parseInt(cid));
		if(cartItem != null ){
			if(q == 0 ){
				cartService.deleteCartItem(cartItem.getId());
				cartItem = null ;
			}else {
				cartItem.setQuantity(q);
				cartItem.setFigure(cartItem.getQuantity()*10f);
				cartService.saveOrUpdate(cartItem);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("number", cartService.getCartItemQuantity(loginUser.getId()));
			jsonObject.put("cart", cartItem);
			callbackData = jsonObject.toJSONString();
		}
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}
	public void addByCid(){
		String callbackData ="";
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		PrintWriter out = null;
		String cid = request.getParameter("cid");
		CartItem cartItem = cartService.findById(Integer.parseInt(cid));
		if(cartItem != null ){
			cartItem.setQuantity(cartItem.getQuantity()+1);
			cartItem.setFigure(cartItem.getQuantity()*10f);
			cartService.saveOrUpdate(cartItem);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("number", cartService.getCartItemQuantity(loginUser.getId()));
			jsonObject.put("cart", cartItem);
			callbackData = jsonObject.toJSONString();
		}
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}
	public void reduceByCid(){
		String callbackData ="";
		PrintWriter out = null;
		String cid = request.getParameter("cid");
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		CartItem cartItem = cartService.findById(Integer.parseInt(cid));
		if(cartItem != null ){
			if(cartItem.getQuantity() == 1 ){
				cartService.deleteCartItem(cartItem.getId());
				cartItem = null ;
			}else {
				cartItem.setQuantity(cartItem.getQuantity()-1);
				cartItem.setFigure(cartItem.getQuantity()*10f);
				cartService.saveOrUpdate(cartItem);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("number", cartService.getCartItemQuantity(loginUser.getId()));
			jsonObject.put("cart", cartItem);
			callbackData = jsonObject.toJSONString();
		}
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}
	public void addMulti(){
		PrintWriter out = null;
		String callbackData ="";
		try {
			String p = request.getParameter("products");
			User loginUser = (User) request.getSession().getAttribute("loginUser");
			if(p == null||loginUser == null ||loginUser.getId()==null){
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			}else{
				JSONObject jsonObject = new JSONObject();
				String [] pStrings = p.split(",");
				StringBuilder productTittle = new StringBuilder();
				Product product = productService.findById(Product.class, Integer.parseInt(pStrings[0]));
				if(product.getProductCate().getLeast()!=pStrings.length){
					callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
				}else{
					CartItem cartItem = cartService.getCartItemById(p, loginUser.getId());
					if(cartItem != null){
						cartItem.setQuantity(cartItem.getQuantity()+1);
						cartItem.setFigure(cartItem.getQuantity()*10);
						cartService.saveOrUpdate(cartItem);
					}else {
						cartItem = new CartItem();
						cartItem.setProducts(p);
						cartItem.setQuantity(1);
						cartItem.setFigure(10);
						cartItem.setMutli(true);
						cartItem.setUserId(loginUser.getId());
						cartItem.setCategory(product.getProductCate());
						cartItem.setProductTitle(product.getPicture().substring(19));
						float odds = loginUser.hasFx()?product.getOddsFx():loginUser.hasFx()?product.getOddsFx():product.getOdds();
						for(String i : p.split(",")){
							product = productService.findById(Product.class, Integer.parseInt(i));
							if(odds > (loginUser.hasFx()?product.getOddsFx():product.getOdds())){
								odds=loginUser.hasFx()?product.getOddsFx():product.getOdds();
							}
							productTittle.append(product.getPicture().substring(19)+",");
							if(cartItem.getCategory().getId() != product.getProductCate().getId()){
								throw new Exception("非法参数");
							}
						}
						cartItem.setProductTitle(productTittle.toString().substring(0, productTittle.toString().length()-1));
						cartItem.setOdds(odds);
						cartService.saveOrUpdate(cartItem);
					}
					jsonObject.put("number", cartService.getCartItemQuantity(loginUser.getId()));
					jsonObject.put("cart", cartItem);
					callbackData = jsonObject.toJSONString();
				}
			}
		} catch (Exception e) {
			try {
				callbackData = BjuiJson.json("300", e.getMessage(), "", "", "", "", "", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			try {
				out = this.response.getWriter();
				out.print(callbackData);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void reduceMulti(){
		PrintWriter out = null;
		String callbackData ="";
		try {
			String p = request.getParameter("products");
			User loginUser = (User) request.getSession().getAttribute("loginUser");
			if(p == null||loginUser == null ||loginUser.getId()==null){
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			}else{
				JSONObject jsonObject = new JSONObject();
				String [] pStrings = p.split(",");
				Product product = productService.findById(Product.class, Integer.parseInt(pStrings[0]));
				if(product.getProductCate().getLeast()!=pStrings.length){
					callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
				}else{
					CartItem cartItem = cartService.getCartItemById(p, loginUser.getId());
					if(cartItem != null){
						if(cartItem.getQuantity() == 1){
							cartService.deleteCartItem(cartItem.getId());
							cartItem.setQuantity(0);
						}else {
							cartItem.setQuantity(cartItem.getQuantity()-1);
							cartItem.setFigure(cartItem.getQuantity()*10);
							cartService.saveOrUpdate(cartItem);
						}
					}
					jsonObject.put("number", cartService.getCartItemQuantity(loginUser.getId()));
					jsonObject.put("cart", cartItem);
					callbackData = jsonObject.toJSONString();
				}
			}
		} catch (Exception e) {
			try {
				callbackData = BjuiJson.json("300", e.getMessage(), "", "", "", "", "", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			try {
				out = this.response.getWriter();
				out.print(callbackData);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void updateMulti(){
		PrintWriter out = null;
		String callbackData ="";
		try {
			String q = request.getParameter("q");
			String p = request.getParameter("products");
			User loginUser = (User) request.getSession().getAttribute("loginUser");
			if(p == null||loginUser == null ||loginUser.getId()==null ||q == null || "".equals(q)){
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			}else{
				JSONObject jsonObject = new JSONObject();
				String [] pStrings = p.split(",");
				Product product = productService.findById(Product.class, Integer.parseInt(pStrings[0]));
				if(product.getProductCate().getLeast()!=pStrings.length){
					callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
				}else{
					CartItem cartItem = cartService.getCartItemById(p, loginUser.getId());
					
					
					if(cartItem != null){
						//改为0
						if(Integer.parseInt(q) == 0 ){
							cartService.deleteCartItem(cartItem.getId());
							cartItem.setQuantity(0);
						}else {
							//平静修改
							cartItem.setQuantity(Integer.parseInt(q));
							cartItem.setFigure(cartItem.getQuantity()*10);
							cartService.saveOrUpdate(cartItem);
						}
					}else {
						if(Integer.parseInt(q)!= 0){
							cartItem = new CartItem();
							cartItem.setProducts(p);
							cartItem.setQuantity(Integer.parseInt(q));
							cartItem.setFigure(cartItem.getQuantity()*10);
							cartItem.setMutli(true);
							cartItem.setUserId(loginUser.getId());
							cartItem.setCategory(product.getProductCate());
							float odds = loginUser.hasFx()?product.getOddsFx():product.getOdds();
							StringBuilder stringBuilder = new StringBuilder();
							for(String i : p.split(",")){
								product = productService.findById(Product.class, Integer.parseInt(i));
								if(cartItem.getCategory().getId() != product.getProductCate().getId()){
									throw new Exception("非法参数");
								}
								if(odds > (loginUser.hasFx()?product.getOddsFx():product.getOdds())){
									odds=loginUser.hasFx()?product.getOddsFx():product.getOdds();
								}
								stringBuilder.append(product.getPicture().substring(19)+",");
							}
							cartItem.setProductTitle(stringBuilder.substring(0, stringBuilder.length()-1));
							cartItem.setOdds(odds);
							cartService.saveOrUpdate(cartItem);
						}
					}
					jsonObject.put("number", cartService.getCartItemQuantity(loginUser.getId()));
					jsonObject.put("cart", cartItem);
					callbackData = jsonObject.toJSONString();
				}
			}
		} catch (Exception e) {
			try {
				callbackData = BjuiJson.json("300", e.getMessage(), "", "", "", "", "", "");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			try {
				out = this.response.getWriter();
				out.print(callbackData);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	/*public void updateCartItem() {
		String callbackData = "";
		if(cartItem.getProductId()==0||cartItem.getQuantity()==null){
			try {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			cartService.updateCartItem(cartService.getCartItemById(cartItem.getProductId(),user).getId(), cartItem.getQuantity());
			try {
				callbackData = BjuiJson.json("200", "OK", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}*/
	
	public void goCart() {
		User user = (User) request.getSession().getAttribute("loginUser");
		if(user == null){
			//goto err page
		}else{
			List<CartItem> list = cartService.getCartItems(user.getId());
			request.setAttribute("list", list);
			double total = 0;
			for(CartItem cartItem : list){
				total += cartItem.getFigure();
				cartItem.getCategory().setProductCate(productCateService.findById(ProductCate.class, cartItem.getCategory().getFatherId()));
			}
			request.setAttribute("total", total);
			try {
				request.getRequestDispatcher("/user/cart.jsp").forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public void getCartItemById() {
		String callbackData = "";
		String productId = request.getParameter("pid");
		User user = (User) request.getSession().getAttribute("loginUser");
		if(productId == null||"".equals(productId)||user == null){
			try {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("cartItem", cartService.getCartItemById(Integer.parseInt(productId),user.getId()));
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}
	
	public void delete() {
		String callbackData = "";
		String cid = request.getParameter("cid");
		if(cid == null||"".equals(cid)){
			try {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			cartService.deleteCartItem(Integer.parseInt(cid));
			try {
				callbackData = BjuiJson.json("200", "删除成功", "", "", "", "", "", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}

	public CartItem getCartItem() {
		return cartItem;
	}

	public void setCartItem(CartItem cartItem) {
		this.cartItem = cartItem;
	}
	
}
