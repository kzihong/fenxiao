package com.hansan.fenxiao.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.hansan.fenxiao.entities.Config;
import com.hansan.fenxiao.entities.User;
import com.hansan.fenxiao.service.IConfigService;
import com.hansan.fenxiao.service.IUserService;
import com.hansan.fenxiao.utils.SendSms;

import net.sf.json.JsonConfig;

@Controller("phoneValidateCodeAction")
@Scope("prototype")
public class PhoneValidateCodeAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	@Resource(name = "userService")
	private IUserService<User> userService;
	
	@Resource(name="configService")
  	private IConfigService<Config> configService;

	public void createCode() {
		JSONObject json = new JSONObject();
		HttpSession session = request.getSession();
		String testPhone = request.getParameter("phone");
		User user = this.userService.getUserByPhone(testPhone);
		if (user != null) {
			json.put("status", 1);
			json.put("message", "该手机号已经存在，请直接登录或找回密码！");
		} else {
			double i = Math.random() * 1000000 + 1;
			String code = (int) i + "";
			System.out.println(code);
			Config config = configService.findById(Config.class, 1);
			//您的验证码是ABCD ,30秒内有效,若非本人操作请忽略此消息
			String testContent = config.getMsgAPITemplate().replace("ABCD", code);
			String testUsername = config.getMsgAPIUsername(); // 在短信宝注册的用户名ztchaoshi
			String testPassword = config.getMsgAPIPassword(); // 在短信宝注册的密码123456
			
			String httpUrl = "http://api.smsbao.com/sms";
			StringBuffer httpArg = new StringBuffer();
			httpArg.append("u=").append(testUsername).append("&");
			httpArg.append("p=").append(SendSms.md5(testPassword)).append("&");
			httpArg.append("m=").append(testPhone).append("&");
			httpArg.append("c=").append(SendSms.encodeUrlString(testContent, "UTF-8"));
			String result = SendSms.request(httpUrl, httpArg.toString());
			if (result.equals("0")) {
				json.put("status", 1);
				json.put("message", "短信已经发送至" + testPhone + "，注意查收");
				session.setAttribute("r" + testPhone, code);
			}
		}
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print(json.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createCodeF() {
		HttpSession session = request.getSession();
		String testPhone = request.getParameter("phone");
		double i = Math.random() * 1000000 + 1;
		String code = (int) i + "";
		System.out.println(code);
		Config config = configService.findById(Config.class, 1);
		//您的验证码是ABCD ,30秒内有效,若非本人操作请忽略此消息
		String testContent = config.getMsgAPITemplate().replace("ABCD", code);
		String testUsername = config.getMsgAPIUsername(); // 在短信宝注册的用户名ztchaoshi
		String testPassword = config.getMsgAPIPassword(); // 在短信宝注册的密码123456
		
		String httpUrl = "http://api.smsbao.com/sms";
		StringBuffer httpArg = new StringBuffer();
		httpArg.append("u=").append(testUsername).append("&");
		httpArg.append("p=").append(SendSms.md5(testPassword)).append("&");
		httpArg.append("m=").append(testPhone).append("&");
		httpArg.append("c=").append(SendSms.encodeUrlString(testContent, "UTF-8"));
		String result = SendSms.request(httpUrl, httpArg.toString());
		if (result.equals("0")) {
			JSONObject json = new JSONObject();
			json.put("status", 1);
			json.put("message", "短信已经发送至" + testPhone + "，注意查收");
			session.setAttribute("f" + testPhone, code);
			PrintWriter out;
			try {
				out = response.getWriter();
				out.print(json.toString());
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void validationCode() {
		String phone = request.getParameter("phone");
		String backcode = request.getParameter("code");
		HttpSession session = request.getSession();
		String code = (String) session.getAttribute("f" + phone);
		if (code.equals(backcode)) {
			request.setAttribute("phone", phone);
			try {
				request.getRequestDispatcher("findPasswordNext.jsp").forward(request, response);
			} catch (ServletException | IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	public void validationCodeR() {
		String phone = request.getParameter("phone");
		String backcode = request.getParameter("code");
		JSONObject json = new JSONObject();
		HttpSession session = request.getSession();
		String code = (String) session.getAttribute("r" + phone);
		if (code == null) {
			json.put("status", 2);
			json.put("message", "请先获取验证码！");
		}else if (backcode.equals(code)) {
			session.setAttribute("r" + phone, null);
			json.put("status", 1);
			json.put("message", "验证成功");
		}else{
			json.put("status", 2);
			json.put("message", "验证码错误！");
		}
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print(json.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
