<div class="bjui-pageContent">
    <form action="Charge" method="post" class="pageForm" data-toggle="validate" data-reload-navtab="true">
        <div class="pageFormContent" data-layout-h="0">
            <table class="table table-condensed table-hover">
                <thead>
                    <tr>
                    	<td colspan="2" align="center"><h3>充值</h3></td>
                    </tr>
                </thead>
                <tbody>
                	<tr>
                		<td>
                        	<input type="hidden" name="userId" value="${userId}">
                		</td>
                	</tr>
                    <tr>
                        <td>
                            <label for="name" class="control-label x150">充值卡号：</label>
                            <input type="text" name="card" id="card" data-rule="required" size="20" placeholder="请输入充值卡号" value="">
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="bjui-footBar">
            <ul>
                <li><button type="button" class="btn-close">关闭</button></li>
                <li><button type="submit" class="btn-default">充值</button></li>
            </ul>
        </div>
    </form>
</div>