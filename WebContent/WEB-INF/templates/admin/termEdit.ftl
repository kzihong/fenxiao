<div class="bjui-pageContent">
    <form action="fortunaPublish" method="post" class="pageForm" data-toggle="validate" data-reload-navtab="true">
        <div class="pageFormContent" data-layout-h="0">
            <table class="table table-condensed table-hover">
                <thead>
                    <tr>
                    	<td colspan="2" align="center"><h3>公布特码</h3></td>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td width="400">
                            <label for="name" class="control-label x150">期数：${term.id}</label>
                            <input type="hidden" name="term.tid" id="id" data-rule="required" size="20" placeholder="" value="${term.tid}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="odds" class="control-label x150">特码：</label>
                            <input type="text" name="term.fortuna" id="fortuna" data-rule="required" size="20" placeholder="请输入特码" value="${term.fortuna}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="odds" class="control-label x150">平码：</label>
                            <input type="text" name="pingCodes" id="pingCodes" data-rule="required" size="20" placeholder="请输入平码，用英文逗号隔开，如64,23,23" value="${term.pingCodes !""}">
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="bjui-footBar">
            <ul>
                <li><button type="button" class="btn-close">关闭</button></li>
                <li><button type="submit" class="btn-default">保存</button></li>
            </ul>
        </div>
    </form>
</div>