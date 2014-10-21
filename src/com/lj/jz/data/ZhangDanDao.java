package com.lj.jz.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

public class ZhangDanDao {
	Context context=null;
	DatabaseHelper dh=null;
	public ZhangDanDao(Context context){
		this.context=context;
		this.dh=new DatabaseHelper(context);
	}
	//查出所有账单
	public List<ZhangDan> findAll(){
		ArrayList<ZhangDan> zdList=new ArrayList<ZhangDan>();
		dh.getEntryList(zdList, ZhangDan.class, null, null, null, 0, 0);		
		return zdList;
	}
	//根据状态查询所有账单
	public int findAllByStatus(List<ZhangDan> zhangDanList,int status,int status1){
		return dh.getEntryList(zhangDanList, ZhangDan.class, "status=? or status=?", new String[]{String.valueOf(status),String.valueOf(status1)}, "num desc", 0, 0);
	}
	//根据状态查询所有账单
	public int findAllByStatus(List<ZhangDan> zhangDanList,int status){
		return dh.getEntryList(zhangDanList, ZhangDan.class, "status=?", new String[]{String.valueOf(status)}, "num desc", 0, 0);
	}
	//根据算账标志和状态查询所有账单
	public int findAllBySzbz(List<ZhangDan> zhangDanList,String szbz,int offset,int max){
		return dh.getEntryList(zhangDanList, ZhangDan.class, "szbz=? and (status=? or status=?)", new String[]{szbz.trim(),String.valueOf(1),String.valueOf(2)}, "num desc", offset, max);
	}
	//根据算账标志和状态查询所有账单
	public int findAllBySzbzAndStatus(List<ZhangDan> zhangDanList,String szbz,int status){
		return dh.getEntryList(zhangDanList, ZhangDan.class, "szbz=? and status=?", new String[]{szbz.trim(),String.valueOf(status)}, "num desc", 0, 0);
	}
	//根据编号查询一个账单
	public ZhangDan findByNum(long num){
		List<ZhangDan> zhangDanList=new ArrayList<ZhangDan>();
		dh.getEntryList(zhangDanList, ZhangDan.class, "num=?", new String[]{String.valueOf(num)}, "num desc", 0, 1);
		if(!zhangDanList.isEmpty()){
			return zhangDanList.get(0);
		}
		return null;
	}
	//根据编号查询一个账单
	public ZhangDan findByNumAndStatus(long num,int status){
		List<ZhangDan> zhangDanList=new ArrayList<ZhangDan>();
		dh.getEntryList(zhangDanList, ZhangDan.class, "num=? and status=?", new String[]{String.valueOf(num),String.valueOf(status)}, "num desc", 0, 1);
		if(!zhangDanList.isEmpty()){
			return zhangDanList.get(0);
		}
		return null;
	}
	//根据状态查询一个账单
	public ZhangDan findByStatus(int status){
		List<ZhangDan> zhangDanList=new ArrayList<ZhangDan>();
		dh.getEntryList(zhangDanList, ZhangDan.class, "status=?", new String[]{String.valueOf(status)}, "num desc", 0, 1);
		if(!zhangDanList.isEmpty()){
			return zhangDanList.get(0);
		}
		return null;
	}
	//查询出最近条数据
	public ZhangDan findLast(){
		List<ZhangDan> zhangDanList=new ArrayList<ZhangDan>();
		dh.getEntryList(zhangDanList, ZhangDan.class, null, null, "num desc", 0, 1);
		if(zhangDanList.isEmpty()){
			return null;
		}else{
			return zhangDanList.get(0);
		}
	}
	//写入一条数据
	public int insert(ZhangDan zhangDan){
		//查询是否存在相同的数据
		List<ZhangDan> zhangDanList=new ArrayList<ZhangDan>();
		dh.getEntryList(zhangDanList, ZhangDan.class, "fkr=? and rq=? and yt=? and je=? and jemx=? and yqr=? and status=?", 
				new String[]{zhangDan.fkr,zhangDan.rq,zhangDan.yt,String.valueOf(zhangDan.je),(String)zhangDan.jemx,zhangDan.yqr,String.valueOf(zhangDan.status)}, "num desc", 0, 1);
		if(!zhangDanList.isEmpty()){
			return 1;//已经存在相同的数据
		}
		if(dh.insert(zhangDan)>-1){
			writeBack();
			return 0;
		}
		return -1;
	}
	//从系统外部写入数据
	public int insertFromExternal(ZhangDan zhangDan){
		//查询是否存在相同的数据
		List<ZhangDan> zhangDanList=new ArrayList<ZhangDan>();
		dh.getEntryList(zhangDanList, ZhangDan.class, "fkr=? and rq=? and yt=? and je=? and yqr=?", 
				new String[]{zhangDan.fkr,zhangDan.rq,zhangDan.yt,String.valueOf(zhangDan.je),zhangDan.yqr}, "num desc", 0, 1);
		if(!zhangDanList.isEmpty()){
			//如果新数据的账单算账标志为“已算账”，则更新账单为已算账
			ZhangDan zd=zhangDanList.get(0);
			if(zd.szbz.equals("未算账")&&zhangDan.szbz.equals("已算账")){
				zd.szbz="已算账";
				if(dh.update(zd)>0){
					return 0;
				}
			}
			return 1;//已经存在相同的数据
		}
		if(dh.insert(zhangDan)>-1){
			//writeBack();
			return 0;
		}
		return -1;
	}
	//更新一条记录
	public int updateNoCheck(ZhangDan zhangDan){
		if(dh.update(zhangDan)>0){
			writeBack();
			return 0;
		}
		return -1;
	}
	//更新一条记录
	public int update(ZhangDan zhangDan){
		//查询是否存在相同的数据
		List<ZhangDan> zhangDanList=new ArrayList<ZhangDan>();
		dh.getEntryList(zhangDanList, ZhangDan.class, "fkr=? and rq=? and yt=? and je=? and jemx=? and yqr=? and status=?", 
				new String[]{zhangDan.fkr,zhangDan.rq,zhangDan.yt,String.valueOf(zhangDan.je),(String)zhangDan.jemx,zhangDan.yqr,String.valueOf(zhangDan.status)}, "num desc", 0, 1);
		System.out.println("zhangDanList-zhangDan-->"+zhangDanList+"-"+zhangDan);
		if(!zhangDanList.isEmpty()){
			return 1;//已经存在相同的数据
		}
		if(dh.update(zhangDan)>0){
			writeBack();
			return 0;
		}
		return -1;
	}
	//更新未算账的账单的算账标志为已算账
	public int updateToYsz(){
		HashMap<String, Object> hm=new HashMap<String, Object>();
		hm.put("szbz", "已算账");
		if(dh.update(ZhangDan.class,hm,"szbz=? and (status=? or status=?)", new String[]{"未算账",String.valueOf(1),String.valueOf(2)})>0){
			writeBack();
			return 0;
		}
		return -1;
	}
	
	//删除一条记录
	public boolean delete(ZhangDan zhangDan){
		int affectedNumber=dh.delete(zhangDan.getClass(), "num=?", new String[]{String.valueOf(zhangDan.num)});
		if(affectedNumber>0){
			writeBack();
			return true;
		}
		return false;
	}
	//删除一条记录
	public boolean delete(long num){
		int affectedNumber=dh.delete(ZhangDan.class, "num=?", new String[]{String.valueOf(num)});
		if(affectedNumber>0){
			writeBack();
			return true;
		}
		return false;
	}
	//备份数据
	public void writeBack(){
		SystemParamDao spd=new SystemParamDao(this.context);
		SystemParam sp=spd.findFirstByName(SystemParamNames.needBackZhangDan);
		boolean isNew=false;
		if(sp==null){
			isNew=true;
			sp=new SystemParam();
			sp.paramName=SystemParamNames.needBackZhangDan;
		}
		sp.paramValue="true";
		if(isNew){
			spd.insert(sp);
		}else{
			spd.update(sp);
		}
	}
}
