package com.powerblock.traincalendaralpha;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.powerblock.traincalenderalpha.R;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	parentCommunicateInterface mParent;
	private Fragment mParentFrag;
	
	public interface parentCommunicateInterface{
		void calculateTrainTime(int year, int month, int day);
	}
	
	public DatePickerFragment(){
		throw new ClassCastException("parent must implement parentCommunicateInterface");
	}

	public DatePickerFragment(Fragment parent){
		mParentFrag = parent;
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		try{
			mParent = (parentCommunicateInterface) mParentFrag;
		} catch (ClassCastException e){
			throw new ClassCastException(getActivity().toString() + " must implement parentCommunicateInterface");
		}
		
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		mParent.calculateTrainTime(year, monthOfYear, dayOfMonth);
		TextView textView = (TextView) getActivity().findViewById(R.id.textView1);
		textView.setText(new StringBuilder().append(dayOfMonth).append("-").append(monthOfYear + 1).append("-").append(year).toString());
	}

}
