package com.lj.jz.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class SystemParamDao {
	Context context=null;
	DatabaseHelper dh=null;
	public SystemParamDao(Context context){
		this.context=context;
		this.dh=new DatabaseHelper(context);
	}
	//查出所有参数
	public List<SystemParam> findAll(){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, null, null, null, 0, 0);		
		return csList;
	}
	//根据名称查出所有参数
	public SystemParam findFirstByName(String name){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, "paramname=?", new String[]{name}, null, 0, 0);	
		if(csList!=null&&!csList.isEmpty()){
			return csList.get(0);
		}
		return null;
	}
	//根据名称查出所有参数
	public List<SystemParam> findAllByName(String name){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, "paramname=?", new String[]{name}, null, 0, 0);		
		return csList;
	}
	//根据编号查询一个账单
	public SystemParam findByNum(long num){
		List<SystemParam> SystemParamList=new ArrayList<SystemParam>();
		dh.getEntryList(SystemParamList, SystemParam.class, "num=?", new String[]{String.valueOf(num)}, "num desc", 0, 1);
		if(!SystemParamList.isEmpty()){
			return SystemParamList.get(0);
		}
		return null;
	}
	//写入一条数据
	public int insert(SystemParam systemParam){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, "paramname=? and paramvalue=?", new String[]{systemParam.paramName,systemParam.paramValue}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//已经存在相同记录
		}
		System.out.println("csList-->"+csList);
		if(dh.insert(systemParam)>-1){
			return 0;
		}
		return -1;
	}
	//更新一条记录
	public int update(SystemParam systemParam){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, "paramname=? and paramvalue=?", new String[]{systemParam.paramName,systemParam.paramValue}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//已经存在相同记录
		}
		if(dh.update(systemParam)>0){
			return 0;
		}
		return -1;
	}
	//删除一条记录
	public boolean delete(long num){
		int affectedNumber=dh.delete(SystemParam.class, "num=?", new String[]{String.valueOf(num)});
		if(affectedNumber>0){
			return true;
		}
		return false;
	}
}
