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
		
		//去掉标题
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
				}//休眠3秒
				Intent intent=new Intent();
				intent.setClass(WelcomActivity.this, UserActivity.class);
				startActivity(intent);
				WelcomActivity.this.finish();
			}
		}).start();
		
		//启动更新服务
		StaticParams.activity=this;//启动检查更新的服务的活动,必须设置，不然报错
		StaticParams.CURRENT_PACKAGE="com.lj.jz";//app的包名,必须设置，不然报错
		StaticParams.URL_VERSIONINFO="http://121.40.148.140/ljjz/version.txt";//版本信息访问地址
		StaticParams.URL_UPDATEFILEPATH="http://121.40.148.140/ljjz/ljjz.apk";//app下载地址
		StaticParams.UPDATE_SOFTWARE_FILE_NAME="ljjz.apk";//下载的最新app的保存的文件名
		StaticParams.UPDATE_SOFTWARE_DIR_NAME="ljjzV/";//下载的最新的app的保存目录
		StaticParams.appMsgTitle="灵久AA记账";
		Intent intent=new Intent();
		//intent.setClass(getApplicationContext(), VersionService.class);
		intent.setClass(getApplicationContext(), VersionServiceNew.class);//新版本
		startService(intent);//启动一个更新服务
		
	}
	
}
