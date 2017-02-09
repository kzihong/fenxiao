package com.hansan.fenxiao.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

public class FortunaUtil {
	
	private static final int NUMBER = 49;
	private static final int ZODIA = 12;
	
	
	private static final int[] RED = {2,8,12,18,24,30,34,40,46,1,7,13,19,23,29,35,45};
	private static final int[] BLUE = {4,3,15,20,31,42,41,9,10,14,26,25,37,36,48,47};
	private static final int[] GREEN = {5,6,16,17,27,28,32,44,43,39,11,21,22,33,38,49};
	
	private static final String COLORString[] = {"red", "blue" , "green"};
	private static final String BIGString[] = {"da","xiao"};
	private static final String ZODIAString[] = {"shu","niu","hu","tu","long","she","ma","yang","hou","ji","gou","zhu"};
	private static final String SINGLEString[] = {"dan","shuang"};
	/**
	 * 得出幸运号对应的球的产品id
	 * @param fortuna
	 * @return
	 */
	public static String getBallColor(int fortuna) {
		for(int i : RED){
			if(i==fortuna){
				return "red";
			}
		}
		for(int i :BLUE){
			if(i==fortuna){
				return "blue";
			}
		}
		for(int i: GREEN){
			if(i==fortuna){
				return "green";
			}
		}
		return "";
	}

	public static String getBallcolerString(int fortuna) {
		for(int i : RED){
			if(i==fortuna){
				return COLORString[0];
			}
		}
		for(int i :BLUE){
			if(i==fortuna){
				return COLORString[1];
			}
		}
		for(int i: GREEN){
			if(i==fortuna){
				return COLORString[2];
			}
		}
		throw new RuntimeException("out of fortuna number");
	}
	
	public static String getBigsString(int fortuna) {
		if (fortuna == 49) {
			return null; // 和
		} else {
			if (fortuna < 25) {
				return BIGString[1];
			} else {
				return BIGString[0];// 大 53
			}
		}
	}
	

	public static String getTmString(int fortuna) {
		return "特码："+ fortuna + "号";
	}
	
	public static String getSingleString(int fortuna) {
		if (fortuna == 49) {
			return null;
		} else {
			if (fortuna % 2 == 0) {
				// 特码双 56
				return SINGLEString[1];
			} else {
				// 特码单 55
				return SINGLEString[0];
			}
		}
	}
	
	public static List<String> getTmSenior(int fortuna){
		List<String> list = new ArrayList<>();
		if (fortuna == 49) {
			return null;
		} else {
			if ((fortuna % 10) > 4) {
				list.add("teweida.png"); // 特尾大 57
			} else {
				list.add("teweixiao.png"); // 特尾小 58
			}
			if (fortuna % 2 == 0) {
				// 双
				if (fortuna > 24) {
					// 大双 61
					list.add("dashuang.png");
				} else {
					// 小双 62
					list.add("xiaoshuang.png");
				}
			} else {
				// 单
				if (fortuna > 24) {
					// 大单 59
					list.add("dadan.png");
				} else {
					// 小单 60
					list.add("xiaodan.png");
				}
				
			}
			int digit_1 = fortuna % 10;
			int digit_2 = fortuna / 10;
			if ((digit_1 + digit_2) % 2 == 0) {
				// 合数双 64
				list.add("heshushuang.png");
			} else {
				// 合数单 63
				list.add("heshudan.png");
			}
		}
		return list;
	}
	
	public static List<Integer> getNumberByLast(int i) {
		List<List<Integer>> number = new ArrayList<>();
		for(int b = 0 ; b < 12 ; b++){
			number.add(new ArrayList<>());
		}
		for(int k = 1 ; k<=49; k++){
			if(k%10 == 0) number.get(0).add(k);
			if(k%10 == 1) number.get(1).add(k);
			if(k%10 == 2) number.get(2).add(k);
			if(k%10 == 3) number.get(3).add(k);
			if(k%10 == 4) number.get(4).add(k);
			if(k%10 == 5) number.get(5).add(k);
			if(k%10 == 6) number.get(6).add(k);
			if(k%10 == 7) number.get(7).add(k);
			if(k%10 == 8) number.get(8).add(k);
			if(k%10 == 9) number.get(9).add(k);
		}
		return number.get(i);
	}

