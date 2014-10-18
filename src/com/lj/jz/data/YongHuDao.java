package com.lj.jz.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class YongHuDao {
	Context context=null;
	DatabaseHelper dh=null;
	public YongHuDao(Context context){
		this.context=context;
		this.dh=new DatabaseHelper(context);
	}
	//获取最近一条记录
	public YongHu findLast(){
		List<YongHu> yongHuList=new ArrayList<YongHu>();
		dh.getEntryList(yongHuList, YongHu.class, null, null, "num desc", 0, 1);
		if(yongHuList.isEmpty()){
			return null;
		}else{
			return yongHuList.get(0);
		}
	}
	
	//写入一条数据
	public int insert(YongHu yongHu){
		if(dh.insert(yongHu)>-1){
			return 0;
		}
		return -1;
	}
	
	//更新一条记录
	public int update(YongHu yongHu){
		if(dh.update(yongHu)>0){
			return 0;
		}
		return -1;
	}
}
