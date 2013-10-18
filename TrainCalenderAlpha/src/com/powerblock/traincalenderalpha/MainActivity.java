package com.powerblock.traincalenderalpha;

import java.util.Calendar;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements DatePickerFragment.parentCommunicateInterface {
	
	private Button bChangeDate;
	private TextView dateShow;
	private DatabaseHandler dbHandler;
	
	private int year;
	private int month;
	private int day;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHandler = new DatabaseHandler(this);
		setContentView(R.layout.activity_main);
		setCurrentDate();
		addListenerToButton();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(!(bChangeDate instanceof View.OnClickListener)){
			addListenerToButton();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public String TrainDatePick(View v){
		return null;
		
	}
	
	public void addListenerToButton(){
		bChangeDate = (Button) findViewById(R.id.button1);
		dateShow = (TextView) findViewById(R.id.textView1);
		bChangeDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createDialog();
			}
		});
	}
	
	public void setCurrentDate(){
		dateShow = (TextView) findViewById(R.id.textView1);
		final Calendar c = Calendar.getInstance();
		
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		dateShow.setText(new StringBuilder()
						.append(day).append("-").append(month + 1).append("-").append(year).append(" "));
		calculateTrainTime(year, month, day);
	}
	
	public void createDialog(){
		DialogFragment newFrag = new DatePickerFragment();
		newFrag.show(getSupportFragmentManager(), "datePicker");
	}
	
	
	public void calculateTrainTime(int givenYear, int givenMonth, int givenDay){
		ContentValues values = dbHandler.getDate(givenYear);
		if(values.getAsInteger(DatabaseHandler.KEY_YEAR) == -1){
			Toast.makeText(this, String.valueOf(givenYear) + " is not a supported year", Toast.LENGTH_LONG).show();
			return;
		}
		int startMonth = values.getAsInteger(DatabaseHandler.KEY_MONTH);
		int startDay = values.getAsInteger(DatabaseHandler.KEY_DAY);
		int startYear = values.getAsInteger(DatabaseHandler.KEY_YEAR);
		
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.YEAR, startYear);
		startCal.set(Calendar.MONTH, startMonth - 1);
		startCal.set(Calendar.DAY_OF_MONTH, startDay);
		int startWeekOfYear = startCal.get(Calendar.WEEK_OF_YEAR);
		Log.v(toString(),String.valueOf(startWeekOfYear));
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, givenYear);
		cal.set(Calendar.MONTH, givenMonth);
		cal.set(Calendar.DAY_OF_MONTH, givenDay);
		int trainWeekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
		int trainDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		trainDayOfWeek += 1;
		if(trainDayOfWeek > 7){
			trainDayOfWeek -= 7;
			trainWeekOfYear +=1;
		}
		
		int weekOfYear = trainWeekOfYear - startWeekOfYear;
		
		StringBuilder builder = new StringBuilder();
		builder.append("Week: ").append(weekOfYear).append(" Day: ").append(trainDayOfWeek);
		String trainTime = builder.toString();
		
		TextView tv = (TextView) findViewById(R.id.textView2);
		tv.setText(trainTime);
	}


}
