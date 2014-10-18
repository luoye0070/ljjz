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
	//������в���
	public List<SystemParam> findAll(){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, null, null, null, 0, 0);		
		return csList;
	}
	//�������Ʋ�����в���
	public SystemParam findFirstByName(String name){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, "paramname=?", new String[]{name}, null, 0, 0);	
		if(csList!=null&&!csList.isEmpty()){
			return csList.get(0);
		}
		return null;
	}
	//�������Ʋ�����в���
	public List<SystemParam> findAllByName(String name){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, "paramname=?", new String[]{name}, null, 0, 0);		
		return csList;
	}
	//���ݱ�Ų�ѯһ���˵�
	public SystemParam findByNum(long num){
		List<SystemParam> SystemParamList=new ArrayList<SystemParam>();
		dh.getEntryList(SystemParamList, SystemParam.class, "num=?", new String[]{String.valueOf(num)}, "num desc", 0, 1);
		if(!SystemParamList.isEmpty()){
			return SystemParamList.get(0);
		}
		return null;
	}
	//д��һ������
	public int insert(SystemParam systemParam){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, "paramname=? and paramvalue=?", new String[]{systemParam.paramName,systemParam.paramValue}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//�Ѿ�������ͬ��¼
		}
		System.out.println("csList-->"+csList);
		if(dh.insert(systemParam)>-1){
			return 0;
		}
		return -1;
	}
	//����һ����¼
	public int update(SystemParam systemParam){
		ArrayList<SystemParam> csList=new ArrayList<SystemParam>();
		dh.getEntryList(csList, SystemParam.class, "paramname=? and paramvalue=?", new String[]{systemParam.paramName,systemParam.paramValue}, null, 0, 1);
		if(!csList.isEmpty()){
			return 1;//�Ѿ�������ͬ��¼
		}
		if(dh.update(systemParam)>0){
			return 0;
		}
		return -1;
	}
	//ɾ��һ����¼
	public boolean delete(long num){
		int affectedNumber=dh.delete(SystemParam.class, "num=?", new String[]{String.valueOf(num)});
		if(affectedNumber>0){
			return true;
		}
		return false;
	}
}
