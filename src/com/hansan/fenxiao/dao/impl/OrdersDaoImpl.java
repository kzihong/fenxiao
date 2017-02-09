/*    */ package com.hansan.fenxiao.dao.impl;

import java.util.List;

/*    */ import javax.annotation.Resource;

import org.hibernate.Query;
/*    */ import org.hibernate.Session;
/*    */ import org.hibernate.SessionFactory;
/*    */ import org.springframework.context.annotation.Scope;
/*    */ import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
/*    */
/*    */ import com.hansan.fenxiao.dao.IOrdersDao;
import com.hansan.fenxiao.dto.CodeResult;
import com.hansan.fenxiao.dto.Data;
import com.hansan.fenxiao.entities.Config;
import com.hansan.fenxiao.entities.OrderItem;
/*    */ import com.hansan.fenxiao.entities.Orders;
import com.hansan.fenxiao.entities.Term;
import com.hansan.fenxiao.utils.SendSms;

/*    */
/*    */ @Repository("ordersDao")
/*    */ @Scope("prototype")
/*    */ public class OrdersDaoImpl extends BaseDaoImpl<Orders>/*    */ implements IOrdersDao
/*    */ {
	/*    */
	/*    */ @Resource(name = "sessionFactory")
	/*    */ private SessionFactory sessionFactory;

	/*    */
	/*    */ private Session getSession()
	/*    */ {
		/* 26 */ return this.sessionFactory.getCurrentSession();
		/*    */ }

	/*    */
	/*    */ public Orders findByNo(String no) {
		/* 30 */ String hql = "from Orders where no=:no";
		/* 31 */ Orders orders = (Orders) getSession().createQuery(hql)/* 32 */ .setString("no", no).uniqueResult();
		/* 33 */ return orders;
		/*    */ }

	/*    */
	@Override
	public void savaOrderItems(List<OrderItem> orderItems) {
		for(OrderItem orderItem :orderItems){
			getSession().persist(orderItem);
		}

	}

	@Override
	public List<OrderItem> getOrderItemsByOrderId(Integer id) {
		@SuppressWarnings("unchecked")
		List<OrderItem> list = getSession().createQuery("select bean from OrderItem bean where bean.orders.id = :id").setInteger("id", id).list();
		return list;
	}

	@Override
	public int getCurrentTerm() {
		Integer term =  (Integer) getSession().createQuery("select bean.tid from Term bean where bean.end = false ").uniqueResult();
		if(term == null)
			return 0;
		else return term;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int insertNewTerm() {
		int curr = getCurrentTerm();
		if(curr == 0){
			Term term = new Term();
			Config config = (Config) getSession().get(Config.class, 1);
			int id = config.getNextTerm();
			term.setId(id);
			term.setCreateTime(System.currentTimeMillis());
			term.setFortuna(0);
			getSession().persist(term);
			config.setNextTerm(++id);
			List<OrderItem> orderItems = getSession().createQuery("select bean from OrderItem bean where bean.orders.status = 0").list();
			for(OrderItem orderItem : orderItems){
				getSession().delete(orderItem);
			}
			List<Orders> orderss = getSession().createQuery("select bean from Orders bean where bean.status = 0").list();
			for(Orders orders : orderss){
				getSession().delete(orders);
			}
			return 1;
		}else {
			return 0;
		}
		
	}

	@Override
	public int stopTerm() {
		Term term = (Term) getSession().get(Term.class,getCurrentTerm());
		if(term==null){
			return 0;
		}
		term.setEnd(true);
		term.setSent(true);
		return 1;
	}

	@Override
	public int StartTerm() {
		Term term = getLastUnsettleTerm();
		term = (Term)getSession().get(Term.class, term.getTid());
		term.setSent(false);
		term.setEnd(false);
		return 1;
	}

	@Override
	public List<OrderItem> getOrderItemsByTerm(int currentTerm) {
		String jpql = "select bean from OrderItem bean where bean.orders.tid = :term and bean.orders.status = 1";
		return getSession().createQuery(jpql).setInteger("term", currentTerm).list();
	}

	@Override
	public Term getTermByID(int id) {
		return (Term) getSession().get(Term.class, id);
	}

	@Override
	public void setFortuna(Integer id, Integer fortuna,String pingCodes) {
		Term term = getTermByID(id);
		term.setFortuna(fortuna);
		System.out.println(pingCodes);
		term.setPingCodes(pingCodes);
		term.setRollTime(System.currentTimeMillis());
		getSession().merge(term);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderItem> updateandGetFortunateOrderItems(List<Integer> fortunates,Integer term) {
		if(fortunates.size() == 0) return null;
		String jpql = "select bean from OrderItem bean where bean.orders.tid = :term and (";
		for(int i = 0 ; i < fortunates.size() ; i++){
			jpql+="bean.product.id = :pid"+i+" or ";
		}
		jpql = jpql.substring(0, jpql.length()-4);
		System.out.println(jpql);
		Query query = getSession().createQuery(jpql+")");
		query.setInteger("term", term);
		System.out.println(fortunates);
		for(int i = 0 ; i<fortunates.size();i++){
			query.setInteger("pid"+i, fortunates.get(i));
		}
		List<OrderItem> list= query.list();
		try {
			for(OrderItem orderItem :list){
				orderItem.setFortunate(true);
				getSession().saveOrUpdate(orderItem);
				getSession().flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Term getLastUnsettleTerm() {
		String jpql = "select bean from Term bean where bean.end = true order by bean.createTime desc";
		@SuppressWarnings("unchecked")
		List<Term> list = getSession().createQuery(jpql).list();
		if(list.size() > 0)
			return list.get(0);
		else return null;
	}

	@Override
	public void setSettled(Integer id) {
		Term term = (Term)getSession().get(Term.class, id);
		term.setSettled(true);	
	}

	@Override
	public int getTermCount() {
		long row = (long)getSession().createQuery("select count(*) from Term where fortuna != 0").uniqueResult();
		return (int)row;
	}

	@Override
	public List<Term> list2(int start, int pageSize) {
		String hql = "select bean from Term bean where bean.fortuna != 0 order by bean.tid desc";
		
		/*String url ="http://c.apiplus.net/daily.do?token=60da82703f39d7ca&code=hk6&format=json";
		String result = SendSms.request2(url);
		CodeResult codeResult = JSONObject.parseObject(result,CodeResult.class);
		System.out.println(codeResult);
		for(Data data : codeResult.getData()){
			Term term = new Term();
			term.setId(Integer.parseInt(data.getExpect().substring(5, 7)));
			term.setCreateTime(data.getOpentimestamp()*1000);
			term.setEnd(true);
			term.setSent(true);
			term.setSettled(true);
			String pingcode = data.getOpencode().split("\\+")[0];
			String fortuna = data.getOpencode().split("\\+")[1];
			int termF = Integer.parseInt(fortuna);
			term.setFortuna(termF);
			term.setPingCodes(pingcode);
			getSession().persist(term);
		}*/
		return getSession().createQuery(hql).setFirstResult(start).setMaxResults(pageSize).list();
	}
	
	public void termlist() {
		String url ="http://c.apiplus.net/daily.do?token=60da82703f39d7ca&code=hk6&format=json";
		String result = SendSms.request2(url);
		System.out.println(result);
	}

	@Override
	public void saveOrUpdate(Term findTerm) {
		getSession().saveOrUpdate(findTerm);
		
	}

	@Override
	public void saveOrUpdate(OrderItem orderItem) {
		getSession().saveOrUpdate(orderItem);
		
	}
}
