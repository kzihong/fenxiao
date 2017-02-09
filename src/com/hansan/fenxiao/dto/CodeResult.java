package com.hansan.fenxiao.dto;

import java.util.Date;
import java.util.List;

public class CodeResult {
	private int rows;
	private String code;
	private String info;
	private List<Data> data;
	private int fortuna;
	private String pingCodes;
	
	
	
	public String getPingCodes() {
		Data term = data.get(0);
		for(Data cterm : data){
			if(term.getOpentimestamp()<term.getOpentimestamp()){
				term = cterm;
			}
		}
		if(System.currentTimeMillis() - term.getOpentimestamp()*1000 < 12 * 3600*1000){
			String[] codeString = term.getOpencode().split("\\+");
			String pingCode = codeString[0];
			this.setPingCodes(pingCode);
		}
		return pingCodes;
	}
	public void setPingCodes(String pingCodes) {
		this.pingCodes = pingCodes;
	}
	public int getFortuna() {
		Data term = data.get(0);
		for(Data cterm : data){
			if(term.getOpentimestamp()<term.getOpentimestamp()){
				term = cterm;
			}
		}
		if(System.currentTimeMillis() - term.getOpentimestamp()*1000 < 12 * 3600*1000){
			String[] codeString = term.getOpencode().split("\\+");
			String fortunaString = codeString[1];
			setFortuna(Integer.parseInt(fortunaString));
		}//必须是半天内的结果，超过一天内的我不要
		return fortuna;
	}
	public void setFortuna(int fortuna) {
		this.fortuna = fortuna;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public List<Data> getData() {
		return data;
	}
	public void setData(List<Data> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "CodeResult [rows=" + rows + ", code=" + code + ", info=" + info + ", data=" + data + ", fortuna="
				+ getFortuna() + ", pingCodes=" + getPingCodes() + "]";
	}
	
	
	
	
	
	
	
}
