package com.lj.jz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lj.jz.data.ZhangDan;
import com.lj.jz.data.ZhangDanDao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button Nav_zdBt=null;
	Button Nav_scBt=null;
	Button Nav_csBt=null;
	Button Nav_szBt=null;
	ListView zdAy_zdListLv=null;
	Button zdAy_tjBt=null;
	List<ZhangDan> zhangDanList=null;
	//List<Map<String,String>> dataList=null;
	TextView zdAy_noNotifyTv=null;
	
	
	ArrayList<HashMap<String, Object>> dataList=null;
	BListAdapter sa=null;
	
	int visibleLastIndex=0;
	
	int pageNum;//页号
	final static int PAGE_MAX=20;//每页记录数
	int totalCount;//总数量
	
	View loadmoreView=null;
	TextView loadingMoreTv=null;
	ProgressBar progressBar1=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//去掉标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
		
		Nav_zdBt=(Button) findViewById(R.id.Nav_zdBt);
		Nav_scBt=(Button) findViewById(R.id.Nav_scBt);
		Nav_szBt=(Button) findViewById(R.id.Nav_szBt);
		
		Nav_csBt=(Button) findViewById(R.id.Nav_csBt);
		zdAy_zdListLv=(ListView) findViewById(R.id.zdAy_zdListLv);
		zdAy_tjBt=(Button) findViewById(R.id.zdAy_tjBt);
		zdAy_noNotifyTv=(TextView) findViewById(R.id.zdAy_noNotifyTv);
		
		loadmoreView=getLayoutInflater().inflate(R.layout.dynamicload_list_loadmore, null);
		loadingMoreTv=(TextView) loadmoreView.findViewById(R.id.loadingMoreTv);
		progressBar1=(ProgressBar) loadmoreView.findViewById(R.id.progressBar1);
		zdAy_zdListLv.addFooterView(loadmoreView);//设置列表底部视图
		
		registerListener();
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		pageNum=0;
		zhangDanList=null;
		dataList=null;
		sa=null;
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
				MainActivity.this.finish();
			}
		});
		Nav_scBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), UploadActivity.class);
				startActivity(intent);
				MainActivity.this.finish();
			}
		});
		Nav_csBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), CsListActivity.class);
				startActivity(intent);
				MainActivity.this.finish();
			}
		});
		Nav_szBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), SzActivity.class);
				startActivity(intent);
				//MainActivity.this.finish();
			}
		});
		
		zdAy_tjBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), AddActivity.class);
				intent.putExtra("zdNum", 0l);
				startActivity(intent);
			}
		});
		zdAy_zdListLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, arg2+"-"+arg3, Toast.LENGTH_LONG).show();
				//System.out.println("zhangDanList.get(arg2).num-->"+zhangDanList.get(arg2).num);
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), AddActivity.class);
				intent.putExtra("zdNum", zhangDanList.get(arg2).num);
				startActivity(intent);
			}
		});
		zdAy_zdListLv.setOnScrollListener(new LvOnScrollListener());
	}
	
	/***********************列表滚动事件监听器**********************/
	class LvOnScrollListener implements OnScrollListener{
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			visibleLastIndex=firstVisibleItem+visibleItemCount-1;
		}
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			if(dataList==null)
			{
				return;
			}
			int itemsLastIndex = dataList.size()-1;  //数据集最后一项的索引    
			int lastIndex = itemsLastIndex + 1;  
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE  
					&& visibleLastIndex >= lastIndex) {  
				// 如果是自动加载,可以在这里放置异步加载数据的代码  
				if(dataList.size()<totalCount)
					initLv();
			}  

		}
		
	}
	//设置ListView FooterView显示没了
	void setNoMore()
	{
		loadingMoreTv.setText("没有更多账单了哦");
		progressBar1.setVisibility(View.GONE);
	}
	//设置ListView FooterView显示正在加载数据
	void setHasMore()
	{
		loadingMoreTv.setText(getString(R.string.loadingMore));
		progressBar1.setVisibility(View.VISIBLE);
	}
	private void initLv(){
		//查询出数据
		ZhangDanDao zdd=new ZhangDanDao(getApplicationContext());
		//int totalCount=zdd.findAllByStatus(zhangDanList, 1);
		ArrayList<ZhangDan> zhangDanListTemp=new ArrayList<ZhangDan>();
		totalCount=zdd.findAllBySzbz(zhangDanListTemp,"未算账", pageNum*PAGE_MAX,PAGE_MAX);
		if(zhangDanList==null){
			zhangDanList=new ArrayList<ZhangDan>();
		}
		if(!zhangDanListTemp.isEmpty()){
			zhangDanList.addAll(zhangDanListTemp);
		}
		System.out.println("totalCount-->"+totalCount);
		System.out.println("zhangDanList-->"+zhangDanList);
		if(!zhangDanList.isEmpty()){
			zdAy_noNotifyTv.setVisibility(View.GONE);
			zdAy_zdListLv.setVisibility(View.VISIBLE);
			
			ArrayList<HashMap<String,Object>> dataListTemp=new ArrayList<HashMap<String,Object>>();
			for(int i=0;i<zhangDanListTemp.size();i++){
				HashMap<String,Object> dataMap=new HashMap<String, Object>();
				ZhangDan zd=zhangDanListTemp.get(i);
				dataMap.put("zdLi_je", new SpannableString("￥"+zd.je));
				dataMap.put("zdLi_rq", new SpannableString(""+zd.rq));
				dataMap.put("zdLi_yt", new SpannableString(""+zd.yt));
				dataMap.put("zdLi_fkr", new SpannableString(""+zd.fkr));
				dataMap.put("zdLi_yqr", new SpannableString(""+zd.yqr));
				dataMap.put("zdLi_scBt", new ItemBtOnClickListener(zd.num));
				dataListTemp.add(dataMap);
			}
			
			if(dataList==null){
				dataList=new ArrayList<HashMap<String,Object>>();
			}
			
			dataList.addAll(dataListTemp);
			
			if(sa==null){
				sa=new BListAdapter(MainActivity.this,zdAy_zdListLv,dataList, R.layout.lvitem_zhangdan, 
						new String[]{"zdLi_je","zdLi_rq","zdLi_yt","zdLi_fkr","zdLi_yqr","zdLi_scBt"}, 
						new int[]{R.id.zdLi_je,R.id.zdLi_rq,R.id.zdLi_yt,R.id.zdLi_fkr,R.id.zdLi_yqr,R.id.zdLi_scBt});
				zdAy_zdListLv.setAdapter(sa);
			}
			sa.notifyDataSetChanged();
			
			System.out.println("dataList.size-->"+dataList.size());
			//更新底部和页号
			if(dataList.size()>=totalCount)
			{
				setNoMore();
			}else{
				setHasMore();
			}
			
			pageNum++;
			
		}
		else{
			zdAy_noNotifyTv.setVisibility(View.VISIBLE);
			zdAy_zdListLv.setVisibility(View.GONE);
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
			AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
			View wxczqrView=getLayoutInflater().inflate(R.layout.dialog_wxczqr, null);
			final EditText et=(EditText) wxczqrView.findViewById(R.id.wxDialog_qdEt);
			dialog.setView(wxczqrView);
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if("确定".equals(et.getText().toString().trim())){
						ZhangDanDao zdd=new ZhangDanDao(getApplicationContext());
						if(zdd.delete(num)){
							pageNum=0;
							zhangDanList=null;
							dataList=null;
							sa=null;
							initLv();
						}else
							Toast.makeText(MainActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
					}
					else{
						Toast.makeText(MainActivity.this, "输入不正确", Toast.LENGTH_SHORT).show();
					}
				}
			});
			dialog.setNegativeButton("取消", null);
			dialog.show();
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
			AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("退出");
			dialog.setMessage("确定要退出吗？");
			dialog.setNegativeButton("取消", null);
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					MainActivity.this.finish();
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
