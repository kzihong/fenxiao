<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${webpage.title!""}-${config.siteName!""}</title>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<link rel="stylesheet" href="../css/styles.css" type="text/css">
<script type="text/javascript" src="../js/jquery.min.js"></script>
</head>
<body>
  <div class="wrap bc">
    <div class="title tc pr top">
	  <span class="back-btn"></span>${webpage.title!""}
	  <div class="nav-btn">
	    <ul class="pa">
	    <li><a href="../">首页</a></li>
	      <li><a href="../about/index.do">关于我们</a></li>
		  <li><a href="../article/list.do?id=1">新闻动态</a></li>
		  <li><a href="../product/list.do">产品展示</a></li>
		  <li><a href="../contact/index.do">联系我们</a></li>
		  <li><a href="../en">English</a></li>
	    </ul>
	  </div>
	</div>
	<div class="content">
	  <div class="article mt10">
	    ${webpage.content!""}
	  </div>
	</div>
<#include "footer.ftl"/>
</body>
</html>