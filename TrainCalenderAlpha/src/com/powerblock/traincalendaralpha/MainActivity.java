package com.powerblock.traincalendaralpha;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.powerblock.traincalenderalpha.R;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends ActionBarActivity{
	
	private String[] mTitles;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;

	private FragmentManager mFragManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(getLayoutInflater().inflate(R.layout.actionbar_image_view, null));
		setContentView(R.layout.activity_main);
		
		mFragManager = getSupportFragmentManager();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		mTitles = getResources().getStringArray(R.array.navArray);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mTitle = mDrawerTitle = getTitle();
		
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mTitles));
		
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close){
			
			public void onDrawerClosed(View view){
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView){
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}
			
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		addFragment();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	private void addFragment(){
		CalculatorFragment calcFrag = new CalculatorFragment();
		FragmentTransaction fragTransact = mFragManager.beginTransaction();
		fragTransact.add(R.id.fragment_parent, calcFrag);
		fragTransact.commit();
	}
	
	public void selectFrag(int position){
		FragmentTransaction fragTransact = mFragManager.beginTransaction();
		Fragment frag = null;
		CharSequence title = "";
		switch(position){
			case 0:
				frag = new CalculatorFragment();
				title = "Calculator";
				break;
			case 1:
				frag = new PdfFragment();
				title = "Calendar";
				break;
			default:
				break;
		}
		fragTransact.replace(R.id.fragment_parent, frag).commit();
		
		mDrawerList.setItemChecked(position, true);
		setTitle(title);
		mDrawerLayout.closeDrawer(mDrawerList);
		
	}
	
	@Override
	public void setTitle(CharSequence title){
		mTitle = title;
		getSupportActionBar().setTitle(title);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectFrag(position);
			
		}
		
	}
	
}