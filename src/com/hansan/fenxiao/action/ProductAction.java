package com.hansan.fenxiao.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hansan.fenxiao.entities.CartItem;
import com.hansan.fenxiao.entities.Config;
import com.hansan.fenxiao.entities.Product;
import com.hansan.fenxiao.entities.ProductCate;
import com.hansan.fenxiao.entities.ProductList;
import com.hansan.fenxiao.entities.User;
import com.hansan.fenxiao.service.CartService;
import com.hansan.fenxiao.service.IConfigService;
import com.hansan.fenxiao.service.IProductCateService;
import com.hansan.fenxiao.service.IProductService;
import com.hansan.fenxiao.utils.BjuiJson;
import com.hansan.fenxiao.utils.BjuiPage;
import com.hansan.fenxiao.utils.FortunaUtil;
import com.hansan.fenxiao.utils.FreemarkerUtils;

import freemarker.template.Configuration;

@Controller("productAction")
@Scope("prototype")
public class ProductAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	@Resource(name = "productService")
	private IProductService<Product> productService;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "productCateService")
	private IProductCateService<ProductCate> productCateService;

	@Resource(name = "configService")
	private IConfigService<Config> configService;
	private Product product;

	public void list() {
		String key = this.request.getParameter("key");
		int count = 0;
		if (("".equals(key)) || (key == null)) {
			count = this.productService.getTotalCount("select count(*) from Product where deleted=0", new Object[0]);
			key = "";
		} else {
			count = this.productService.getTotalCount("from Product where title like '%" + key + "%' and deleted=0",
					new Object[0]);
		}

		this.page = new BjuiPage(this.pageCurrent, this.pageSize);
		this.page.setTotalCount(count);
		List list = this.productService.list("from Product where deleted=0 order by id desc", this.page.getStart(),
				this.page.getPageSize(), new Object[0]);
		this.cfg = new Configuration();

		this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(), "WEB-INF/templates/admin");
		Map root = new HashMap();
		root.put("list", list);
		root.put("page", this.page);
		FreemarkerUtils.freemarker(this.request, this.response, "productList.ftl", this.cfg, root);
	}

	public void add() {
		List<ProductCate> productCatelist = this.productCateService.list("from ProductCate where deleted=0");
		String zNodes = "";
		for (ProductCate productCate : productCatelist) {
			zNodes = zNodes + "<li data-id='" + productCate.getId() + "' data-pid='" + productCate.getFatherId()
					+ "' data-tabid='" + productCate.getId() + "'>" + productCate.getName() + "</li>";
		}
		this.cfg = new Configuration();

		this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(), "WEB-INF/templates/admin");
		Map root = new HashMap();
		root.put("zNodes", zNodes);
		FreemarkerUtils.freemarker(this.request, this.response, "productAdd.ftl", this.cfg, root);
	}

	public void save() {
		String callbackData = "";
		try {
			if (this.product.getProductCate().getId().intValue() == 0) {
				callbackData = BjuiJson.json("300", "请选择栏目", "", "", "", "", "", "");
			} else if ("".equals(this.product.getContent())) {
				callbackData = BjuiJson.json("300", "请输入内容", "", "", "", "", "", "");
			} else {
				if (StringUtils.isEmpty(this.product.getPicture())) {
					this.product.setPicture("images/nopicture.jpg");
				}
				this.product.setDeleted(false);

				this.product.setCreateDate(new Date());
				boolean result = this.productService.saveOrUpdate(this.product);
				if (result)
					callbackData = BjuiJson.json("200", "添加成功", "", "", "", "true", "", "");
				else
					callbackData = BjuiJson.json("300", "添加失败", "", "", "", "", "", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}

	public void info() {
		String callbackData = "";
		String idStr = this.request.getParameter("id");
		try {
			PrintWriter out = this.response.getWriter();

			if ((idStr == null) || ("".equals(idStr))) {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
				out.print(callbackData);
				out.flush();
				out.close();
			} else {
				int id = 0;
				try {
					id = Integer.parseInt(idStr);
				} catch (Exception e) {
					callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
					out.print(callbackData);
					out.flush();
					out.close();
				}
				Product findProduct = (Product) this.productService.findById(Product.class, id);
				if (findProduct == null) {
					callbackData = BjuiJson.json("300", "产品不存在", "", "", "", "", "", "");
					out.print(callbackData);
					out.flush();
					out.close();
				} else {
					List<ProductCate> productCatelist = this.productCateService
							.list("from ProductCate where deleted=0");
					String zNodes = "";
					for (ProductCate productCate : productCatelist) {
						zNodes = zNodes + "<li data-id='" + productCate.getId() + "' data-pid='"
								+ productCate.getFatherId() + "' data-tabid='" + productCate.getId() + "'>"
								+ productCate.getName() + "</li>";
					}
					this.cfg = new Configuration();

					this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(),
							"WEB-INF/templates/admin");
					Map root = new HashMap();
					root.put("product", findProduct);
					root.put("zNodes", zNodes);
					FreemarkerUtils.freemarker(this.request, this.response, "productEdit.ftl", this.cfg, root);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		String callbackData = "";
		try {
			PrintWriter out = this.response.getWriter();
			if (this.product == null) {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} else {
				Product findProduct = (Product) this.productService.findById(Product.class,
						this.product.getId().intValue());
				findProduct.setProductCate(this.product.getProductCate());
				findProduct.setPicture(this.product.getPicture());
				findProduct.setTitle(this.product.getTitle());
				findProduct.setContent(this.product.getContent());
				findProduct.setOdds(this.product.getOdds());
				findProduct.setOddsFx(this.product.getOddsFx());
				findProduct.setMoney(this.product.getMoney());
				boolean result = this.productService.saveOrUpdate(findProduct);

				if (result) {
					callbackData = BjuiJson.json("200", "修改成功", "", "", "", "true", "", "");
				} else {
					callbackData = BjuiJson.json("300", "修改失败", "", "", "", "", "", "");
				}
			}
			out.print(callbackData);
			out.flush();
			out.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		String callbackData = "";
		String idStr = this.request.getParameter("id");
		try {
			PrintWriter out = this.response.getWriter();

			if ((idStr == null) || ("".equals(idStr))) {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} else {
				int id = 0;
				try {
					id = Integer.parseInt(idStr);
				} catch (Exception e) {
					callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
				}
				Product findProduct = (Product) this.productService.findById(Product.class, id);
				if (findProduct == null) {
					callbackData = BjuiJson.json("300", "产品不存在", "", "", "", "", "", "");
				} else
					try {
						boolean result = this.productService.delete(findProduct);
						if (result)
							callbackData = BjuiJson.json("200", "删除成功", "", "", "", "", "", "");
						else
							callbackData = BjuiJson.json("300", "删除失败", "", "", "", "", "", "");
					} catch (JSONException e) {
						e.printStackTrace();
					}
			}

			out.print(callbackData);
			out.flush();
			out.close();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void indexProduct() {
		String idStr = this.request.getParameter("id");
		JSONObject json = new JSONObject();
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if ((idStr == null) || ("".equals(idStr))) {
			json.put("status", "0");
			json.put("message", "参数错误");
		} else {
			int id = 0;
			try {
				id = Integer.parseInt(idStr);
			} catch (Exception e) {
				json.put("status", "0");
				json.put("message", "参数错误");
			}
			Product findproduct = (Product) this.productService.findById(Product.class, id);
			if (findproduct == null) {
				json.put("status", "0");
				json.put("message", "产品不存在");
			} else {
				JSONObject jsonObj = (JSONObject) JSONObject.toJSON(findproduct);
				json.put("status", "1");
				json.put("product", jsonObj);
			}
		}
		out.print(json);
		out.flush();
		out.close();
	}

	public void productDetail() {
		String idStr = this.request.getParameter("id");
		PrintWriter out = null;
		String callback = "";
		try {
			out = this.response.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if ((idStr == null) || ("".equals(idStr))) {
			callback = "参数错误";
			out.print(callback);
			out.flush();
			out.close();
		} else {
			int id = 0;
			try {
				id = Integer.parseInt(idStr);
			} catch (Exception e) {
				callback = "参数错误";
				out.print(callback);
				out.flush();
				out.close();
			}
			Product findproduct = (Product) this.productService.findById(Product.class, id);
			if (findproduct == null) {
				callback = "产品不存在";
				out.print(callback);
				out.flush();
				out.close();
			} else {
				this.request.setAttribute("product", findproduct);
				try {
					this.request.getRequestDispatcher("detail.jsp").forward(this.request, this.response);
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getProductsByCategory() {
		String idString = request.getParameter("categoryId");
		List<Product> products = productService.getListByCategory(Integer.parseInt(idString));
		try {
			response.getWriter().write(JSON.toJSONString(products));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void productList() {
		String c = request.getParameter("c");
		Config config = configService.findById(Config.class, 1);
		String html = "";
		User user = (User) request.getSession().getAttribute("loginUser");
		if (c == null || "".equals(c) || user == null) {
			return;
		}
		switch (c) {
		case "10": {
			// 特码——号码
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>特码——<span style='color: red;'>号码</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle().substring(2, product.getTitle().length())
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "11": {
			// 特码——波色
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>特码——<span style='color: red;'>波色</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle().substring(2, product.getTitle().length())
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "12": {
			// 特码——大小
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>特码——<span style='color: red;'>大小</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle().substring(2, product.getTitle().length())
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "13": {
			// 特码——单双
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>特码——<span style='color: red;'>单双</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle().substring(2, product.getTitle().length())
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "14": {
			// 特码——高级
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>特码——<span style='color: red;'>高级</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 60px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "16": {
			// 正码——号码
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码——<span style='color: red;'>号码</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle().substring(2, product.getTitle().length())
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "17": {
			// 正码——总和
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码——<span style='color: red;'>总和</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle().substring(2, product.getTitle().length())
						+ "' style='height: 30px; width: 60px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "19": {
			// 正码特——正一特
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码特——<span style='color: red;'>正一特</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "20": {
			// 正码特——正二特
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码特——<span style='color: red;'>正二特</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "21": {
			// 正码特——正三特
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码特——<span style='color: red;'>正三特</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "22": {
			// 正码特——正四特
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码特——<span style='color: red;'>正四特</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "23": {
			// 正码特——正五特
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码特——<span style='color: red;'>正五特</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "24": {
			// 正码特——正六特
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码特——<span style='color: red;'>正六特</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "26": {
			// 正码——正码一
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码——<span style='color: red;'>正码一</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "27": {
			// 正码——正码二
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码——<span style='color: red;'>正码二</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "28": {
			// 正码——正码三
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码——<span style='color: red;'>正码三</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "29": {
			// 正码——正码四
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong> 正码——<span style='color: red;'>正码四</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "30": {
			// 正码——正码五
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong> 正码——<span style='color: red;'>正码五</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "31": {
			// 正码——正码六
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong>正码——<span style='color: red;'>正码六</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "37": {
			// 连码——三全中
			html += "<table class='table'><caption><strong>连码——<span style='color: red;'>三全中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th>"
					+ "<th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>"
                        + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";

			break;
		}
		case "38": {
			// 连码——三中二
			html += "<table class='table'><caption><strong>连码——<span style='color: red;'>三中二(中三赔率高达"+config.getSzezzs()+")</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
                        + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "39": {
			// 连码——二全中
			html += "<table class='table'><caption><strong>连码——<span style='color: red;'>二全中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
                        + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "40": {
			// 连码——二中特
			html += "<table class='table'><caption><strong>连码——<span style='color: red;'>二中特(中二赔率高达"+config.getEztzze()+")</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "41": {
			// 连码——特串
			html += "<table class='table'><caption><strong>连码——<span style='color: red;'>特串</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "42": {
			// 连码——四中一
			html += "<table class='table'><caption><strong>连码——<span style='color: red;'>四中一</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "34": {
			// 生肖/尾数——一肖
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong> 生肖——<span style='color: red;'>一肖</span></strong>";
			html += "</caption><thead><tr><th >名称</th><th >赔率</th><th ><span style='margin-left: 20px;'>购买数量</span></th><th >金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "35": {
			// 生肖/尾数——特肖
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong> 生肖——<span style='color: red;'>特肖</span></strong>";
			html += "</caption><thead><tr><th >名称</th><th >赔率</th><th ><span style='margin-left: 20px;'>购买数量</span></th><th >金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "36": {
			// 生肖/尾数——尾数
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong><span style='color: red;'>尾数</span></strong>";
			html += "</caption><thead><tr><th>尾号</th><th >赔率</th><th ><span style='margin-left: 20px;'>购买数量</span></th><th >金额</th>";
			html += "</tr></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 30px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		case "44": {
			// 合肖——二肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>二肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "45": {
			// 合肖——三肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>三肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "46": {
			// 合肖——四肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>四肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "47": {
			// 合肖——五肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>五肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "48": {
			// 合肖——六肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>六肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "49": {
			// 合肖——七肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>七肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "50": {
			// 合肖——八肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>八肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "51": {
			// 合肖——九肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>九肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "52": {
			// 合肖——十肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>十肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "53": {
			// 合肖——十一肖
			html += "<table class='table'><caption><strong>合肖——<span style='color: red;'>十一肖</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "58": {
			// 生肖连——二肖连中
			html += "<table class='table'><caption><strong>生肖连——<span style='color: red;'>二肖连中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "59": {
			// 生肖连——三肖连中
			html += "<table class='table'><caption><strong>生肖连——<span style='color: red;'>三肖连中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "60": {
			// 生肖连——四肖连中
			html += "<table class='table'><caption><strong>生肖连——<span style='color: red;'>四肖连中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "61": {
			// 生肖连——五肖连中
			html += "<table class='table'><caption><strong>生肖连——<span style='color: red;'>五肖连中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "62": {
			// 生肖连——二肖连不中
			html += "<table class='table'><caption><strong>生肖连——<span style='color: red;'>二肖连不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "63": {
			// 生肖连——三肖连不中
			html += "<table class='table'><caption><strong>生肖连——<span style='color: red;'>三肖连不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "64": {
			// 生肖连——四肖连不中
			html += "<table class='table'><caption><strong>生肖连——<span style='color: red;'>四肖连不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "65": {
			// 尾数连——二尾连中
			html += "<table class='table'><caption><strong>尾数连——<span style='color: red;'>二尾连中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>尾数</th><th>赔率</th><th>选中</th><th>尾数</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "66": {
			// 尾数连——三尾连中
			html += "<table class='table'><caption><strong>尾数连——<span style='color: red;'>三尾连中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>尾数</th><th>赔率</th><th>选中</th><th>尾数</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "67": {
			// 尾数连——四尾连中
			html += "<table class='table'><caption><strong>尾数连——<span style='color: red;'>四尾连中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>尾数</th><th>赔率</th><th>选中</th><th>尾数</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "68": {
			// 尾数连——四尾连不中
			html += "<table class='table'><caption><strong>尾数连——<span style='color: red;'>四尾连不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>尾数</th><th>赔率</th><th>选中</th><th>尾数</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "69": {
			// 尾数连——三尾连不中
			html += "<table class='table'><caption><strong>尾数连——<span style='color: red;'>三尾连不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>尾数</th><th>赔率</th><th>选中</th><th>尾数</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "70": {
			// 尾数连——二尾连不中
			html += "<table class='table'><caption><strong>尾数连——<span style='color: red;'>二尾连不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>尾数</th><th>赔率</th><th>选中</th><th>尾数</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				j++;
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				if (++j >= products.size()) {
					break;
				}
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "71": {
			// 合肖——五不中
			html += "<table class='table'><caption><strong>全不中——<span style='color: red;'>五不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "72": {
			// 全不中——六不中
			html += "<table class='table'><caption><strong>全不中——<span style='color: red;'>六不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "73": {
			// 全不中——七不中
			html += "<table class='table'><caption><strong>全不中——<span style='color: red;'>七不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "74": {
			// 全不中——八不中
			html += "<table class='table'><caption><strong>全不中——<span style='color: red;'>八不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "75": {
			// 全不中——九不中
			html += "<table class='table'><caption><strong>全不中——<span style='color: red;'>九不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "76": {
			// 全不中——十不中
			html += "<table class='table'><caption><strong>全不中——<span style='color: red;'>十不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "77": {
			// 全不中——十一不中
			html += "<table class='table'><caption><strong>全不中——<span style='color: red;'>十一不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "78": {
			// 全不中——十二不中
			html += "<table class='table'><caption><strong>全不中——<span style='color: red;'>十二不中</span></strong><span><a class='btn btn-info'  href='javascript:clear()' style='color:white; float: right;'>清空</a></span></caption><thead><th>名称</th><th>赔率</th><th>选中</th><th>名称</th><th>赔率</th><th>选中</th></thead><tbody>";
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			int j = 0;
			for (int i = 0; i < products.size() / 2 + 1; i++) {
				html += "<tr>";
				html += "<td><img src='" + products.get(j).getPicture();
				html += "' class='img-rounded product_pic' alt='" + products.get(j).getTitle()
						+ "' style='height: 30px; width: 30px;' ></td><td>" + (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId();
				html += "'></td>";
				if (++j >= products.size()) {
					break;
				}
				html += "<td><img src='" + products.get(j).getPicture() + "' class='img-rounded product_pic'alt='"
						+ products.get(j).getTitle() + "' style='height: 30px; width: 30px;'></td><td>"
						+ (user.hasFx()?products.get(j).getOddsFx():products.get(j).getOdds())
						+ "</td><td><input type='checkbox' class='checkbox' style='width: 20px;height: 20px;' id='c"
						+ products.get(j).getId() + "'></td>";
				html += "</tr>";
				j++;
			}
			html += "</tbody></table>";
			html += "<table class='table' align='center'><thead><tr><th width='50%'>已选</th><th width='15%'>赔率</th><th width='20%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;购买数量</th><th width='15%'>金额</th></tr></thead><tbody><tr class='info'><td id='selected'></td><td>0</td><td><div class='sn-count'><a href='javascript:void(0);' class='count-min'></a><input type='text' value='0' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)'/><a href='javascript:void(0);'class='count-add'></a></div></td><td>0.0</td></tr></tbody></table>";
			break;
		}
		case "80": {
			// 半波
			List<Product> products = productService.getListByCategory(Integer.parseInt(c));
			html += "<table class='table' align='center'>";
			html += "<caption style='margin-left: 2%;'>";
			html += "<strong><span style='color: red;'>半波</span></strong>";
			html += "</caption><thead><tr><th style='width: 20%;'>名称</th><th style='width: 20%;'>赔率</th><th style='width: 40%;'><span style='margin-left: 20px;'>购买数量</span></th><th style='width: 20%;'>金额</th>";
			html += "</tr></thead><tbody>";
			for (Product product : products) {
				CartItem cartItem = cartService.getCartItemById(product.getId(), user.getId());
				int quantity = 0;
				if (cartItem == null) {
					quantity = 0;
				} else {
					quantity = cartItem.getQuantity();
				}
				html += "<tr><td><img src='";
				html += product.getPicture();
				html += "' class='img-rounded product_pic' alt='" + product.getTitle()
						+ "' style='height: 30px; width: 60px;'></td>";
				html += "<td>" + (user.hasFx()?product.getOddsFx():product.getOdds()) + "</td>";
				html += "<td><div class='sn-count'><a href='javascript:void(0);' class='count-min' id='"
						+ product.getId() + "'></a>";
				html += "<input type='text' value='" + quantity
						+ "' class='input-reset count-num' maxlength='5' onblur='updateQuantityOnblur(this)' id='"
						+ product.getId() + "'/> <a href='javascript:void(0);'class='count-add' id='" + product.getId()
						+ "'></a></div></td>";
				html += "<td>" + quantity * 10.0f + "</td></tr>";
			}
			html += "</tbody></table>";
			break;
		}
		default:
			break;
		}
		request.setAttribute("nowTerm", config.getNextTerm());
		request.setAttribute("least", productCateService.findById(ProductCate.class, Integer.parseInt(c)).getLeast());
		request.setAttribute("number", cartService.getCartItemQuantity(user.getId()));
		request.setAttribute("html", html);
		try {
			request.getRequestDispatcher("productList.jsp").forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}