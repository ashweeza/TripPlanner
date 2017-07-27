package com.ashweeza.tripplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class DayPlanner extends AppCompatActivity implements View.OnClickListener {
    public static final String MyPREFERENCES = "dayspref";
    public static final String numdays = "numdays";
    public static final String dbnameprefs = "dbnamepref";
    public static final String dbname = "dbname";
    public static int keyid;
    Button btndayplan, btnview, btnupdate, btndelrow, btndelall, btndata;
    Button showonmapbtn;
    ListView lv;
    EditText et1;
    Spinner spinner, spinner2;
    //long numofdays;
    ArrayList<PlaceData> places = new ArrayList();
    ArrayList<PlaceData> selecteddayplanlist = new ArrayList();
    ArrayList<PlaceData> resultlist = new ArrayList<>();
    ListViewAdapter adapter;
    SharedPreferences sharedPref, tableprefs;
    String selectedTimeofday;
    String selectedDay;
    String cityname;
    DataBaseHelper myDB;
    StringBuffer buffer;
    StringBuffer databuffer;
    String idupdate;
    String iddel;
    ActionMode modeeeeee;
    String tablename = "";
    String placesjson;
    private String[] timeofday = {"Morning", "Afternoon", "Evening", "Night Stay"};
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_planner);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        placesjson = getIntent().getExtras().getString("selectedplaces");
        cityname = getIntent().getExtras().getString("cityname");
        btndayplan = (Button) findViewById(R.id.dayaddbtn);
        btnview = (Button) findViewById(R.id.viewallbtn);
        btnupdate = (Button) findViewById(R.id.updatebtn);
        btndelrow = (Button) findViewById(R.id.delbtn);
        btndelall = (Button) findViewById(R.id.delallbtn);
        btndata = (Button) findViewById(R.id.viewbtn);
        showonmapbtn = (Button) findViewById(R.id.showonmapbtn);

        btndayplan.setOnClickListener(this);
        btnview.setOnClickListener(this);
        btnupdate.setOnClickListener(this);
        btndelrow.setOnClickListener(this);
        btndelrow.setOnClickListener(this);
        btndelall.setOnClickListener(this);
        btndata.setOnClickListener(this);
        showonmapbtn.setOnClickListener(this);
        //int quantity = getIntent().getIntExtra("quantity", 1);
        //int datearray = getIntent().getIntExtra("startdate", 1);
        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tableprefs = getSharedPreferences(dbnameprefs, Context.MODE_PRIVATE);
        int numofdays = sharedPref.getInt(numdays, 1);
        tablename = tableprefs.getString(dbname, "tripdata_table");
        Log.d("tag", "Table name in Dayplanner:" + tablename);
        Log.d("tag", "num of daysssss:" + numofdays);

        spinner = (Spinner) findViewById(R.id.daynum);
        spinner2 = (Spinner) findViewById(R.id.timenum);
        //et1 = (EditText) this.findViewById(R.id.editText);

        lv = (ListView) this.findViewById(R.id.selected_list);


        myDB = new DataBaseHelper(this);
        List<String> days = new ArrayList<String>();
        for (int i = 1; i <= numofdays; i++) {
            days.add("DAY==" + i);
        }


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, days);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        // Creating adapter for spinner2
        ArrayAdapter<String> timeofdayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeofday);
        timeofdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(timeofdayAdapter);
        //places = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        final ArrayList<PlaceData> selectedplaces = gson.fromJson(placesjson, new TypeToken<ArrayList<PlaceData>>() {
        }.getType());
        places.addAll(selectedplaces);
        //adapter = new JustAdapter(DayPlanner.this, R.layout.nearby_item, places);
        adapter = new ListViewAdapter(this, R.layout.items, places, metrics);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                modeeeeee = mode;
                // Capture total checked items
                final int checkedCount = lv.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menus, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = adapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                PlaceData selecteditem = adapter
                                        .getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                adapter.remove(selecteditem);
                                places.remove(selecteditem);
                            }
                        }

                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.removeSelection();
            }
        });


        /*lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int pos, long id) {

                AlertDialog.Builder ad1 = new AlertDialog.Builder(DayPlanner.this);
                ad1.setTitle("WARNING");
                ad1.setMessage("Do you want to delete item?");
                ad1.setCancelable(false);
                ad1.setIcon(R.drawable.alert);
                ad1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makeText(getApplicationContext(),
                                "Clicked NO!", Toast.LENGTH_SHORT).show();
                    }
                });
                ad1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!places.isEmpty()) {
                            PlaceData place = places.get(pos);
                            places.remove(place);
                            adapter.notifyDataSetChanged();
                            makeText(DayPlanner.this, place.getName() + "was deleted", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                AlertDialog alert = ad1.create();
                alert.show();
                return false;
            }
        });*/
        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dayaddbtn:

                int itempositions = adapter.getSelectedCount();
                if (itempositions > 0 && itempositions < 5) {
                    for (int i = 0; i < itempositions; i++) {
                        selecteddayplanlist.add(i, adapter.getWorldPopulation().get(i));
                        resultlist.add(adapter.getWorldPopulation().get(i));
                    }
               /* for (int j=itempositions;j<4;j++)
                {
                    selecteddayplanlist.add(j,null);
                }*/
                    Log.v("tag", "selectedplanlist:" + selecteddayplanlist);
                    // adapter.notifyDataSetChanged();


                    addData();
                    SparseBooleanArray selected = adapter
                            .getSelectedIds();
                    // Captures all selected ids with a loop
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            PlaceData selecteditem = adapter
                                    .getItem(selected.keyAt(i));
                            // Remove selected items following the ids
                            adapter.remove(selecteditem);
                            Log.v("tag", "Num selected:" + adapter.getSelectedCount());
                        }
                    }
                    modeeeeee.finish();
                    //selecteddayplanlist.clear();
                    //adapter.notifyDataSetChanged();
                    if (spinner2.getSelectedItemPosition() < (spinner2.getAdapter().getCount() - 1)) {
                        Log.v("tag", "selected item position:" + spinner2.getSelectedItemPosition() + "Adapter count:" + spinner2.getAdapter().getCount());

                        spinner2.setSelection(spinner2.getSelectedItemPosition() + 1);
                    } else if (spinner2.getSelectedItemPosition() == (spinner2.getAdapter().getCount() - 1)) {
                        Log.v("tag", "selected item position:" + spinner2.getSelectedItemPosition() + "Adapter count:" + spinner2.getAdapter().getCount());

                        if (spinner.getAdapter().getCount() != 1 && spinner.getSelectedItemPosition() < (spinner.getAdapter().getCount() - 1)) {
                            spinner.setSelection(spinner.getSelectedItemPosition() + 1);
                            spinner2.setSelection(0);
                        } else {
                            spinner2.setSelection(0);
                            spinner.setSelection(0);
                            Toast.makeText(this, "All Days are scheduled", Toast.LENGTH_LONG).show();
                            Log.v("tag", "Result List:" + resultlist);
                        }

                    }
                } else if (itempositions < 1) {
                    Toast.makeText(this, "Please Select ATLEAST One Place", Toast.LENGTH_LONG).show();
                } else if (itempositions > 4) {
                    Toast.makeText(this, "Please Select ATMOST 4 Places", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.viewallbtn:
                viewAllData();
                showMessage("Place Data", buffer.toString());
                break;
            case R.id.updatebtn:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText edittext = new EditText(this);
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setMessage("Enter Row Number");
                alert.setTitle("Update Row");
                alert.setView(edittext);
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //idupdate = edittext.getText().toString();
                        updateData(edittext.getText().toString());
                        makeText(DayPlanner.this, "ID value in update is:" + edittext.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                });
                alert.show();

                break;
            case R.id.delbtn:
                AlertDialog.Builder delalert = new AlertDialog.Builder(this);
                final EditText etdel = new EditText(this);
                etdel.setInputType(InputType.TYPE_CLASS_NUMBER);
                delalert.setMessage("Enter Row Number");
                delalert.setTitle("Delete Row");
                delalert.setView(etdel);
                delalert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // iddel = etdel.getText().toString();
                        deleteData(etdel.getText().toString());
                        makeText(DayPlanner.this, "ID value in delete is:" + etdel.getText().toString(), Toast.LENGTH_LONG).show();

                    }
                });
                delalert.show();
                break;
            case R.id.delallbtn:
                AlertDialog.Builder delallalert = new AlertDialog.Builder(this);
                delallalert.setMessage("Are you sure?");
                delallalert.setTitle("Delete All Rows!!");
                delallalert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDB.deleteAll(tablename);
                    }
                });
                delallalert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                delallalert.show();
                break;
            case R.id.viewbtn:
                // viewData(String.valueOf(1));
                // showMessage("Place Data", databuffer.toString());
                /*Intent intent = new Intent(this, ViewTripActivity.class);
                startActivity(intent);*/

                Intent intent = new Intent(this, WholeTrip.class);
                intent.putExtra("tablename", tablename);
                startActivity(intent);
                break;
            case R.id.showonmapbtn:
                Intent intent1 = new Intent(this, PlacesOnMapActivity.class);
                intent1.putExtra("selectedplaces", placesjson);
                startActivity(intent1);
                break;
        }

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void addData() {
        boolean isInserted;
        selectedDay = String.valueOf(spinner.getSelectedItemPosition() + 1);
        selectedTimeofday = spinner2.getSelectedItem().toString();
        int sizelist = selecteddayplanlist.size();
      /*  String placeone;
        String placetwo;
        String placethree;
        String placefour;*/
        Log.v("tag", "Size in add data function:" + sizelist);
        String place[] = new String[4];
        for (int i = 0; i < adapter.getSelectedCount(); i++) {
            place[i] = selecteddayplanlist.get(i).getName();
        }

        if (sizelist < 4) {
            for (int j = sizelist; j < 4; j++) {
                place[j] = null;
            }
        }



        /*if (placethree.equalsIgnoreCase("")) {
            isInserted = myDB.insertData(selectedDay, selectedTimeofday, cityname, placeone, placetwo, null, placefour);
        } else if (placefour.equalsIgnoreCase("")) {
            isInserted = myDB.insertData(selectedDay, selectedTimeofday, cityname, placeone, placetwo, placethree, null);
        } else {}*/

        isInserted = myDB.insertData(tablename, selectedDay, selectedTimeofday, cityname, place[0], place[1], place[2], place[3]);
        Log.v("tag", "place1:" + place[0] + "\nplace2:" + place[1] + "\nplace3:" + place[2] + "\nplace4:" + place[3]);

        if (isInserted) {
            Log.v("tag", "insert successfull");
        } else {
            Log.v("tag", "insert failed");
        }
    }

    public void viewAllData() {
        Cursor res = myDB.getAllData(tablename);
        if (res.getCount() == 0) {
            Log.v("tag", "Cursor No Data");
            showMessage("Error", "NO Data found");
        }
        buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("ID:" + res.getString(0) + "\n");
            buffer.append("DAYNUM:" + res.getString(1) + "\n");
            buffer.append("DAYTIME:" + res.getString(2) + "\n");
            buffer.append("CITY:" + res.getString(3) + "\n");
            buffer.append("PLACEONE:" + res.getString(4) + "\n");
            buffer.append("PLACETWO:" + res.getString(5) + "\n");
            buffer.append("PLACETHREE:" + res.getString(6) + "\n");
            buffer.append("PLACEFOUR:" + res.getString(7) + "\n\n");

        }

    }

    public void viewData(int daynum) {
        Cursor data = myDB.getData(tablename, daynum);
        if (data.getCount() == 0) {
            Log.v("tag", "Cursor No Data");
            showMessage("Error", "NO Data found");
        }
        databuffer = new StringBuffer();
        while (data.moveToNext()) {
            databuffer.append("ID:" + data.getString(0) + "\n");
            databuffer.append("DAYNUM:" + data.getString(1) + "\n");
            databuffer.append("DAYTIME:" + data.getString(2) + "\n");
            databuffer.append("CITY:" + data.getString(3) + "\n");
            databuffer.append("PLACEONE:" + data.getString(4) + "\n");
            databuffer.append("PLACETWO:" + data.getString(5) + "\n");
            databuffer.append("PLACETHREE:" + data.getString(6) + "\n");
            databuffer.append("PLACEFOUR:" + data.getString(7) + "\n\n");

        }
    }

    public void updateData(String idvalue) {
        selectedDay = String.valueOf(spinner.getSelectedItemPosition() + 1);
        selectedTimeofday = spinner2.getSelectedItem().toString();
        String id = idvalue;
        String placeone = places.get(0).getName();
        String placetwo = places.get(1).getName();
        String placethree = places.get(2).getName();
        String placefour = places.get(3).getName();
        boolean isUpdate = myDB.updateData(tablename, id, selectedDay, selectedTimeofday, cityname, placeone, placetwo, placethree, placefour);
        if (isUpdate) {
            Log.v("tag", "update successfull");
        } else {
            Log.v("tag", "update NOT successfull");
        }
    }

    public void deleteData(String delvalue) {
        Integer deletedrows = myDB.deleteData(tablename, delvalue);
        if (deletedrows > 0) {
            makeText(this, "Data Row Deleted", Toast.LENGTH_LONG).show();
        }
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
