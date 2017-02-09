<%@ page language='java' contentType='text/html; charset=UTF-8'
	pageEncoding='UTF-8'%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<title></title>
<meta name='viewport'
	content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' />
<meta name='apple-mobile-web-app-capable' content='yes' />
<meta name='apple-mobile-web-app-status-bar-style' content='black' />
<meta content='telephone=no' name='format-detection' />
<link rel='stylesheet' type='text/css'
	href='http://apps.bdimg.com/libs/bootstrap/3.3.4/css/bootstrap.css'
	media='all'>
<link rel='stylesheet' type='text/css' href='../css/style.css'
	media='all'>
<script type='text/javascript'
	src='http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js'></script>
<script type='text/javascript'
	src='http://apps.bdimg.com/libs/bootstrap/3.3.4/js/bootstrap.min.js'></script>
</head>
<body>
	<div class='sn-nav'>
		<div class='sn-nav-back'>
			<a class='sn-iconbtn' href='index.jsp'>返回</a>
		</div>
		<div class='sn-nav-title of'>下注选项</div>
	</div>
	<div class='panel-group' id='accordion' style='margin-top: 40px;'>
		${html }
		<div class="panel panel-default" style="margin-top: 40px;">
			<div class="panel-heading">
				<a data-toggle="collapse" data-parent="#accordion" href="#collapse1">
					<h4 class="panel-title">${xz.title }</h4>
				</a>
			</div>
			<div id="collapse1" class="panel-collapse collapse">
				<div class="panel-body">
					${xz.content }
				</div>
			</div>
		</div>
		<div class="panel panel-default" style="margin-top: 10px; margin-bottom: 40px;">
			<div class="panel-heading">
				<a data-toggle="collapse" data-parent="#accordion" href="#collapse2">
					<h4 class="panel-title">${yx.title }</h4>
				</a>
			</div>
			<div id="collapse2" class="panel-collapse collapse">
				<div class="panel-body">
					${yx.content }
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="nav2.jsp"></jsp:include>
</body>
</html>