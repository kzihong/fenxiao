<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
* {
	padding: 0;
	margin: 0;
}

.bg {
	position: absolute;
	z-index: -1;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	opacity: 0.8;
}

ul, ol, li, dl {
	list-style-type: none;
}

.box {
	width: 100%;
	display: -webkit-box;
	display: -moz-box;
	-webkit-box-orient: horizontal;
	-moz-box-orient: horizontal;
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
}

.box>* {
	-webkit-box-flex: 1;
	-moz-box-flex: 1;
}

a:link, a:visited {
	color: #575757;
	text-decoration: none;
}

a {
	text-decoration: none;
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0.35);
}

a:link, a:visited {
	color: #575757;
	text-decoration: none;
}

a {
	text-decoration: none;
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0.35);
}

.nav4 {
	height: 45px;
}

.nav4 ul {
	position: fixed;
	z-index: 200;
	bottom: 0;
	left: 0;
	width: 100%
}

.nav4 li {
	border: 1px solid rgba(190, 190, 190, 1);
	height: 45px;
	border-bottom: 0;
	border-right: 0;
	position: relative;
	-webkit-box-shadow: inset 0 0 3px #fff;
	float: left;

}

.nav4 li:nth-of-type(1) {border-left; 0;
	
}

.nav4 li>a {
	font-size: 15px;
	-webkit-box-sizing: border-box;
	box-sizing: border-box;
	/*border:1px solid #f9f8f9;*/
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0);
	border-bottom: 0;
	display: block;
	line-height: 45px;
	text-align: center;
	background: -webkit-gradient(linear, 0 0, 0 25%, from(#f1f1f1),
		to(#dcdcdc), color-stop(25%, #ededed), color-stop(50%, #e3e3e3));
}

.nav4 li>a:only-child span {
	background: none;
	padding-left: 0;
}

.nav4 li>a.on+dl {
	display: block;
}

.nav4 li>a span {
	color: #4f4d4f;
	display: inline-block;
	padding-left: 15px;
	background: url(imgs/1.svg#2) no-repeat 4px 18px;
	-webkit-background-size: 9px auto;
	text-shadow: 0px 1px 0px #ffffff;
}
/***********************/


</style>
<div class='nav4'>
	<nav>
	<div id='nav4_ul' class='nav_4'>
		<ul class='box'>

			<li><a href='<%=basePath %>user/betting' class=''><span>我要下注</span></a></li>
            <li><a class='sn-iconbtn' href='ordersList.jsp'><span>下注管理</span></a></li>
			<li><a href='<%=basePath %>cart/goCart' class=''><span id='shopcart' >清单(<span style="color: red;">**</span>单)</li>
            <li><a href='<%=basePath %>user/index.jsp' class=''><span>会员中心&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></a></li>

		</ul>
	</div>
	</nav>
</div>