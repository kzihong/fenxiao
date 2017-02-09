<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	Calendar c = Calendar.getInstance(Locale.CHINA);
	c.setTime(new Date());
	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
	String[] week = { "星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
	String today = new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + week[dayOfWeek - 1];
	Long a = System.currentTimeMillis();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>内幕资料</title>
<link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body>
	<div class="sn-nav">
		<div class="sn-nav-back">
			<a href="javascript:history.back(-1)">返回</a>
		</div>
		<div class="sn-nav-title of" id="addAddr">内幕资料</div>
		<div class="sn-nav-right tr pr">
			<a href="index.jsp">首页</a>
		</div>
	</div>
	<div style="margin: 0 auto; width: 90%">
		<h2><%=today%></h2>
		<iframe
			src="${config.newsUrl}?a=<%=a%>"
			width="90%" frameborder="0"> </iframe>
		<h6 style="float: right; color: red;">
			这里是：香港赛马会官方网 － 标准开奖时间日期表
			</h5>
	</div>
	<jsp:include page="nav2.jsp"></jsp:include>
</body>
</html>