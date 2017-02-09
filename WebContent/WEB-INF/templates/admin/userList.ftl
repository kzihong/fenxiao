<form id="pagerForm" onsubmit="return navTabSearch(this);" action="userList.do" method="post">
        <input type="hidden" name="pageCurrent" value="${page.pageCurrent}">
        <input type="hidden" name="pageSize" value="${page.pageSize}">
</form>

<div class="bjui-pageHeader">
    <form id="pagerForm" data-toggle="ajaxsearch" action="userList" method="post">
        <ul class="bjui-searchBar">
            <li><label>关键词：</label>
            <input type="text" id="key" name="key"  size="15" value="${key!''}"/>
            <button type="submit" class="btn-default" data-icon="search">查询</button>
            <a class="btn btn-orange" href="javascript:;" data-toggle="reloadsearch" data-clear-query="true" data-icon="undo">清空查询</a>可以搜索会员编号、用户名、手机号</li>
            <li>&nbsp;</li>
            <li>
            	<label>所有用户共有  ${allMoney}  元</label>
            </li>
        </ul>
    </form>
</div>
<div class="bjui-pageContent">
    <table data-toggle="tablefixed" data-width="100%" data-layout-h="0" data-nowrap="true">
        <thead>
			<tr>
				<th orderField="code">ID</th>
				<th orderField="name">编号</th>
                <th orderField="name">用户名</th>
                <th orderField="name">手机号码</th>
                <th orderField="grade">余额</th>
                <th orderField="idno">佣金</th>
                <th orderField="idno">注册时间</th>
                <th orderField="idno">注册IP</th>
                <th >操作</th>
			</tr>
		</thead>
		<tbody>
        <#list userList as user>
            <tr>
                <td>${user.id}</td>
                <td>
                	<#if user.status == 1 >
                		${user.no}
            		</#if>
                </td>
                <td>${user.name}</td>
                <td>${user.phone!""}</td>
                <td>${user.balance}</td>
                <td>${user.commission!""}</td>
                <td>${user.createDate}</td>
                <td>${user.registerIp}</td>
                <td>
                    <a href="userEdit.action?id=${user.id?string('#')}" class="btn btn-green" data-toggle="dialog" data-width="800" data-height="400" data-id="userEdit" data-mask="true">编辑</a>
                    <#if user.status == 1>
                    	<a href="userAssign.action?id=${user.id?string('#')}" class="btn btn-blue" data-toggle="doajax" data-confirm-msg="确定要取消该用户分销功能吗？">取消授权</a>
                    	<a href="levelCount.jsp?id=${user.id}" class="btn btn-green" data-toggle="dialog" data-width="400" data-height="800" data-id="userNext" data-mask="true">下级</a>
                    </#if>
                    <#if user.status == 0>
	                    <a href="userAssign.action?id=${user.id?string('#')}" class="btn btn-blue" data-toggle="doajax" data-confirm-msg="确定要授予该用户分销功能吗？">授权分销</a>
                    </#if>
                    <a href="userCharge.action?id=${user.id?string('#')}" class="btn btn-green" data-toggle="dialog" data-width="800" data-height="400" data-id="userCharge" data-mask="true">充值</a>
                    <a href="userDelete.action?id=${user.id?string('#')}" class="btn btn-red" data-toggle="doajax" data-confirm-msg="确定要删除该行信息吗？">删</a>
                </td>
            </tr>
		</#list>
		</tbody>
    </table>
    <#include "pageBar.ftl"/>
</div>