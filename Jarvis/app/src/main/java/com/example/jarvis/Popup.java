package com.example.jarvis;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

//Popup for the user to input preferred times.
public class Popup extends AppCompatActivity {
    private static final String TAG = "PopUp";

    //CODE
    private static final int START = 0;
    private static final int END = 0;

    //XML
    private Button Choose1, Choose2, Choose3, Choose4;
    private TextView tv1, tv2, tv3, tv4;
    private SimpleDateFormat dateformat, timeformat;

    //return values
    private Date startdate, enddate;
    private Date starttime, endtime;

    //Others
    Calendar calendar;
    int year, month, dayofMonth, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        initializeFields();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((width * 8), (height*7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        Choose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePiccckerDialog(tv1, START);
            }
        });
        Choose4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePiccckerDialog(tv3, END);
            }
        });

        Choose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //TODO: Allow user to input date and time
        //TODO: Dont forget to finish
    }

    private void initializeFields() {
        Choose1 = findViewById(R.id.choosedate_pop_bttn_st);
        Choose2 = findViewById(R.id.choosetime_pop_bttn_st);
        Choose3 = findViewById(R.id.enddate_bttn);
        Choose4 = findViewById(R.id.endtime_bttn);

        tv1 = findViewById(R.id.tvstartPreferredDate);
        tv2 = findViewById(R.id.tvTime_Pop_st);
        tv3 = findViewById(R.id.tvendPreferredDate);
        tv4 = findViewById(R.id.tvTime_pop_end);

        dateformat = new SimpleDateFormat("yyyy-MM-dd");
        timeformat = new SimpleDateFormat("hh:mm");
    }

    private void showTimePiccckerDialog(final TextView Time, final int CODE) {
        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getApplicationContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, //doublecheck getApplicationContext()
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

    private void showDatePickerDialog(final TextView Date, final int CODE) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);

        Toast.makeText(getApplicationContext(), "Clicked!", Toast.LENGTH_LONG).show();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getApplicationContext(),
                R.style.Theme_AppCompat_DayNight_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "-" + month + "-" + dayOfMonth;
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
}
