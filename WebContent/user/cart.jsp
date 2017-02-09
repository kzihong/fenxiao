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
<meta charset="UTF-8" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta content="telephone=no" name="format-detection" />
<title>购物车</title>
<link rel="stylesheet" href="../css/cart1.css" />
<script type='text/javascript' src='../js/zepto.min.js'></script>
<script type="text/javascript">
	var updateFlag = true;
	function changeQuantity(data){
		/* if(data.cart == null){
			window.location.reload(true);
		} */
		$("#quantity"+data.cart.id).val(data.cart.quantity);
		var money = data.cart.figure;
		$("#money"+data.cart.id).html("&yen;"+money);
		$("#userPayAllPrice").html(data.number*10);
		
	}
	function updateQuantity(update,cid){
		if(!updateFlag){
			alertDefaultStyle('mini','亲操作太快啦，休息一下');
			return ;
		}
		updateFlag = false;
		if(update>0){
			$.ajax({
				type : 'POST',
				contentType : "application/json; charset=utf-8",
				url : "<%=basePath%>cart/addByCid?cid="+cid,
				success : function(data) {
					changeQuantity(data);
					updateFlag = true;
				},
				dataType : 'json'
			});
		}else{
			$.ajax({
				type : 'POST',
				contentType : "application/json; charset=utf-8",
				url : "<%=basePath%>cart/reduceByCid?cid="+cid,
				success : function(data) {
					var quantity = $("#quantity"+cid).val();
					if(quantity == 1){
						$("#quantity"+cid).parent().parent().parent().parent().parent().parent().parent().remove();
					}else{
						changeQuantity(data);
					}
					updateFlag = true;
				},
				dataType : 'json'
			});
		}
	}
	
	function updateQuantityOnblur(cid){
		if(!updateFlag){
			alertDefaultStyle('mini','亲操作太快啦，休息一下');
			return ;
		}
		updateFlag = false;
		var reg = new RegExp("^[0-9]*$");
		var quantity = $("#quantity"+cid).val();
		if(quantity == "" || !reg.test(quantity)){
			alert("亲，请正确输入购买数量");
			return;
		}
		if(quantity > 1000){
			alert("亲，最高只接受一万块的订单哦~");
			return;
		}
		$.ajax({
			type : 'POST',
			contentType : "application/json; charset=utf-8",
			url : "<%=basePath%>cart/updateByCid?c="+cid+"&q="+quantity,
			success : function(data) {
				if(quantity == 0){
					$("#quantity"+cid).parent().parent().parent().parent().parent().parent().parent().remove();
					$("#userPayAllPrice").html(data.number*10);
				}else{
					changeQuantity(data);
				}
				updateFlag = true;
			},
			dataType : 'json'
		});
	}
	function ordersSave(){
		$("#submitbutton").removeAttr('onclick');
		window.location.href = "<%=basePath%>user/ordersSaveBC";
	}
	function empty(){
		$("#snList").empty();
		$.ajax({
			type : 'POST',
			contentType : "application/json; charset=utf-8",
			url : "<%=basePath%>cart/empty",
			success : function(data) {
				
			},
			dataType : 'json'
		});
	}
</script>
</head>
<body>
	<section class="ww" style="min-height: 11rem;">
		<div class="loagMask" id="loading"
			style="-webkit-transform-origin: 0px 0px 0px; opacity: 1; -webkit-transform: scale(1, 1); display: none;">
			<div class="sn-mask-loading fixedLoading"></div>
		</div>
		<div class="sn-nav sn-block">
			<div class="sn-nav-back">
				<a class="sn-iconbtn" href="javascript:history.go(-1);">返回</a>
			</div>
			<div class="sn-nav-title of">购物车</div>
			<div class="sn-nav-right tr pr">
				<a href="<%=basePath%>user/index.jsp">首页</a>
			</div>
		</div>
		<div class="cart-1-6" style="display: none;" id="cartLogin">
			<div class="cart-cont">
				<i></i>
				<p></p>
				<span><a href="javascript:void(0);"
					class="sn-btn sn-btn-positive" id="cartBtn"></a></span>
			</div>
		</div>
		<div id="hwgList" class="mt30"></div>
		<div id="cartList" class="mt30">
			<div class="cart-list" id="snList">
				<ul class="cart-item">
					<c:forEach var="ci" items="${list}">
						<li id="li_0">
							<dl>
								<dt>
									<div class="pr">
										<div class="wbox ">
											<div class="pro-box">
												<p class="text-clamp2 fs26">${ci.category.productCate.name }——<span
														style="color: red;">${ci.category.name }</span>
												</p>
												<div>
													<input type="hidden" value="${ci.productTitle}"
														class="picbean">
												</div>
												<br>
												<div class="sn-count" style="float: right;">
													<a href="javascript:void(0);" class="count-min "
														onclick="updateQuantity(-1,${ci.id});"></a> <input
														type="text" value="${ci.quantity }"
														class="input-reset count-num" maxlength="5"
														id="quantity${ci.id }"
														onblur="updateQuantityOnblur(${ci.id})" /> <a
														href="javascript:void(0);" class="count-add "
														onclick="updateQuantity(1,${ci.id});"></a>
												</div>
											</div>
										</div>
										<div class="cart-list-opra wbox">
											<div class="wbox-flex tr">
												<p class="fs26 sn-txt-assertive ofv " id="money${ci.id }">&yen;${ci.figure}</p>
											</div>
											<em class="cart-edit-ico cart-fav-ico"></em> <em
												class="cart-edit-ico cart-del-ico"></em>
										</div>
									</div>
									<div class="cart-active sn-txt-assertive wbox"
										id="error_message_0"></div>
								</dt>
							</dl>
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</section>
	<section class="cart-foot-bar hide" id="cartTotal"
		style="display: block;">
		<div class="cart-fixed">
			<div class="wbox pd">
				<div class="wbox-flex sn-txt-muted">
					<div class="fs30">
						合计: <i class="sn-txt-assertive">&yen;<span
							id="userPayAllPrice">${total} </span></i>
					</div>
				</div>
				<div class="cart-btn-box" id="">
					<a href="javascript:void(0);"
						class="sn-btn sn-btn-assertive sn-btn-big" onclick="empty()" id="">清空购物车</a>
				</div>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<div class="cart-btn-box" id="goSettlement">
					<a href="javascript:void(0);"
						class="sn-btn sn-btn-assertive sn-btn-big" onclick="ordersSave()"
						id="submitbutton">提交订单</a>
				</div>
			</div>
		</div>
	</section>
	<script type="text/javascript">
	var inputs = $(".picbean");
	$.each(inputs,function(i,n){
		var pics = n.value.split(",");
		$.each(pics,function(j,m){
			var img = "<img src = '";
			img	+= "../upload/20160629/"+m;
			if(m.indexOf("png")>0){
				img += "' class='prodPicBig'/>"
			}else
				img += "' class='prodPic'/>"
			$(n).parent().append($(img));
		})
	});
	</script>
</body>
</html>