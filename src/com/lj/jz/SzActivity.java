package com.lj.jz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.lj.jz.UploadActivity.tbcsRunnable;
import com.lj.jz.UploadActivity.tbzdRunnable;
import com.lj.jz.data.ZhangDan;
import com.lj.jz.data.ZhangDanDao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SzActivity extends Activity {
	Button Nav_zdBt=null;
	Button Nav_scBt=null;
	EditText szAy_resultEt=null;
	Button szAy_qxBt=null;
	Button szAy_qdBt=null;
	Button navAy_backBt=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//去掉标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_sz);
		
		Nav_zdBt=(Button) findViewById(R.id.Nav_zdBt);
		Nav_scBt=(Button) findViewById(R.id.Nav_scBt);
		
		szAy_resultEt=(EditText) findViewById(R.id.szAy_resultEt);
		szAy_qxBt=(Button) findViewById(R.id.szAy_qxBt);
		szAy_qdBt=(Button) findViewById(R.id.szAy_qdBt);
		navAy_backBt=(Button) findViewById(R.id.navAy_backBt);
		
		registerListener();
		calculate();
	}
	
	private void registerListener(){
		Nav_zdBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				SzActivity.this.finish();
			}
		});
		Nav_scBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), UploadActivity.class);
				startActivity(intent);
				SzActivity.this.finish();
			}
		});
		navAy_backBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SzActivity.this.finish();
			}
		});
		szAy_qxBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SzActivity.this.finish();
			}
		});
		szAy_qdBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder dialog=new AlertDialog.Builder(SzActivity.this);
				View wxczqrView=getLayoutInflater().inflate(R.layout.dialog_wxczqr, null);
				final TextView tv=(TextView) wxczqrView.findViewById(R.id.wxDialog_msgTv);
				tv.setText("完成算账后将清除所有账单！\n如果你确定要这么做，请输入‘确定’后，点击确定");
				final EditText et=(EditText) wxczqrView.findViewById(R.id.wxDialog_qdEt);
				dialog.setView(wxczqrView);
				dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if("确定".equals(et.getText().toString().trim())){
							ZhangDanDao zdd=new ZhangDanDao(getApplicationContext());
							int recode=zdd.updateToYsz();
							if(recode==0){
								Toast.makeText(SzActivity.this, "完成算账成功", Toast.LENGTH_SHORT).show();
								SzActivity.this.finish();
							}else
								Toast.makeText(SzActivity.this, "完成算账失败", Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(SzActivity.this, "输入不正确", Toast.LENGTH_SHORT).show();
						}
					}
				});
				dialog.setNegativeButton("取消", null);
				dialog.show();
			}
		});
	}
	private void calculate(){
		ArrayList<ZhangDan> zdList = new ArrayList<ZhangDan>();
		ZhangDanDao zdd=new ZhangDanDao(getApplicationContext());
		int totalCount=zdd.findAllBySzbz(zdList,"未算账", 0,0);
		int count=zdList.size();
		if(count==0){
			Toast.makeText(SzActivity.this, "没有未算账账单", Toast.LENGTH_SHORT).show();
			SzActivity.this.finish();
			return;
		}
		HashMap<String, Double> fkrJehzHm=new HashMap<String, Double>();
		HashMap<String, Double> yqrJehzHm=new HashMap<String, Double>();
		HashMap<String, Double> hqJehzHm=new HashMap<String, Double>();
		for(int i=0;i<count;i++){
			ZhangDan zd=zdList.get(i);
			//付款人金额汇总
			if(fkrJehzHm.get(zd.fkr)!=null){
				fkrJehzHm.put(zd.fkr, fkrJehzHm.get(zd.fkr).doubleValue()+zd.je);
			}else{
				fkrJehzHm.put(zd.fkr, zd.je);
			}
			//用钱人金额汇总
			String[] yqrs=zd.yqr.split("\\|");
			System.out.println("zd.yqr-yqrs.length-->"+zd.yqr+"-"+yqrs.length);
			int yqrsCount=yqrs.length;
			if(yqrs!=null&&yqrs.length>0){
				//保留2位小数
//				BigDecimal bg = new BigDecimal(zd.je/yqrsCount);
//				double yqje= bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				double yqje=zd.je/yqrsCount;
				
				for(int n=0;n<yqrsCount;n++){
					if(yqrJehzHm.get(yqrs[n])!=null){
						yqrJehzHm.put(yqrs[n], yqrJehzHm.get(yqrs[n])+yqje);
					}else{
						yqrJehzHm.put(yqrs[n], yqje);
					}
				}
			}
			System.out.println("fkrJehzHm-->"+fkrJehzHm.toString());
			System.out.println("yqrJehzHm-->"+yqrJehzHm.toString());			
		}
		//计算还钱方案
		//合并key
		ArrayList<String> rList=new ArrayList<String>();
		if(fkrJehzHm.keySet()!=null&&!fkrJehzHm.keySet().isEmpty()){
			Iterator fkrKeyIterator=fkrJehzHm.keySet().iterator();
			while(fkrKeyIterator.hasNext()){
				String fkr=(String) fkrKeyIterator.next();
				rList.add(fkr);
			}
		}
		if(yqrJehzHm.keySet()!=null&&!yqrJehzHm.keySet().isEmpty()){
			Iterator yqrKeyIterator=yqrJehzHm.keySet().iterator();
			while(yqrKeyIterator.hasNext()){
				String yqr=(String) yqrKeyIterator.next();
				if(!isInList(yqr, rList)){
					rList.add(yqr);
				}
			}
		}
		//计算还钱金额
		int size=rList.size();
		for(int i=0;i<size;i++){
			String r=rList.get(i);
			Double yqje=yqrJehzHm.get(r);
			Double fkje=fkrJehzHm.get(r);
			if(yqje!=null&&fkje!=null){
				//保留2位小数
				BigDecimal bg = new BigDecimal(yqje.doubleValue()-fkje.doubleValue());
				double hkje= bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				//double hkje= yqje.doubleValue()-fkje.doubleValue();
				hqJehzHm.put(r, hkje);
				
				bg = new BigDecimal(yqje.doubleValue());
				double yqjeT=bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				yqrJehzHm.put(r, yqjeT);
				
				bg = new BigDecimal(fkje.doubleValue());
				double fkjeT=bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				fkrJehzHm.put(r, fkjeT);
				
			}else if(yqje!=null){
				
				BigDecimal bg = new BigDecimal(yqje.doubleValue());
				double yqjeT=bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				yqrJehzHm.put(r, yqjeT);
				
				hqJehzHm.put(r, yqjeT);
			}else if(fkje!=null){
				BigDecimal bg = new BigDecimal(fkje.doubleValue());
				double fkjeT=bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				fkrJehzHm.put(r, fkjeT);
				
				hqJehzHm.put(r, -fkjeT);
			}	
		}
		System.out.println("hqJehzHm-->"+hqJehzHm.toString());
		
		//将算账结果写入文本框显示出来
		StringBuilder resultMsg=new StringBuilder("算账结果如下：\r\n\r\n");
		
		resultMsg.append("付款人付款汇总情况如下：");
		//resultMsg.append(fkrJehzHm.toString()+"\r\n\r\n");
		if(fkrJehzHm.keySet()!=null&&!fkrJehzHm.keySet().isEmpty()){
			Iterator fkrKeyIterator=fkrJehzHm.keySet().iterator();
			while(fkrKeyIterator.hasNext()){
				String fkr=(String) fkrKeyIterator.next();
				resultMsg.append("\r\n"+fkr+":"+fkrJehzHm.get(fkr));
			}
		}
		
		resultMsg.append("\r\n\r\n用钱人用钱汇总情况如下：");
		//resultMsg.append(yqrJehzHm.toString()+"\r\n\r\n");
		if(yqrJehzHm.keySet()!=null&&!yqrJehzHm.keySet().isEmpty()){
			Iterator yqrKeyIterator=yqrJehzHm.keySet().iterator();
			while(yqrKeyIterator.hasNext()){
				String yqr=(String) yqrKeyIterator.next();
				resultMsg.append("\r\n"+yqr+":"+yqrJehzHm.get(yqr));
			}
		}
		
		resultMsg.append("\r\n\r\n还钱方案如下（正数表示应还款，负数表示应收款）：");
		//resultMsg.append(hqJehzHm.toString()+"");
		if(hqJehzHm.keySet()!=null&&!hqJehzHm.keySet().isEmpty()){
			Iterator hqKeyIterator=hqJehzHm.keySet().iterator();
			while(hqKeyIterator.hasNext()){
				String hq=(String) hqKeyIterator.next();
				resultMsg.append("\r\n"+hq+":"+hqJehzHm.get(hq));
			}
		}
		
		szAy_resultEt.setText(resultMsg.toString());
		szAy_resultEt.setSelection(resultMsg.toString().length());
	}
	private boolean isInList(String item,List list){
		if(item==null||list==null){
			return false;
		}
		int size=list.size();
		for(int i=0;i<size;i++){
			if(item.equals(list.get(i))){
				return true;
			}
		}
		return false;
	}
}
