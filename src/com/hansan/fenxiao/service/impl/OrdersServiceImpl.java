package com.hansan.fenxiao.service.impl;

import com.hansan.fenxiao.dao.ICommissionDao;
import com.hansan.fenxiao.dao.IFinancialDao;
import com.hansan.fenxiao.dao.IOrdersDao;
import com.hansan.fenxiao.dao.IUserDao;
import com.hansan.fenxiao.entities.CartItem;
import com.hansan.fenxiao.entities.Commission;
import com.hansan.fenxiao.entities.Config;
import com.hansan.fenxiao.entities.Financial;
import com.hansan.fenxiao.entities.OrderItem;
import com.hansan.fenxiao.entities.Orders;
import com.hansan.fenxiao.entities.Term;
import com.hansan.fenxiao.entities.User;
import com.hansan.fenxiao.service.IOrdersService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("ordersService")
@Scope("prototype")
public class OrdersServiceImpl<T extends Orders> extends BaseServiceImpl<T> implements IOrdersService<T> {

	@Resource(name = "ordersDao")
	private IOrdersDao ordersDao;

	@Resource(name = "commissionDao")
	private ICommissionDao commissionDao;

	@Resource(name = "financialDao")
	private IFinancialDao financialDao;

	@Resource(name = "userDao")
	private IUserDao userDao;

	public Orders findByNo(String no) {
		return this.ordersDao.findByNo(no);
	}

	@Override
	public void savaOrderItems(List<OrderItem> orderItems) {
		this.ordersDao.savaOrderItems(orderItems);

	}

	@Override
	public List<OrderItem> getOrderItemsByOrderId(Integer id) {
		return this.ordersDao.getOrderItemsByOrderId(id);
	}

	@Override
	public int getCurrentTerm() {
		return this.ordersDao.getCurrentTerm();
	}

	@Override
	public int insertNewTerm() {
		return ordersDao.insertNewTerm();
	}

	@Override
	public int stopTerm() {
		return ordersDao.stopTerm();
	}

	@Override
	public int StartTerm() {
		return ordersDao.StartTerm();
	}

	@Override
	public List<OrderItem> getOrderItemsByTerm(int currentTerm) {
		return ordersDao.getOrderItemsByTerm(currentTerm);
	}

	@Override
	public Term getTermByID(int id) {
		return ordersDao.getTermByID(id);
	}

	@Override
	public void setFortuna(Integer id, Integer fortuna, String pingCodes) {
		ordersDao.setFortuna(id, fortuna, pingCodes);
	}

	@Override
	public List<OrderItem> updateandGetFortunateOrderItems(List<Integer> fortunates, Integer term) {
		return ordersDao.updateandGetFortunateOrderItems(fortunates, term);
	}

	@Override
	public Term getLastUnsettleTerm() {
		return ordersDao.getLastUnsettleTerm();
	}

	@Override
	public void setSettled(Integer id) {
		ordersDao.setSettled(id);

	}

	@Override
	public List<OrderItem> getOrderItemlist(String hql, int firstResult, int maxResult, Object[] params) {
		Query query = createQuery(hql);
		for (int i = 0; (params != null) && (i < params.length); i++) {
			query.setParameter(i + 1, params[i]);
		}
		@SuppressWarnings("unchecked")
		List<OrderItem> list = createQuery(hql).setFirstResult(firstResult).setMaxResults(maxResult).list();
		return list;
	}

	@Override
	public int getTermCount() {
		return ordersDao.getTermCount();
	}

	@Override
	public List<Term> list2(int start, int pageSize) {
		return ordersDao.list2(start, pageSize);
	}

	@Override
	public void saveOrUpdate(Term findTerm) {
		ordersDao.saveOrUpdate(findTerm);

	}

	@Override
	public void mergeBatch(List<OrderItem> standOff) {
		for (OrderItem orderItem : standOff) {
			ordersDao.saveOrUpdate(orderItem);
		}

	}

