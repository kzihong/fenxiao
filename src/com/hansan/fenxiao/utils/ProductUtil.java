package com.hansan.fenxiao.utils;

import java.util.ArrayList;
import java.util.List;

public class ProductUtil {

	private static final int NUMBER = 49;
	private static final int COLOR = 3;
	private static final int SINGLE = 2;
	private static final int ZODIA = 12;
	private static final int BIG = 2;
	private static final int SENIOR = 8;
	private static final int TM = NUMBER + COLOR + SINGLE + BIG + SENIOR;
	private static final int PM = NUMBER + BIG+SINGLE;

	private static final int[] RED = { 2, 8, 12, 18, 24, 30, 34, 40, 46, 1, 7, 13, 19, 23, 29, 35, 45 };
	private static final int[] BLUE = { 4, 3, 15, 20, 31, 42, 41, 9, 10, 14, 26, 25, 37, 36, 48, 47 };
	private static final int[] GREEN = { 5, 6, 16, 17, 27, 28, 32, 44, 43, 39, 11, 21, 22, 33, 38, 49 };

	private static final String COLORString[] = { "波色：红色", "波色：蓝色", "波色：绿色" };
	private static final String BIGString[] = { "大小：大", "大小：小" };
	private static final String ZODIAString[] = { "生肖：鼠", "生肖：牛", "生肖：虎", "生肖：兔", "生肖：龙", "生肖：蛇", "生肖：马", "生肖：羊",
			"生肖：猴", "生肖：鸡", "生肖：狗", "生肖：猪" };
	private static final String SINGLEString[] = { "单相：单号", "单相：双号" };

	public static void main(String[] args) {
		int [] pingCodes = {2, 38, 42, 18, 44 ,45};
		System.out.println(getFortunaProductIds(40, pingCodes, 9));
	}

	public static List<Integer> getFortunaProductIds(int fortuna, int[] pingCodes, int currentYear) {
		List<Integer> productIds = new ArrayList<>();
		// 特码
		int bigger = tmBigger(fortuna);
		int color = tmColor(fortuna);
		int numer = tmNumber(fortuna);
		List<Integer> senior = tmSenior(fortuna);
		int single = tmSingle(fortuna);
		if (numer != 0)
			productIds.add(numer);
		if (color != 0)
			productIds.add(color);
		if (bigger != 0)
			productIds.add(bigger);
		if (single != 0)
			productIds.add(single);
		if (!senior.contains(0))
			productIds.addAll(senior);
		productIds.addAll(pm(pingCodes));
		return productIds;
	}

	public static Integer tmNumber(int fortuna) {
		return fortuna;
	}

	public static Integer tmColor(int fortuna) {
		for (int i : RED) {
			if (i == fortuna) {
				return NUMBER + 1; // 50
			}
		}
		for (int i : BLUE) {
			if (i == fortuna) {
				return NUMBER + 2; // 51
			}
		}
		for (int i : GREEN) {
			if (i == fortuna) {
				return NUMBER + 3; // 52
			}
		}
		return 0;
	}

	public static Integer tmBigger(int fortuna) {
		if (fortuna == 49) {
			return 0; // 和
		} else {
			if (fortuna < 25) {
				return NUMBER + COLOR + 2;// 小 54
			} else {
				return NUMBER + COLOR + 1;// 大 53
			}
		}
	}

	public static Integer tmSingle(int fortuna) {
		if (fortuna == 49) {
			return 0;
		} else {
			if (fortuna % 2 == 0) {
				// 特码双 56
				return NUMBER + COLOR + BIG + 2;
			} else {
				// 特码单 55
				return NUMBER + COLOR + BIG + 1;
			}
		}
	}

	public static List<Integer> tmSenior(int fortuna) {
		List<Integer> integers = new ArrayList<>();
		if (fortuna == 49) {
			integers.add(0);
		} else {
			if ((fortuna % 10) > 4) {
				integers.add(NUMBER + COLOR + BIG + SINGLE + 1); // 特尾大 57
			} else {
				integers.add(NUMBER + COLOR + BIG + SINGLE + 2); // 特尾小 58
			}
			if (fortuna % 2 == 0) {
				// 双
				if (fortuna > 24) {
					// 大双 61
					integers.add(NUMBER + COLOR + BIG + SINGLE + 5);
				} else {
					// 小双 62
					integers.add(NUMBER + COLOR + BIG + SINGLE + 6);
				}
			} else {
				// 单
				if (fortuna > 24) {
					// 大单 59
					integers.add(NUMBER + COLOR + BIG + SINGLE + 3);
				} else {
					// 小单 60
					integers.add(NUMBER + COLOR + BIG + SINGLE + 4);
				}
				
			}
			int digit_1 = fortuna % 10;
			int digit_2 = fortuna / 10;
			if ((digit_1 + digit_2) % 2 == 0) {
				// 合数双 64
				integers.add(NUMBER + COLOR + BIG + SINGLE + 8);
			} else {
				// 合数单 63
				integers.add(NUMBER + COLOR + BIG + SINGLE + 7);
			}
		}
		return integers;
	}
	
	public static List<Integer> pm(int[] pingCodes) {
		List<Integer> pm = new ArrayList<>();
		int sum = 0;
		for(int i :pingCodes){
			sum += i ;
		}
		System.out.println(sum);
		if(sum%2 == 0){
			pm.add(TM+NUMBER+4);
		}else {
			pm.add(TM+NUMBER+3);
		}
		if(sum >= 175){
			pm.add(TM+NUMBER+1);
		}else {
			pm.add(TM+NUMBER+2);
		}
		return pm;
	}
}
