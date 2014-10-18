package com.lj.jz.data;

public class ZhangDan {
	public long num;//编号，自动增长
	public String fkr="";//付款人
	public String rq="";//日期
	public String yt="";//用途
	public double je;//金额
	public String jemx="";//金额明细
	public String yqr="";//用钱人
	public String bz="";//备注
	public String szbz="未算账";//算账标志
	public String jzr="";//记账人
	public int status=0;//状态，0初始态，1确认,2上传到服务器
	@Override
	public String toString() {
		return "ZhangDan [num=" + num + ", fkr=" + fkr + ", rq=" + rq + ", yt="
				+ yt + ", je=" + je + ", jemx=" + jemx + ", yqr=" + yqr
				+ ", bz=" + bz + ", szbz=" + szbz + ", jzr=" + jzr
				+ ", status=" + status + "]";
	}
}