	@Override
	public void pay(Integer userId, Integer orderId, Config config) {
		User findUser = userDao.findById(User.class, userId);
		Orders findOrders = this.ordersDao.findById(Orders.class, orderId);
		// 扣钱
		findUser.setBalance(Double.valueOf(findUser.getBalance().doubleValue() - findOrders.getMoney().doubleValue()));
		userDao.saveOrUpdate(findUser);
		findOrders.setOrderItems(this.ordersDao.getOrderItemsByOrderId(findOrders.getId()));
		findOrders.setStatus(Integer.valueOf(1));
		Date date = new Date();
		findOrders.setPayDate(date);
		this.ordersDao.saveOrUpdate(findOrders);

		Financial financial = new Financial();
		financial.setType(Integer.valueOf(0));
		financial.setMoney(Double.valueOf(-findOrders.getMoney().doubleValue()));
		financial.setNo("" + System.currentTimeMillis());

		financial.setOperator(findUser.getName());

		financial.setUser(findUser);

		financial.setCreateDate(new Date());
		financial.setDeleted(false);

		financial.setBalance(findUser.getBalance());
		financial.setPayment("余额付款");
		financial.setRemark("购买" + findOrders.getTerm() + "期的支出,余额：" + findUser.getBalance());
		this.financialDao.saveOrUpdate(financial);
        //返利
        if(findUser.hasFx()){
            //每个商品都要算一次
            double rebateMoney = 0;
            for(OrderItem orderItem : findOrders.getOrderItems()){
                rebateMoney += 0.01*orderItem.getCategory().getRebate()*orderItem.getFigure();
            }
            findUser.setCommission(findUser.getCommission()+rebateMoney);
            System.out.println(rebateMoney);
            userDao.saveOrUpdate(findUser);
            Commission commission = new Commission();
            commission.setType(2);
            commission.setMoney(rebateMoney);
            commission.setNo("" + System.currentTimeMillis());
            commission.setOperator(findUser.getName());
            commission.setUser(findUser);
            commission.setCreateDate(date);
            commission.setDeleted(false);
            commission.setLevel(0);
            commission.setRemark( "分销用户自身"+ "购买商品返利");
            this.commissionDao.saveOrUpdate(commission);
            String levelNos = findUser.getSuperior();
            //这个时候如果我还有上级的话,按一定比例给我上级返佣
            if (!StringUtils.isEmpty(levelNos)) {
                double rate = config.getFirstLevel();
                String[] leverNoArr = levelNos.split(";");
                int i = leverNoArr.length - 1;
                User levelUser = this.userDao.getUserByNo(leverNoArr[i]);
                levelUser.setCommission(levelUser.getCommission()+rebateMoney*rate);
                userDao.saveOrUpdate(levelUser);
                Commission commission_ = new Commission();
                commission_.setType(1);
                commission_.setMoney(rebateMoney * rate);
                commission_.setNo("" + System.currentTimeMillis());
                commission_.setOperator(findUser.getName());
                commission_.setUser(levelUser);
                commission_.setCreateDate(date);
                commission_.setDeleted(false);
                commission_.setLevel(1);
                commission_.setRemark("第" + 1 + "级用户(具备分销能力):电话号码【"
                        + findUser.getPhone().substring(0, findUser.getPhone().length() - 4) + "****】购买商品奖励");
                this.commissionDao.saveOrUpdate(commission_);
            }
        }else{
            // 给分销人算佣金
            String levelNos = findUser.getSuperior();
            if (!StringUtils.isEmpty(levelNos)) {
                String[] leverNoArr = levelNos.split(";");
                int i = leverNoArr.length - 1;
                for (int j = 1; i > 0; j++) {
                    if (!StringUtils.isEmpty(leverNoArr[i])) {
                        User levelUser = this.userDao.getUserByNo(leverNoArr[i]);
                        if (levelUser != null) {
                            // 每件商品都要算一次
                            double commissionNum = 0;
                            double commissionRate = 0.0D;
                            for (OrderItem orderItem : findOrders.getOrderItems()) {
                                if (j == 1)
                                    commissionRate = orderItem.getCategory().getFirstLevel();
                                else if (j == 2)
                                    commissionRate = orderItem.getCategory().getSecoundLevel();
                                else if (j == 3) {
                                    commissionRate = orderItem.getCategory().getThirdLevel();
                                }
                                commissionNum += orderItem.getFigure() * commissionRate * 0.01;
                            }
                            System.out.println(commissionNum);
                            levelUser
                                    .setCommission(Double.valueOf(levelUser.getCommission().doubleValue() + commissionNum));
                            this.userDao.saveOrUpdate(levelUser);

                            Commission commission = new Commission();
                            commission.setType(Integer.valueOf(1));
                            commission.setMoney(Double.valueOf(commissionNum));
                            commission.setNo("" + System.currentTimeMillis());

                            commission.setOperator(findUser.getName());

                            commission.setUser(levelUser);

                            commission.setCreateDate(date);
                            commission.setDeleted(false);
                            commission.setLevel(Integer.valueOf(j));
                            commission.setRemark("第" + j + "级用户:电话号码【"
                                    + findUser.getPhone().substring(0, findUser.getPhone().length() - 4) + "****】购买商品奖励");
                            this.commissionDao.saveOrUpdate(commission);
                        }
                    }
                    i--;
                }
            }

        }

	}

}
