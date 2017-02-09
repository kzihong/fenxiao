package com.hansan.fenxiao.utils;

import com.alibaba.fastjson.JSONObject;
import com.hansan.fenxiao.dto.CodeResult;

public class CodeAPIUtil implements Runnable {

	@Override
	public void run() {
		System.out.println("开启写入");
		int time = 0;
		while(time < 3){
			String requestUrl = "http://c.apiplus.net/newly.do?token=TOKEN&code=hk6&format=json".replace("TOKEN", "60da82703f39d7ca");
			String result = SendSms.request2(requestUrl);
			CodeResult codeResult = JSONObject.parseObject(result, CodeResult.class);
			System.out.println(codeResult);
			if(codeResult.getFortuna() != 0){
				System.out.println("写入最新一期的特码"+codeResult.getFortuna());
			}else {
				System.out.println("本期还没有开启");
			}
			time++;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
