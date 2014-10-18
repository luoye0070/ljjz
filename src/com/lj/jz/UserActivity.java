package com.lj.jz;

import com.lj.jz.data.YongHu;
import com.lj.jz.data.YongHuDao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends Activity {

	TextView userAy_title=null;
	EditText userAy_nameEt=null;
	EditText userAy_pwEt=null;
	EditText userAy_rpwEt=null;
	LinearLayout userAy_rpwLl=null;
	CheckBox userAy_rememberPw=null;
	Button userAy_submitBt=null;
	TextView userAy_jtb=null;
	YongHu yh=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user);
		
		init();
		register();
	}
	private void init(){
		userAy_title=(TextView) findViewById(R.id.userAy_title);
		userAy_nameEt=(EditText) findViewById(R.id.userAy_nameEt);
		userAy_pwEt=(EditText) findViewById(R.id.userAy_pwEt);
		userAy_rpwEt=(EditText) findViewById(R.id.userAy_rpwEt);
		userAy_rpwLl=(LinearLayout) findViewById(R.id.userAy_rpwLl);
		userAy_rememberPw=(CheckBox) findViewById(R.id.userAy_rememberPw);
		userAy_submitBt=(Button) findViewById(R.id.userAy_submitBt);
		userAy_jtb=(TextView) findViewById(R.id.userAy_jtb);
		
		//����Ƿ�����û�
		YongHuDao yhd=new YongHuDao(getApplicationContext());
		yh=yhd.findLast();
		if(yh!=null){
			userAy_title.setText("�û���¼");
			userAy_rpwLl.setVisibility(View.GONE);
			userAy_nameEt.setText(yh.yhm);
			if("��".equals(yh.jzmm)){
				userAy_pwEt.setText(yh.mm);
				userAy_rememberPw.setChecked(true);
			}
			userAy_submitBt.setText("��¼");
		}
		
		//�����»���
		userAy_jtb.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //�»���
	}
	private void register(){
		userAy_submitBt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				YongHuDao yhd=new YongHuDao(getApplicationContext());
				if(yh==null){
					if(userAy_nameEt.getText().toString().equals("")){
						Toast.makeText(UserActivity.this, "�û�������Ϊ��!", Toast.LENGTH_LONG).show();
						return;
					}
					if(userAy_pwEt.getText().toString().equals("")){
						Toast.makeText(UserActivity.this, "���벻��Ϊ��!", Toast.LENGTH_LONG).show();
						return;
					}
					if(!userAy_rpwEt.getText().toString().equals(userAy_pwEt.getText().toString())){
						Toast.makeText(UserActivity.this, "������������벻һ��!", Toast.LENGTH_LONG).show();
						return;
					}
					yh=new YongHu();
					yh.yhm=userAy_nameEt.getText().toString();
					yh.mm=userAy_rpwEt.getText().toString();
					if(userAy_rememberPw.isChecked()){
						yh.jzmm="��";
					}else{
						yh.jzmm="��";
					}
					yhd.insert(yh);
				}else{
					if(!yh.yhm.equals(userAy_nameEt.getText().toString())){
						Toast.makeText(UserActivity.this, "�û�������ȷ!", Toast.LENGTH_LONG).show();
						return;
					}
					if(!yh.mm.equals(userAy_pwEt.getText().toString())){
						Toast.makeText(UserActivity.this, "���벻��ȷ!", Toast.LENGTH_LONG).show();
						return;
					}
					if(userAy_rememberPw.isChecked()){
						yh.jzmm="��";
					}else{
						yh.jzmm="��";						
					}
					yhd.update(yh);
				}
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				UserActivity.this.finish();
			}
		});
		
		userAy_jtb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(getApplicationContext(), UploadActivity.class);
				intent.putExtra("isOnlyTb", true);
				startActivity(intent);
				UserActivity.this.finish();
			}
		});
	}
	
}
