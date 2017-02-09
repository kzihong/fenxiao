<script type="text/javascript">
    function logo_upload_success(file, data) {
        var json = $.parseJSON(data)
        
        $(this).bjuiajax('ajaxDone', json)
        if (json[BJUI.keys.statusCode] == BJUI.statusCode.ok) {
            $('#logo').val(json.filename)
            $('#logo_span_pic').html('<img src="../'+ json.filename +'" height="80px">')
        }
    }
    
    function weixin_upload_success(file, data) {
        var json = $.parseJSON(data)
        
        $(this).bjuiajax('ajaxDone', json)
        if (json[BJUI.keys.statusCode] == BJUI.statusCode.ok) {
            $('#weixin').val(json.filename)
            $('#weixin_span_pic').html('<img src="../'+ json.filename +'" height="80px">')
        }
    }
</script>
<div class="bjui-pageContent">
    <form action="configUpdate" method="post" class="pageForm" data-toggle="validate" data-reload-navtab="true">
        <div class="pageFormContent" data-layout-h="0">
            <table class="table table-condensed table-hover">
            <input type="hidden" name="config.id" value="${config.id}">
                <tbody>
                    <tr>
                        <td colspan="2" align="center"><h3>系统设置</h3></td>
                    </tr>
                    <tr>
                    	<td>
                            <label for="j_dialog_code" class="control-label x120">网站名称：</label>
                            <input type="text" name="config.siteName" id="siteName" data-rule="" size="30" value="${config.siteName!''}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">网址：</label>
                            <input type="text" name="config.siteUrl" id="siteUrl" data-rule="" size="30" value="${config.siteUrl!''}">如：http://www.919dns.com/
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">关键词：</label>
                            <input type="text" name="config.siteKeys" id="siteKeys" data-rule="" size="30" value="${config.siteKeys!''}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">描述：</label>
                            <textarea cols="30" name="config.siteDescription">${config.siteDescription!''}</textarea>
                        </td>
                    </tr>
                    <tr>
                    	<td>
                            <label for="j_dialog_code" class="control-label x120">LOGO：</label>
                            <input type="hidden" name="config.logo" id="logo" data-rule="" size="30" value="${config.logo!''}">
                        	<span id="logo_span_pic"><#if (config.logo)!=""><img src="../${config.logo!""}" height="80px"/></#if></span>
                        	<div style="display:inline-block; vertical-align:middle;">
							    <div id="logo_pic_up" data-toggle="upload"
							     	data-uploader="uploadImages" 
							        data-file-obj-name="filedata" 
							        data-file-size-limit="1024000000" 
							        data-file-type-exts="*.jpg;*.png;*.gif;*.mpg" 
							        data-multi="false" 
							        data-auto="true" 
							        data-on-upload-success="logo_upload_success" 
							        data-icon="cloud-upload"></div>
							</div>
                        </td>
                    </tr>
                    <tr>
                    	<td>
                    		<label for="currentYeas" class="control-label x120">今年是：</label>
                            <select name="config.currentYeas" id="currentYeas" data-style="btn-default btn-sel" data-width="auto">
                                <option value="1" <#if config.currentYeas==1>selected="selected"</#if>>鼠</option>
                                <option value="2" <#if config.currentYeas==2>selected="selected"</#if>>牛</option>
                                <option value="3" <#if config.currentYeas==3>selected="selected"</#if>>虎</option>
                                <option value="4" <#if config.currentYeas==4>selected="selected"</#if>>兔</option>
                                <option value="5" <#if config.currentYeas==5>selected="selected"</#if>>龙</option>
                                <option value="6" <#if config.currentYeas==6>selected="selected"</#if>>蛇</option>
                                <option value="7" <#if config.currentYeas==7>selected="selected"</#if>>马</option>
                                <option value="8" <#if config.currentYeas==8>selected="selected"</#if>>羊</option>
                                <option value="9" <#if config.currentYeas==9>selected="selected"</#if>>猴</option>
                                <option value="10" <#if config.currentYeas==10>selected="selected"</#if>>鸡</option>
                                <option value="11" <#if config.currentYeas==11>selected="selected"</#if>>狗</option>
                                <option value="12" <#if config.currentYeas==12>selected="selected"</#if>>猪</option>
                            </select>
                            <span>年,代号为${config.currentYeas !""}</span>
                    	</td>
                    </tr>
                    <tr>
                    	<td>
                    		<label for="nextTerm" class="control-label x120">下一期是：</label>
                    		<input type="text" name="config.nextTerm" id="nextTerm" data-rule="" size="30" value="${config.nextTerm !''}">
                    	</td>
                    </tr>
                    <tr>
                        <td>
                            <label for="firstLevel" class="control-label x120">分销上级比例：</label>
                            <input type="text" name="config.firstLevel" id="nextTerm" data-rule="" size="30" value="${config.firstLevel !''}">
                        </td>
                    </tr>
                    <tr>
                    	<td>
                    		<label for="eztzze" class="control-label x120">三中二之中三赔率：</label>
                    		<input type="text" name="config.szezzs" id="szezzs" data-rule="" size="30" value="${config.szezzs !''}">
                    	</td>
                    </tr>
                    <tr>
                    	<td>
                    		<label for="eztzze" class="control-label x120">二中特之中二赔率：</label>
                    		<input type="text" name="config.eztzze" id="eztzze" data-rule="" size="30" value="${config.eztzze !''}">
                    	</td>
                    </tr>
                    <tr>
                    	<td>
                    		<label for="msgAPITemplate" class="control-label x120">短信模板(请保留ABCD)：</label>
                    		<input type="text" name="config.msgAPITemplate" id="msgAPITemplate" data-rule="" size="30" value="${config.msgAPITemplate !''}">
                    	</td>
                    </tr>
                    <tr>
                    	<td>
                    		<label for="msgAPIUsername" class="control-label x120">短信API用户名：</label>
                    		<input type="text" name="config.msgAPIUsername" id="msgAPIUsername" data-rule="" size="30" value="${config.msgAPIUsername !''}">
                    	</td>
                    </tr>
                    <tr>
                    	<td>
                    		<label for="msgAPIPassword" class="control-label x120">短信API密码：</label>
                    		<input type="text" name="config.msgAPIPassword" id="msgAPIPassword" data-rule="" size="30" value="${config.msgAPIPassword !''}">
                    	</td>
                    </tr>
                    
                    <tr>
                        <td>
                            <label for="newsUrl" class="control-label x120">最新开奖链接：</label>
                            <input type="text" name="config.newsUrl" id="newsUrl" data-rule="" size="30" value="${config.newsUrl!''}">
                        </td>
                     </tr>
                    <tr>
                        <td>
                            <label for="acceptExcelEmail" class="control-label x120">接收截单邮箱：</label>
                            <input type="text" name="config.acceptExcelEmail" id="acceptExcelEmail" data-rule="" size="30" value="${config.acceptExcelEmail!''}">
                        </td>
                     </tr>
                     <tr>
                        <td>
                        	<label for="acceptExcelEmailPassword" class="control-label x120">邮箱STMP授权码：</label>
                        	<input type="text" name="config.emailStmpCode" id="emailStmpCode" data-rule="" size="30" value="${config.emailStmpCode!''}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">电话：</label>
                            <input type="text" name="config.phone" id="phone" data-rule="" size="30" value="${config.phone!''}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">QQ：</label>
                            <input type="text" name="config.qq" id="qq" data-rule="" size="30" value="${config.qq!''}">请输入QQ号码,如:582866070
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">首页大图：</label>
                            <input type="hidden" name="config.weixin" id="weixin" data-rule="" size="30" value="${config.weixin!''}">
                        	<span id="weixin_span_pic"><#if (config.weixin)!=""><img src="../${config.weixin!""}" height="80px"/></#if></span>
                        	<div style="display:inline-block; vertical-align:middle;">
							    <div id="weixin_pic_up" data-toggle="upload"
							     	data-uploader="uploadImages" 
							        data-file-obj-name="filedata" 
							        data-file-size-limit="1024000000" 
							        data-file-type-exts="*.jpg;*.png;*.gif;*.mpg" 
							        data-multi="false" 
							        data-auto="true" 
							        data-on-upload-success="weixin_upload_success" 
							        data-icon="cloud-upload"></div>
							</div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                        	<label for="bannerUrl" class="control-label x120">大图链接：</label>
                        	<input type="text" name="config.bannerUrl" id="bannerUrl" data-rule="" size="30" value="${config.bannerUrl!''}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">支付宝合作ID：</label>
                            <input type="text" name="config.alipayPartner" id="alipayPartner" data-rule="" size="30" value="${config.alipayPartner!''}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">支付宝账号：</label>
                            <input type="text" name="config.alipaySellerEmail" id="alipaySellerEmail" data-rule="" size="30" value="${config.alipaySellerEmail!''}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">支付宝商户私钥：</label>
                            <input type="text" name="config.alipayKey" id="alipayKey" data-rule="" size="30" value="${config.alipayKey!''}">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="j_dialog_name" class="control-label x120">软件下载地址：</label>
                            <input type="text" name="config.downloadUrl" id="downloadUrl" data-rule="" size="30" value="${config.downloadUrl!''}">
                        </td>
                    </tr>
                    <tr>
	                        <td>
	                            <label for="status" class="control-label x120">在线充值：</label>
	                            <select name="config.onlinePayIsOpen" id="onlinePayIsOpen" data-style="btn-default btn-sel" data-width="auto">
	                                <option value="0" <#if config.onlinePayIsOpen==0>selected="selected"</#if>>禁用</option>
	                                <option value="1" <#if config.onlinePayIsOpen==1>selected="selected"</#if>>可用</option>
	                            </select>
	                        </td>
	                    </tr>
	                    <tr>
	                        <td>
	                            <label for="status" class="control-label x120">充值卡充值：</label>
	                            <select name="config.rechargeCardIsOpen" id="rechargeCardIsOpen" data-style="btn-default btn-sel" data-width="auto">
	                                <option value="0" <#if config.rechargeCardIsOpen==0>selected="selected"</#if>>禁用</option>
	                                <option value="1" <#if config.rechargeCardIsOpen==1>selected="selected"</#if>>可用</option>
	                            </select>
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

