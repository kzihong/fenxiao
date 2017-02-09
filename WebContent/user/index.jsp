<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta content="telephone=no" name="format-detection" />
<title>会员中心</title>
<link rel="stylesheet" type="text/css" href="../css/base.css">
<link rel="stylesheet" type="text/css" href="../css/member.rem.css">
<link rel="stylesheet" type="text/css" href="../css/index.css">
<link rel="stylesheet" type="text/css" href="../css/search.css?v=3.1.3">
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script>
	function jump(location) {
		window.location.href = location;
	}
	$(function() {
		$.ajax({
			url : "userInfoJson",
			type : "GET",
			dataType : "json",
			async : false,
			success : function(data) {
				$("#balance").html(data.balance + "元");
				$("#commission").html(data.commission + "元");
			}
		});
	});
</script>
</head>

<body>

	<!-- 公用头部导航 -->
	<div class="sn-nav sn-block">
		<div class="sn-nav-title of">会员中心</div>
		<!-- banner -->
		<div class="floor w lazyimg" id="floor">
			<section class="cnn-banner w" id="S_banner">
				<ul class="slide-ul fix">
					<li><a name="sjzx_none_floor1_lb1"
						href="${config.bannerUrl }"> <img
							src="../${config.weixin }">
					</a></li>
				</ul>
			</section>

			<div class="app01"></div>
			<div class="app06 floor-area" id="navCarousel_box">
				<ul class="fix">
					<li><a name="index_none_floor3_330052" href="betting">
							<img alt="" data-src="done" src="../images/ico-myDate.png">
							<div class="title">我要下注</div>
					</a></li>
					<li><a name="index_none_floor3_330051" href="../article/rechangePage">
							<img alt="" data-src="done" src="../images/ico-sales.png">
							<div class="title">我要充值</div>
					</a></li>
					<li><a name="index_none_floor3_330053" href="ordersList.jsp"><img
							alt="" data-src="done" src="../images/143262692801385199.jpg">
							<div class="title">下注管理</div></a></li>
					<li><a name="index_none_floor3_330054"
						href="../article/articleListPage"> <img alt="" data-src="done"
							src="../images/143385213411317884.jpg">
							<div class="title">知识学习</div></a></li>
					<li><a name="index_none_floor3_330055"
						href="${config.downloadUrl }"> <img alt="" data-src="done"
							src="../images/143385208970280372.jpg">
							<div class="title">客户端下载</div></a></li>
				</ul>
			</div>
		</div>
		<div class="sn-block meb-list">
			<ul class="sn-list-input">
				<p class="list-title sn-txt-muted">
					会员中心<span style="float: right;"><strong>
							${loginUser.phone }</strong></span>
				</p>
				<li style="align: center;">货币:<span id="balance">${loginUser.balance }元</span>&nbsp;
					奖金:<span id="commission">${loginUser.commission }元</span>
				</li>
				<c:if test="${config.rechargeCardIsOpen==1 }">
					<li><a href="../article/rechangePage" class="block wbox"> <label
							class="meb-list-ico"
							style="background: url('../images/ico-sales.png') no-repeat left center; background-size: 1rem 1rem;">我要充值</label>
							<div class="wbox-flex tr sn-txt-muted arrow"></div>
					</a></li>
				</c:if>
				<li><a href="betting" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/ico-myDate.png') no-repeat left center; background-size: 1rem 1rem;">我要下注</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
				<li><a href="ordersList.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/143262692801385199.jpg') no-repeat left center; background-size: 1rem 1rem;">下注管理</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
				<li><a href="termList.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/143558597675811964.jpg') no-repeat left center; background-size: 1rem 1rem;">开奖结果</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
				<li><a href="news.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/143385213411317884.jpg') no-repeat left center; background-size: 1rem 1rem;">内幕资料</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
			</ul>
			<ul class="sn-list-input">
				<p class="list-title sn-txt-muted">财务信息</p>
				<li><a href="info.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/my.jpg') no-repeat left center; background-size: 1rem 1rem;">我的信息</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
				<li><a href="withdraw.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/ico-myS.png') no-repeat left center; background-size: 1rem 1rem;">申请提现</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
				<li><a href="withdrawList.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/143558597675811964.jpg') no-repeat left center; background-size: 1rem 1rem;">提现列表</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>

				<li><a href="financialList.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/ico-myCollection.png') no-repeat left center; background-size: 1rem 1rem;">财务明细</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
			</ul>
			<ul class="sn-list-input">
				<p class="list-title sn-txt-muted">我要推广</p>
				<li><a href="../article/promotePage" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/ico-myTicket.png') no-repeat left center; background-size: 1rem 1rem;">推广链接</label>
						<div class="wbox-flex tr sn-txt-muted arrow">
							<span id="toExpiredCouponsNum"></span>
						</div>
				</a></li>
				<li><a href="levelCount.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/ico-addr.png') no-repeat left center; background-size: 1rem 1rem;">下级统计</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
				<li><a href="commissionList.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/ico-mylc.png ') no-repeat left center; background-size: 1rem 1rem;">佣金明细</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
				<%-- <c:if test="${config.onlinePayIsOpen==1 }">
				<li>
					<a href="recharge.jsp" class="block wbox" >
						<label class="meb-list-ico" style="background:url('../images/143385213411317884.jpg') no-repeat left center;background-size:1rem 1rem;">在线充值</label>
						<div class="wbox-flex tr sn-txt-muted arrow">
						</div>
					</a>
				</li>
				</c:if> --%>
				<li><a href="commissionToBalance.jsp" class="block wbox"> <label
						class="meb-list-ico"
						style="background: url('../images/ico-mylqb.png') no-repeat left center; background-size: 1rem 1rem;">佣金转入</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
				<li><a href="<%=basePath%>article/contact" class="block wbox">
						<label class="meb-list-ico"
						style="background: url('../images/my.jpg') no-repeat left center; background-size: 1rem 1rem;">联系我们</label>
						<div class="wbox-flex tr sn-txt-muted arrow"></div>
				</a></li>
				<!-- <li>
					<a href="balanceToUser.jsp" class="block wbox" >
						<label class="meb-list-ico" style="background:url('../images/ico-myDate.png') no-repeat left center;background-size:1rem 1rem;">会员转账</label>
						<div class="wbox-flex tr sn-txt-muted arrow">
						</div>
					</a>
				</li> -->
			</ul>
		</div>
		<div class="btn-cont" id="tanchuan" style="padding-bottom: 40px;">
			<a href="logout" class="sn-btn sn-btn-dark-big sn-btn-outline">退出</a>
		</div>
		<jsp:include page="nav2.jsp"></jsp:include>
</body>
</html>