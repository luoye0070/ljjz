package com.lj.jz.data;

public class CanShu {
	public long num;//编号，自动增长
	public String lb="";//类别
	public String xx="";//选项
	public String by="";//备用
	@Override
	public String toString() {
		return "CanShu [num=" + num + ", lb=" + lb + ", xx=" + xx + ", by="
				+ by + "]";
	}
}
