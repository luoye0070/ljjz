package com.lj.jz;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class BListAdapter extends BaseAdapter {
	
	Activity contex=null;
	ListView listView=null;
	ArrayList<HashMap<String,Object>> list=null;
	int itemLayoutId=0;
	String[] hashKeys=null;
	int[] viewIds=null;
	
	
	public BListAdapter(Activity contex, ListView listView,
			ArrayList<HashMap<String, Object>> list, int itemLayoutId,
			String[] hashKeys, int[] viewIds) {
		super();
		this.contex = contex;
		this.listView = listView;
		this.list = list;
		this.itemLayoutId = itemLayoutId;
		this.hashKeys = hashKeys;
		this.viewIds = viewIds;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
		ArrayList<View> viewList=null;
		if(view==null){
			view=contex.getLayoutInflater().inflate(itemLayoutId, null);
			viewList=new ArrayList<View>();
			for(int i=0;i<viewIds.length;i++){
				View itemView=view.findViewById(viewIds[i]);
				viewList.add(itemView);
			}
			view.setTag(viewList);
		}else{
			viewList=(ArrayList<View>) view.getTag();
		}
		if(viewList!=null){
			//设置各控件的值
			int i_vs=viewList.size();
			for(int i=0;i<i_vs;i++){
				View itemView=viewList.get(i);
				if(itemView instanceof TextView){
					if(itemView instanceof Button){
						((Button)itemView).setOnClickListener((OnClickListener)list.get(position).get(hashKeys[i]));
					}else{
						TextView itemTextView=(TextView)itemView;
						itemTextView.setText((SpannableString)list.get(position).get(hashKeys[i]));
					}
				}
			}
		}
		
		return view;
	}

}
