package com.hansan.fenxiao.utils;

public enum Chinese {
	RED("red","红波"),
	GREEN("green","绿波"),
	BLUE("blue","蓝波"),
	TWD("teweida","特尾大"),
	TWX("teweixiao","特尾小"),
	DD("dadan","大单"),
	XD("xiaodan","小单"),
	DS("dashuang","大双"),
	XS("xiaoshuang","小双"),
	HSD("heshudan","合数单"),
	HSS("heshushuang","合数双"),
	HSDA("heshuda","合数大"),
	HSX("heshuxiao","合数小"),
	DA("da","大"),
	X("xiao","小"),
	D("dan","单"),
	S("shuang","双"),
	ZHDA("zongheda","总和大"),
	ZHX("zonghexiao","总和小"),
	ZHD("zonghedan","总和单"),
	ZHS("zongheshuang","总和双"),
	HDA("heda","合大"),
	HX("hexiao","合小"),
	HD("hedan","合单"),
	HS("heshuang","合双"),
	WX("weixiao","尾小"),
	WDA("weida","尾大"),
	SHU("shu","鼠"),
	NIU("niu","牛"),
	HU("hu","虎"),
	TU("tu","兔"),
	LONG("long","龙"),
	SHE("she","蛇"),
	MA("ma","马"),
	YANG("yang","羊"),
	HOU("hou","猴"),
	JI("ji","鸡"),
	GOU("gou","狗"),
	ZHU("zhu","猪"),
	HONGDAN("hongdan","红单"),
	HONGSHUANG("hongshuang","红双"),
	HONGDA("hongda","红大"),
	HONGXIAO("hongxiao","红小"),
	HONGHEDAN("honghedan","红合单"),
	HONGHESHUANG("hongheshuang","红合双"),
	LVDAN("lvdan","绿单"),
	LVSHUANG("lvshuang","绿双"),
	LVDA("lvda","绿大"),
	LVXIAO("lvxiao","绿小"),
	LVHEDAN("lvhedan","绿合单"),
	LVHESHUANG("lvheshuang","绿合双"),
	LANDAN("landan","蓝单"),
	LANSHUANG("lanshuang","蓝双"),
	LANDA("landa","蓝大"),
	LANXIAO("lanxiao","蓝小"),
	LANHEDAN("lanhedan","蓝合单"),
	LANHESHUANG("lanheshuang","蓝合双"),
	;
	
	
	private String key;
	private String info;
	private Chinese(String key, String info) {
		this.key = key;
		this.info = info;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	public static String get(String key){
		String x = null;
		for(Chinese chinese : values()){
			if(key.equals(chinese.getKey())){
				x=chinese.getInfo();
			}
		}
		return x;
	}
	
}
