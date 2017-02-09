<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta content="telephone=no" name="format-detection">
<link rel="stylesheet" type="text/css" href="../css/style.css"
	media="all">
<title>推广链接</title>
<script type="text/javascript" async="" src="../js/aywmq.js"></script>
<script async="" src="../js/analytics.js"></script>
<script type="text/javascript" async="" src="../js/da_opt.js"></script>
<script type="text/javascript" src="../js/jquery.min.js"></script>

</head>
<body>
	<div class="sn-nav">
		<div class="sn-nav-back">
			<a class="sn-iconbtn" href="javascript:history.go(-1)">返回</a>
		</div>
		<div class="sn-nav-title of">推广链接</div>
		<div class="sn-nav-right tr pr">
			<a href="../user/index.jsp">首页</a>
		</div>
	</div>
	<section class="sn-main pr">
		<c:if test="${sessionScope.loginUser.status==0 }">
			<div class="input-a sn-block wbox mt30 pr">
				<span><a href="../list.jsp">您的账号未具有分销功能，请联系管理员</a></span>
			</div>
		</c:if>
		<c:if test="${sessionScope.loginUser.status==1 }">
			<div class="input-a sn-block wbox mt30 pr">
				<span>推广链接:</span>
				<div class="wbox-flex ml30 pr">
					<input type="text"
						value="<%=basePath %>promote?no=${sessionScope.loginUser.no }"
						style="width: 98%">
				</div>
			</div>
			<div class="input-a sn-block wbox mt30 pr">
				<span>推广二维码:</span>
				<div class="wbox-flex ml30 pr"></div>
			</div>
			<div style="margin-left: 30%; margin-top: 15px;">
				<img
					src="<%=basePath%>user/getQrcode?no=${sessionScope.loginUser.no}">
			</div>
		</c:if>
	</section>
	<jsp:include page="nav2.jsp"></jsp:include>
	<div id='articleDiv' style="margin-top: 75px;padding: 10px;">
		<h1 id="articleTitle">${article.title }</h1>
		<div id="articleContent" style="padding: 10px;">
		${article.content }
		</div>
		<p>
			<span style="float: right;" id="articleTime">${article.createTime }</span>
		</p>
	</div>
	<script type="text/javascript" src="../js/zepto.min.js"></script>

</body>
</html>