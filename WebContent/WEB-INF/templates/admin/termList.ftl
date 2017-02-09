<form id="pagerForm" onsubmit="return navTabSearch(this);" action="termList" method="post">
        <input type="hidden" name="pageCurrent" value="${page.pageCurrent}">
        <input type="hidden" name="pageSize" value="${page.pageSize}">
</form>
<div class="bjui-pageHeader">
    <form id="pagerForm" data-toggle="ajaxsearch" action="termList" method="post">
        <ul class="bjui-searchBar">
            <li>
            <a href="insertNew.action" class="btn btn-red" data-toggle="doajax" data-confirm-msg="确定要开启下一期吗？">开启新的一期</a>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <label>关键词：</label>
            <input type="text" id="key" name="key"  size="30" value="${key!''}"/>
            <button type="submit" class="btn-default" data-icon="search">查询</button>
            <a class="btn btn-orange" href="javascript:;" data-toggle="reloadsearch" data-clear-query="true" data-icon="undo">清空查询</a>可以搜索期数、特码</li>
        </ul>
    </form>
</div>
<div class="bjui-pageContent">
    <table data-toggle="tablefixed" data-width="100%" data-layout-h="0" data-nowrap="true">
        <thead>
			<tr>
				<th orderField="code">期数</th>
                <th orderField="idno">生成时间</th>
				<th orderField="name">是否发送至邮箱</th>
				<th orderField="name">是否结算</th>
                <th orderField="idno">状态</th>
                <th orderField="grade">特码</th>
                <th>平码</th>
                <th >操作</th>
			</tr>
		</thead>
		<tbody>
        <#list termList as term>
            <tr>
                <td>${term.id}</td>
                <td>${term.createTimeString}</td>
                <td>
                	<#if term.sent == true>
                		已发送
                	<#else>
                		未发送
                	</#if>
                </td>
                <td>
                	<#if term.settled == true>
                		已结算
                	<#else>
                		未结算
                	</#if>
                </td>
                <td>
                	<#if term.end == true>
                		已结束
                	<#else>
                		未结束
                	</#if>
                </td>
                <td>
            		<#if term.fortuna == 0>
                		未公布
                	<#else>
                		${term.fortuna}
                	</#if>
                </td>
                <td>
                	${term.pingCodes !""}
                </td>
                <td>
                    <a href="fortunaShow?id=${term.tid}" class="btn btn-green" data-toggle="dialog" data-width="300" data-height="300" data-id="termEdit" data-mask="true">公布</a>
                    <a href="termSettle.action?id=${term.tid}" class="btn btn-blue" data-toggle="doajax" data-confirm-msg="确定要结算本期吗？">结算</a>
                    <#if term.end == true>
                    	<a href="termEnd.action?id=${term.tid}" class="btn btn-green" data-toggle="doajax" data-confirm-msg="确定要重新开始本期吗">开启</a>
					<#else>                    
	                    <a href="termEnd.action?id=${term.tid}" class="btn btn-red" data-toggle="doajax" data-confirm-msg="确定要终止本期？">结束</a>
                    </#if>
                </td>
            </tr>
		</#list>
		</tbody>
    </table>
    <#include "pageBar.ftl"/>
</div>