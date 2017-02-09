<%@ page language='java' contentType='text/html; charset=UTF-8'
	pageEncoding='UTF-8'%>
<%@taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core'%>
<%@taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt'%>
<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<meta name='viewport'
	content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' />
<meta name='apple-mobile-web-app-capable' content='yes' />
<meta name='apple-mobile-web-app-status-bar-style' content='black' />
<meta content='telephone=no' name='format-detection' />
<title></title>
<link rel='stylesheet' type='text/css'
	href='http://apps.bdimg.com/libs/bootstrap/3.3.4/css/bootstrap.css'
	media='all'>
<link rel='stylesheet' type='text/css' href='../css/style.css'
	media='all'>
<link rel='stylesheet' href='../css/cart1.css' />
<script type='text/javascript'
	src='http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js'></script>
	<script type='text/javascript' src='../js/zepto.min.js'></script>
<script type='text/javascript'
	src='http://apps.bdimg.com/libs/bootstrap/3.3.4/js/bootstrap.min.js'></script>
</head>
<body>
	<div class='sn-nav'>
		<div class='sn-nav-back'>
			<a class='sn-iconbtn' href='javascript:history.back(-1)'>返回</a>
		</div>
		<div class='sn-nav-title of'>下注列表</div>
		<div class='sn-nav-right tr pr'>
			<a href='index.jsp'>首页</a>
		</div>
	</div>
	<div class='cart'>
		<div class='wbox pd'>
			<div class='wbox-flex sn-txt-muted'>
				<div class='fs30'>
					第 <i class='sn-txt-assertive'><span>${nowTerm }
					</span></i>期<br> 
					每天封盘时间:<i class='sn-txt-assertive'><span>21:25</span></i><br>
					公布结算时间:<i class='sn-txt-assertive'><span>22:15</span></i><br><br>
					 <i class='sn-txt-assertive'>注意：<span style="float: right;">
					</span>按下‘+’或者修改购买数量下单哦~</i>
				</div>
			</div>
		</div>

	</div>
	<div>
		${html}
	</div>
	<jsp:include page="nav.jsp"></jsp:include>
