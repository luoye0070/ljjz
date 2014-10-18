package com.lj.jz.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class CanShuDao {
	Context context=null;
	DatabaseHelper dh=null;
	public CanShuDao(Context context){
		this.context=context;
		this.dh=new DatabaseHelper(context);
	}
	//查出所有参数
	public List<CanShu> findAll(){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, null, null, null, 0, 0);		
		return csList;
	}
	//根据类别查出所有参数
	public List<CanShu> findAllByLb(String lb){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, "lb=?", new String[]{lb}, null, 0, 0);		
		return csList;
	}
	//根据编号查询一个参数
	public CanShu findByNum(long num){
		List<CanShu> canShuList=new ArrayList<CanShu>();
		dh.getEntryList(canShuList, CanShu.class, "num=?", new String[]{String.valueOf(num)}, "num desc", 0, 1);
		if(!canShuList.isEmpty()){
			return canShuList.get(0);
		}
		return null;
	}
	//写入一条数据
	public int insertFromExternal(CanShu canShu){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, "lb=? and xx=?", new String[]{canShu.lb,canShu.xx}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//已经存在相同记录
		}
		System.out.println("csList-->"+csList);
		if(dh.insert(canShu)>-1){
			//writeBack();
			return 0;
		}
		return -1;
	}
	//写入一条数据
	public int insert(CanShu canShu){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, "lb=? and xx=?", new String[]{canShu.lb,canShu.xx}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//已经存在相同记录
		}
		System.out.println("csList-->"+csList);
		if(dh.insert(canShu)>-1){
			writeBack();
			return 0;
		}
		return -1;
	}
	//更新一条记录
	public int update(CanShu canShu){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, "lb=? and xx=?", new String[]{canShu.lb,canShu.xx}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//已经存在相同记录
		}
		System.out.println("csList-->"+csList);
		if(dh.update(canShu)>0){
			writeBack();
			return 0;
		}
		return -1;
	}
	//删除一条记录
	public boolean delete(long num){
		int affectedNumber=dh.delete(CanShu.class, "num=?", new String[]{String.valueOf(num)});
		if(affectedNumber>0){
			writeBack();
			return true;
		}
		return false;
	}
	//备份数据
	public void writeBack(){
		SystemParamDao spd=new SystemParamDao(this.context);
		SystemParam sp=spd.findFirstByName(SystemParamNames.needBackCanShu);
		boolean isNew=false;
		if(sp==null){
			isNew=true;
			sp=new SystemParam();
			sp.paramName=SystemParamNames.needBackCanShu;
		}
		sp.paramValue="true";
		if(isNew){
			spd.insert(sp);
		}else{
			spd.update(sp);
		}
	}
}
