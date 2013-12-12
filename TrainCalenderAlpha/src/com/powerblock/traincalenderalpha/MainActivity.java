package com.powerblock.traincalenderalpha;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends ActionBarActivity implements DatePickerFragment.parentCommunicateInterface {
	
	private Button bChangeDate;
	private TextView dateShow;
	private DatabaseHandler dbHandler;
	private Button bTrainDate;
	private EditText yearEditText;
	private EditText dayEditText;
	private EditText weekEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setCustomView(getLayoutInflater().inflate(R.layout.actionbar_image_view, null));
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
		return false;
	}
	
	public String TrainDatePick(View v){
		return null;
		
	}
	
	public void addListenerToButton(){
		bChangeDate = (Button) findViewById(R.id.button1);
		bTrainDate = (Button) findViewById(R.id.button2);
		dateShow = (TextView) findViewById(R.id.textView1);
		bChangeDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createDateDialog();
			}
		});
		bTrainDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createTrainWeekDialog();
			}
		});
	}
	
	public void setCurrentDate(){
		dateShow = (TextView) findViewById(R.id.textView1);
		final Calendar c = Calendar.getInstance();
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		dateShow.setText(new StringBuilder()
						.append(day).append("-").append(month + 1).append("-").append(year).toString());
		calculateTrainTime(year, month, day);
	}
	
	public void createDateDialog(){
		DialogFragment newFrag = new DatePickerFragment();
		newFrag.show(getSupportFragmentManager(), "datePicker");
	}
	
	public void createTrainWeekDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View v = getLayoutInflater().inflate(R.layout.numberpickerdialog, null);
		builder.setTitle("Enter Week and Day");
		builder.setView(v).setPositiveButton("Set", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				weekEditText = (EditText) v.findViewById(R.id.editText1);   
				dayEditText = (EditText) v.findViewById(R.id.editTextDays); 
				yearEditText = (EditText) v.findViewById(R.id.yearEditText);
				weekEditText.getText().toString();
				int weekNo = Integer.parseInt(weekEditText.getText().toString());
				int dayNo = Integer.parseInt(dayEditText.getText().toString());
				int year = Integer.parseInt(yearEditText.getText().toString());
				if(weekNo > 52 || dayNo > 7){
					Toast.makeText(getApplicationContext(), "Unsupported date", Toast.LENGTH_SHORT).show();
					return;
				}
				calculateRealTime(year, weekNo, dayNo);
				TextView weekText = (TextView) findViewById(R.id.textView2);
				weekText.setText(new StringBuilder().append("Week: " ).append(weekNo).append(" Day: ").append(dayNo).toString());
			}
			
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
		
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
		
		Calendar startCal = Calendar.getInstance(Locale.US);
		startCal.clear();
		startCal.set(Calendar.YEAR, startYear);
		startCal.set(Calendar.MONTH, startMonth - 1);
		startCal.set(Calendar.DAY_OF_MONTH, startDay);
		int startWeekOfYear = startCal.get(Calendar.WEEK_OF_YEAR);
		Log.v(toString(),String.valueOf(startWeekOfYear));
		
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.clear();
		cal.set(Calendar.YEAR, givenYear);
		cal.set(Calendar.MONTH, givenMonth);
		cal.set(Calendar.DAY_OF_MONTH, givenDay);
		
		int trainWeekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
		int trainDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);  
		
		switch(trainDayOfWeek){
		case 1:
			//Sunday
			trainDayOfWeek = 2;
			break;
		case 2:
			//Monday
			trainDayOfWeek = 3;
			break;
		case 3:
			//Tueday
			trainDayOfWeek = 4;
			break;
		case 4:
			//Wednesday
			trainDayOfWeek = 5;
			break;
		case 5:
			//Thursday
			trainDayOfWeek = 6;
			break;
		case 6:
			//Friday
			trainDayOfWeek = 7;
			break;
		case 7:
			trainDayOfWeek = 1;
			trainWeekOfYear +=1;
		}
		
		int weekOfYear = trainWeekOfYear - startWeekOfYear;
		
		if(cal.compareTo(startCal) == -1){
			weekOfYear += 52;
		}
		
		calculatePeriodAndDay(weekOfYear);
		StringBuilder builder = new StringBuilder();
		builder.append("Week: ").append(weekOfYear).append(" Day: ").append(trainDayOfWeek);
		String trainTime = builder.toString();
		
		TextView tv = (TextView) findViewById(R.id.textView2);
		tv.setText(trainTime);
	}

	public void calculateRealTime(int givenYear, int givenWeek, int givenDay){
		
		if(givenWeek > 52 || givenDay > 7){
			return;
		}

		ContentValues values = dbHandler.getDate(givenYear);
		if(values.getAsInteger(DatabaseHandler.KEY_DAY) == -1){
			Toast.makeText(this, "This Year is not supported", Toast.LENGTH_LONG).show();
			return;
		}
		int startMonth = values.getAsInteger(DatabaseHandler.KEY_MONTH);
		int startDay = values.getAsInteger(DatabaseHandler.KEY_DAY);
		int startYear = values.getAsInteger(DatabaseHandler.KEY_YEAR);
		Log.v("calculateRealTime", new StringBuilder().append("Month: ").append(startMonth).append(" Day: ").append(startDay).toString());
		
		Calendar startCal = Calendar.getInstance(Locale.US);
		startCal.clear();
		startCal.set(Calendar.MONTH, startMonth - 1);
		startCal.set(Calendar.DAY_OF_MONTH, startDay);
		startCal.set(Calendar.YEAR, startYear);
		int startWeekOfYear = startCal.get(Calendar.WEEK_OF_YEAR);
		Log.v("calculateRealTime", new StringBuilder().append(startWeekOfYear).toString());
		
		switch(givenDay){
		case 1:
			//Saturday
			givenDay = 7;
			givenWeek -= 1;
			break;
		case 2:
			//Sunday
			givenDay = 1;
			break;
		case 3:
			//Monday
			givenDay = 2;
			break;
		case 4:
			//Tuesday
			givenDay = 3;
			break;
		case 5:
			//Wednesday
			givenDay = 4;
			break;
		case 6:
			//Thursday
			givenDay = 5;
			break;
		case 7:
			//Friday
			givenDay = 6;
			break;
		}
		
		int realWeekOfYear = givenWeek + startWeekOfYear;
		if(realWeekOfYear > 52){
			realWeekOfYear -= 52;
			givenYear += 1;
		}

	
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.clear();
		cal.set(Calendar.WEEK_OF_YEAR, realWeekOfYear);
		cal.set(Calendar.DAY_OF_WEEK, givenDay);
		cal.set(Calendar.YEAR, givenYear);
		Date result = cal.getTime();
	
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String dateString = df.format(result);
		Log.v("dateString",dateString);
		
		TextView dateShow = (TextView) findViewById(R.id.textView1);
		dateShow.setText(dateString);
	}
	
	public void calculatePeriodAndDay(int weekNo){
		
		int result;
		
		//gets the period number
		int periodNum = 1;
		int weekNoForPeriod = weekNo - 1;
		int base = 4;
		while(true){
			if(weekNoForPeriod < base){
				break;
			} else {
				periodNum += 1;
				base += 4;		
			}
		}
		
		//gets the day number in the period
		int periodForCalc = periodNum - 1;
		int baseMod = periodForCalc * 4;
		if(baseMod == 0){
			result = weekNo;
		} else {
			result = weekNo % baseMod;
		}
		Toast.makeText(this, new StringBuilder().append(periodNum).append("/").append(result).toString(), Toast.LENGTH_SHORT).show();
		Log.v("PeriodAndDay Result", String.valueOf(periodNum) + "/" + String.valueOf(result));
	}
	
}