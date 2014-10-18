package com.lj.jz.data;

public class SystemParam {
	public long num;//编号，自动增长
	public String paramName;//参数名
	public String paramValue;//参数值
	@Override
	public String toString() {
		return "SystemParam [num=" + num + ", paramName=" + paramName
				+ ", paramValue=" + paramValue + "]";
	}
	
}
