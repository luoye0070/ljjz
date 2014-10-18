package com.lj.jz;

import com.lj.version.v.StaticParams;
import com.lj.version.v.VersionServiceNew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class WelcomActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//ȥ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_welcom);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//����3��
				Intent intent=new Intent();
				intent.setClass(WelcomActivity.this, UserActivity.class);
				startActivity(intent);
				WelcomActivity.this.finish();
			}
		}).start();
		
		//�������·���
		StaticParams.activity=this;//���������µķ���Ļ,�������ã���Ȼ����
		StaticParams.CURRENT_PACKAGE="com.lj.jz";//app�İ���,�������ã���Ȼ����
		StaticParams.URL_VERSIONINFO="http://121.40.148.140/ljjz/version.txt";//�汾��Ϣ���ʵ�ַ
		StaticParams.URL_UPDATEFILEPATH="http://121.40.148.140/ljjz/ljjz.apk";//app���ص�ַ
		StaticParams.UPDATE_SOFTWARE_FILE_NAME="ljjz.apk";//���ص�����app�ı�����ļ���
		StaticParams.UPDATE_SOFTWARE_DIR_NAME="ljjzV/";//���ص����µ�app�ı���Ŀ¼
		StaticParams.appMsgTitle="���AA����";
		Intent intent=new Intent();
		//intent.setClass(getApplicationContext(), VersionService.class);
		intent.setClass(getApplicationContext(), VersionServiceNew.class);//�°汾
		startService(intent);//����һ�����·���
		
	}
	
}
