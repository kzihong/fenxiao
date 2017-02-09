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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.tuckey.web.filters.urlrewrite.Conf;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hansan.fenxiao.entities.CartItem;
import com.hansan.fenxiao.entities.Commission;
import com.hansan.fenxiao.entities.Config;
import com.hansan.fenxiao.entities.Financial;
import com.hansan.fenxiao.entities.OrderItem;
import com.hansan.fenxiao.entities.Orders;
import com.hansan.fenxiao.entities.Product;
import com.hansan.fenxiao.entities.Term;
import com.hansan.fenxiao.entities.User;
import com.hansan.fenxiao.service.CartService;
import com.hansan.fenxiao.service.ICommissionService;
import com.hansan.fenxiao.service.IConfigService;
import com.hansan.fenxiao.service.IFinancialService;
import com.hansan.fenxiao.service.IOrdersService;
import com.hansan.fenxiao.service.IProductService;
import com.hansan.fenxiao.service.IUserService;
import com.hansan.fenxiao.utils.BjuiJson;
import com.hansan.fenxiao.utils.BjuiPage;
import com.hansan.fenxiao.utils.Chinese;
import com.hansan.fenxiao.utils.Error;
import com.hansan.fenxiao.utils.FortunaUtil;
import com.hansan.fenxiao.utils.FreemarkerUtils;
import com.hansan.fenxiao.utils.PageModel;

import freemarker.template.Configuration;

