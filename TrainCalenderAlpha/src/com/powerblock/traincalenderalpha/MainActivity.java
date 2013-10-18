package com.powerblock.traincalenderalpha;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements DatePickerFragment.parentCommunicateInterface {
	
	private Button bChangeDate;
	private TextView dateShow;
	private DatabaseHandler dbHandler;
	private Button bTrainDate;
	private EditText yearEditText;
	private EditText dayEditText;
	private EditText weekEditText;
	
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
		
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		dateShow.setText(new StringBuilder()
						.append(day).append("-").append(month + 1).append("-").append(year).append(" "));
		calculateTrainTime(year, month, day);
	}
	
	public void createDateDialog(){
		DialogFragment newFrag = new DatePickerFragment();
		newFrag.show(getSupportFragmentManager(), "datePicker");
	}
	
	public void createTrainWeekDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View v = getLayoutInflater().inflate(R.layout.numberpickerdialog, null);
		builder.setTitle("Enter Week and Day");
		builder.setView(v).setPositiveButton("Set", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getWeekAndDate();
			}
			
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
	
	public void getWeekAndDate(){
		EditText weekEditText = (EditText) findViewById(R.id.editText1);
		EditText dayEditText = (EditText) findViewById(R.id.editTextDays);
		EditText yearEditText = (EditText) findViewById(R.id.yearEditText);
		int weekNo = Integer.parseInt(weekEditText.getText().toString());
		int dayNo = Integer.parseInt(dayEditText.getText().toString());
		int year = Integer.parseInt(yearEditText.getText().toString());
		calculateRealTime(year, weekNo, dayNo);
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

	public void calculateRealTime(int givenYear, int givenWeek, int givenDay){
		ContentValues values = dbHandler.getDate(givenYear);
		if(values.getAsInteger(DatabaseHandler.KEY_DAY) == -1){
			Toast.makeText(this, "This Year is not supported", Toast.LENGTH_LONG).show();
			return;
		}
		int startMonth = values.getAsInteger(DatabaseHandler.KEY_MONTH);
		int startDay = values.getAsInteger(DatabaseHandler.KEY_DAY);
		
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.MONTH, startMonth - 1);
		startCal.set(Calendar.DAY_OF_MONTH, startDay);
		startCal.set(Calendar.YEAR, givenYear);
		int startWeekOfYear = startCal.get(Calendar.WEEK_OF_YEAR);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.WEEK_OF_YEAR, givenWeek);
		cal.set(Calendar.DAY_OF_WEEK, givenDay);
		cal.set(Calendar.YEAR, givenYear);
		int givenWeekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
		
		int realWeek = startWeekOfYear + givenWeekOfYear;
		
		cal.set(Calendar.WEEK_OF_YEAR, realWeek);
		cal.set(Calendar.YEAR, givenYear);
		cal.set(Calendar.DAY_OF_MONTH, givenDay);
		
		TextView dateShow = (TextView) findViewById(R.id.textView1);
		dateShow.setText(new StringBuilder().append(givenDay).append("/").append(cal.get(Calendar.MONTH)).append("/").append(givenYear).toString());
		TextView weekText = (TextView) findViewById(R.id.textView2);
		weekText.setText(new StringBuilder().append("Week:" ).append(givenWeek).append(" Day: ").append(givenDay).toString());
	}

}
