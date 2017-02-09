<form id="pagerForm" onsubmit="return navTabSearch(this);" action="ordersList" method="post">
        <input type="hidden" name="pageCurrent" value="${page.pageCurrent}">
        <input type="hidden" name="pageSize" value="${page.pageSize}">
</form>
<div class="bjui-pageHeader">
    <form id="pagerForm" data-toggle="ajaxsearch" action="ordersList" method="post">
        <ul class="bjui-searchBar">
            <li><label>关键词：</label>
            <input type="text" id="key" name="key"  size="30" value="${key!''}"/>
            <button type="submit" class="btn-default" data-icon="search">查询</button>
            <a class="btn btn-orange" href="javascript:;" data-toggle="reloadsearch" data-clear-query="true" data-icon="undo">清空查询</a>可以搜索会员名、订单号、产品名称</li>
        </ul>
    </form>
</div>
<div class="bjui-pageContent">
    <table data-toggle="tablefixed" data-width="100%" data-layout-h="0" data-nowrap="true">
        <thead>
			<tr>
				<th orderField="code">ID</th>
				<th orderField="name">订单号</th>
				<th orderField="name">会员编号</th>
                <th orderField="grade">订单总额</th>
                <th orderField="grade">期数</th>
                <th orderField="idno">下单时间</th>
                <th orderField="idno">状态</th>
                <th orderField="productName">购买产品</th>
                <th orderField="productMoney">购买金额</th>
                <th orderField="productMoney">是否中奖</th>
			</tr>
		</thead>
		<tbody>
        <#list orderItemList as orderItem>
            <tr>
                <td>${orderItem.id}</td>
                <td>${orderItem.orders.no}</td>
                <td>${orderItem.orders.user}</td>
                <td>${orderItem.orders.money}</td>
                <td>${orderItem.orders.term}</td>
                <td>${orderItem.orders.createDate}</td>
                <td>
                <#if orderItem.orders.status==0>
                	未付款
                <#else>
                	已付款
                </#if>
                </td>
                <td>${orderItem.category.name}--${orderItem.productTitle}</td>
                <td>${orderItem.figure}</td>
                <td>
                <#if orderItem.fortunate == true>
                	中奖
                </#if>
                <#if orderItem.fortunate == false>
                	未中奖
                </#if>
                </td>
            </tr>
		</#list>
		</tbody>
    </table>
    <#include "pageBar.ftl"/>
</div>