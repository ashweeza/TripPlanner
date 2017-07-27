package com.ashweeza.tripplanner;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewTripFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public static final String dbnameprefs = "dbnamepref";
    public static final String dbname = "dbname";
    public static final String MyPREFERENCES = "dayspref";
    public static final String numdays = "numdays";
    int quantity = 1;
    SharedPreferences sharedPref, dbprefs;
    TextView sdate, edate;
    TextView sdateView;
    Calendar calendar;
    Calendar c;
    int datearray;
    EditText tripnameET;
    TextView tv;
    TextView quantityTextView;
    ListView lvtest;
    DataBaseHelper mydb;
    String tripname = "";
    private Button btnStart, btnEnd, btnplan, btntest, btnplus, btnminus;
    private int year, month, day;
    private int startMonth;
    private int startDay;
    private int startYear;

    public NewTripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_trip, container, false);
        edate = (TextView) rootView.findViewById(R.id.enddate);
        btnStart = (Button) rootView.findViewById(R.id.sbutton);
        btnEnd = (Button) rootView.findViewById(R.id.ebutton);
        btntest = (Button) rootView.findViewById(R.id.testbutton);
        btnplus = (Button) rootView.findViewById(R.id.buttonplus);
        btnminus = (Button) rootView.findViewById(R.id.buttonminus);
        tripnameET = (EditText) rootView.findViewById(R.id.tripnameET);
        tv = (TextView) rootView.findViewById(R.id.textView3);
        //   lvtest = (ListView) rootView.findViewById(R.id.lisviewmain);
        sdateView = (TextView) rootView.findViewById(R.id.startdate);
        quantityTextView = (TextView) rootView.findViewById(R.id.quantity_text_view);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        final int dayth = calendar.get(Calendar.DAY_OF_WEEK);
        showDate(year, month + 1, day);
        c = Calendar.getInstance();
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH);
        //startMonth++;
        startDay = c.get(Calendar.DAY_OF_MONTH);
        dbprefs = getActivity().getSharedPreferences(dbnameprefs, Context.MODE_PRIVATE);
        sharedPref = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //int quan = sharedPref.getInt(numdays, 1);
        //  int numofdays = dbprefs.getInt(dbname, 1);

        btnplan = (Button) rootView.findViewById(R.id.plan);
        btnplan.setOnClickListener(this);
        btnplus.setOnClickListener(this);
        btnminus.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btntest.setOnClickListener(this);
        mydb = new DataBaseHelper(getActivity());
        mydb.createTableofTables();
        //String dateInString2 = startYear + "/" + startMonth + "/" + startDay;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy-EEEE");
        SimpleDateFormat sdf3 = new SimpleDateFormat("dd MMM,yyyy");
        Date startdate = new Date(c.getTimeInMillis());
        String dateInString2 = sdf.format(startdate);
        String dateInString3 = sdf3.format(startdate);
        tv.setText("Today is " + dateInString2);
        edate.setText(dateInString3);

        // btntest.setOnClickListener(this);

        return rootView;
    }

    private void showDate(int year, int month, int day) {


        // display the current date
        String CurrentDate = year + "/" + month + "/" + day;

        String dateInString = CurrentDate; // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        c = Calendar.getInstance();
        c.set(year, month - 1, day);
        datearray = day;
        sdf = new SimpleDateFormat("dd MMM,yyyy");
        Date startdate = new Date(c.getTimeInMillis());
        dateInString = sdf.format(startdate);

        sdateView.setText(dateInString);
        try {
            c.setTime(sdf.parse(dateInString));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        c.add(Calendar.DATE, quantity);//insert the number of days you want to be added to the current date
        //sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date resultdate = new Date(c.getTimeInMillis() - 1);
        dateInString = sdf.format(resultdate);

        //Display the Result in the Edit Text or Text View your Choice
        //EditText etDOR = (EditText)findViewById(R.id.etDateOfReturn);
        edate.setText(dateInString);

        //edate.setText(new StringBuilder().append(newday).append("/").append(newmonth).append("/").append(year));
    }

    public void increment() {


        quantity = quantity + 1;
        display(quantity);
        //showDate(year,month+1,day+1);

    }

    public void decrement() {

        if (quantity == 1) {
            return;
        }
        quantity = quantity - 1;
        display(quantity);
        //showDate(this.year,this.month+1,this.day-1);
    }

    private void display(int number) {

        quantityTextView.setText("" + number);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonplus:
                increment();
                break;
            case R.id.buttonminus:
                decrement();
                break;
            case R.id.plan:
                String tripnamessss = tripnameET.getText().toString();
                if (!tripnamessss.isEmpty()) {
                    //shared prefs of num of days
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(numdays, quantity);
                    Log.v("tag", "qunatity in prefs:" + quantity);
                    editor.apply();
                    String tripname = tripnamessss.replace(" ", "_");
                    //shared pref of table name
                    SharedPreferences.Editor editor1 = dbprefs.edit();
                    editor1.putString(dbname, tripname);
                    Log.v("tag", "table name in prefs:" + tripname);
                    editor1.apply();
                    mydb.createTable(tripname);
               /* Trip trip = new Trip();
                trip.setNumdays(String.valueOf(quantity));
                trip.setTripname(tripname);*/
                    mydb.insertDataintotableoftables("tableoftables", tripname, quantity);
//                af.adapter.notifyDataSetChanged();
                    Intent i = new Intent(getContext(), PlannerActivity.class);
                    i.putExtra("quantity", quantity);
                    // int datearray=day;
                    i.putExtra("startdate", datearray);
                    startActivity(i);
                } else {
                    tripnameET.setError("Enter Trip name");
                }

                break;
            case R.id.sbutton:
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
                dpd.show();
                break;
            case R.id.testbutton:
                //mydb.deleteAllTables();
                //Intent intent = new Intent(getActivity(), Placetypetest.class);
                //startActivity(intent);

                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        showDate(year, monthOfYear + 1, dayOfMonth);
    }


}
