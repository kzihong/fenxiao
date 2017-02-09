package com.hansan.fenxiao.service.impl;

import com.hansan.fenxiao.dao.IFinancialDao;
import com.hansan.fenxiao.dao.IUserDao;
import com.hansan.fenxiao.entities.Financial;
import com.hansan.fenxiao.entities.OrderItem;
import com.hansan.fenxiao.entities.Term;
import com.hansan.fenxiao.entities.User;
import com.hansan.fenxiao.service.IFinancialService;
import com.hansan.fenxiao.utils.Chinese;
import com.hansan.fenxiao.utils.FortunaUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("financialService")
@Scope("prototype")
public class FinancialServiceImpl<T extends Financial> extends BaseServiceImpl<T> implements IFinancialService<T> {

	@Resource(name = "financialDao")
	private IFinancialDao financialDao;
	
	@Resource(name = "userDao")
	private IUserDao userDao;

	public List<Financial> getByUser(Integer userId) {
		return this.financialDao.getByUser(userId);
	}

	@Override
	public void saveOrUpdateBatch(List<OrderItem> standOff) {
		for(OrderItem orderItem :standOff){
			int findTerm = orderItem.getOrders().getTerm();
			User user = userDao.findById(User.class, orderItem.getOrders().getUser());
			double money = new BigDecimal(String.valueOf(orderItem.getFigure()*orderItem.getOdds())).doubleValue();
			String str1 = orderItem.getProductTitle().replaceAll(".jpg", "").replaceAll(".png", "");
			String str = Chinese.get(str1);
			if(str == null) str = str1;
			if(orderItem.getFortunate()){
				user.setBalance(user.getBalance()+money);
				userDao.saveOrUpdate(user);
				Financial financial = new Financial();
				financial.setRemark("购买了第" + findTerm + "期" + orderItem.getCategory().getName()
						+ "--"+str+"的奖金<br>当前余额："+new DecimalFormat("######0.00").format(user.getBalance()));
				financial.setMoney(money);
				financial.setType(Integer.valueOf(1));
				financial.setNo("" + System.currentTimeMillis());
				financial.setOperator(user.getName());
				financial.setUser(user);
				financial.setCreateDate(new Date());
				financial.setDeleted(false);
				financial.setBalance(user.getBalance());
				financial.setPayment("奖金到账");
				financialDao.saveOrUpdate(financial);
			}else {
				user.setBalance(user.getBalance()+orderItem.getFigure()*orderItem.getOdds());
				userDao.saveOrUpdate(user);
				Financial financial = new Financial();
				financial.setRemark("购买了第" + findTerm + "期" + orderItem.getCategory().getName()
						+ "--"+str+"的和局退款<br>当前余额："+new DecimalFormat("######0.00").format(user.getBalance()));
				financial.setMoney(money);
				financial.setType(Integer.valueOf(1));
				financial.setNo("" + System.currentTimeMillis());
				financial.setOperator(user.getName());
				financial.setUser(user);
				financial.setCreateDate(new Date());
				financial.setDeleted(false);
				financial.setBalance(user.getBalance());
				financial.setPayment("和局退款");
				financialDao.saveOrUpdate(financial);
			}
		}

	}
}