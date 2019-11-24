package com.example.jarvis;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class PTDialog extends AppCompatDialogFragment {
    private static final String TAG = "PopUp";

    //CODE
    private static final int START = 0;
    private static final int END = 0;

    private Calendar calendar;
    int year, month, dayofMonth, hour, minute;
    private SimpleDateFormat dateformat, timeformat;

    private Button Choose1, Choose2, Choose3, Choose4;
    private TextView tv1, tv2, tv3, tv4;

    //return values
    //return values
    private Date startdate, enddate;
    private Date starttime, endtime;

    //
    private PTDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_popup, null);

        builder.setView(view)
                .setTitle("Preferred Times")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(tv1.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(), "What day?", Toast.LENGTH_LONG).show();
                        }else if(tv2.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(), "What time?", Toast.LENGTH_LONG).show();
                        }else if(tv3.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(), "What day?", Toast.LENGTH_LONG).show();
                        }else if(tv4.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(), "What time?", Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                Date StartD = dateformat.parse(tv1.getText().toString());
                                Date StartT= timeformat.parse(tv2.getText().toString());
                                Date EndD = dateformat.parse(tv3.getText().toString());
                                Date EndT = timeformat.parse(tv4.getText().toString());
                                Toast.makeText(getActivity(), "Successfully created PT!", Toast.LENGTH_LONG).show();
                                listener.applyTexts(StartD, StartT, EndD, EndT);
                            }catch(ParseException e){
                                Log.e(TAG, "Parsing Date Failed", e);
                            }

                        }

                    }
                });

        Choose1 = view.findViewById(R.id.choosedate_pop_bttn_st);
        Choose2 = view.findViewById(R.id.choosetime_pop_bttn_st);
        Choose3 = view.findViewById(R.id.enddate_bttn);
        Choose4 = view.findViewById(R.id.endtime_bttn);

        tv1 = view.findViewById(R.id.tvDate_Pop_st);
        tv2 = view.findViewById(R.id.tvTime_Pop_st);
        tv3 = view.findViewById(R.id.tvDate_pop_end);
        tv4 = view.findViewById(R.id.tvTime_pop_end);

        dateformat = new SimpleDateFormat("yyyy-MM-dd");
        timeformat = new SimpleDateFormat("hh:mm");

        Choose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(tv1, START);
            }
        });
        Choose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePiccckerDialog(tv2, START);
            }
        });
        Choose3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(tv3, END);
            }
        });
        Choose4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePiccckerDialog(tv4, END);
            }
        });

        return builder.create();
    }

    private void showDatePickerDialog(final TextView Date, final int CODE) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);

        Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_LONG).show();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                R.style.Theme_AppCompat_DayNight_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "-" + ++month + "-" + dayOfMonth;
                        Date.setText(date);
                        if (CODE == START){
                            try {
                                starttime = dateformat.parse(date);
                            } catch (ParseException e) {
                                Log.d(TAG, "starttime: " + starttime);
                                Log.e(TAG, "dateformat exception", e);
                            }
                        }else if (CODE == END){
                            try {
                                endtime = dateformat.parse(date);
                            } catch (ParseException e) {
                                Log.d(TAG, "endtime: " + endtime);
                                Log.e(TAG, "dateformat exception", e);
                            }
                        }
                    }
                },
                year, month, dayofMonth);
        datePickerDialog.show();
    }

    private void showTimePiccckerDialog(final TextView Time, final int CODE) {
        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, //doublecheck getApplicationContext()
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timeselected = hourOfDay + ":" + minute;
                        Time.setText(timeselected);
                        if(CODE == START) {
                            try {
                                startdate = timeformat.parse(timeselected);
                            } catch (ParseException e) {
                                Log.e(TAG, "parse exception caught", e);
                            }
                        }
                        else if(CODE == END){
                            try {
                                enddate = timeformat.parse(timeselected);
                            } catch (ParseException e) {
                                Log.e(TAG, "parse exception caught", e);
                            }
                        }
                    }
                },  hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (PTDialogListener) context;
        }catch (Exception e){
            Log.e(TAG, "onAttach line 206", e);
        }
    }

    public interface PTDialogListener{
        void applyTexts(Date startdate, Date starttime, Date enddate, Date endtime);
    }
}
