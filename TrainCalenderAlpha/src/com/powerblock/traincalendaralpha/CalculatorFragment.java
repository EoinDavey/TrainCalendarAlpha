package com.powerblock.traincalendaralpha;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.powerblock.traincalendaralpha.R;

public class CalculatorFragment extends Fragment implements DatePickerFragment.parentCommunicateInterface {
	
	private ActionBarActivity mParent;
	private Button bChangeDate;
	private Button bTrainDate;
	private TextView mDateShow;
	private TextView mTrainDateShow;
	//private TextView mPeriodShow;
	private DatabaseHandler mDbHandler;
	private EditText mYearEditText;
	private EditText mDayEditText;
	private EditText mWeekEditText;
	
	public CalculatorFragment(){
		
	}
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View layout =  inflater.inflate(R.layout.calculator_test_layout, container, false);
		bChangeDate = (Button) layout.findViewById(R.id.button1);
		bTrainDate = (Button) layout.findViewById(R.id.button2);
		mDateShow = (TextView) layout.findViewById(R.id.textView1);
		mTrainDateShow = (TextView) layout.findViewById(R.id.textView2);
		//mPeriodShow = (TextView) layout.findViewById(R.id.textView3);
		//mArrowsView = (ImageView) layout.findViewById(R.id.arrows_image);
		mDbHandler = new DatabaseHandler(mParent);
		setCurrentDate();
		addListenersToButtons();
		return layout;
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mParent = (ActionBarActivity) activity;
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	private void setCurrentDate(){
		final Calendar c = Calendar.getInstance();
		
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		mDateShow.setText(new StringBuilder().append(day).append("-").append(month+1).append("-").append(year).toString());
		calculateTrainTime(year, month, day);
		//mArrowsView.setImageResource(R.drawable.arrows_off);
		
	}
	
	private void addListenersToButtons(){
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
	
	private void createDateDialog(){
		DialogFragment newFrag = new DatePickerFragment(this);
		newFrag.show(mParent.getSupportFragmentManager(), "datePicker");
	}
	
	private void createTrainWeekDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
		final View v = mParent.getLayoutInflater().inflate(R.layout.numberpickerdialog, null);
		builder.setTitle("Enter Week and Day");
		builder.setView(v).setPositiveButton("Set", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mWeekEditText = (EditText) v.findViewById(R.id.editText1);   
				mDayEditText = (EditText) v.findViewById(R.id.editTextDays); 
				mYearEditText = (EditText) v.findViewById(R.id.yearEditText);
				int weekNo = Integer.parseInt(mWeekEditText.getText().toString());
				int dayNo = Integer.parseInt(mDayEditText.getText().toString());
				int year = Integer.parseInt(mYearEditText.getText().toString());
				if(weekNo > 52 || dayNo > 7){
					Toast.makeText(mParent.getApplicationContext(), "Unsupported date", Toast.LENGTH_SHORT).show();
					return;
				}
				calculateRealTime(year, weekNo, dayNo);
				mTrainDateShow.setText(new StringBuilder().append("Week: " ).append(weekNo).append("  Day: ").append(dayNo).append("  Period:").append(calculatePeriodAndWeek(weekNo)).toString());
			}
			
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
	
@SuppressLint("SimpleDateFormat")
	public void calculateRealTime(int givenYear, int givenWeek, int givenDay){
		
		if(givenWeek > 52 || givenDay > 7){
			return;
		}

		ContentValues values = mDbHandler.getDate(givenYear);
		if(values.getAsInteger(DatabaseHandler.KEY_DAY) == -1){
			Toast.makeText(mParent, "This Year is not supported", Toast.LENGTH_LONG).show();
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
		
		//mArrowsView.setImageResource(R.drawable.arrows_up);
		
		mDateShow.setText(dateString);
	}

	@Override
	public void calculateTrainTime(int givenYear, int givenMonth, int givenDay) {
		ContentValues values = mDbHandler.getDate(givenYear);
		if(values.getAsInteger(DatabaseHandler.KEY_YEAR) == -1){
			Toast.makeText(mParent, String.valueOf(givenYear) + " is not a supported year", Toast.LENGTH_LONG).show();
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
		
		String period = calculatePeriodAndWeek(weekOfYear);
		StringBuilder builder = new StringBuilder();
		builder.append("Week: ").append(weekOfYear).append("  Day: ").append(trainDayOfWeek).append("  Period: ").append(period);
		String trainTime = builder.toString();
		
		//mArrowsView.setImageResource(R.drawable.arrows_down);
		
		mTrainDateShow.setText(trainTime);
		
	}
	
	private String calculatePeriodAndWeek(int weekNo){
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
		//mPeriodShow.setText("Period: " + String.valueOf(periodNum) + "/" + String.valueOf(result));
		Toast.makeText(mParent, new StringBuilder().append(periodNum).append("/").append(result).toString(), Toast.LENGTH_SHORT).show();
		Log.v("PeriodAndDay Result", String.valueOf(periodNum) + "/" + String.valueOf(result));
		return String.valueOf(periodNum) + "/" + String.valueOf(result);
	}

}
