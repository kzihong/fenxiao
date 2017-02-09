<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<meta name='viewport'
	content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' />
<meta name='apple-mobile-web-app-capable' content='yes' />
<meta name='apple-mobile-web-app-status-bar-style' content='black' />
<meta content='telephone=no' name='format-detection' />
<title>知识学习</title>
<link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body>
	<div class="sn-nav">
		<div class="sn-nav-back">
			<a href="javascript:history.back(-1)">返回</a>
		</div>
		<div class="sn-nav-title of" id="addAddr">知识学习</div>
		<div class="sn-nav-right tr pr">
			<a href="../user/index.jsp">首页</a>
		</div>
	</div>
	<section class="sn-main pr"> <c:forEach items="${list}"
		var="article">
		<a href="<%=basePath%>article/detail?id=${article.id}">
			<div class="input-a sn-block wbox mt30 pr">
				<span>${article.title }</span> <em class="delete"
					style="display: none" name="Wap_reg_person_001"></em>
			</div>
		</a>

	</c:forEach> </section>
	<jsp:include page="nav2.jsp"></jsp:include>
</body>
</html>