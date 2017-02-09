<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
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
<link rel="stylesheet" type="text/css" href="css/style.css" media="all">
<title>会员注册</title>
<script type="text/javascript" async="" src="js/aywmq.js"></script>
<script async="" src="js/analytics.js"></script>
<script type="text/javascript" async="" src="js/da_opt.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	createUserNo();
});
var countdown=30; 
function settime() {
	var val = $("#getCode");
	if (countdown == 0) { 
		val.attr("href", "javascript:findPassword()");
		val.html("获取验证码");
		countdown = 30; 
	} else { 
		val.attr("href", "javascript:void(0)");
		val.html("重新发送(" + countdown + ")"); 
		countdown--;
		setTimeout(function() { 
			settime();
		},1000);
	} 
}
function next(){
	if(!$("#che").is(':checked')){
		alertDefaultStyle("mini", "您未同意相关服务条款");
		return;
	}
	var code = $("#code").val();
	if (code=="") {
		 alertDefaultStyle("mini", "手机验证码不能为空");
		 return;
	}
	var phone = $("#phone").val();
	var c = /^[1]\d{10}$/;
	if(phone==""){
		alertDefaultStyle("mini", "手机号不能为空");
		return ;
	}
	if (!c.test(phone)) {
		 alertDefaultStyle("mini", "手机号码格式错误");
		 return;
	}
	$.ajax({
	    url: "<%=basePath%>validationCodeR",
			type : "POST",
			data : {
				"code" : code,
				"phone" : phone
			},
			dataType : "json",
			async : true,
			success : function(data) {
				alertDefaultStyle("mini", data.message);
				if (data.status == "1") {
					$("#codeDiv").hide();
					$("#passwordDiv").show();
				}
			}
		});
	}
	function getCode() {
		var phone = $("#phone").val();
		var c = /^[1]\d{10}$/;
		if (phone == "") {
			alertDefaultStyle("mini", "手机号不能为空");
		} else if (!c.test(phone)) {
			alertDefaultStyle("mini", "手机号码格式错误");
		} else {
			$.ajax({
				url : "createPhoneCode",
				type : "POST",
				data : {
					"phone" : phone
				},
				dataType : "json",
				async : true,
				success : function(data) {
					alertDefaultStyle("mini", data.message);
					if (data.status == "1") {
						settime();
					}
				}
			});
		}
	}
</script>
</head>
<body>
	<div class="sn-nav">
		<div class="sn-nav-back">
			<a class="sn-iconbtn" href="javascript:void(0)">返回</a>
		</div>
		<div class="sn-nav-title of">注册</div>
	</div>
	<section class="sn-main pr">
		<div id="codeDiv">
			<div class="input-a sn-block wbox mt30 pr">
				<span>手机号</span>
				<div class="wbox-flex ml30 pr">
					<input type="tel" id="phone" name="user.phone" value=""
						placeholder="请输入11位手机号码" maxlength="11">
				</div>
				<em class="delete" style="display: none" name="Wap_reg_person_001"></em>
			</div>
			<div class="input-a sn-block wbox mt30 pr">
				<span>短信验证码</span>
				<div class="wbox-flex ml30 pr">
					<input type="text" id="code" name="code" value=""
						placeholder="请输入验证码" maxlength="32">
				</div>
				<em class="delete" style="display: none" name="Wap_reg_person_001"></em>
				<a href="javascript:void(0)" onclick="getCode()"
					class="getimgcode bl" id="getCode"> 获取验证码 </a>
			</div>
			<div class="input-a sn-block wbox mt30 pr">
			<div class="wbox-flex ml30 pr">
					<input type="checkbox" id="che" style="width: 25px;height: 25px; color: blue; ">&nbsp;&nbsp;<a href="article/detail?id=6"><u>我已阅读并同意相关服务条款和隐私政策</u></a>
				</div>
			</div>
			<a href="javascript:void(0)" name="Wap_reg_person_005"
				onclick="next(); return false;"
				class="first-step sn-btn sn-btn-big sn-btn-block m30 sn-btn-positive">下一步</a>
		</div>
		<div id="passwordDiv" style="display: none;">
			<div class="input-a sn-block wbox mt30 pr">
				<span>密码</span>
				<div class="wbox-flex ml30 pr">
					<input type="password" id="password" name="user.password" value=""
						placeholder="请输入密码" maxlength="32">
				</div>
				<em class="delete" style="display: none" name="Wap_reg_person_001"></em>
			</div>
			<div class="input-a sn-block wbox mt30 pr">
				<span>确认密码</span>
				<div class="wbox-flex ml30 pr">
					<input type="password" id="repassword" value=""
						placeholder="请重新输入密码" maxlength="32">
				</div>
				<em class="delete" style="display: none" name="Wap_reg_person_001"></em>
			</div>
			<a href="javascript:void(0)" id="nextStep" name="Wap_reg_person_005"
				onclick="register(); return false;"
				class="first-step sn-btn sn-btn-big sn-btn-block m30 sn-btn-positive">注册</a>
		</div>
		<p class="assisFun f14 m30">
			<a href="login.jsp" name="WAP_login_none_register">登录</a> <a
				href="findPassword.jsp">忘记密码?</a>
		</p>
	</section>
	<script type="text/javascript" src="js/zepto.min.js"></script>

</body>
</html>