</body>
<script type='text/javascript'>
	var least = ${least};
	var updateFlag = true;
	var products = new Array();
	function clear(){
		$('#selected').empty();
		products =[];
		$('.checkbox').attr('checked',false);
		$('#selected').next().html(0);
		$('#selected').next().next().next().html(0);
		$('#selected').next().next().find('input').val(0);
	}
	$('.count-add').bind('click', function() {
		if(!updateFlag){
			alertDefaultStyle('mini','亲操作太快啦，休息一下');
			return ;
		}
		updateFlag = false;
		if(least != 1){
			if(products.length != least){
				alertDefaultStyle('mini','注单选项个数不足，您还得再选择'+(least-products.length)+'件');
				return;
			}else{
				var str='';
				$.each(products,function(i,n){
					str +=''+n+',';
				});
				var that = this;
				$.ajax({
					type : 'POST',
					contentType : 'application/json; charset=utf-8',
					url : '../cart/addMulti?products='+str.substring(0,str.length-1),
					success : function(data) {
						$('#cartItemNumber').html(data.number);
						$(that).parent().find('input').val(data.cart.quantity);
						$(that).parent().parent().prev().html(data.cart.odds);
						$(that).parent().parent().next().html(data.cart.figure);
						$.each(products,function(i,n){
							var img = $('#i'+n);
							var flyElm = img.clone().css('opacity', 0.75);
							$('body').append(flyElm);
							flyElm.css({
								'z-index' : 9000,
								'display' : 'block',
								'position' : 'absolute',
								'top' : img.offset().top + 'px',
								'left' : img.offset().left + 'px',
								'width' : img.width() + 'px',
								'height' : img.height() + 'px'
							});
							flyElm.animate({
								top : $('#shopcart').offset().top,
								left : $('#shopcart').offset().left,
								width : 20,
								height : 32
							}, 'slow', function() {
								flyElm.remove();
							});
						});
						updateFlag = true;
					},
					dataType : 'json'
				});
				return;
			}
		}
		var input = $(this).parent().find('input');
		var current = input.val();
		var moneytd = $(this).parent().parent().next();
		var pid = $(this).attr('id');
		var img = $(this).parent().parent().parent().find('img')[0];
		$.ajax({
			type : 'POST',
			contentType : 'application/json; charset=utf-8',
			url : '../cart/add?p=' + pid,
			success : function(data) {
				$('#cartItemNumber').html(data.number);
				input.val(++current);
				moneytd.html(current * 10.0);
				var flyElm = $(img).clone().css('opacity', 0.75);
				$('body').append(flyElm);
				flyElm.css({
					'z-index' : 9000,
					'display' : 'block',
					'position' : 'absolute',
					'top' : $(img).offset().top + 'px',
					'left' : $(img).offset().left + 'px',
					'width' : $(img).width() + 'px',
					'height' : $(img).height() + 'px'
				});
				flyElm.animate({
					top : $('#shopcart').offset().top,
					left : $('#shopcart').offset().left,
					width : 20,
					height : 32
				}, 'slow', function() {
					flyElm.remove();
				});
				updateFlag = true;
			},
			dataType : 'json'
		});
	});
	$(".product_pic").bind('click',function(){
		if(!updateFlag){
			alertDefaultStyle('mini','亲操作太快啦，休息一下');
			return ;
		}
		updateFlag = false;
		if(least != 1){
			return;
		}else{
			var input = $(this).parent().next().next().find('input');
			var current = input.val();
			var moneytd = input.parent().parent().next();
			var pid = input.attr('id');
			var img = this;
			$.ajax({
				type : 'POST',
				contentType : 'application/json; charset=utf-8',
				url : '../cart/add?p=' + pid,
				success : function(data) {
					$('#cartItemNumber').html(data.number);
					input.val(++current);
					moneytd.html(current * 10.0);
					var flyElm = $(img).clone().css('opacity', 0.75);
					$('body').append(flyElm);
					flyElm.css({
						'z-index' : 9000,
						'display' : 'block',
						'position' : 'absolute',
						'top' : $(img).offset().top + 'px',
						'left' : $(img).offset().left + 'px',
						'width' : $(img).width() + 'px',
						'height' : $(img).height() + 'px'
					});
					flyElm.animate({
						top : $('#shopcart').offset().top,
						left : $('#shopcart').offset().left,
						width : 20,
						height : 32
					}, 'slow', function() {
						flyElm.remove();
					});
					updateFlag = true;
				},
				dataType : 'json'
			});
		}
	})
	$('.count-min').bind('click', function() {
		if(!updateFlag){
			alertDefaultStyle('mini','亲操作太快啦，休息一下');
			return ;
		}
		updateFlag = false;
		if(least != 1){
			var str='';
			$.each(products,function(i,n){
				str +=''+n+',';
			});
			if(str == '') return;
			var that = this;
			$.ajax({
				type : 'POST',
				contentType : 'application/json; charset=utf-8',
				url : '../cart/reduceMulti?products='+str.substring(0,str.length-1),
				success : function(data) {
					$('#cartItemNumber').html(data.number);
					if(data.cart.quantity == 0 ){
						clear();
					}else{
						$(that).parent().find('input').val(data.cart.quantity);
						$(that).parent().parent().prev().html(data.cart.odds);
						$(that).parent().parent().next().html(data.cart.figure);
					}
					updateFlag = true;
				},
				dataType : 'json'
			});
			return;
		}
		var input = $(this).parent().find('input');
		var current = input.val();
		if (--current == -1)
			return;
		var moneytd = $(this).parent().parent().next();
		var pid = $(this).attr('id');
		$.ajax({
			type : 'POST',
			contentType : 'application/json; charset=utf-8',
			url : '../cart/reduce?p=' + pid,
			success : function(data) {
				$('#cartItemNumber').html(data.number);
				input.val(current);
				moneytd.html(current * 10.0);
				updateFlag = true;
			},
			dataType : 'json'
		});
	});
	$('.checkbox').bind('click', function() {
		if(this.checked){
			if(least <= products.length){
				alertDefaultStyle('mini','个数达到上限，请先提交或清空注单');
				this.checked = false;
				return;
			}
			var img = $(this).parent().prev().prev().find('img').clone();
			var id = this.id;
			img.attr('class','img-rounded');
			img.attr('id','i'+id.substring(1, id.length));
			img.attr('onclick','tog(this)');
			$('#selected').append(img);
			
			//赔率的变化
			var odds = $("#selected").next().html();
			var podds = $(this).parent().prev().html(); 
			if(odds == 0 || podds <= odds){
				$("#selected").next().html(podds);
			}
			products.push(id.substring(1, id.length));
			if(least == products.length){
				alertDefaultStyle('mini','请修改购买数量');
			}
		}else{
			var id = this.id.substring(1, this.id.length);
			var img = $('#i'+id);
			img.parent().next().next().find('input').val('0');
			img.remove();
			$.each(products,function(i,n){
				if(n==id){
					products.splice(i,1);
				}
			});
			console.log(products);
		}
	});
	function tog(that){
		var str = that.id;
		var id = str.substring(1, str.length);
		var checkbox = document.getElementById('c'+id);
		checkbox.checked = false;
		$(that).parent().next().next().find('input').val("0");
		$(that).remove();
		$.each(products,function(i,n){
			if(n==id){
				products.splice(i,1);
			}
		});
		console.log(products);
	}
	function updateQuantityOnblur(that) {
		if(least != 1){
			var str='';
			$.each(products,function(i,n){
				str +=''+n+',';
			});
			var q = parseInt($(that).val());
			var reg = new RegExp("^[0-9]*$");
			if(q == "" || !reg.test(q)){
				alert("亲，请正确输入下注数量");
				return;
			}
			$.ajax({
				type : 'POST',
				contentType : 'application/json; charset=utf-8',
				url : '../cart/updateMulti?q='+q+'&products='+str.substring(0,str.length-1),
				success : function(data) {
					$('#cartItemNumber').html(data.number);
					if(data.cart.quantity == 0){
						clear();
					}else{
						$(that).val(data.cart.quantity);
						$(that).parent().parent().prev().html(data.cart.odds);
						$(that).parent().parent().next().html(data.cart.figure);
						if(data.cart.quantity > 0){
							$.each(products,function(i,n){
								var img = $('#i'+n);
								var flyElm = img.clone().css('opacity', 0.75);
								$('body').append(flyElm);
								flyElm.css({
									'z-index' : 9000,
									'display' : 'block',
									'position' : 'absolute',
									'top' : img.offset().top + 'px',
									'left' : img.offset().left + 'px',
									'width' : img.width() + 'px',
									'height' : img.height() + 'px'
								});
								flyElm.animate({
									top : $('#shopcart').offset().top,
									left : $('#shopcart').offset().left,
									width : 20,
									height : 32
								}, 'slow', function() {
									flyElm.remove();
								});
							});
						}
					}
				},
				dataType : 'json'
			});
			return;
		}
		var quantity = $(that).val();
		var reg = new RegExp("^[0-9]*$");
		if(quantity == "" || !reg.test(quantity)){
			alert("亲，请正确输入下注数量");
			return;
		}
		var pid = $(that).attr('id');
		$.ajax({
			type : 'POST',
			contentType : 'application/json; charset=utf-8',
			url : '../cart/update?p=' + pid + '&q=' + quantity,
			success : function(data) {
				$('#cartItemNumber').html(data.number);
				var moneytd = $(that).parent().parent().next();
				moneytd.html(quantity * 10.0);
				var img = $(that).parent().parent().parent().find('img');
				var flyElm = img.clone().css('opacity', 0.75);
				$('body').append(flyElm);
				flyElm.css({
					'z-index' : 9000,
					'display' : 'block',
					'position' : 'absolute',
					'top' : img.offset().top + 'px',
					'left' : img.offset().left + 'px',
					'width' : img.width() + 'px',
					'height' : img.height() + 'px'
				});
				flyElm.animate({
					top : $('#shopcart').offset().top,
					left : $('#shopcart').offset().left,
					width : 20,
					height : 32
				}, 'slow', function() {
					flyElm.remove();
				});
			},
			dataType : 'json'
		});
	}
</script>
</html>