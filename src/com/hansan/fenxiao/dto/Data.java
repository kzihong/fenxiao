package com.hansan.fenxiao.dto;

public class Data {
	private String expect;
	private String opencode;
	private String opentime;
	private long opentimestamp;
	public String getExpect() {
		return expect;
	}
	public void setExpect(String expect) {
		this.expect = expect;
	}
	public String getOpencode() {
		return opencode;
	}
	public void setOpencode(String opencode) {
		this.opencode = opencode;
	}
	public String getOpentime() {
		return opentime;
	}
	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}
	public long getOpentimestamp() {
		return opentimestamp;
	}
	public void setOpentimestamp(long opentimestamp) {
		this.opentimestamp = opentimestamp;
	}
	@Override
	public String toString() {
		return "Data [expect=" + expect + ", opencode=" + opencode + ", opentime=" + opentime + ", opentimestamp="
				+ opentimestamp + "]";
	}
	
	
}
