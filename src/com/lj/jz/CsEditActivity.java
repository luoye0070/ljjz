package com.lj.jz;

import com.lj.jz.data.CanShu;
import com.lj.jz.data.CanShuDao;
import com.lj.jz.data.CanShuLbEnum;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class CsEditActivity extends Activity {
	Spinner ceAy_lbSp=null;
	EditText ceAy_xxEt=null;
	Button ceAy_qxBt=null;
	Button ceAy_qdBt=null;
	
	
	long num=0;
	CanShu canShu=null;
	String [] lbTexts=null;
	
	Button navAy_backBt=null;
	TextView ceAy_title=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//去掉标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_csedit);
		
		ceAy_lbSp=(Spinner) findViewById(R.id.ceAy_lbSp);
		ceAy_xxEt=(EditText) findViewById(R.id.ceAy_xxEt);
		ceAy_qxBt=(Button) findViewById(R.id.ceAy_qxBt);
		ceAy_qdBt=(Button) findViewById(R.id.ceAy_qdBt);
		navAy_backBt=(Button) findViewById(R.id.navAy_backBt);
		ceAy_title=(TextView) findViewById(R.id.ceAy_title);
		
		Intent preIntent=getIntent();
		num=preIntent.getLongExtra("csNum", 0);
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
			ceAy_title.setText("编辑参数-"+num);
		}
		//根据账单num获取一个账单
		CanShuDao csd=new CanShuDao(getApplicationContext());
		canShu=csd.findByNum(num);//查询参数
		if(canShu==null){//没找到则创建一个
			canShu=new CanShu();
		}
		
		//类别
		lbTexts=new String[]{CanShuLbEnum.fkr,CanShuLbEnum.yqr,CanShuLbEnum.yt};
		ArrayAdapter<String> lbTextsAdapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, lbTexts);
		lbTextsAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
		ceAy_lbSp.setAdapter(lbTextsAdapter);
		int position=getPosition(canShu.lb,lbTexts);
		ceAy_lbSp.setSelection(position);
		if(position==0){
			canShu.lb=lbTexts[0];
		}		
		
		//选项
		ceAy_xxEt.setText(canShu.xx+"");
	
	}
	
	private void registerListener(){
		ceAy_lbSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				canShu.lb=lbTexts[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		ceAy_xxEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				
			}
		});
		
		navAy_backBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CsEditActivity.this.finish();
			}
		});
		
		ceAy_qxBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CsEditActivity.this.finish();
			}
		});
		ceAy_qdBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(canShu.lb==null||"".equals(canShu.lb)){
					Toast.makeText(getApplicationContext(), "类别不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				canShu.xx=ceAy_xxEt.getText().toString().trim();
				if(canShu.xx==null||"".equals(canShu.xx)){
					Toast.makeText(getApplicationContext(), "选项不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				
				CanShuDao csd=new CanShuDao(getApplicationContext());
				int recode=0;
				if(canShu.num==0){
					recode=csd.insert(canShu);
				}else{
					recode=csd.update(canShu);
				}
				if(recode==1){
					Toast.makeText(getApplicationContext(), "已存在相同记录，请修改后重试", Toast.LENGTH_LONG).show();
					return;
				}
				if(recode==-1){
					Toast.makeText(getApplicationContext(), "保存数据失败，请重试", Toast.LENGTH_LONG).show();
					return;
				}
				
				Toast.makeText(getApplicationContext(), "保存数据成功", Toast.LENGTH_LONG).show();
				CsEditActivity.this.finish();
			}
		});
	}
}
