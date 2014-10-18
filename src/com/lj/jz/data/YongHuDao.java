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
	//��ȡ���һ����¼
	public YongHu findLast(){
		List<YongHu> yongHuList=new ArrayList<YongHu>();
		dh.getEntryList(yongHuList, YongHu.class, null, null, "num desc", 0, 1);
		if(yongHuList.isEmpty()){
			return null;
		}else{
			return yongHuList.get(0);
		}
	}
	
	//д��һ������
	public int insert(YongHu yongHu){
		if(dh.insert(yongHu)>-1){
			return 0;
		}
		return -1;
	}
	
	//����һ����¼
	public int update(YongHu yongHu){
		if(dh.update(yongHu)>0){
			return 0;
		}
		return -1;
	}
}
