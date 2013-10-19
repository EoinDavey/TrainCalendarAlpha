package com.powerblock.traincalenderalpha;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	
	public void getWeekAndDate(){
		/*EditText weekEditText = (EditText) findViewById(R.id.editText1);
		EditText dayEditText = (EditText) findViewById(R.id.editTextDays);
		EditText yearEditText = (EditText) findViewById(R.id.yearEditText);*/
		
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
		startCal.clear();
		startCal.set(Calendar.YEAR, startYear);
		startCal.set(Calendar.MONTH, startMonth - 1);
		startCal.set(Calendar.DAY_OF_MONTH, startDay);
		int startWeekOfYear = startCal.get(Calendar.WEEK_OF_YEAR);
		Log.v(toString(),String.valueOf(startWeekOfYear));
		
		Calendar cal = Calendar.getInstance();
		cal.clear();
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
		int startYear = values.getAsInteger(DatabaseHandler.KEY_YEAR);
		Log.v("calculateRealTime", new StringBuilder().append("Month: ").append(startMonth).append(" Day: ").append(startDay).toString());
		
		Calendar startCal = Calendar.getInstance();
		startCal.clear();
		startCal.set(Calendar.MONTH, startMonth - 1);
		startCal.set(Calendar.DAY_OF_MONTH, startDay);
		startCal.set(Calendar.YEAR, startYear);
		int startWeekOfYear = startCal.get(Calendar.WEEK_OF_YEAR);
		Log.v("calculateRealTime", new StringBuilder().append(startWeekOfYear).toString());
		
		switch(givenDay){
		case 1:
			givenDay = 7;
			givenWeek -= 1;
			break;
		case 2:
			givenDay = 1;
			break;
		case 3:
			givenDay = 2;
			break;
		case 4:
			givenDay = 3;
			break;
		case 5:
			givenDay = 4;
			break;
		case 6:
			givenDay = 5;
		case 7:
			givenDay = 6;
			break;
		}

		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.WEEK_OF_YEAR, givenWeek + startWeekOfYear);
		cal.set(Calendar.DAY_OF_WEEK, givenDay);
		cal.set(Calendar.YEAR, givenYear);
		//int givenWeekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
		Date result = cal.getTime();
		
		
		//Test
		Calendar testCal = Calendar.getInstance();
		testCal.clear();
		testCal.set(Calendar.YEAR, 2013);
		testCal.set(Calendar.WEEK_OF_YEAR, 25 + 1);
		testCal.set(Calendar.DAY_OF_WEEK, 1 + 1);
		Date testDate = testCal.getTime();
		DateFormat testDf = new SimpleDateFormat("dd-MM-yyyy");
		String testString = testDf.format(testDate);
		Log.v("Test",testString);

		
		
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String dateString = df.format(result);
		Log.v("dateString",dateString);
		
		TextView dateShow = (TextView) findViewById(R.id.textView1);
		dateShow.setText(dateString);
	}

}