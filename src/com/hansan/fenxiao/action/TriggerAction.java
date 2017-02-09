package com.hansan.fenxiao.action;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.alibaba.fastjson.JSONObject;
import com.hansan.fenxiao.dto.CodeResult;
import com.hansan.fenxiao.entities.Config;
import com.hansan.fenxiao.entities.Financial;
import com.hansan.fenxiao.entities.OrderItem;
import com.hansan.fenxiao.entities.Orders;
import com.hansan.fenxiao.entities.Product;
import com.hansan.fenxiao.entities.Term;
import com.hansan.fenxiao.entities.User;
import com.hansan.fenxiao.service.IConfigService;
import com.hansan.fenxiao.service.IFinancialService;
import com.hansan.fenxiao.service.IOrdersService;
import com.hansan.fenxiao.service.IProductService;
import com.hansan.fenxiao.service.IUserService;
import com.hansan.fenxiao.utils.FortunaUtil;
import com.hansan.fenxiao.utils.SendSms;

import freemarker.template.utility.StringUtil;

public class TriggerAction {

	@Resource(name = "ordersService")
	private IOrdersService<Orders> ordersService;

	@Resource(name = "userService")
	private IUserService<User> userService;

	@Resource(name = "financialService")
	private IFinancialService<Financial> financialService;

	@Resource(name = "configService")
	private IConfigService<Config> configService;
	
	@Resource(name = "productService")
	private IProductService<Product> productService;
	
	protected synchronized void endSale() {
		System.out.println("21:25-23：59 例行封锁，请第二天的00:00再来购买，谢谢配合");
		try {
			int term = this.ordersService.getCurrentTerm();
			if(term == 0){
				System.err.println("重复结束被打断");
				return;
			}
			List<OrderItem> toTerms = ordersService.getOrderItemsByTerm(term);
			System.out.println(toTerms.size());
			ordersService.stopTerm();
			Term term2 = ordersService.getTermByID(term);
			File excel = getExcelList(toTerms, term2.getId());
			System.out.println();
			System.out.println("发送邮件");
			Config config = (Config) this.configService.findById(Config.class, 1);
			sendFileEmail(config.getAcceptExcelEmail(), config.getEmailStmpCode(),
					excel.getName(), excel);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private synchronized void sendFileEmail(String acceptExcelEmail, String emailStmpCode , String name,
			File file) {
		MultiPartEmail email = new MultiPartEmail();
		try {
			// 这里是发送服务器的名字：
			email.setHostName("smtp.126.com");
			// 编码集的设置
			email.setCharset("utf-8");
			// 收件人的邮箱
			email.addTo(acceptExcelEmail);
			// 发送人的邮箱
			email.setFrom("yjiawen@126.com");
			// 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码
			email.setAuthentication("yjiawen", emailStmpCode);
			email.setSubject(name);
			// 要发送的信息
			email.setMsg(name);
			email.attach(file);
			// 发送
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
		}
		
	}

	protected synchronized void startSale() {
		if(this.ordersService.getCurrentTerm() != 0 ){
			System.err.println("重复开启被打断");
			return;
		}
		try {
			Term term = this.ordersService.getLastUnsettleTerm();
			if (term.getFortuna() == 0) {
				System.out.println("今天没有开，还是下在这一期");
				this.ordersService.StartTerm();
			} else {
				System.out.println("今天开过了，下在新的一期里面");
				this.ordersService.insertNewTerm();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected synchronized void settle(){
		System.out.println("开始结账");
		try {
			Term term = this.ordersService.getLastUnsettleTerm();
			// 已经结算过了的话
			if (term.isSettled()){
				System.err.println("重复结账被打断");
				return;
			}

			int fortuna = term.getFortuna();
			if (fortuna == 0) {
				return;
			} // 还没发布的话,说明这一期没有开
			List<OrderItem> orderItems = getFortunateOrderItem(fortuna, term.getPingCodes(), term.getTid());
			handleStandOff(orderItems);
			this.ordersService.setSettled(term.getTid());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void setFortuna() {
		try {
			Term term = this.ordersService.getLastUnsettleTerm();
			if(term == null){
				return;
			}
			if(term.getFortuna() != 0 && null != term.getPingCodes() && term.getPingCodes() != ""){
				System.err.println("重复写入被打断");
				return;
			}
			System.out.println("开启写入");
			String requestUrl = "http://c.apiplus.net/newly.do?token=TOKEN&code=hk6&format=json".replace("TOKEN", "60da82703f39d7ca");
			int time = 0 ;
			while (time < 120) {
				String result = SendSms.request2(requestUrl);
				if(result == null) continue;
				CodeResult codeResult = JSONObject.parseObject(result, CodeResult.class);
				if(codeResult.getFortuna() != 0){
					this.ordersService.setFortuna(term.getTid(), codeResult.getFortuna(),codeResult.getPingCodes());
					time = 200;
					System.out.println("写入最新一期的特码");
					System.out.println(codeResult);
				}
				Thread.sleep(15000);
				time++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private File getExcelList(List<OrderItem> orderItems, int term) {
		System.out.println("开始生成excel");
		Config config = configService.findById(Config.class, 1);
		String foldername = config.getFoldername();
		File file = new File(foldername);
		String filename = new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "第" + term + "期.xls";
		if (!file.exists()) {
			file.mkdirs();
		}
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("订单项目截止表");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		// 创建一个居中格式
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("编号");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("购买产品");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("购买者手机号");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("购买金额");
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue("赔率");
		cell.setCellStyle(style);
		cell = row.createCell(5);
		cell.setCellValue("用户名");
		cell.setCellStyle(style);
		for (int i = 0; i < orderItems.size(); i++) {
			User thisUser = this.userService.findById(User.class, orderItems.get(i).getOrders().getUser());
			row = sheet.createRow(i + 1);
			row.createCell(0).setCellValue(orderItems.get(i).getId());
			row.createCell(1).setCellValue(orderItems.get(i).getCategory().getName()+"-"+orderItems.get(i).getProductTitle().replaceAll(".jpg", "").replaceAll(".png", ""));
			row.createCell(2).setCellValue(thisUser.getPhone());
			row.createCell(3).setCellValue(orderItems.get(i).getFigure());
			row.createCell(4).setCellValue(orderItems.get(i).getOdds());
			row.createCell(5).setCellValue(thisUser.getName());
		}
		try {
			FileOutputStream fout = new FileOutputStream(foldername + filename);
			wb.write(fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new File(foldername + filename);
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
	
}
