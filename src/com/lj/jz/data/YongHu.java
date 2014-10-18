package com.lj.jz.data;

public class YongHu {
	public long num=0;//编号，自动增长
	public String yhm="";//用户名
	public String mm="";//密码
	public String qx="";//权限(查看，全部)	
	public String jzmm="";//是否记住密码
	public String zddl="";//是否自动登录
	public String mmts="";//密码提示
	@Override
	public String toString() {
		return "YongHu [num=" + num + ", yhm=" + yhm + ", mm=" + mm + ", qx="
				+ qx + ", jzmm=" + jzmm + ", zddl=" + zddl + ", mmts=" + mmts
				+ "]";
	}
}
