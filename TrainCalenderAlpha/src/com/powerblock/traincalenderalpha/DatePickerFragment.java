package com.powerblock.traincalenderalpha;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	parentCommunicateInterface mParent;
	
	public interface parentCommunicateInterface{
		void calculateTrainTime(int year, int month, int day);
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		try{
			mParent = (parentCommunicateInterface) getActivity();
		} catch (ClassCastException e){
			throw new ClassCastException(getActivity().toString() + " must implement parentCommunicateInterface");
		}
		
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		mParent.calculateTrainTime(year, monthOfYear, dayOfMonth);
	}

}
