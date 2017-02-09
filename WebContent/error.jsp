<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>错误</title>
<link rel="stylesheet" type="text/css" href="<%=basePath %>css/style.css">
</head>
<body>
	<div class="sn-nav">
		<div class="sn-nav-back">
			<a href="javascript:history.back(-1)">返回</a>
		</div>
		<div class="sn-nav-title of" id="addAddr">错误${error.code }</div>
		<div class="sn-nav-right tr pr">
			<a href="index.jsp">首页</a>
		</div>
	</div>
	<section class="sn-main pr">
		<div class="input-a sn-block wbox mt30 pr">
			<div class="wbox-flex ml30 pr">
				${error.message }
			</div>
			<em class="delete" style="display:none" name="Wap_reg_person_001"></em>
		</div>
	</section>
</body>
</html>