package com.powerblock.traincalendaralpha;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class CustomWebView extends WebView {
	
	public CustomWebView(Context context) {
		super(context);
		activatePreferences();
	}
	
	public CustomWebView(Context context, AttributeSet attrs){
		super(context, attrs);
		activatePreferences();
	}
	
	public CustomWebView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		activatePreferences();
	}
	
	public void activatePreferences(){
		getSettings().setBuiltInZoomControls(true);
		getSettings().setSupportZoom(true);
	}
	
	/*@Override
	public boolean onTouchEvent(MotionEvent ev){
		if(ev.getAction() == MotionEvent.ACTION_DOWN ||
	            ev.getAction() == MotionEvent.ACTION_POINTER_DOWN){
			if(ev.getPointerCount() > 1){
				getSettings().setBuiltInZoomControls(true);
				getSettings().setSupportZoom(true);
			} else {
				getSettings().setBuiltInZoomControls(false);
				getSettings().setSupportZoom(false);
			}
		}
		return super.onTouchEvent(ev);
	}*/

}
