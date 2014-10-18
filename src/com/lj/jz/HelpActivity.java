package com.lj.jz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

public class HelpActivity extends Activity {
	Button navAy_backBt=null;
	WebView hAy_docWv=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//»•µÙ±ÍÃ‚
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_help);
		
		navAy_backBt=(Button) findViewById(R.id.navAy_backBt);
		navAy_backBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HelpActivity.this.finish();
			}
		});
		hAy_docWv=(WebView) findViewById(R.id.hAy_docWv);
		hAy_docWv.loadUrl("file:///android_asset/help.html");
	}
}
