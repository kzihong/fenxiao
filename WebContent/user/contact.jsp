<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>联系我们</title>
<link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body>
	<div class="sn-nav">
		<div class="sn-nav-back">
			<a href="javascript:history.back(-1)">返回</a>
		</div>
		<div class="sn-nav-title of" id="addAddr">联系我们</div>
		<div class="sn-nav-right tr pr">
			<a href="../user/index.jsp">首页</a>
		</div>
	</div>
	<section class="sn-main pr">
	<div class="input-a sn-block wbox mt30 pr">
		<span>电话</span>
		<div class="wbox-flex ml30 pr">${config.phone }</div>
		<em class="delete" style="display: none" name="Wap_reg_person_001"></em>
	</div>
	<div class="input-a sn-block wbox mt30 pr">
		<span>QQ</span>
		<div class="wbox-flex ml30 pr">${config.qq }<a
				href="http://wpa.qq.com/msgrd?v=3&uin=${config.qq }&site=qq&menu=yes"
				style="float: right;">发起聊天</a>
		</div>
		<em class="delete" style="display: none" name="Wap_reg_person_001"></em>
	</div>
	</section>
	<div id='articleDiv' style="margin-top: 75px;padding: 10px;">
		<h1 id="articleTitle">${article.title }</h1>
		<div id="articleContent" style="padding: 10px;">
		${article.content }
		</div>
		<p>
			<span style="float: right;" id="articleTime">${article.createTime }</span>
		</p>
	</div>
	<jsp:include page="nav2.jsp"></jsp:include>
</body>
</html>