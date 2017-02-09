package com.hansan.fenxiao.dao.impl;

import com.hansan.fenxiao.dao.IUserDao;
import com.hansan.fenxiao.entities.User;
import com.sun.xml.internal.bind.v2.model.core.ID;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository("userDao")
@Scope("prototype")
public class UserDaoImpl extends BaseDaoImpl<User> implements IUserDao {

	@Resource(name = "sessionFactory")
	private SessionFactory sessionFactory;

	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}

	public User getUserByName(String name) {
		String hql = "from User where name=:name and deleted=0";
		User user = (User) getSession().createQuery(hql).setString("name", name).uniqueResult();
		return user;
	}

	public User login(String phone, String password) {
		String hql = "from User where phone=:phone and password=:password and deleted=0";
		User user = (User) getSession().createQuery(hql).setString("phone", phone).setString("password", password)
				.uniqueResult();
		return user;
	}

	public User getUserByPhone(String phone) {
		String hql = "from User where phone=:phone and deleted=0";
		User user = (User) getSession().createQuery(hql).setString("phone", phone).uniqueResult();
		return user;
	}

	public User getUserByNo(String no) {
		String hql = "from User where no=:no and deleted=0";
		User user = (User) getSession().createQuery(hql).setString("no", no).uniqueResult();
		return user;
	}

	public List<User> levelUserList(String no) {
		String hql = "from User where superior like :no and deleted=0";

		List levelUserList = getSession().createQuery(hql).setString("no", "%;" + no + ";%").list();
		return levelUserList;
	}

	public List<User> levelUserTodayList(String no) {
		String hql = "from User where superior like '%:no%' and deleted=0 and date(createDate)=curdate()";

		List levelUserTodayList = getSession().createQuery(hql).setString("no", no).list();
		return levelUserTodayList;
	}

	public List<User> levelUserTodayStatusList(String no) {
		String hql = "from User where superior like '%:no%' and deleted=0 and date(statusDate)=curdate()";

		List levelUserTodayStatusList = getSession().createQuery(hql).setString("no", no).list();
		return levelUserTodayStatusList;
	}

	public User getUserByNameAndPhone(String name, String phone) {
		String hql = "from User where name=:name and phone=:phone and deleted=0";
		User user = (User) getSession().createQuery(hql).setString("name", name).setString("phone", phone)
				.uniqueResult();
		return user;
	}

	@Override
	public void assign(int parseInt) {
		User user = (User) getSession().get(User.class, parseInt);
		if(user.getStatus()==1){
			user.setStatus(0);
		}else{
			user.setStatus(1);
		}
		user.setNo(""+user.getId());
		user.setStatusDate(new Date());
	}

	@Override
	public double getAllMoney() {
		double balance = (Double) getSession().createQuery("SELECT SUM(balance) FROM User").uniqueResult();
		double commission = (Double) getSession().createQuery("SELECT SUM(commission) FROM User").uniqueResult();
		return balance+commission;
	}
}