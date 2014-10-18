package com.lj.jz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lj.jz.MainActivity.ItemBtOnClickListener;
import com.lj.jz.data.CanShu;
import com.lj.jz.data.CanShuDao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CsListActivity extends Activity {
	Button Nav_zdBt=null;
	Button Nav_scBt=null;
	Button Nav_csBt=null;
	Button Nav_szBt=null;
	
	ListView cslistAy_csListLv=null;
	Button cslistAy_tjBt=null;
	List<CanShu> canShuList=null;
	//List<Map<String,String>> dataList=null;
	TextView cslistAy_noNotifyTv=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//去掉标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_cslist);
		
		Nav_zdBt=(Button) findViewById(R.id.Nav_zdBt);
		Nav_scBt=(Button) findViewById(R.id.Nav_scBt);
		Nav_csBt=(Button) findViewById(R.id.Nav_csBt);
		Nav_szBt=(Button) findViewById(R.id.Nav_szBt);
		
		cslistAy_csListLv=(ListView) findViewById(R.id.cslistAy_csListLv);
		cslistAy_tjBt=(Button) findViewById(R.id.cslistAy_tjBt);
		cslistAy_noNotifyTv=(TextView) findViewById(R.id.cslistAy_noNotifyTv);
		
		registerListener();
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initLv();
	}
	private void registerListener(){
		Nav_zdBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				CsListActivity.this.finish();
			}
		});
		Nav_scBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), UploadActivity.class);
				startActivity(intent);
				CsListActivity.this.finish();
			}
		});
		Nav_csBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), CsListActivity.class);
				startActivity(intent);
				CsListActivity.this.finish();
			}
		});
		Nav_szBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), SzActivity.class);
				startActivity(intent);
				//CsListActivity.this.finish();
			}
		});
		
		cslistAy_tjBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), CsEditActivity.class);
				intent.putExtra("csNum", 0);
				startActivity(intent);
			}
		});
		cslistAy_csListLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, arg2+"-"+arg3, Toast.LENGTH_LONG).show();
				//System.out.println("CanShuList.get(arg2).num-->"+CanShuList.get(arg2).num);
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), CsEditActivity.class);
				intent.putExtra("csNum", canShuList.get(arg2).num);
				startActivity(intent);
			}
		});
	}
	
	private void initLv(){
		//查询出数据
		CanShuDao csd=new CanShuDao(getApplicationContext());
		canShuList=csd.findAll();
		if(canShuList!=null&&!canShuList.isEmpty()){
			cslistAy_noNotifyTv.setVisibility(View.GONE);
			cslistAy_csListLv.setVisibility(View.VISIBLE);
			
			ArrayList<HashMap<String, Object>> dataList=new ArrayList<HashMap<String,Object>>();
			for(int i=0;i<canShuList.size();i++){
				HashMap<String,Object> dataMap=new HashMap<String, Object>();
				CanShu cs=canShuList.get(i);
				dataMap.put("csLi_lb", new SpannableString(""+cs.lb));
				dataMap.put("csLi_xx", new SpannableString(""+cs.xx));
				dataMap.put("csLi_scBt", new ItemBtOnClickListener(cs.num));
				dataList.add(dataMap);
			}
			//System.out.println("dataList-->"+dataList);
			BListAdapter sa=new BListAdapter(CsListActivity.this,cslistAy_csListLv,dataList, R.layout.lvitem_cs, 
					new String[]{"csLi_lb","csLi_xx","csLi_scBt"}, 
					new int[]{R.id.csLi_lb,R.id.csLi_xx,R.id.csLi_scBt});
			cslistAy_csListLv.setAdapter(sa);
			//sa.notifyDataSetChanged();
		}
		else{
			cslistAy_noNotifyTv.setVisibility(View.VISIBLE);
			cslistAy_csListLv.setVisibility(View.GONE);
		}
	}
	
	class ItemBtOnClickListener implements OnClickListener{
		long num=-1;
		ItemBtOnClickListener(long num){
			this.num=num;
		}
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
//			AlertDialog.Builder dialog=new AlertDialog.Builder(CsListActivity.this);
//			View wxczqrView=getLayoutInflater().inflate(R.layout.dialog_wxczqr, null);
//			final EditText et=(EditText) wxczqrView.findViewById(R.id.wxDialog_qdEt);
//			dialog.setView(wxczqrView);
//			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//					if("确定".equals(et.getText().toString().trim())){
//						CanShuDao csd=new CanShuDao(getApplicationContext());
//						if(csd.delete(num))
//							initLv();
//						else
//							Toast.makeText(CsListActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
//					}
//					else{
//						Toast.makeText(CsListActivity.this, "输入不正确", Toast.LENGTH_SHORT).show();
//					}
//				}
//			});
//			dialog.setNegativeButton("取消", null);
//			dialog.show();
			
			CanShuDao csd=new CanShuDao(getApplicationContext());
			if(csd.delete(num))
				initLv();
			else
				Toast.makeText(CsListActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
			AlertDialog.Builder dialog=new AlertDialog.Builder(CsListActivity.this);
			dialog.setTitle("退出");
			dialog.setMessage("确定要退出吗？");
			dialog.setNegativeButton("取消", null);
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					CsListActivity.this.finish();
					Intent intent=new Intent();
					intent.setClass(getApplicationContext(), MakeBackupService.class);
					startService(intent);
				}
			});
			dialog.show();
		    return true;
		}		
		return super.onKeyDown(keyCode, event);
	}
}
