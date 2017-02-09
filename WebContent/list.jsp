<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String zodia[] = {"鼠","牛","虎","兔","龙","蛇","马","羊","猴","鸡","狗","猪"};
%>

<!DOCTYPE html>
<html class="">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" async src="js/aywmq.js"></script>
<script async src="js/analytics.js"></script>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta content="telephone=no" name="format-detection">
<meta name="wap-font-scale" content="no">
<title>产品列表</title>
<link rel="stylesheet" href="css/base.css?v=3.1.3">
<link rel="stylesheet" href="css/search.css?v=3.1.3">
<link rel="stylesheet" href="css/index.css?v=3.1.3">
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/zepto.min.js"></script>
<link rel="stylesheet" type="text/css" href="css/cart1.css">
<link rel="stylesheet" href="css/font.css">
<style type="text/css">
.shopping-car>div, .shopping-car-fun {
	display: -webkit-box;
	-webkit-box-orient: horizontal;
	-webkit-box-align: center
}

 
</style>
<script>
	var page = 1;
</script>
<script src="js/da_opt.js" type="text/javascript"></script>
</head>
<body>
	<!-- 公用头部导航 -->
	<div class="sn-nav sn-block sn-nav-search">
		<div class="sn-nav-back">
			<a class="sn-iconbtn" href="user/index.jsp">返回</a>
		</div>
		<div class="sn-nav-title of">商品列表</div>
		<a href="javascript:void(0);" class="search-btn hide"
			id="wapSearchURL" name="wapssjgy_none_ssan_ssan"
			style="display: none;">搜索</a> <a href="javascript:;"
			class="result-type result-type-img" name="wapssjgy_none_dtqh_dtqh"
			style="-webkit-transform-origin: 0px 0px 0px; opacity: 1; -webkit-transform: scale(1, 1);"></a>
	</div>
	<div class="search-h88"></div>
	<div id="article" style="margin-bottom: 20px;"></div>
	<div class="app02 floor-wp fix" id="menu">
		<h4 class="title" style="margin-left: 2%; margin-top: 1%;">
			<a href="user/index.jsp">返回</a>
			<p></p>
		</h4>
		<h4 class="title" style="margin-left: 4%; margin-top: 1%;">
			<a href="#tmproductsList">特码</a>
			<p></p>
		</h4>
		<h4 class="title" style="margin-left: 4%; margin-top: 1%;">
			<a href="#dxproductsList">大小</a>
		</h4>
		<h4 class="title" style="margin-left: 4%; margin-top: 1%;">
			<a href="#dxgproductsList">单相</a>
		</h4>
		<h4 class="title" style="margin-left: 4%; margin-top: 1%;">
			<a href="#bsproductsList">波色</a>
		</h4>
		<h4 class="title" style="margin-left: 4%; margin-top: 1%;">
			<a href="#sxproductsList">生肖</a>
		</h4>
	</div>
	<!-- 联想词 -->
	<div class="search-ass" id="searchAss">
		<div class="list-ass-wrap" id="typesList"></div>
		<ul class="keywords-ass" id="keywordsList">
		</ul>
	</div>
	<!-- 热门搜索 -->

	<script>
		$(function() {
			list();
			
			//init odds
			$.ajax({
				type : 'POST',
				contentType : "application/json; charset=utf-8",
				url : "getProductcate",
				success : function(data) {
					$.each(data,function(i,n){
						if(n.id == 1){
							$(".tmOdds").html(n.sumary);
						}else if(n.id == 2){
							$("#bsOdds").html(n.sumary);
						}else if(n.id == 3){
							$("#dxgOdds").html(n.sumary);
						}else if(n.id == 4){
							$("#sxOdds").html(n.sumary);
						}else if(n.id == 5){
							$("#dxOdds").html(n.sumary);
						}
					});
				},
				dataType : 'json'
			});
			
			var ab = document.getElementsByClassName("search-h88")[0];
			var menu = document.getElementById("menu");
			
			window.onscroll = function(){ 
				if(ab.offsetHeight<document.body.scrollTop){
					menu.style.position = "fixed";
					menu.style.top = 0;
				}
				else{
					menu.style.position = "";
				}
			}
			$.ajax({
				type : 'POST',
				contentType : "application/json; charset=utf-8",
				url : "<%=basePath%>article/getTopArticles",
				success : function(data) {
					var str = "";
					$.each(data,function(i,n){
						str += '<p><a href="<%=basePath%>article/detail?id='+n.id+'" style="margin-left:10%; ">'+n.title+'</a><span style="float: right;marin-right:3%">'+n.createTime+'</span></p>';
					});
					$("#article").append(str);
				},
				dataType : 'json'
			});
			/* $(window).scroll(function () {
                var a = document.getElementById("menu").offsetTop;
                if (a >= $(window).scrollTop() && a < ($(window).scrollTop()+$(window).height())) {
                    
                }else{
                	
                }
            }); */
		});
		function addToCart(pid) {
			$.ajax({
				type : 'POST',
				contentType : "application/json; charset=utf-8",
				url : "<%=basePath%>cart/add?p="+pid,
				success : function(data) {
					$("#cartItemNumber").html(data.number);
					alertDefaultStyle("mini", "已成功加进购物车,可在购物车修改下注数量");
				},
				dataType : 'json'
			});
		}
		function list() {
			$.ajax({
				type : 'post',
				contentType : "application/json; charset=utf-8",
				url : "<%=basePath%>cart/number",
				success : function(data) {
					console.log(data);
					$("#cartItemNumber").html(data.number);
				},
				dataType : 'json'
				});
		}
		document.onkeydown = function(event) {
			var e = event || window.event
					|| arguments.callee.caller.arguments[0];
			if (e && e.keyCode == 13) { // enter 键
				$("#wapSearchURL").click();
			}
		};

		function searchSubmit() {
			$("#wapSearchURL").click();
		}
		
		function gotoCart(){
			window.location.href = "<%=basePath%>cart/goCart";
		}
	</script>
	<!-- 推荐店铺 -->
	<div class="result-propose-store lazyimg hide"></div>

	<div class="result-wrap">

		<!-- 商品列表 -->


		<ul id="tmproductsList" style="padding-top: 50px;" id="tmList">
			<c:forEach var="tm" items="${tmProductList}">
				<li>
					<div class="app02 floor-wp fix" id="menu">
						<h5 class="title" style="margin-left: 2%; margin-top: 1%;">
							特码 — <span style="color: red">${tm.listName }</span>
							<span style="padding-left: 120px; color: red;" class="tmOdds"></span>
						</h5>
					</div>
					<div class="result-list lazyimg result-list-img">
						<ul>
							<c:forEach var="tm1" items="${tm.productList}">
								<li class='wbox' id='${tm1.id }'><a
									href='javascript:addToCart(${tm1.id })'>
										<div class='img'>
											<img data-src='done' alt='${tm1.title}' src='${tm1.picture}'
												id='pi${tm1.id }'> <span class='bang-t bang-t-35'></span>
										</div>
								</a>
									<div class='wbox-flex'>
										<a href='javascript:addToCart(${tm1.id })'>
										
										</a>
										<p class='info'></p>
										<div class='price' id='sp${tm1.id }'>
											<span class='fl' id='sprice${tm1.id }'>¥${tm1.money }</span>
											<a href='javascript:addToCart(${tm1.id })' class='icon-cart'
												style='float: right'></a>
										</div>
									</div></li>
							</c:forEach>
						</ul>
					</div>
					<br>
				</li>
			</c:forEach>
		</ul>
		<div class="result-list lazyimg result-list-img" id="productList"
			style="padding-top: 50px">
			<div class="app02 floor-wp fix" id="menu">
				<h5 class="title" style="margin-left: 2%; margin-top: 1%;">大小</h5>
				<span style="padding-left: 120px; color: red;" id="dxOdds"></span>
			</div>
			<br>
			<ul id="dxproductsList">
				<c:forEach var="dx" items="${dxProduct }">
					<li class='wbox' id='${dx.id }'><a
						href='javascript:addToCart(${dx.id })'>
							<div class='img'>
								<img data-src='done' alt='${dx.title}' src='${dx.picture}' id='pi${dx.id }'>
								<span class='bang-t bang-t-35'></span>
							</div>
					</a>
						<div class='wbox-flex'>
							<a href='javascript:addToCart(${dx.id })'>
							</a>
							<p class='info'></p>
							<div class='price' id='sp${dx.id }'>
								<span class='fl' id='sprice${dx.id }'>¥${dx.money }</span> <a
									href='javascript:addToCart(${dx.id })' class='icon-cart'
									style='float: right'></a>
							</div>
						</div></li>
				</c:forEach>
			</ul>
			<br>
			<div class="app02 floor-wp fix" id="menu">
				<h5 class="title" style="margin-left: 2%; margin-top: 1%;">波色</h5>
				<span style="padding-left: 120px; color: red;" id="bsOdds"></span>
			</div>
			<ul id="bsproductsList">
				<c:forEach var="bs" items="${bsProduct }">
					<li class='wbox' id='${bs.id }'><a
						href='javascript:addToCart(${bs.id })'>
							<div class='img'>
								<img data-src='done' alt='${bs.title}' src='${bs.picture}' id='pi${bs.id }'>
								<span class='bang-t bang-t-35'></span>
							</div>
					</a>
						<div class='wbox-flex'>
							<a href='javascript:addToCart(${bs.id })'>
							</a>
							<p class='info'></p>
							<div class='price' id='sp${bs.id }'>
								<span class='fl' id='sprice${bs.id }'>¥${bs.money }</span> <a
									href='javascript:addToCart(${bs.id })' class='icon-cart'
									style='float: right'></a>
							</div>
						</div></li>
				</c:forEach>
			</ul>
			<br>
			<div class="app02 floor-wp fix" id="menu">
				<h5 class="title" style="margin-left: 2%; margin-top: 1%;">单相</h5>
				<span style="padding-left: 120px; color: red;" id="dxgOdds"></span>
			</div>
			<ul id="dxgproductsList">
				<c:forEach var="dxg" items="${dxgProduct }">
					<li class='wbox' id='${dxg.id }'><a
						href='javascript:addToCart(${dxg.id })'>
							<div class='img'>
								<img data-src='done' alt='${dxg.title}' src='${dxg.picture}'
									id='pi${dxg.id }'> <span class='bang-t bang-t-35'></span>
							</div>
					</a>
						<div class='wbox-flex'>
							<a href='javascript:addToCart(${dxg.id })'>
							</a>
							<p class='info'></p>
							<div class='price' id='sp${dxg.id }'>
								<span class='fl' id='sprice${dxg.id }'>¥${dxg.money }</span> <a
									href='javascript:addToCart(${dxg.id })' class='icon-cart'
									style='float: right'></a>
							</div>
						</div></li>
				</c:forEach>
			</ul>
			<br>
			<div class="app02 floor-wp fix" id="menu">
				<h5 class="title" style="margin-left: 2%; margin-top: 1%;">生肖</h5>
				<span style="padding-left: 120px; color: red;" id="sxOdds"></span>
			</div>
			<ul id="sxproductsList">
				<c:forEach var="sx" items="${sxProduct }">
					<li class='wbox' id='${sx.id }'><a
						href='javascript:addToCart(${sx.id })'>
							<div class='img'>
								<img data-src='done' alt='${sx.title}' src='${sx.picture}' id='pi${sx.id }'>
								<span class='bang-t bang-t-35'></span>
							</div>
					</a>
						<div class='wbox-flex'>
							<a href='javascript:addToCart(${sx.id })'>
							</a>
							<p class='info'></p>
							<div class='price' id='sp${sx.id }'>
								<span class='fl' id='sprice${sx.id }'>¥${sx.money }</span> <a
									href='javascript:addToCart(${sx.id })' class='icon-cart'
									style='float: right'></a>
							</div>
						</div></li>
				</c:forEach>
			</ul>
		</div>
		<!-- 加载更多 -->
		<div class="loadmore">
			<a href="javascript:void(0)" id="nextPage" name="Wap_reg_person_005"
				onclick="list();return false;"
				class="first-step sn-btn sn-btn-big sn-btn-block m30">加载更多</a>
		</div>
		<!-- 加载完成提示 -->
		<div class="result-loaded-tip hide" id="end_load"
			style="display: none;">
			<span>Duang～到底了</span>
		</div>
		<!-- 搜索无结果 -->
		<div class="result-none" id="noResultMsg" style="display: none;">

		</div>

		<!-- 遮罩 -->
		<div class="mask" style="display: none;"></div>

	</div>
	<div class="app01"></div>
	<div class="app01"></div>
	<div class="app01"></div>
	<br />
	<br />
	<section class="cart-foot-bar hide" id="cartTotal"
		style="display: block;">
		<div class="cart-fixed">
			<div class="wbox pd">
				<div class="wbox-flex sn-txt-muted">
					<div class="fs30">
						第 <i class="sn-txt-assertive"><span
							>${config.nextTerm-1 }
							</span></i>期<br>
							每天封盘时间：
							<i class="sn-txt-assertive"><span
							>21:25
							</span></i>
					</div>
				</div>

				<div class="cart-btn-box" id="goSettlement" style="margin-left: 5px;">
					<a href="javascript:gotoCart()"
						class="sn-btn sn-btn-assertive sn-btn-big">购物车(共<span id="cartItemNumber">0</span>件)</a>
				</div>

			</div>
			
		</div>
	</section>
	<div class="copyright sn-block tc pdlayout sn-txt-muted">
		Copyright © 2015 -
		<script type="text/javascript">
			document.write(new Date().getFullYear())
		</script>
		${config.siteName } 版权所有
	</div>

	<script type="text/javascript" src="js/iscroll-lite.js"></script>
	<script type="text/javascript" src="js/waputils.js"></script>
	<script type="text/javascript" src="js/search.js"></script>
	<script type="text/javascript" src="js/wapsearch.js"></script>
</body>
</html>
