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
	//������в���
	public List<CanShu> findAll(){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, null, null, null, 0, 0);		
		return csList;
	}
	//������������в���
	public List<CanShu> findAllByLb(String lb){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, "lb=?", new String[]{lb}, null, 0, 0);		
		return csList;
	}
	//���ݱ�Ų�ѯһ������
	public CanShu findByNum(long num){
		List<CanShu> canShuList=new ArrayList<CanShu>();
		dh.getEntryList(canShuList, CanShu.class, "num=?", new String[]{String.valueOf(num)}, "num desc", 0, 1);
		if(!canShuList.isEmpty()){
			return canShuList.get(0);
		}
		return null;
	}
	//д��һ������
	public int insertFromExternal(CanShu canShu){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, "lb=? and xx=?", new String[]{canShu.lb,canShu.xx}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//�Ѿ�������ͬ��¼
		}
		System.out.println("csList-->"+csList);
		if(dh.insert(canShu)>-1){
			//writeBack();
			return 0;
		}
		return -1;
	}
	//д��һ������
	public int insert(CanShu canShu){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, "lb=? and xx=?", new String[]{canShu.lb,canShu.xx}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//�Ѿ�������ͬ��¼
		}
		System.out.println("csList-->"+csList);
		if(dh.insert(canShu)>-1){
			writeBack();
			return 0;
		}
		return -1;
	}
	//����һ����¼
	public int update(CanShu canShu){
		ArrayList<CanShu> csList=new ArrayList<CanShu>();
		dh.getEntryList(csList, CanShu.class, "lb=? and xx=?", new String[]{canShu.lb,canShu.xx}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//�Ѿ�������ͬ��¼
		}
		System.out.println("csList-->"+csList);
		if(dh.update(canShu)>0){
			writeBack();
			return 0;
		}
		return -1;
	}
	//ɾ��һ����¼
	public boolean delete(long num){
		int affectedNumber=dh.delete(CanShu.class, "num=?", new String[]{String.valueOf(num)});
		if(affectedNumber>0){
			writeBack();
			return true;
		}
		return false;
	}
	//��������
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