	public static List<String> getZt(int fortuna) {
		List<String> list = new ArrayList<>();
		list.add(getBallColor(fortuna)+".jpg");
		list.add(getSingleString(fortuna)+".jpg");
		list.add(getBigsString(fortuna)+".jpg");
		int digit_1 = fortuna % 10;
		int digit_2 = fortuna / 10;
		if ((digit_1 + digit_2) % 2 == 0) {
			// 合数双 64
			list.add("heshushuang.png");
		} else {
			// 合数单 63
			list.add("heshudan.png");
		}
		return list;
	}
	
	public static List<String> getZm(int fortuna) {
		List<String> list = new ArrayList<>();
		list.add(getBallColor(fortuna)+".jpg");
		list.add(getSingleString(fortuna)+".jpg");
		list.add(getBigsString(fortuna)+".jpg");
		int digit_1 = fortuna % 10;
		int digit_2 = fortuna / 10;
		if ((digit_1 + digit_2) % 2 == 0) {
			// 合数双 64
			list.add("heshushuang.png");
		} else {
			// 合数单 63
			list.add("heshudan.png");
		}
		if((digit_1 + digit_2) > 6){
			//合数大
			list.add("heshuda.png");
		}else {
			//合数小
			list.add("heshuxiao.png");
		}
		if(digit_1 > 4){
			//尾大
			list.add("weida.png");
		}else {
			//尾小
			list.add("weixiao.png");
		}
		return list;
	}

	public static List<String> getHalfBall(int fortuna) {
		if(fortuna == 49){
			return null;
		}
		List<String> result = new ArrayList<>();
		String color = getBallcolerString(fortuna);
		int digit_1 = fortuna % 10;
		int digit_2 = fortuna / 10;
		switch (color) {
		case "red":
			if(fortuna > 24){
				//红单
				result.add("hongda.png");
			}else {
				//红双
				result.add("hongxiao.png");
			}
			if(fortuna % 2 != 0){
				result.add("hongdan.png");
			}else {
				result.add("hongshuang.png");
			}
			if ((digit_1 + digit_2) % 2 != 0) {
				// 红合数双 64
				result.add("honghedan.png");
			} else {
				// 红合数单 63
				result.add("hongheshuang.png");
			}
			break;
		case "blue":
			if(fortuna > 24){
				//红单
				result.add("landa.png");
			}else {
				//红双
				result.add("lanxiao.png");
			}
			if ((digit_1 + digit_2) % 2 != 0) {
				// 红合数双 64
				result.add("lanhedan.png");
			} else {
				// 红合数单 63
				result.add("lanheshuang.png");
			}
			if(fortuna % 2 != 0){
				result.add("landan.png");
			}else {
				result.add("lanshuang.png");
			}
			break;
			
		case "green":
			if(fortuna > 24){
				//红单
				result.add("lvda.png");
			}else {
				//红双
				result.add("lvxiao.png");
			}
			if ((digit_1 + digit_2) % 2 != 0) {
				// 红合数双 64
				result.add("lvhedan.png");
			} else {
				// 红合数单 63
				result.add("lvheshuang.png");
			}
			if(fortuna % 2 != 0){
				result.add("lvdan.png");
			}else {
				result.add("lvshuang.png");
			}
			break;
			
		default:
			break;
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(getZodiaString(10,9));
		System.out.println(getZodiaString(14,9));
		System.out.println(getZodiaString(7,9));
		System.out.println(getZodiaString(29,9));
		System.out.println(getZodiaString(45,9));
		System.out.println(getZodiaString(46,9));
	}
	public static String getZodiaString(int fortuna,Integer currentYear) {
		int i = ((NUMBER - fortuna)%12)+currentYear;
		if(i > 12 ){
			i = i -ZODIA;
		}
		return ZODIAString[i-1];
	}
}
