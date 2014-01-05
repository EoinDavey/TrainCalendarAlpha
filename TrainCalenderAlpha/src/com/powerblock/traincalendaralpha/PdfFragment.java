package com.powerblock.traincalendaralpha;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.powerblock.traincalenderalpha.R;

public class PdfFragment extends Fragment {
	
	private WebView mWebView;
	private ActionBarActivity mParent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.pdf_webview_layout, container, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		mWebView = (WebView) mParent.findViewById(R.id.web_view);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSupportZoom(true);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mParent = (ActionBarActivity) activity;
	}
	
	
}
