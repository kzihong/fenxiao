package com.hansan.fenxiao.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hansan.fenxiao.entities.Admin;
import com.hansan.fenxiao.entities.Config;
import com.hansan.fenxiao.entities.Financial;
import com.hansan.fenxiao.entities.Orders;
import com.hansan.fenxiao.entities.Term;
import com.hansan.fenxiao.entities.User;
import com.hansan.fenxiao.service.IConfigService;
import com.hansan.fenxiao.service.IFinancialService;
import com.hansan.fenxiao.service.IOrdersService;
import com.hansan.fenxiao.service.IUserService;
import com.hansan.fenxiao.utils.BjuiJson;
import com.hansan.fenxiao.utils.BjuiPage;
import com.hansan.fenxiao.utils.FreemarkerUtils;
import com.hansan.fenxiao.utils.IpUtils;
import com.hansan.fenxiao.utils.MatrixToImageWriter;
import com.hansan.fenxiao.utils.Md5;
import com.hansan.fenxiao.utils.ResourcesConfiguration;
import com.opensymphony.xwork2.ActionContext;

import freemarker.template.Configuration;

@Controller("userAction")
@Scope("prototype")
public class UserAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	@Resource(name = "userService")
	private IUserService<User> userService;

	@Resource(name = "configService")
	private IConfigService<Config> configService;

	@Resource(name = "financialService")
	private IFinancialService<Financial> financialService;
	private User user;
	private String ftlFileName;

	public void list() {
		String key = this.request.getParameter("key");
		String countHql = "select count(*) from User where deleted=0";
		String hql = "from User where deleted=0";
		if (StringUtils.isNotEmpty(key)) {
			countHql = countHql + " and (name='" + key + "' or no='" + key + "' or phone='" + key + "')";
			hql = hql + " and (name='" + key + "' or no='" + key + "' or phone='" + key + "')";
		}
		hql = hql + " order by id desc";

		int count = 0;
		count = this.userService.getTotalCount(countHql, new Object[0]);
		this.page = new BjuiPage(this.pageCurrent, this.pageSize);
		this.page.setTotalCount(count);
		this.cfg = new Configuration();

		this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(), "WEB-INF/templates/admin");
		List userList = this.userService.list(hql, this.page.getStart(), this.page.getPageSize(), new Object[0]);
		double allMoney = this.userService.getAllMoney();
		Map root = new HashMap();
		root.put("allMoney", allMoney);
		root.put("userList", userList);
		root.put("page", this.page);
		root.put("key", key);
		FreemarkerUtils.freemarker(this.request, this.response, this.ftlFileName, this.cfg, root);
	}

	public void add() {
		this.cfg = new Configuration();

		this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(), "WEB-INF/templates/admin");
		Map root = new HashMap();
		FreemarkerUtils.freemarker(this.request, this.response, this.ftlFileName, this.cfg, root);
	}

	public void toLogin() {
		try {
			this.request.getRequestDispatcher("login.jsp").forward(request, response);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void register() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String tuijianren = this.request.getParameter("tuijianren");
		JSONObject json = new JSONObject();
		if (this.user == null) {
			json.put("status", "0");
			json.put("message", "参数错误");
		} else if (this.userService.getUserByName(this.user.getName()) != null) {
			json.put("status", "0");
			json.put("message", "账号已存在");
		} else if (this.userService.getUserByName(this.user.getPhone()) != null) {
			json.put("status", "0");
			json.put("message", "手机号已存在");
		} else {
			if (!tuijianren.equals("")) {
				User tjrUser = this.userService.getUserByNo(tuijianren);
				if (StringUtils.isEmpty(tjrUser.getSuperior())) {
					this.user.setSuperior(";" + tuijianren + ";");
				} else if (tjrUser.getSuperior().split(";").length > 3)
					this.user.setSuperior(";" + tjrUser.getSuperior().split(";", 3)[2] + tuijianren + ";");
				else {
					this.user.setSuperior(tjrUser.getSuperior() + tuijianren + ";");
				}
			}
			// 没有推荐人,低端注册
			try {
				String ip = IpUtils.getIpAddress(this.request);
				this.user.setRegisterIp(ip);
			} catch (Exception e) {
				this.user.setRegisterIp("0.0.0.0");
			}
			this.user.setName(user.getPhone());
			this.user.setPassword(Md5.getMD5Code(this.user.getPassword()));
			this.user.setLoginCount(Integer.valueOf(0));
			this.user.setStatus(Integer.valueOf(0));
			this.user.setBalance(Double.valueOf(0.0D));
			this.user.setCommission(Double.valueOf(0.0D));
			this.user.setDeleted(false);

			this.user.setCreateDate(new Date());
			boolean res = this.userService.saveOrUpdate(this.user);
			if (res) {
				User loginUser = this.userService.getUserByPhone(this.user.getPhone());
				loginUser.setLoginCount(Integer.valueOf(loginUser.getLoginCount().intValue() + 1));
				try {
					String ip = IpUtils.getIpAddress(this.request);
					loginUser.setLastLoginIp(ip);
				} catch (Exception e) {
					loginUser.setLastLoginIp("0.0.0.0");
				}
				loginUser.setLastLoginTime(new Date());
				this.userService.saveOrUpdate(loginUser);
				HttpSession session = this.request.getSession();
				session.setAttribute("loginUser", loginUser);
				json.put("status", "1");
				json.put("message", "注册成功");
			} else {
				json.put("status", "0");
				json.put("message", "注册失败，请重试");
			}
			try {
				String ip = IpUtils.getIpAddress(this.request);
				this.user.setRegisterIp(ip);
			} catch (Exception e) {
				this.user.setRegisterIp("0.0.0.0");
			}
			this.user.setSuperior(null);
		}
		out.print(json.toString());
		out.flush();
		out.close();
	}

	public void assign() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String idStr = this.request.getParameter("id");
		String callbackData = "";
		try {
			HttpSession session = this.request.getSession();
			Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
			if (loginAdmin.getJuri() == 0) {
				callbackData = BjuiJson.json("300", "权限不足", "", "", "", "", "", "");
			} else {
				userService.assign(Integer.parseInt(idStr));
				callbackData = BjuiJson.json("200", "修改成功", "", "", "", "", "", "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}

	public void info() throws JSONException {
		String idStr = this.request.getParameter("id");
		String callbackData = "";
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if ((idStr == null) || ("".equals(idStr))) {
			callbackData = BjuiJson.json("300", "参数不能为空", "", "", "", "", "", "");
		} else {
			int id = 0;
			try {
				id = Integer.parseInt(idStr);
			} catch (Exception e) {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			}
			User findUser = (User) this.userService.findById(User.class, id);
			if (findUser == null) {
				callbackData = BjuiJson.json("300", "用户不存在", "", "", "", "", "", "");
			} else {
				this.cfg = new Configuration();

				this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(),
						"WEB-INF/templates/admin");
				Map root = new HashMap();
				root.put("user", findUser);
				FreemarkerUtils.freemarker(this.request, this.response, this.ftlFileName, this.cfg, root);
			}
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
			if (this.user == null) {
				callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "", "");
			} else {
				User findUser = (User) this.userService.findById(User.class, this.user.getId().intValue());
				if (StringUtils.isNotEmpty(this.user.getPassword())) {
					findUser.setPassword(Md5.getMD5Code(this.user.getPassword()));
				}
				findUser.setName(this.user.getName());
				boolean result = this.userService.saveOrUpdate(findUser);

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
				if (id == 1) {
					callbackData = BjuiJson.json("300", "该用户不能删除", "", "", "", "true", "", "");
				} else {
					User findUser = (User) this.userService.findById(User.class, id);
					if (findUser == null) {
						callbackData = BjuiJson.json("300", "用户不存在", "", "", "", "true", "", "");
					} else {
						boolean result = this.userService.delete(findUser);
						if (result)
							callbackData = BjuiJson.json("200", "删除成功", "", "", "", "", "", "");
						else
							callbackData = BjuiJson.json("300", "删除失败", "", "", "", "", "", "");
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.print(callbackData);
		out.flush();
		out.close();
	}

	public void login() {
		ActionContext ac = ActionContext.getContext();
	     ServletContext sc = (ServletContext)ac
	       .get("com.opensymphony.xwork2.dispatcher.ServletContext");
	     String savePath = sc.getRealPath("/");
	     
		Config config = configService.findById(Config.class, 1);
		config.setFoldername(savePath);
		configService.saveOrUpdate(config);
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpSession session = this.request.getSession();
		JSONObject json = new JSONObject();
		if (this.user == null) {
			json.put("status", "0");
			json.put("message", "参数错误");
		} else {
			User loginUser = this.userService.login(this.user.getPhone(), Md5.getMD5Code(this.user.getPassword()));
			if (loginUser == null) {
				json.put("status", "0");
				json.put("message", "用户名或者密码错误");
			} else {
				loginUser.setLoginCount(Integer.valueOf(loginUser.getLoginCount().intValue() + 1));
				session.setAttribute("loginUser", loginUser);
				try {
					String ip = IpUtils.getIpAddress(this.request);
					loginUser.setLastLoginIp(ip);
				} catch (Exception e) {
					loginUser.setLastLoginIp("0.0.0.0");
				}
				loginUser.setLastLoginTime(new Date());
				this.userService.saveOrUpdate(loginUser);
				json.put("status", "1");
				json.put("message", "登录成功");
			}
		}
		out.print(json.toString());
		out.flush();
		out.close();
	}

	public void promote() {
		String no = this.request.getParameter("no");
		User findUser = this.userService.getUserByNo(no);
		if (findUser == null) {
			this.request.setAttribute("status", "0");
			this.request.setAttribute("message", "推广链接无效");
		} else if (findUser.getStatus().intValue() == 0) {
			this.request.setAttribute("status", "0");
			this.request.setAttribute("message", "推广链接无效");
		} else {
			this.request.setAttribute("status", "1");
			this.request.setAttribute("no", no);
		}
		try {
			this.request.getRequestDispatcher("register.jsp").forward(this.request, this.response);
		} catch (ServletException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void logout() throws IOException {
		HttpSession session = this.request.getSession();
		session.setAttribute("loginUser", null);
		this.response.sendRedirect("../login.jsp");
	}

	public void accountEdit() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String account = this.request.getParameter("account");
		HttpSession session = this.request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");
		User findUser = (User) this.userService.findById(User.class, loginUser.getId().intValue());
		JSONObject json = new JSONObject();
		try {
			findUser.setBankAccount(account);
			this.userService.saveOrUpdate(findUser);
			session.setAttribute("loginUser", findUser);
			json.put("status", "1");
			json.put("message", "填写账户成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(json);
		out.flush();
		out.close();
	}

	public void changePassword() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String oldPassword = this.request.getParameter("oldPassword");
		String newPassword = this.request.getParameter("newPassword");
		HttpSession session = this.request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");
		User findUser = (User) this.userService.findById(User.class, loginUser.getId().intValue());
		JSONObject json = new JSONObject();
		try {
			if (!StringUtils.equals(findUser.getPassword(), Md5.getMD5Code(oldPassword))) {
				json.put("status", "0");
				json.put("message", "旧密码错误");
			} else {
				findUser.setPassword(Md5.getMD5Code(newPassword));
				this.userService.saveOrUpdate(findUser);
				json.put("status", "1");
				json.put("message", "密码修改成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(json);
		out.flush();
		out.close();
	}

	public void resetPassword() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String password = this.request.getParameter("password");
		String phone = this.request.getParameter("phone");
		User findUser = this.userService.getUserByPhone(phone);
		JSONObject json = new JSONObject();
		try {
			if (findUser == null) {
				json.put("status", "0");
				json.put("message", "用户不存在");
			} else {
				findUser.setPassword(Md5.getMD5Code(password));
				this.userService.saveOrUpdate(findUser);
				json.put("status", "1");
				json.put("message", "密码重置成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(json);
		out.flush();
		out.close();
	}

	public void createUserNo() {
		User findUser = null;
		String no = "";
		do {
			Random random = new Random();
			int num = random.nextInt(899999) + 100000;
			this.user = this.userService.getUserByNo("" + num);
		} while (findUser != null);
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.print(no);
		out.flush();
		out.close();
	}

	public void userInfoJson() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpSession session = this.request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");
		User findUser = (User) this.userService.findById(User.class, loginUser.getId().intValue());
		JSONObject json = (JSONObject) JSONObject.toJSON(findUser);
		out.print(json.toString());
		out.flush();
		out.close();
	}

	public void commissionToBalance() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject json = new JSONObject();
		String moneyStr = this.request.getParameter("money");
		Double money = Double.valueOf(0.0D);
		try {
			money = Double.valueOf(Double.parseDouble(moneyStr));
		} catch (Exception e) {
			json.put("status", "0");
			json.put("message", "参数错误");
			out.print(json.toString());
			out.flush();
			out.close();
			return;
		}
		if (money.doubleValue() <= 0.0D) {
			json.put("status", "0");
			json.put("message", "金额必须大于0");
		} else {
			HttpSession session = this.request.getSession();
			User loginUser = (User) session.getAttribute("loginUser");
			User findUser = (User) this.userService.findById(User.class, loginUser.getId().intValue());
			if (money.doubleValue() > findUser.getCommission().doubleValue()) {
				json.put("status", "0");
				json.put("message", "佣金额度不足");
			} else {
				findUser.setBalance(Double.valueOf(findUser.getBalance().doubleValue() + money.doubleValue()));
				findUser.setCommission(Double.valueOf(findUser.getCommission().doubleValue() - money.doubleValue()));
				this.userService.saveOrUpdate(findUser);

				Financial financial = new Financial();
				financial.setType(Integer.valueOf(1));
				financial.setMoney(money);
				financial.setNo("" + System.currentTimeMillis());

				financial.setOperator(loginUser.getName());

				financial.setUser(findUser);

				financial.setCreateDate(new Date());
				financial.setDeleted(false);

				financial.setBalance(findUser.getBalance());
				financial.setPayment("佣金转入");
				financial.setRemark("佣金转入");
				this.financialService.saveOrUpdate(financial);
				json.put("status", "1");
				json.put("message", "转入成功");
			}
		}
		out.print(json.toString());
		out.flush();
		out.close();
	}

	public void balanceToUser() {
		PrintWriter out = null;
		try {
			out = this.response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject json = new JSONObject();
		String moneyStr = this.request.getParameter("money");
		String userno = this.request.getParameter("userno");
		Double money = Double.valueOf(0.0D);
		try {
			money = Double.valueOf(Double.parseDouble(moneyStr));
		} catch (Exception e) {
			json.put("status", "0");
			json.put("message", "参数错误");
			out.print(json.toString());
			out.flush();
			out.close();
			return;
		}
		User toUser = this.userService.getUserByNo(userno);
		if (toUser == null) {
			json.put("status", "0");
			json.put("message", "会员编号不存在");
		} else if (money.doubleValue() <= 0.0D) {
			json.put("status", "0");
			json.put("message", "金额必须大于0");
		} else {
			HttpSession session = this.request.getSession();
			User loginUser = (User) session.getAttribute("loginUser");
			User findUser = (User) this.userService.findById(User.class, loginUser.getId().intValue());
			if (money.doubleValue() > findUser.getBalance().doubleValue()) {
				json.put("status", "0");
				json.put("message", "货币额度不足");
			} else {
				findUser.setBalance(Double.valueOf(findUser.getBalance().doubleValue() - money.doubleValue()));
				toUser.setBalance(Double.valueOf(toUser.getBalance().doubleValue() + money.doubleValue()));
				this.userService.saveOrUpdate(findUser);
				this.userService.saveOrUpdate(toUser);

				Financial financial = new Financial();
				financial.setType(Integer.valueOf(0));
				financial.setMoney(Double.valueOf(-money.doubleValue()));
				financial.setNo("" + System.currentTimeMillis());

				financial.setOperator(loginUser.getName());

				financial.setUser(findUser);

				financial.setCreateDate(new Date());
				financial.setDeleted(false);

				financial.setBalance(findUser.getBalance());
				financial.setPayment("会员转账");
				financial.setRemark("会员转账，转入到会员编号【" + userno + "】");
				this.financialService.saveOrUpdate(financial);

				Financial financial2 = new Financial();
				financial2.setType(Integer.valueOf(1));
				financial2.setMoney(money);
				financial2.setNo("" + System.currentTimeMillis());

				financial2.setOperator(loginUser.getName());

				financial2.setUser(toUser);

				financial2.setCreateDate(new Date());
				financial2.setDeleted(false);

				financial2.setBalance(toUser.getBalance());
				financial2.setPayment("会员转账");
				financial2.setRemark("由会员编号【" + loginUser.getNo() + "】转入");
				this.financialService.saveOrUpdate(financial2);
				json.put("status", "1");
				json.put("message", "转入成功");
			}
		}

		out.print(json.toString());
		out.flush();
		out.close();
	}

	public void getPic() {
		String filename = request.getParameter("n");
		ActionContext ac = ActionContext.getContext();
		ServletContext sc = (ServletContext) ac.get("com.opensymphony.xwork2.dispatcher.ServletContext");
		String filePath = sc.getRealPath("/");
		File outputFile = new File(filePath + filename);
		try {
			OutputStream os;
			os = response.getOutputStream();
			FileInputStream fis = new FileInputStream(outputFile);
			int size = fis.available(); // 得到文件大小
			byte data[] = new byte[size];
			fis.read(data); // 读数据
			fis.close();
			os.write(data);
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void levelUserList() {
		String userS = request.getParameter("useId");
		User loginUser;
		if(userS == null || "".equals(userS)){
			HttpSession session = this.request.getSession();
			loginUser = (User) session.getAttribute("loginUser");
		}else {
			loginUser = userService.getUserByNo(userS);
		}
		List<User> levelUserList = this.userService.levelUserList(loginUser.getNo());
		List<String> firstLevelUsers = new ArrayList<String>();
		List<String> secondLevelUsers = new ArrayList<String>();
		List<String> thirdLevelUsers = new ArrayList<String>();
		int firstLevelNum = 0;

		int secondLevelNum = 0;

		int thirdLevelNum = 0;

		int allLevelNum = levelUserList.size();

		int unStatusUserNum = 0;

		int todayRegUserNum = 0;

		int todayStatusUserNum = 0;

		for (User user : levelUserList) {
			if (user.getStatus().intValue() == 0) {
				unStatusUserNum++;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String todayDate = sdf.format(new Date());

			String createDate = sdf.format(user.getCreateDate());

			String statusDate = "";
			if (user.getStatusDate() != null) {
				statusDate = sdf.format(user.getStatusDate());
			}

			if (StringUtils.equals(createDate, todayDate)) {
				todayRegUserNum++;
			}

			if (StringUtils.equals(statusDate, todayDate)) {
				todayStatusUserNum++;
			}

			String levelNos = user.getSuperior();
			if (!StringUtils.isEmpty(levelNos)) {
				String[] leverNoArr = levelNos.split(";");
				int i = leverNoArr.length - 1;
				for (int j = 1; i > 0; j++) {
					if (!StringUtils.isEmpty(leverNoArr[i])) {
						User levelUser = this.userService.getUserByNo(leverNoArr[i]);
						if (levelUser != null) {
							if ((j == 1) && (StringUtils.equals(loginUser.getNo(), leverNoArr[i]))) {
								if(userS == null || "".equals(userS))
									firstLevelUsers.add(user.getPhone().substring(0, 6) + "****");
								else firstLevelUsers.add(user.getPhone());
								firstLevelNum++;
							} else if ((j == 2) && (StringUtils.equals(loginUser.getNo(), leverNoArr[i]))) {
								if(userS == null || "".equals(userS))
									secondLevelUsers.add(user.getPhone().substring(0, 6) + "****");
								else firstLevelUsers.add(user.getPhone());
								secondLevelNum++;
							} else if ((j == 3) && (StringUtils.equals(loginUser.getNo(), leverNoArr[i]))) {
								if(userS == null || "".equals(userS))
									thirdLevelUsers.add(user.getPhone().substring(0, 6) + "****");
								else firstLevelUsers.add(user.getPhone());
								thirdLevelNum++;
							}
						}
					}
					i--;
				}

			}

		}

		JSONObject json = new JSONObject();
		json.put("firstLevelNum", Integer.valueOf(firstLevelNum));
		json.put("firstLevelUsers", firstLevelUsers);
		json.put("secondLevelUsers", secondLevelUsers);
		json.put("thirdLevelUsers", thirdLevelUsers);
		json.put("secondLevelNum", Integer.valueOf(secondLevelNum));
		json.put("thirdLevelNum", Integer.valueOf(thirdLevelNum));
		json.put("allLevelNum", Integer.valueOf(allLevelNum));
		json.put("unStatusUserNum", Integer.valueOf(unStatusUserNum));
		json.put("todayRegUserNum", Integer.valueOf(todayRegUserNum));
		json.put("todayStatusUserNum", Integer.valueOf(todayStatusUserNum));
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

	public void userCharge() {
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
				this.cfg = new Configuration();
				this.cfg.setServletContextForTemplateLoading(this.request.getServletContext(),
						"WEB-INF/templates/admin");
				Map root = new HashMap();
				root.put("userId", id);
				FreemarkerUtils.freemarker(this.request, this.response, "userCharge.ftl", this.cfg, root);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void getQrcode() {
		ActionContext ac = ActionContext.getContext();
		ServletContext sc = (ServletContext) ac.get("com.opensymphony.xwork2.dispatcher.ServletContext");
		String filePath = sc.getRealPath("/");
		
		
		String no = request.getParameter("no");
		File outputFile = new File(filePath + no+".gif");
		String text = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + "/" + "promote?no=" + no;
		int width = 300;
		int height = 300;
		// 二维码的图片格式
		String format = "gif";
		Hashtable hints = new Hashtable();
		// 内容所使用编码
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix bitMatrix;
		try {
			bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
		} catch (WriterException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 生成二维码
		try {
			OutputStream os = response.getOutputStream();
			FileInputStream fis = new FileInputStream(outputFile);
			int size = fis.available(); // 得到文件大小
			byte data[] = new byte[size];
			fis.read(data); // 读数据
			fis.close();
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getFtlFileName() {
		return this.ftlFileName;
	}

	public void setFtlFileName(String ftlFileName) {
		this.ftlFileName = ftlFileName;
	}

}