@Controller("ordersAction")
@Scope("prototype")
public class OrdersAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	@Resource(name = "ordersService")
	private IOrdersService<Orders> ordersService;

	@Resource(name = "userService")
	private IUserService<User> userService;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "productService")
	private IProductService<Product> productService;

	@Resource(name = "financialService")
	private IFinancialService<Financial> financialService;

	@Resource(name = "commissionService")
	private ICommissionService<Commission> commissionService;
	private Orders orders;
	private Term term;
	private String ftlFileName;

	@Resource(name = "configService")
	private IConfigService<Config> configService;

	public void list() {
		/*String result = SendSms.request2("http://c.apiplus.net/daily.do?token=60da82703f39d7ca&code=hk6&date=2016-07-23&format=json");
		CodeResult codeResult = JSONObject.parseObject(result, CodeResult.class);
		System.out.println(codeResult);
		for(Data data : codeResult.getData()){
			String aString = data.getExpect().substring(5, data.getExpect().length());
			System.out.println(aString);
			Term term = new Term();
			term.setId(Integer.parseInt(aString));
			String pingcode = data.getOpencode().split("\\+")[0];
			term.setPingCodes(pingcode);
			term.setFortuna(Integer.parseInt(data.getOpencode().split("\\+")[1]));
			term.setRollTime(data.getOpentimestamp()*1000);
			term.setCreateTime(System.currentTimeMillis());
			term.setEnd(true);
			term.setSent(true);
			term.setSettled(true);
			ordersService.saveOrUpdate(term);
		}*/
		
		String key = this.request.getParameter("key");
		String countHql = "select count(*) from OrderItem where 1=1";
		String hql = "from OrderItem where 1=1";
		if (StringUtils.isNotEmpty(key)) {
			countHql = countHql + " and (orders.user.name='" + key + "' or orders.no='" + key + "' or product.title='"
					+ key + "')";
			hql = hql + " and (orders.user.name='" + key + "' or orders.no='" + key + "' or product.title='" + key
					+ "')";
		}
		hql = hql + " order by orders.createDate desc";

		int count = 0;
		count = this.ordersService.getTotalCount(countHql, new Object[0]);
		this.page = new BjuiPage(this.pageCurrent, this.pageSize);
		this.page.setTotalCount(count);
		this.cfg = new Configuration();

		this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(), "WEB-INF/templates/admin");
		List<OrderItem> ordersList = this.ordersService.getOrderItemlist(hql, this.page.getStart(), this.page.getPageSize(),
				new Object[0]);
		Map root = new HashMap();
		for(OrderItem orderItem : ordersList){
			StringBuffer sBuffer = new StringBuffer();
			for(String string : orderItem.getProductTitle().split(",")){
				String keyValue = string.replace(".jpg", "").replace(".png", "");
				String chinese = Chinese.get(keyValue);
				sBuffer.append(chinese == null ? keyValue :chinese);
				sBuffer.append(",");
			}
			orderItem.setProductTitle(sBuffer.toString().substring(0,sBuffer.length()-1));
		}
		root.put("orderItemList", ordersList);
		root.put("page", this.page);
		root.put("key", key);
		FreemarkerUtils.freemarker(this.request, this.response, this.ftlFileName, this.cfg, root);

	}

	public void termList() {
		String key = this.request.getParameter("key");
		String countHql = "select count(*) from Term where 1=1";
		String hql = "from Term where 1=1";
		if (StringUtils.isNotEmpty(key)) {
			countHql = countHql + " and (id='" + key + "' or fortuna='" + key + "')";
			hql = hql + " and (id='" + key + "' or fortuna='" + key + "')";
		}
		hql = hql + " order by tid desc";

		int count = 0;
		count = this.ordersService.getTotalCount(countHql, new Object[0]);
		this.page = new BjuiPage(this.pageCurrent, this.pageSize);
		this.page.setTotalCount(count);
		this.cfg = new Configuration();

		this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(), "WEB-INF/templates/admin");
		List termList = this.ordersService.list(hql, this.page.getStart(), this.page.getPageSize(), new Object[0]);
		Map root = new HashMap();
		root.put("termList", termList);
		root.put("page", this.page);
		root.put("key", key);
		FreemarkerUtils.freemarker(this.request, this.response, this.ftlFileName, this.cfg, root);
	}

	public void fortunaPublish() {
		String callbackData = "";
		try {
			PrintWriter out = this.response.getWriter();
			int fortuna = 0;
			if (this.term == null || this.term.getFortuna() == 0) {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} else {
				fortuna = this.term.getFortuna();
				if (fortuna > 49 || fortuna < 1) {
					callbackData = BjuiJson.json("300", "输入的特码不符合规范", "", "", "", "", "", "");
				} else {
					boolean flag = true;
					String pingCodes = request.getParameter("pingCodes");
					for (String string : pingCodes.split(",")) {
						try {
							int pc = Integer.parseInt(string);
							if (pc > 49 || pc < 1) {
								flag = false;
								callbackData = BjuiJson.json("300", "输入的平码不符合规范", "", "", "", "", "", "");
							}
						} catch (Exception e) {
							e.printStackTrace();
							callbackData = BjuiJson.json("300", "输入的平码不符合规范", "", "", "", "", "", "");
							flag = false;
						}
					}
					if (flag) {
						this.term = this.ordersService.getTermByID(this.term.getTid());
						// 修改特码
						this.ordersService.setFortuna(this.term.getTid(), fortuna, pingCodes);
						callbackData = BjuiJson.json("200", "公布成功，奖金将在今晚十点十五分打入获奖者账户", "", "", "", "true", "", "");
					}
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

	public void termEnd() {
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
				Term findTerm = ordersService.getTermByID(id);
				if (findTerm.isEnd()) {
					int c = ordersService.getCurrentTerm();
					if (c != 0) {
						callbackData = BjuiJson.json("300", "系统已经有未关闭的期数了！", "", "", "", "", "", "");
					} else if (findTerm.isSettled()) {
						callbackData = BjuiJson.json("300", "该期已经结算过了，不允许再次开启！", "", "", "", "", "", "");
					} else {
						findTerm.setEnd(false);
						ordersService.saveOrUpdate(findTerm);
						callbackData = BjuiJson.json("200", "成功开启本期", "", "", "", "", "", "");
					}
				} else {
					findTerm.setEnd(true);
					ordersService.saveOrUpdate(findTerm);
					callbackData = BjuiJson.json("200", "成功结束本期", "", "", "", "", "", "");
				}
			}
			out.print(callbackData);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void insertNew() {
		String callbackData = "";
		try {
			PrintWriter out = this.response.getWriter();
			int term = this.ordersService.getCurrentTerm();
			if ((term != 0)) {
				callbackData = BjuiJson.json("300", "系统存在一个未关闭的期数，无法开启下一期", "", "", "", "", "", "");
			} else {
				this.ordersService.insertNewTerm();
				callbackData = BjuiJson.json("200", "成功开启下一期", "", "", "", "", "", "");
			}
			out.print(callbackData);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void termSettle() throws Exception {
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
				Term findTerm = ordersService.getTermByID(id);
				if (findTerm.isSettled()) {
					callbackData = BjuiJson.json("300", "该期已经结算过了！", "", "", "", "", "", "");
				} else {
					if (findTerm.getFortuna() == 0) {
						callbackData = BjuiJson.json("300", "该期还没有公布！", "", "", "", "", "", "");
					} else {
						int fortuna = findTerm.getFortuna();
						String pingCode = findTerm.getPingCodes();
						// orderItem
						List<OrderItem> orderItems = getFortunateOrderItem(fortuna, pingCode, findTerm.getTid());
						handleStandOff(orderItems);
						/*for (OrderItem orderItem : orderItems) {
							float rate = orderItem.getOdds();
							float backMoney = 0.1f * rate * orderItem.getFigure(); // 奖金
							// 加钱
							User user = this.userService.findById(User.class, orderItem.getOrders().getUser());
							user.setBalance(user.getBalance() + backMoney);
							this.userService.saveOrUpdate(user);
							Product product = productService.findById(Product.class,
									Integer.parseInt(orderItem.getProducts()));
							// 生成交易记录
							Financial financial = new Financial();
							financial.setRemark("购买了第" + findTerm.getId() + "期" + product.getProductCate().getName()
									+ "--" + product.getTitle() + "的奖金,当前余额：" + user.getBalance());
							financial.setMoney((double) backMoney);

							financial.setType(Integer.valueOf(1));
							financial.setNo("" + System.currentTimeMillis());

							financial.setOperator(user.getName());

							financial.setUser(user);

							financial.setCreateDate(new Date());
							financial.setDeleted(false);

							financial.setBalance(user.getBalance());
							financial.setPayment("奖金到账");
							this.financialService.saveOrUpdate(financial);
						}
					*/
						this.ordersService.setSettled(findTerm.getTid());
						callbackData = BjuiJson.json("200", "结算成功", "", "", "", "true", "", "");
					}
				}
			}
			out.print(callbackData);
			out.flush();
			out.close();
		}  catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public List<OrderItem> getFortunateOrderItem(int fortuna, String pingCode, int tid) throws Exception {
		List<OrderItem> orderItems = ordersService.getOrderItemsByTerm(tid);
		List<OrderItem> fortunateOrders = new ArrayList<>();
		List<Integer> pingcodes = new ArrayList<>();
		Config config = configService.findById(Config.class, 1);
		String fortunaZodia = FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg";
		List<OrderItem> standOff = new ArrayList<>(); 
		for (String pingc : pingCode.split(",")) {
			pingcodes.add(Integer.parseInt(pingc));
		}
		for (OrderItem orderItem : orderItems) {
			switch (orderItem.getCategory().getId()) {
			case 10:
				// 特码——号码
				if (orderItem.getProductTitle().equals(fortuna + ".jpg")) {
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 11:
				// 特码——波色
				if (orderItem.getProductTitle().equals(FortunaUtil.getBallColor(fortuna) + ".jpg")) {
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 12:
				// 特码——大小
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (orderItem.getProductTitle().equals(FortunaUtil.getBigsString(fortuna) + ".jpg")) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 13:
				// 特码——单双
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (orderItem.getProductTitle().equals(FortunaUtil.getSingleString(fortuna) + ".jpg")) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 14:
				// 特码——高级
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (FortunaUtil.getTmSenior(fortuna).contains(orderItem.getProductTitle())) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 16:
				// 正码——号码
				int i = Integer.parseInt(orderItem.getProductTitle().replace(".jpg", ""));
				for (int pingc : pingcodes) {
					if (pingc == i) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
						break;
					}
				}
				break;
			case 17:
				// 正码——总和
				int sum = fortuna;
				for (int pingc : pingcodes) {
					sum += pingc;
				}
				List<String> list = new ArrayList<>();
				if (sum % 2 == 0) {
					list.add("zongheshuang.png");
				} else {
					list.add("zonghedan.png");
				}
				if (sum >= 175) {
					list.add("zongheda.png");
				} else {
					list.add("zonghexiao.png");
				}
				if (list.contains(orderItem.getProductTitle())) {
					fortunateOrders.add(orderItem);
					orderItem.setFortunate(true);
				}
				break;
			case 19:
				// 正码特——正一特
				int z1t = pingcodes.get(0);
				if(orderItem.getProductTitle().equals(z1t+".jpg")){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				if(z1t == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (FortunaUtil.getZt(z1t).contains(orderItem.getProductTitle())) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 20:
				// 正码特——正二特
				int z2t = pingcodes.get(1);
				if(orderItem.getProductTitle().equals(z2t+".jpg")){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				if(z2t == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (FortunaUtil.getZt(z2t).contains(orderItem.getProductTitle())) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 21:
				// 正码特——正三特
				int z3t = pingcodes.get(2);
				if(orderItem.getProductTitle().equals(z3t+".jpg")){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				if(z3t == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (FortunaUtil.getZt(z3t).contains(orderItem.getProductTitle())) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 22:
				// 正码特——正四特
				int z4t = pingcodes.get(3);
				if(orderItem.getProductTitle().equals(z4t+".jpg")){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				if(z4t == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (FortunaUtil.getZt(z4t).contains(orderItem.getProductTitle())) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 23:
				// 正码特——正五特
				int z5t = pingcodes.get(4);
				if(orderItem.getProductTitle().equals(z5t+".jpg")){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				if(z5t == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (FortunaUtil.getZt(z5t).contains(orderItem.getProductTitle())) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 24:
				// 正码特——正六特
				int z6t = pingcodes.get(5);
				if(orderItem.getProductTitle().equals(z6t+".jpg")){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				if(z6t == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (FortunaUtil.getZt(z6t).contains(orderItem.getProductTitle())) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 26:
				// 正码1
				int zm1 = pingcodes.get(0);
				if(zm1 == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if(FortunaUtil.getZm(zm1).contains(orderItem.getProductTitle())){
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 27:
				// 正码2
				int zm2 = pingcodes.get(1);
				if(zm2 == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if(FortunaUtil.getZm(zm2).contains(orderItem.getProductTitle())){
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 28:
				// 正码3
				int zm3 = pingcodes.get(2);
				if(zm3 == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if(FortunaUtil.getZm(zm3).contains(orderItem.getProductTitle())){
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 29:
				// 正码4
				int zm4 = pingcodes.get(3);
				if(zm4 == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if(FortunaUtil.getZm(zm4).contains(orderItem.getProductTitle())){
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 30:
				// 正码5
				int zm5 = pingcodes.get(4);
				if(zm5 == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if(FortunaUtil.getZm(zm5).contains(orderItem.getProductTitle())){
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 31:
				// 正码6
				int zm6 = pingcodes.get(5);
				if(zm6 == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if(FortunaUtil.getZm(zm6).contains(orderItem.getProductTitle())){
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 37:
				// 三全中
				String[] zqz = orderItem.getProductTitle().split(",");
				int count = 0;
				for (String aStri : zqz) {
					if (pingcodes.contains(Integer.parseInt(aStri.replace(".jpg", ""))))
						count++;
				}
				if (count == 3) {
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 38:
				// 三中二
				String[] aStrings = orderItem.getProductTitle().split(",");
				int szecount = 0;
				for (String aStri : aStrings) {
					if (pingcodes.contains(Integer.parseInt(aStri.replace(".jpg", ""))))
						szecount++;
				}
				if (szecount == 2) {
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				if (szecount == 3) {
					orderItem.setFortunate(true);
					orderItem.setOdds((float) (config.getSzezzs()));
					fortunateOrders.add(orderItem);
				}
				break;
			case 39:
				// 二全中
				String[] eqz = orderItem.getProductTitle().split(",");
				int eqzcount = 0;
				for (String aStri : eqz) {
					if (pingcodes.contains(Integer.parseInt(aStri.replace(".jpg", ""))))
						eqzcount++;
				}
				if (eqzcount == 2) {
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 40:
				// 二中特
				String[] ezt = orderItem.getProductTitle().split(",");
				int eztcount = 0;
				for (String aStri : ezt) {
					if (pingcodes.contains(Integer.parseInt(aStri.replace(".jpg", ""))))
						eztcount++;
				}
				if (eztcount == 2) {
					orderItem.setFortunate(true);
					orderItem.setOdds((float) config.getEztzze());
					fortunateOrders.add(orderItem);
				}
				if (eztcount == 1) {
					for (String aStri : ezt) {
						if (fortuna == Integer.parseInt(aStri.replace(".jpg", ""))) {
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
						}
					}
				}
				break;
			case 41:
				// 特串
				String[] tc = orderItem.getProductTitle().split(",");
				int tccount = 0;
				for (String aStri : tc) {
					if (pingcodes.contains(Integer.parseInt(aStri.replace(".jpg", ""))))
						tccount++;
				}
				if (tccount == 1) {
					for (String aStri : tc) {
						if (fortuna == Integer.parseInt(aStri.replace(".jpg", ""))) {
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
						}
					}
				}
				break;
			case 42:
				// 四中一
				String[] szy = orderItem.getProductTitle().split(",");
				int szycount = 0;
				for (String aStri : szy) {
					if (pingcodes.contains(Integer.parseInt(aStri.replace(".jpg", ""))))
						szycount++;
				}
				if (szycount >= 1) {
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 34:
				// 一肖
				List<String> yx = new ArrayList<>();
				yx.add(FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg");
				for (int pm : pingcodes) {
					yx.add(FortunaUtil.getZodiaString(pm, config.getCurrentYeas()) + ".jpg");
				}
				if (yx.contains(orderItem.getProductTitle())) {
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 35:
				// 特肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if ((FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg")
							.equals(orderItem.getProductTitle())) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			case 36:
				// 尾数
				String ws = fortuna % 10 + ".jpg";//
				List<String> wsList = new ArrayList<>();
				wsList.add(ws);
				for(int p : pingcodes){
					wsList.add(p%10+".jpg");
				}
				System.out.println(111);
				System.out.println(wsList);
				if (wsList.contains(orderItem.getProductTitle())) {
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 44:
				// 2肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 45:
				// 3肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 46:
				// 4肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 47:
				// 5肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 48:
				// 6肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 49:
				// 7肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 50:
				// 8肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 51:
				// 9肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 52:
				// 10肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 53:
				// 11肖
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					for (String a : orderItem.getProductTitle().split(",")) {
						if(a.equals(fortunaZodia)){
							orderItem.setFortunate(true);
							fortunateOrders.add(orderItem);
							break;
						}
					}
				}
				break;
			case 58:
				// 2肖连中
				List<String> ex = new ArrayList<>();
				ex.add(FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg");
				for (int pm : pingcodes) {
					ex.add(FortunaUtil.getZodiaString(pm, config.getCurrentYeas()) + ".jpg");
				}
				int excount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (ex.contains(a)) {
						excount++;
					}
				}
				if(excount >= 2){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 59:
				// 3肖连中
				List<String> sx = new ArrayList<>();
				sx.add(FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg");
				for (int pm : pingcodes) {
					sx.add(FortunaUtil.getZodiaString(pm, config.getCurrentYeas()) + ".jpg");
				}
				int sxcount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (sx.contains(a)) {
						sxcount++;
					}
				}
				if(sxcount >= 3){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 60:
				// 4肖连中
				List<String> s4x = new ArrayList<>();
				s4x.add(FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg");
				for (int pm : pingcodes) {
					s4x.add(FortunaUtil.getZodiaString(pm, config.getCurrentYeas()) + ".jpg");
				}
				int s4xcount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (s4x.contains(a)) {
						s4xcount++;
					}
				}
				if(s4xcount >= 4){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 61:
				// 5肖连中
				List<String> wx = new ArrayList<>();
				wx.add(FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg");
				for (int pm : pingcodes) {
					wx.add(FortunaUtil.getZodiaString(pm, config.getCurrentYeas()) + ".jpg");
				}
				int wxcount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (wx.contains(a)) {
						wxcount++;
					}
				}
				if(wxcount >= 5){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 62:
				// 2肖连不中
				List<String> ex_an = new ArrayList<>();
				ex_an.add(FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg");
				for (int pm : pingcodes) {
					ex_an.add(FortunaUtil.getZodiaString(pm, config.getCurrentYeas()) + ".jpg");
				}
				int ex_ancount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (ex_an.contains(a)) {
						ex_ancount++;
					}
				}
				if(ex_ancount == 0){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 63:
				// 3肖连不中
				List<String> sx_an = new ArrayList<>();
				sx_an.add(FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg");
				for (int pm : pingcodes) {
					sx_an.add(FortunaUtil.getZodiaString(pm, config.getCurrentYeas()) + ".jpg");
				}
				int sx_ancount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (sx_an.contains(a)) {
						sx_ancount++;
					}
				}
				if(sx_ancount == 0){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 64:
				// 4肖连不中
				List<String> s4x_an = new ArrayList<>();
				s4x_an.add(FortunaUtil.getZodiaString(fortuna, config.getCurrentYeas()) + ".jpg");
				for (int pm : pingcodes) {
					s4x_an.add(FortunaUtil.getZodiaString(pm, config.getCurrentYeas()) + ".jpg");
				}
				int s4x_ancount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (s4x_an.contains(a)) {
						s4x_ancount++;
					}
				}
				if(s4x_ancount == 0){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 65:
				// 2尾连中
				List<String> ew = new ArrayList<>();
				ew.add(fortuna%10 + ".jpg");
				for (int pm : pingcodes) {
					ew.add(pm%10 + ".jpg");
				}
				int ewcount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (ew.contains(a)) {
						ewcount++;
					}
				}
				if(ewcount >= 2){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 66:
				// 3尾连中
				List<String> sw = new ArrayList<>();
				sw.add(fortuna%10 + ".jpg");
				for (int pm : pingcodes) {
					sw.add(pm%10 + ".jpg");
				}
				int swcount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (sw.contains(a)) {
						swcount++;
					}
				}
				if(swcount >= 3){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 67:
				// 4尾连中
				List<String> s4w = new ArrayList<>();
				s4w.add(fortuna%10 + ".jpg");
				for (int pm : pingcodes) {
					s4w.add(pm%10 + ".jpg");
				}
				int s4wcount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (s4w.contains(a)) {
						s4wcount++;
					}
				}
				if(s4wcount >= 4){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 68:
				// 4尾连不中
				List<String> s4w_en = new ArrayList<>();
				s4w_en.add(fortuna%10 + ".jpg");
				for (int pm : pingcodes) {
					s4w_en.add(pm%10 + ".jpg");
				}
				int s4w_encount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (s4w_en.contains(a)) {
						s4w_encount++;
					}
				}
				if(s4w_encount == 0){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 69:
				// 3尾连不中
				List<String> sw_en = new ArrayList<>();
				sw_en.add(fortuna%10 + ".jpg");
				for (int pm : pingcodes) {
					sw_en.add(pm%10 + ".jpg");
				}
				int sw_encount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (sw_en.contains(a)) {
						sw_encount++;
					}
				}
				if(sw_encount == 0){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 70:
				// 2尾连不中
				List<String> ew_en = new ArrayList<>();
				ew_en.add(fortuna%10 + ".jpg");
				for (int pm : pingcodes) {
					ew_en.add(pm%10 + ".jpg");
				}
				int ew_encount = 0;
				for (String a : orderItem.getProductTitle().split(",")) {
					if (ew_en.contains(a)) {
						ew_encount++;
					}
				}
				if(ew_encount == 0){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 71:
				// 5不中
				boolean flag5 = true;
				for(String a :orderItem.getProductTitle().split(",")){
					if(a.replace(".jpg", "").equals(fortuna+"")){
						flag5 = false;
					}else {
						if(pingcodes.contains(Integer.parseInt(a.replace(".jpg", "")))){
							flag5 = false;
						}
					}
				}
				if(flag5){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 72:
				// 6不中
				boolean flag6 = true;
				for(String a :orderItem.getProductTitle().split(",")){
					if(a.replace(".jpg", "").equals(fortuna+"")){
						flag6 = false;
					}else {
						if(pingcodes.contains(Integer.parseInt(a.replace(".jpg", "")))){
							flag6 = false;
						}
					}
				}
				if(flag6){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 73:
				// 7不中
				boolean flag7 = true;
				for(String a :orderItem.getProductTitle().split(",")){
					if(a.replace(".jpg", "").equals(fortuna+"")){
						flag7 = false;
					}else {
						if(pingcodes.contains(Integer.parseInt(a.replace(".jpg", "")))){
							flag7 = false;
						}
					}
				}
				if(flag7){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 74:
				// 8不中
				boolean flag8 = true;
				for(String a :orderItem.getProductTitle().split(",")){
					if(a.replace(".jpg", "").equals(fortuna+"")){
						flag8 = false;
					}else {
						if(pingcodes.contains(Integer.parseInt(a.replace(".jpg", "")))){
							flag8 = false;
						}
					}
				}
				if(flag8){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 75:
				// 9不中
				boolean flag9 = true;
				for(String a :orderItem.getProductTitle().split(",")){
					if(a.replace(".jpg", "").equals(fortuna+"")){
						flag9 = false;
					}else {
						if(pingcodes.contains(Integer.parseInt(a.replace(".jpg", "")))){
							flag9 = false;
						}
					}
				}
				if(flag9){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 76:
				// 10不中
				boolean flag10 = true;
				for(String a :orderItem.getProductTitle().split(",")){
					if(a.replace(".jpg", "").equals(fortuna+"")){
						flag10 = false;
					}else {
						if(pingcodes.contains(Integer.parseInt(a.replace(".jpg", "")))){
							flag10 = false;
						}
					}
				}
				if(flag10){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 77:
				// 11不中
				boolean flag11 = true;
				for(String a :orderItem.getProductTitle().split(",")){
					if(a.replace(".jpg", "").equals(fortuna+"")){
						flag11 = false;
					}else {
						if(pingcodes.contains(Integer.parseInt(a.replace(".jpg", "")))){
							flag11 = false;
						}
					}
				}
				if(flag11){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 78:
				// 12不中
				boolean flag12 = true;
				for(String a :orderItem.getProductTitle().split(",")){
					if(a.replace(".jpg", "").equals(fortuna+"")){
						flag12 = false;
					}else {
						if(pingcodes.contains(Integer.parseInt(a.replace(".jpg", "")))){
							flag12 = false;
						}
					}
				}
				if(flag12){
					orderItem.setFortunate(true);
					fortunateOrders.add(orderItem);
				}
				break;
			case 80:
				if(fortuna == 49){
					orderItem.setOdds(1);
					standOff.add(orderItem);
				}else {
					if (FortunaUtil.getHalfBall(fortuna).contains(orderItem.getProductTitle())) {
						orderItem.setFortunate(true);
						fortunateOrders.add(orderItem);
					}
				}
				break;
			default:
				throw new Exception("hehe");
			}
		}
		handleStandOff(standOff);
		return fortunateOrders;
	}

	private void handleStandOff(List<OrderItem> standOff) {
		if(standOff.size() == 0){
			return;
		}else{
			//修改订单状态
			ordersService.mergeBatch(standOff);
			//退回金额并生成财务信息
			this.financialService.saveOrUpdateBatch(standOff);
			
		}
		
	}

	public void fortunaShow() {
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
				Term findTerm = ordersService.getTermByID(id);
				this.cfg = new Configuration();

				this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(),
						"WEB-INF/templates/admin");
				Map root = new HashMap();
				root.put("term", findTerm);
				FreemarkerUtils.freemarker(this.request, this.response, "termEdit.ftl", this.cfg, root);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void add() {
		String pidStr = this.request.getParameter("pid");
		int pid = 0;
		try {
			pid = Integer.parseInt(pidStr);
		} catch (Exception e) {
			this.request.setAttribute("status", "0");
			this.request.setAttribute("message", "参数错误");
			try {
				this.request.getRequestDispatcher("cart.jsp").forward(this.request, this.response);
			} catch (ServletException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		Product findProduct = (Product) this.productService.findById(Product.class, pid);
		if (findProduct == null) {
			this.request.setAttribute("status", "0");
			this.request.setAttribute("message", "商品不存在");
		} else {
			this.request.setAttribute("status", "1");
			this.request.setAttribute("product", findProduct);
		}
		try {
			this.request.getRequestDispatcher("cart.jsp").forward(this.request, this.response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 快速下单 --单商品
	 */
	public void save() {
		int term = this.ordersService.getCurrentTerm();
		if (term == 0) {
			Error error = new Error();
			error.setCode("-100");
			error.setMessage("现在不是购买时间，请在十二点过后再购买");
			request.setAttribute("error", error);
			try {
				this.request.getRequestDispatcher("../error.jsp").forward(this.request, this.response);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String pidStr = this.request.getParameter("pid");
		String numStr = this.request.getParameter("num");
		int pid = 0;
		int num = 1;
		try {
			pid = Integer.parseInt(pidStr);
			num = Integer.parseInt(numStr);
		} catch (Exception e) {
			this.request.setAttribute("status", "0");
			this.request.setAttribute("message", "参数错误");
			try {
				this.request.getRequestDispatcher("orderAdd.jsp").forward(this.request, this.response);
			} catch (ServletException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		Product findProduct = (Product) this.productService.findById(Product.class, pid);
		if (findProduct == null) {
			this.request.setAttribute("status", "0");
			this.request.setAttribute("message", "商品不存在");
		} else {
			HttpSession session = this.request.getSession();
			User loginUser = (User) session.getAttribute("loginUser");
			if ((loginUser == null) || (loginUser.getId() == null)) {
				this.request.setAttribute("status", "0");
				this.request.setAttribute("message", "您未登陆或者登陆失效，请重新登陆");
			} else {
				// 限制条件结束
				// 生成单商品一个订单
				Orders newOrders = new Orders();
				newOrders.setStatus(Integer.valueOf(0));
				newOrders.setMoney(Double.valueOf(num * findProduct.getMoney().doubleValue()));
				String no = "" + System.currentTimeMillis() * 1000 + term;
				newOrders.setNo(no);
				newOrders.setCreateDate(new Date());
				newOrders.setDeleted(false);
				newOrders.setUser(loginUser.getId());
				newOrders.setMoney(findProduct.getMoney() * num);
				newOrders.setTerm(term);
				this.ordersService.saveOrUpdate(newOrders);

				OrderItem orderItem = new OrderItem();
				orderItem.setFigure((float) (findProduct.getMoney() * num));
				orderItem.setQuantity(num);
				orderItem.setFortunate(false);
				orderItem.setOrders(ordersService.findByNo(newOrders.getNo()));
				orderItem.setProducts("" + pid);
				List<OrderItem> orderItems = new ArrayList<OrderItem>();
				orderItems.add(orderItem);
				this.ordersService.savaOrderItems(orderItems);
				try {
					this.response.sendRedirect("settle?no=" + no);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void saveBC() {
		int term = this.ordersService.getCurrentTerm();
		if (term == 0) {
			Error error = new Error();
			error.setCode("-100");
			error.setMessage("现在不是购买时间，请在十二点过后再购买");
			request.setAttribute("error", error);
			try {
				this.request.getRequestDispatcher("../error.jsp").forward(this.request, this.response);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		Term cur = ordersService.getTermByID(term);
		if (cur.getFortuna() != 0 || cur.isSettled()) {
			Error error = new Error();
			error.setCode("-100");
			error.setMessage("系统异常");
			request.setAttribute("error", error);
			try {
				this.request.getRequestDispatcher("../error.jsp").forward(this.request, this.response);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		User user = (User) request.getSession().getAttribute("loginUser");
		if ((user == null) || (user.getId() == null)) {
			this.request.setAttribute("status", "0");
			this.request.setAttribute("message", "您未登陆或者登陆失效，请重新登陆");
		} else {
			// 限制条件结束
			List<CartItem> cartItems = cartService.getCartItems(user.getId());
			if (cartItems.size() == 0) {
				Error error = new Error();
				error.setCode("100");
				error.setMessage("购物车为空或已生成未付款订单，请前往下注管理查看");
				request.setAttribute("error", error);
				try {
					this.request.getRequestDispatcher("../error.jsp").forward(this.request, this.response);
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
			List<OrderItem> orderItems = c2o(cartItems);

			Orders newOrders = new Orders();
			newOrders.setStatus(Integer.valueOf(0));
			newOrders.setMoney(getTotal(orderItems));
			String no = "" + System.currentTimeMillis() * 1000 + cur.getTid();
			newOrders.setNo(no);
			newOrders.setCreateDate(new Date());
			newOrders.setDeleted(false);
			newOrders.setUser(user.getId());
			newOrders.setTerm(cur.getId());
			newOrders.setTid(cur.getTid());
			this.ordersService.saveOrUpdate(newOrders);

			for (OrderItem orderItem : orderItems) {
				orderItem.setOrders(ordersService.findByNo(no));
			}
			this.ordersService.savaOrderItems(orderItems);
			cartService.deleteCartItem(cartItems);
			try {
				newOrders.setOrderItems(orderItems);
				request.setAttribute("order", newOrders);
				request.setAttribute("user", userService.findById(User.class, user.getId()));
				request.getRequestDispatcher("settle2.jsp").forward(request, response);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}

	}

	public void termListUser() {
		String pStr = this.request.getParameter("p");
		int p = 1;
		if (!StringUtils.isEmpty(pStr)) {
			p = Integer.parseInt(pStr);
		}

		HttpSession session = this.request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");

		int count = 0;
		count = this.ordersService.getTermCount();
		PageModel pageModel = new PageModel();
		pageModel.setAllCount(count);
		pageModel.setCurrentPage(p);
		List<Term> termList = this.ordersService.list2(pageModel.getStart(), pageModel.getPageSize());
		JSONObject json = new JSONObject();
		if (termList.size() == 0) {
			json.put("status", "0");

			json.put("isNextPage", "0");
		} else {
			json.put("status", "1");
			if (termList.size() == pageModel.getPageSize()) {
				json.put("isNextPage", "1");
			} else {
				json.put("isNextPage", "0");
			}
			Config config = configService.findById(Config.class, 1);
			int year = config.getCurrentYeas();
			for (Term term : termList) {
				List<String> strings = new ArrayList<>();
				strings.add(FortunaUtil.getTmString(term.getFortuna()));
				strings.add(FortunaUtil.getZodiaString(term.getFortuna(), year));
				strings.add(FortunaUtil.getBallcolerString(term.getFortuna()));
				strings.add(FortunaUtil.getBigsString(term.getFortuna()));
				strings.add(FortunaUtil.getSingleString(term.getFortuna()));
				term.setProducts(strings);
			}
			JSONArray listJson = (JSONArray) JSONArray.toJSON(termList);
			json.put("list", listJson);
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		out.print(json);
		out.flush();
		out.close();
	}

	public void settle() {
		String no = this.request.getParameter("no");
		Orders findOrders = this.ordersService.findByNo(no);
		if (findOrders == null) {
			this.request.setAttribute("status", "0");
			this.request.setAttribute("message", "订单不存在");
		} else {
			HttpSession session = this.request.getSession();
			User loginUser = (User) session.getAttribute("loginUser");
			if ((loginUser == null) || (loginUser.getId() == null)) {
				this.request.setAttribute("status", "0");
				this.request.setAttribute("message", "您未登陆或者登陆失效，请重新登陆");
			} else {
				this.request.setAttribute("orders", findOrders);
				try {
					this.request.getRequestDispatcher("settle.jsp").forward(this.request, this.response);
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void pay() {
		int term = this.ordersService.getCurrentTerm();
		if (term == 0) {
			Error error = new Error();
			error.setCode("-100");
			error.setMessage("现在不是购买时间，请在十二点过后再购买");
			request.setAttribute("error", error);
			try {
				this.request.getRequestDispatcher("../error.jsp").forward(this.request, this.response);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String no = this.request.getParameter("no");
		Orders findOrders = this.ordersService.findByNo(no);
		HttpSession session = this.request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");

		JSONObject json = new JSONObject();
		if ((loginUser == null) || (loginUser.getId() == null)) {
			json.put("status", "0");
			json.put("message", "您未登陆或者登陆失效，请重新登陆");
			json.put("href", "../login.jsp");
		} else {
			User findUser = (User) this.userService.findById(User.class, loginUser.getId().intValue());
			if (findOrders == null) {
				json.put("status", "0");
				json.put("message", "订单不存在");
			} else if (findOrders.getUser() != findUser.getId()) {
				json.put("status", "0");
				json.put("message", "没有权限");
			} else if (findUser.getBalance().doubleValue() < findOrders.getMoney().doubleValue()) {
				json.put("status", "0");
				json.put("message", "余额不足，请先充值");
			} else if (findOrders.getStatus().intValue() == 1) {
				json.put("status", "0");
				json.put("message", "该订单已付款，请不要重复提交支付");
			} else {
				Config config = configService.findById(Config.class, 1);
				try {
					this.ordersService.pay(findUser.getId(),findOrders.getId(),config);
					json.put("status", "1");
					json.put("message", "付款成功");
					json.put("no", findOrders.getNo());
				} catch (Exception e) {
					json.put("status", "0");
					json.put("message", "系统繁忙");
				}
			}
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(json.toString());
		out.flush();
		out.close();
	}

	public void detail() {
		String no = this.request.getParameter("no");
		Orders findOrders = this.ordersService.findByNo(no);
		if (findOrders == null) {
			this.request.setAttribute("status", "0");
			this.request.setAttribute("message", "订单不存在");
		} else {
			HttpSession session = this.request.getSession();
			User loginUser = (User) session.getAttribute("loginUser");
			if (findOrders.getUser() != loginUser.getId()) {
				this.request.setAttribute("status", "0");
				this.request.setAttribute("message", "没有权限");
			} else {
				this.request.setAttribute("orders", findOrders);
				try {
					this.request.getRequestDispatcher("ordersDetail.jsp").forward(this.request, this.response);
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void indexList() {
		String pStr = this.request.getParameter("p");
		int p = 1;
		if (!StringUtils.isEmpty(pStr)) {
			p = Integer.parseInt(pStr);
		}

		String type = this.request.getParameter("type");
		HttpSession session = this.request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");
		String countHql = "select count(*) from Orders where deleted=0 and user=" + loginUser.getId();
		String hql = "from Orders where deleted=0 and user=" + loginUser.getId();
		if (("0".equals(type)) || ("1".equals(type))) {
			countHql = countHql + " and status=" + type;
			hql = hql + " and status=" + type;
		} else if ("3".equals(type)) {
			countHql = countHql + " and tid = " + this.ordersService.getCurrentTerm();
			hql = hql + " and tid = " + this.ordersService.getCurrentTerm();
		}
		hql = hql + " order by id desc";

		int count = 0;
		count = this.ordersService.getTotalCount(countHql, new Object[0]);
		PageModel pageModel = new PageModel();
		pageModel.setAllCount(count);
		pageModel.setCurrentPage(p);
		List<Orders> ordersList = this.ordersService.list(hql, pageModel.getStart(), pageModel.getPageSize(),
				new Object[0]);
		for (Orders orders : ordersList) {
			orders.setOrderItems(this.ordersService.getOrderItemsByOrderId(orders.getId()));
			orders.setUser(null);
		}
		JSONObject json = new JSONObject();
		if (ordersList.size() == 0) {
			json.put("status", "0");

			json.put("isNextPage", "0");
		} else {
			json.put("status", "1");
			if (ordersList.size() == pageModel.getPageSize()) {
				json.put("isNextPage", "1");
			} else {
				json.put("isNextPage", "0");
			}
			JSONArray listJson = (JSONArray) JSONArray.toJSON(ordersList);
			json.put("list", listJson);
		}
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		out.print(json);
		out.flush();
		out.close();
	}

	public void info() {
		String idStr = this.request.getParameter("id");
		String callbackData = "";
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if ((idStr == null) || ("".equals(idStr))) {
				callbackData = BjuiJson.json("300", "参数不能为空", "", "", "", "", "", "");
			} else {
				int id = 0;
				try {
					id = Integer.parseInt(idStr);
				} catch (Exception e) {
					callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
				}
				Orders findorders = (Orders) this.ordersService.findById(Orders.class, id);
				if (findorders == null) {
					callbackData = BjuiJson.json("300", "订单不存在", "", "", "", "", "", "");
				} else {
					this.cfg = new Configuration();

					this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(),
							"WEB-INF/templates/admin");
					Map root = new HashMap();
					root.put("orders", findorders);
					FreemarkerUtils.freemarker(this.request, this.response, this.ftlFileName, this.cfg, root);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}

	public void update() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String callbackData = "";
		try {
			if (this.orders == null) {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} else {
				Orders findorders = (Orders) this.ordersService.findById(Orders.class, this.orders.getId().intValue());
				this.orders.setCreateDate(findorders.getCreateDate());
				this.orders.setDeleted(findorders.isDeleted());
				this.orders.setVersion(findorders.getVersion());
				boolean result = this.ordersService.saveOrUpdate(this.orders);

				if (result) {
					callbackData = BjuiJson.json("200", "修改成功", "", "", "", "true", "", "");
				} else
					callbackData = BjuiJson.json("300", "修改失败", "", "", "", "", "", "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}

	public void delete() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String callbackData = "";
		try {
			String idStr = this.request.getParameter("id");

			if ((idStr == null) || ("".equals(idStr))) {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} else {
				int id = 0;
				try {
					id = Integer.parseInt(idStr);
				} catch (Exception e) {
					callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
				}
				Orders findorders = (Orders) this.ordersService.findById(Orders.class, id);
				if (findorders == null) {
					callbackData = BjuiJson.json("300", "订单不存在", "", "", "", "true", "", "");
				} else {
					boolean result = this.ordersService.delete(findorders);
					if (result)
						callbackData = BjuiJson.json("200", "删除成功", "", "", "", "", "", "");
					else
						callbackData = BjuiJson.json("300", "删除失败", "", "", "", "", "", "");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}

	public Orders getOrders() {
		return this.orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	public String getFtlFileName() {
		return this.ftlFileName;
	}

	public void setFtlFileName(String ftlFileName) {
		this.ftlFileName = ftlFileName;
	}

	private List<OrderItem> c2o(List<CartItem> cartItems) {
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setProducts(cartItem.getProducts());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setFigure(cartItem.getFigure());
			orderItem.setOdds(cartItem.getOdds());
			orderItem.setMutli(cartItem.isMutli());
			orderItem.setProductTitle(cartItem.getProductTitle());
			orderItem.setCategory(cartItem.getCategory());
			orderItems.add(orderItem);
		}
		return orderItems;
	}

	private double getTotal(List<OrderItem> orderItems) {
		double i = 0;
		for (OrderItem orderItem : orderItems) {
			i += orderItem.getFigure();
		}
		return i;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

}
