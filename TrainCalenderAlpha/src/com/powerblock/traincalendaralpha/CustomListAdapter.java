package com.powerblock.traincalendaralpha;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {
	
	private String[] mData;
	private Activity mActivity;
	private LayoutInflater inflater;
	
	public CustomListAdapter(Context context, int textViewResourceId){
		super(context, textViewResourceId);
	}
	
	public CustomListAdapter(Activity a, int resource, String[] items){
		super(a, resource, items);
		mData = items;
		mActivity = a;
		inflater = mActivity.getLayoutInflater();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		
		if(v == null){
			v = inflater.inflate(R.layout.drawer_list_item, parent, false);
		}
		
		String p = mData[position];
		
		if(p != null){
			
			TextView drawerTextView = (TextView) v.findViewById(R.id.drawerTextView);
			
			if(drawerTextView != null){
				Log.v("ArrayAdapter",p);
				if(p.equals("Railway Calendar")){
					Drawable drawable = mActivity.getResources().getDrawable(android.R.drawable.ic_menu_my_calendar);
					drawerTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
				} else if(p.equals("Date Calculator")){
					Drawable drawable = mActivity.getResources().getDrawable(android.R.drawable.ic_menu_month);
					drawerTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
				}
				drawerTextView.setText(p);
			}
		}
		
		return v;
	}

}
