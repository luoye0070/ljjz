package com.lj.jz;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.lj.jz.data.CanShu;
import com.lj.jz.data.CanShuDao;
import com.lj.jz.data.CanShuLbEnum;
import com.lj.jz.data.YongHu;
import com.lj.jz.data.YongHuDao;
import com.lj.jz.data.ZhangDan;
import com.lj.jz.data.ZhangDanDao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SearchViewCompat.OnCloseListenerCompat;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends Activity {
	
	Spinner addAy_fkrSp=null;
	Spinner addAy_ytSp=null;
	Button addAy_rqBt=null;
	Button addAy_yqrBt=null;
	EditText addAy_bzEt=null;
	EditText addAy_jeEt=null;
	Button addAy_jemxBt=null;
	Button addAy_qxBt=null;
	Button addAy_qdBt=null;
	
	long num=0;
	ZhangDan zhangDan=null;
	String [] fkrTexts=null;
	String [] ytTexts=null;
	String [] yqrTexts=null;
	
	ListView jemxLv=null;
	List<String> jemxDataList=null;
	
	TextView addAy_title=null;
	Button navAy_backBt=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//去掉标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_add);
		
		addAy_fkrSp=(Spinner) findViewById(R.id.addAy_fkrSp);
		addAy_ytSp=(Spinner) findViewById(R.id.addAy_ytSp);
		addAy_rqBt=(Button) findViewById(R.id.addAy_rqBt);
		addAy_yqrBt=(Button) findViewById(R.id.addAy_yqrBt);
		addAy_bzEt=(EditText) findViewById(R.id.addAy_bzEt);
		addAy_jeEt=(EditText) findViewById(R.id.addAy_jeEt);
		addAy_jemxBt=(Button) findViewById(R.id.addAy_jemxBt);
		addAy_qxBt=(Button) findViewById(R.id.addAy_qxBt);
		addAy_qdBt=(Button) findViewById(R.id.addAy_qdBt);
		addAy_title=(TextView) findViewById(R.id.addAy_title);
		navAy_backBt=(Button) findViewById(R.id.navAy_backBt);
		
		Intent preIntent=getIntent();
		num=preIntent.getLongExtra("zdNum", 0l);
		initData();
		
		registerListener();
	}
	private int getPosition(String text,String[] texts){
		int position=0;
		if(texts!=null&&text!=null){
			for(int i=0;i<texts.length;i++){
				if(text.equals(texts[i])){
					position=i;
					break;
				}
			}
		}
		return position;
	}
	private void initData(){
		//Toast.makeText(getApplicationContext(), ""+num, Toast.LENGTH_LONG).show();
		if(num!=0){
			addAy_title.setText("编辑账单-"+num);
		}
		//根据账单num获取一个账单
		ZhangDanDao zdd=new ZhangDanDao(getApplicationContext());
		zhangDan=zdd.findByNum(num);//查询已经确认的账单
		if(zhangDan==null){//没找到则查找一个初始态的账单
			zhangDan=zdd.findByStatus(0);
			if(zhangDan==null){//没有则查找最近一个账单
				zhangDan=zdd.findLast();
				if(zhangDan==null){
					zhangDan=new ZhangDan();
				}
				zhangDan.status=0;//初始化账单
				zhangDan.rq=new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
				zhangDan.je=0;
				zhangDan.jemx="";
				zhangDan.szbz="未算账";
				zdd.insert(zhangDan);//插入到数据库
			}else if(zhangDan.je==0){
				zhangDan.rq=new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
				zhangDan.jemx="";
				zdd.update(zhangDan);//更新到数据库
			}
		}
		if("".equals(zhangDan.jemx)&&zhangDan.je!=0){
			zhangDan.jemx=zhangDan.je+",";
		}
		System.out.println("test-zhangDan-->"+zhangDan.toString());
		
		//付款人
		CanShuDao csd=new CanShuDao(getApplicationContext());
		List<CanShu> csList=csd.findAllByLb(CanShuLbEnum.fkr);
		if(csList.isEmpty()){
			Toast.makeText(getApplicationContext(), "缺少系统参数[付款人]，请添加或同步", Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		int size=csList.size();
		fkrTexts=new String[size];
		for(int i=0;i<size;i++){
			fkrTexts[i]=csList.get(i).xx;
		}
		ArrayAdapter<String> fkrTextsAdapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, fkrTexts);
		fkrTextsAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
		addAy_fkrSp.setAdapter(fkrTextsAdapter);
		int position=getPosition(zhangDan.fkr,fkrTexts);
		addAy_fkrSp.setSelection(position);
		if(position==0){
			zhangDan.fkr=fkrTexts[0];
		}
		
		//用途
		csList=csd.findAllByLb(CanShuLbEnum.yt);
		if(csList.isEmpty()){
			Toast.makeText(getApplicationContext(), "缺少系统参数[用途]，请添加或同步", Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		size=csList.size();
		ytTexts=new String[size];
		for(int i=0;i<size;i++){
			ytTexts[i]=csList.get(i).xx;
		}
		ArrayAdapter<String> ytTextsAdapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, ytTexts);
		ytTextsAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
		addAy_ytSp.setAdapter(ytTextsAdapter);
		position=getPosition(zhangDan.yt,ytTexts);
		addAy_ytSp.setSelection(position);
		if(position==0){
			zhangDan.yt=ytTexts[0];
		}
		
		//日期
		addAy_rqBt.setText(zhangDan.rq);
		
		//用钱人
		addAy_yqrBt.setText((zhangDan.yqr==null||"".equals(zhangDan.yqr))?"点击添加":zhangDan.yqr);
		csd=new CanShuDao(getApplicationContext());
		csList=csd.findAllByLb(CanShuLbEnum.yqr);
		if(csList.isEmpty()){
			Toast.makeText(getApplicationContext(), "缺少系统参数[用钱人]，请添加或同步", Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		size=csList.size();
		yqrTexts=new String[size];
		for(int i=0;i<size;i++){
			yqrTexts[i]=csList.get(i).xx;
		}
		
		//备注
		addAy_bzEt.setText(zhangDan.bz==null?"":zhangDan.bz);
		
		//金额
		addAy_jeEt.setText(zhangDan.je+"");
		
		//金额明细
		addAy_jemxBt.setText((zhangDan.jemx==null||"".equals(zhangDan.jemx))?"点击添加":zhangDan.jemx);
	}
	//更新账单到数据库
	private void updateZdRT(){
		if(num==0){
			ZhangDanDao zdd=new ZhangDanDao(getApplicationContext());
			zdd.update(zhangDan);
		}
	}
	private void registerListener(){
		addAy_fkrSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				zhangDan.fkr=fkrTexts[arg2];
				updateZdRT();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		addAy_ytSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				zhangDan.yt=ytTexts[arg2];
				updateZdRT();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		addAy_rqBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
				OnDateSetListener onDateSetListener=new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker datepicker, int i, int j, int k) {
						// TODO Auto-generated method stub
						Date date=new Date(i-1900,j,k);
						//Toast.makeText(getApplicationContext(), "date-->"+date.toLocaleString(), Toast.LENGTH_LONG).show();
						zhangDan.rq=sdf.format(date);
						addAy_rqBt.setText(zhangDan.rq);
						updateZdRT();
					}
				}; 
				Date date=new Date();
				try{
					date=sdf.parse(addAy_rqBt.getText().toString());
				}catch(Exception ex){ex.printStackTrace();}
				DatePickerDialog dpd=new DatePickerDialog(AddActivity.this, onDateSetListener, 1900+date.getYear(), date.getMonth(), date.getDate());
				dpd.show();
			}
		});
		addAy_yqrBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				//String[] yqrSelected=zhangDan.yqr.split("\\|");
				final boolean[] selected=new boolean[yqrTexts.length];
				for(int i=0;i<selected.length;i++){
					if(zhangDan.yqr!=null&&zhangDan.yqr.indexOf(yqrTexts[i])!=-1){
						selected[i]=true;
					}else{
						selected[i]=false;
					}
				}
				AlertDialog.Builder dialog=new AlertDialog.Builder(AddActivity.this);
				dialog.setTitle("请选择用钱人");
				dialog.setMultiChoiceItems(yqrTexts, selected, new DialogInterface.OnMultiChoiceClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						// TODO Auto-generated method stub
						selected[which]=isChecked;
					}
				});
				dialog.setPositiveButton("确定", new DialogInterface.OnClickListener () {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StringBuffer yqrText=new StringBuffer("");
						for(int i=0;i<yqrTexts.length;i++){
							if(selected[i]){
								yqrText.append(yqrTexts[i]);
								yqrText.append("|");
							}
						}
						zhangDan.yqr=yqrText.toString();
						addAy_yqrBt.setText((zhangDan.yqr==null||"".equals(zhangDan.yqr))?"点击添加":zhangDan.yqr);
						updateZdRT();
					}
				});
				dialog.setNegativeButton("取消", null);
				dialog.show();
			}
		});
		addAy_bzEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				zhangDan.bz=addAy_bzEt.getText().toString();
				updateZdRT();
			}
		});
		addAy_jeEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addAy_jemxBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater layoutInflater=getLayoutInflater();
				View layout=layoutInflater.inflate(R.layout.dialog_jemx, null);
				AlertDialog.Builder dialog=new AlertDialog.Builder(AddActivity.this);
				dialog.setTitle("金额明细");
				dialog.setView(layout);
				jemxLv=(ListView) layout.findViewById(R.id.jemxDialog_jesLv);
				final EditText et=(EditText) layout.findViewById(R.id.jemxDialog_newjeEt);
				loadJemxData();
				dialog.setPositiveButton("确定", new DialogInterface.OnClickListener () {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						jemxLv=null;
						double je=0;
						if(jemxDataList!=null){
							int size=jemxDataList.size();
							if(size==0){
								zhangDan.jemx="";
							}else{
								zhangDan.jemx="";
								for(int i=0;i<size;i++){
									zhangDan.jemx+=jemxDataList.get(i)+",";
									double jetemp=0.0;
									try{jetemp=Double.parseDouble(jemxDataList.get(i));}catch(Exception ex){}
									je+=jetemp;
								}
							}
						}
						System.out.println("zhangDan.jemx-->"+zhangDan.jemx);
						jemxDataList=null;
						if(et.getText()!=null&&!"".equals(et.getText().toString())){
							double jetemp=0.0;
							try{jetemp=Double.parseDouble(et.getText().toString());}catch(Exception ex){}
							if(zhangDan.jemx==null){
								zhangDan.jemx="";
							}
							zhangDan.jemx+=jetemp+",";
							je+=jetemp;
						}
//						if("".equals(zhangDan.jemx)){
//							zhangDan.jemx=null;
//						}
						
						//保留2位小数
						BigDecimal bg = new BigDecimal(je);
						je = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

						
						System.out.println("zhangDan.jemx-->"+zhangDan.jemx);
						
						addAy_jemxBt.setText((zhangDan.jemx==null||"".equals(zhangDan.jemx))?"点击添加":zhangDan.jemx);
						zhangDan.je=je;
						addAy_jeEt.setText(je+"");
						updateZdRT();
					}
				});
				dialog.show();
			}
		});
		navAy_backBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddActivity.this.finish();
			}
		});
		addAy_qxBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddActivity.this.finish();
			}
		});
		addAy_qdBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(zhangDan.fkr==null||"".equals(zhangDan.fkr)){
					Toast.makeText(getApplicationContext(), "付款人不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if(zhangDan.yt==null||"".equals(zhangDan.yt)){
					Toast.makeText(getApplicationContext(), "用途不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if(zhangDan.rq==null||"".equals(zhangDan.rq)){
					Toast.makeText(getApplicationContext(), "日期不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if(zhangDan.yqr==null||"".equals(zhangDan.yqr)){
					Toast.makeText(getApplicationContext(), "用钱人不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if(zhangDan.je!=0){
					zhangDan.status=1;
					YongHuDao yhd=new YongHuDao(getApplicationContext());
					YongHu yh=yhd.findLast();
					if(yh!=null){
						zhangDan.jzr=yh.yhm;
					}
					ZhangDanDao zdd=new ZhangDanDao(getApplicationContext());
					int recode=zdd.update(zhangDan);
					if(recode==1){
						zhangDan.status=0;
						Toast.makeText(getApplicationContext(), "已存在相同记录，请修改后重试", Toast.LENGTH_LONG).show();
						return;
					}
					if(recode==-1){
						zhangDan.status=0;
						Toast.makeText(getApplicationContext(), "保存数据失败，请重试", Toast.LENGTH_LONG).show();
						return;
					}
					AddActivity.this.finish();
				}else{
					Toast.makeText(getApplicationContext(), "金额不能为0", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	void loadJemxData(){
		//添加数据
		if(zhangDan.jemx!=null&&!zhangDan.jemx.trim().equals("")&&jemxLv!=null){
			ArrayList<HashMap<String,Object>> list=null;
			if(jemxDataList==null){
				String[] jemxs=zhangDan.jemx.split(",");
				jemxDataList=new ArrayList<String>();
				list=new ArrayList<HashMap<String,Object>>(); 
				for(int i=0;i<jemxs.length;i++){
					HashMap hm=new HashMap();
					hm.put("jemxLi_jeTv", new SpannableString(jemxs[i]));
					hm.put("jemxLi_scBt", new ItemBtOnClickListener(i));
					list.add(hm);
					jemxDataList.add(jemxs[i]);
				}
			}else{
				int size=jemxDataList.size();
				list=new ArrayList<HashMap<String,Object>>(); 
				for(int i=0;i<size;i++){
					HashMap hm=new HashMap();
					hm.put("jemxLi_jeTv", new SpannableString(jemxDataList.get(i)));
					hm.put("jemxLi_scBt", new ItemBtOnClickListener(i));
					list.add(hm);
				}
			}
			
			BListAdapter bla=new BListAdapter(AddActivity.this, jemxLv, list, R.layout.lvitem_jemx, 
					new String[]{"jemxLi_jeTv","jemxLi_scBt"}, new int[]{R.id.jemxLi_jeTv,R.id.jemxLi_scBt});
			jemxLv.setAdapter(bla);
			
		}
	}
	class ItemBtOnClickListener implements OnClickListener{
		int index=-1;
		ItemBtOnClickListener(int index){
			this.index=index;
		}
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			jemxDataList.remove(index);
			loadJemxData();
		}
		
	}
}
