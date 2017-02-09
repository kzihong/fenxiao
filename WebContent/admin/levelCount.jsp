<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String userId = request.getParameter("id");
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
<link rel="stylesheet" type="text/css" href="../css/style.css">
<link rel="stylesheet" type="text/css" href="../css/cart.css">
<title>下级统计</title>
<script type="text/javascript" async="" src="../js/aywmq.js"></script>
<script async="" src="../js/analytics.js"></script>
<script type="text/javascript" async="" src="../js/da_opt.js"></script>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script>
	$(function() {
		$.ajax({
			url : "levelUserList?useId=<%=userId%>",
			type : "GET",
			dataType : "json",
			async : false,
			success : function(data) {
				$("#allLevelNum").html(data.allLevelNum);
				$("#firstLevelNum").html(data.firstLevelNum);
				$("#secondLevelNum").html(data.secondLevelNum);
				$("#thirdLevelNum").html(data.thirdLevelNum);
				$("#todayRegUserNum").html(data.todayRegUserNum);
				$("#todayStatusUserNum").html(data.todayStatusUserNum);
				$("#unStatusUserNum").html(data.unStatusUserNum);
				var str = "";
				$.each(data.firstLevelUsers, function(i, n) {
					str += "<p>"+n+"</p>";
				});
				$("#firstUser").append(str);
				str = "";
				$.each(data.secondLevelUsers, function(i, n) {
					str += "<p>"+n+"</p>";
				});
				$("#secondUser").append(str);
				str = "";
				$.each(data.thirdLevelUsers, function(i, n) {
					str += "<p>"+n+"</p>";
				});
				$("#thridUser").append(str);
			}
		});
	});
</script>
</head>
<body>
	<section class="sn-main pr">
		<div class="input-a sn-block wbox mt30 pr">
			<span>总共下级人数:</span>
			<div class="wbox-flex ml30 pr" id="allLevelNum">0</div>
		</div>
		<div class="input-a sn-block wbox mt30 pr">
			<span>第1级人数:</span>
			<div class="wbox-flex ml30 pr" id="firstLevelNum">0</div>
		</div>
		<div id="firstUser" style="padding: 10px;"></div>
		<div class="input-a sn-block wbox mt30 pr">
			<span>第2级人数:</span>
			<div class="wbox-flex ml30 pr" id="secondLevelNum">0</div>
		</div>
		<div id="secondUser" style="padding: 10px;"></div>
		<div class="input-a sn-block wbox mt30 pr">
			<span>第3级人数:</span>
			<div class="wbox-flex ml30 pr" id="thirdLevelNum">0</div>
		</div>
		<div id="thridUser" style="padding: 10px;"></div>
		<div class="input-a sn-block wbox mt30 pr">
			<span>当日注册人数:</span>
			<div class="wbox-flex ml30 pr" id="todayRegUserNum">0</div>
		</div>
		<div class="input-a sn-block wbox mt30 pr">
			<span>当日报单人数:</span>
			<div class="wbox-flex ml30 pr" id="todayStatusUserNum">0</div>
		</div>
		<div class="input-a sn-block wbox mt30 pr">
			<span>已注册未报单人数:</span>
			<div class="wbox-flex ml30 pr" id="unStatusUserNum">0</div>
		</div>
	</section>
	<script type="text/javascript" src="../js/zepto.min.js"></script>

</body>
</html>