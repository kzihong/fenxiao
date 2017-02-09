<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta charset="UTF-8" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta content="telephone=no" name="format-detection" />
<meta name="wap-font-scale" content="no" />
<title>往期开奖</title>
<link rel="stylesheet" href="../css/base.css" />
<link rel="stylesheet" type="text/css" href="../css/cart3.css" />
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript" src="../js/zepto.min.js"></script>
<script>
	var page = 1;
</script>
<style type="text/css">
.numberPic{
	width: 30px;
	height: 30px;
}
</style>
</head>
<body>
	<section class="ww order-wrap">
		<div class="sn-nav sn-block">
			<div class="sn-nav-back">
				<a class="sn-iconbtn" href="javascript:history.go(-1);">返回</a>
			</div>
			<div class="sn-nav-title of">往期开奖</div>
			<div class="sn-nav-right tr pr">
				<a href="index.jsp">首页</a>
			</div>
		</div>
		<div class="order-tab" id="b2cOrder"
			style="-webkit-transform-origin: 0px 0px 0px; opacity: 1; -webkit-transform: scale(1, 1);">
			<div class="order-tab-count" id="orderList"></div>
			<script>
				$(function() {
					list();
				});
				function list() {
					$.ajax({
								url : "termList?p=" + page,
								type : "GET",
								dataType : "json",
								async : false,
								success : function(data) {
									if (data.status == 1) {
										var list = data.list;
										var status = "";
										for (var i = 0; i < list.length; i++) {
											if (list[i].status == "0") {
												status = "未处理";
											} else {
												status = "已成功";
											}
											var tm = list[i].fortuna;
											var ps = list[i].pingCodes.split(",");
											var str = "";
											$.each(ps,function(i,n){
												n=parseInt(n);
												str += "<img src='../upload/20160629/"+n+".jpg' class='numberPic'/>&nbsp";	
											});
											str+="<span style='color:red;'>+</span>&nbsp<img src='../upload/20160629/"+tm+".jpg' class='numberPic'/>"
											$("#orderList")
													.append(
															"<div class='order-list' id='"+list[i].id+"'><a href='javascript:void(0)'><div class='cart-title wbox'><div class='of f30'>第"+list[i].id+"期&nbsp;&nbsp;"
																	+ "</div><div class='wbox-flex tr sn-txt-muted'><i class='f30'> "
																	+ " </i></div></div></a><ul class='order-pro-list order-box'><li id='"+list[i].id+"_'><a href='javascript:void(0)' class='pro-list'><div class='wbox'><div class='pro-info wbox-flex'><div class='pro-name text-clamp2 fs26'>"
																	+ str
																	+ "</div>"
																	+ "<div class='list-opra sn-txt-muted'>"
																	+ list[i].rollTimeString
																	+ "</div></div></div></a></li></ul></div>");
										}
										$("#nextPage").html("点击加载更多");
										$("#nextPage")
												.attr("style", "display:");
										$("#noLoadNextPage").attr("style",
												"display:none");
										if (data.isNextPage == 0) {
											$("#nextPage").html("已全部加载完");
											$("#nextPage").attr("style",
													"display:none");
											$("#noLoadNextPage").attr("style",
													"display:");
										}
										page++;
									} else {
										alertDefaultStyle("mini", "暂无数据");
									}
								}
							});
				}
			</script>
		</div>
		</div>
		<div class="mb30 sn-txt-muted tc" id="noLoadNextPage"
			style="display: none">
			<span>Duang~到底了</span>
		</div>
		<div id="loadNextPage">
			<div class="mb30 tc">
				<a href="javascript:void(0)" id="nextPage" name="Wap_reg_person_005"
					onclick="list();return false;" class="sn-txt-muted">点击加载更多</a>
			</div>
		</div>
	</section>
	<jsp:include page="nav2.jsp"></jsp:include>
</body>
</html>