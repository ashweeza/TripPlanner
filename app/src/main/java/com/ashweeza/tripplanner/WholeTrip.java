package com.ashweeza.tripplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WholeTrip extends AppCompatActivity {
    ListView triplv;
    ArrayList<DbTripData> tripdata;
    WholeTripAdapter wholeTripAdapter;
    DataBaseHelper mydb;
    float x1, x2;
    float y1, y2;
    String tablename;
    Bitmap bigbitmap;
    Address saddress;
    LatLng sourcepoint;
    int counter = 1;
    ArrayList<LatLng> value = new ArrayList<>();
    float distance[];
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_trip);
        toolbar = (Toolbar) findViewById(R.id.toolbarc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Whole Trip");
        /*getSupportActionBar().setLogo(R.drawable.tour);*/
        Intent intent = getIntent();
        tablename = intent.getStringExtra("tablename");
        Log.v("tag", "tablename in wholetrip activity is:" + tablename);
        mydb = new DataBaseHelper(this);
        triplv = (ListView) findViewById(R.id.triplv);
        tripdata = new ArrayList<>();
        //mydb.getAllData("wheeling");
        Cursor res = mydb.getAllData(tablename);
        if (res.getCount() == 0) {
            Log.v("tag", "Cursor No Data");
            showMessage("INFO", "NO Data for this trip");
        }
        //buffer = new StringBuffer();
        while (res.moveToNext()) {

         /*   buffer.append("ID:" + res.getString(0) + "\n");
            buffer.append("DAYNUM:" + res.getString(1) + "\n");
            buffer.append("DAYTIME:" + res.getString(2) + "\n");
            buffer.append("CITY:" + res.getString(3) + "\n");
            buffer.append("PLACEONE:" + res.getString(4) + "\n");
            buffer.append("PLACETWO:" + res.getString(5) + "\n");
            buffer.append("PLACETHREE:" + res.getString(6) + "\n");
            buffer.append("PLACEFOUR:" + res.getString(7) + "\n\n");*/
            DbTripData dbbbb = new DbTripData();
            dbbbb.setDaynum(res.getInt(1));
            dbbbb.setDaytime(res.getString(2));
            dbbbb.setCityname(res.getString(3));
            dbbbb.setPlaceone(res.getString(4));
            dbbbb.setPlacetwo(res.getString(5));
            dbbbb.setPlacethree(res.getString(6));
            dbbbb.setPlacefour(res.getString(7));

            //value.add();
            tripdata.add(dbbbb);

            /* else {
                DbTripData dbbbb = new DbTripData();
                dbbbb.setDaynum(res.getInt(1));
                dbbbb.setDaytime(res.getString(2));
                dbbbb.setCityname(res.getString(3));
                dbbbb.setPlaceone(res.getString(4));
                dbbbb.setPlacetwo(res.getString(5));
                dbbbb.setPlacethree(res.getString(6));
                dbbbb.setPlacefour(res.getString(7));
                LatLng value1 = giveLatLong(res.getString(4));
                dbbbb.setLatLng(value1);
               *//* float results[] = new float[1];
                Location.distanceBetween(value.latitude, value.longitude, value1.latitude, value1.longitude, results);
                dbbbb.setDistance(results[0]);*//*
                double resss = distance(value.latitude, value.longitude, value1.latitude, value1.longitude);
                dbbbb.setDistance(Float.valueOf(new DecimalFormat("##.#").format(resss)));

                tripdata.add(dbbbb);

            }*/

        }
        int size = tripdata.size();
        distance=  new float[size];
        for (int i = 0; i < size; i++)
        {
            giveLatLong(tripdata.get(i).getPlaceone());

            //distance[i];
        }

        wholeTripAdapter = new WholeTripAdapter(WholeTrip.this, R.layout.wholertripitem, tripdata,distance);
        triplv.setAdapter(wholeTripAdapter);
        wholeTripAdapter.notifyDataSetChanged();
        wholeTripAdapter.setNotifyOnChange(true);
        triplv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


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

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WholeTrip.this.finish();
            }
        });
        builder.show();
    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN: {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = touchevent.getX();
                y2 = touchevent.getY();

                //if left to right sweep event on screen
                if (x1 < x2) {
                    Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_LONG).show();
                }

                // if right to left sweep event on screen
                if (x1 > x2) {
                    Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_LONG).show();
                }

               /* // if UP to Down sweep event on screen
                if (y1 < y2)
                {
                    Toast.makeText(this, "UP to Down Swap Performed", Toast.LENGTH_LONG).show();
                }

                //if Down to UP sweep event on screen
                if (y1 > y2)
                {
                    Toast.makeText(this, "Down to UP Swap Performed", Toast.LENGTH_LONG).show();
                }*/
                break;
            }
        }
        return false;
    }

    public void getWholeListViewItemsToBitmap(ListView p_ListView) {
        ListView listview = p_ListView;
        ListAdapter adapter = listview.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<Bitmap>();
        for (int i = 0; i < itemscount; i++) {
            View childView = adapter.getView(i, null, listview);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }
        bigbitmap = Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight,
                Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);
        Paint paint = new Paint();
        int iHeight = 0;
        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
            bmp.recycle();
            bmp = null;
        }
        //storeImage(bigbitmap, "Test.jpg");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wholetripmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.share:
                getWholeListViewItemsToBitmap(triplv);
                File file2 = new File(WholeTrip.this.getCacheDir(), "trip:" + tablename + ".png");
                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(file2);
                    bigbitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file2.setReadable(true, false);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file2));
                intent.setType("image/png");
                startActivity(intent);
                break;
            case R.id.download:
                getWholeListViewItemsToBitmap(triplv);
                Save savefile = new Save();     //calling the Save java file
                savefile.SaveImage(WholeTrip.this, bigbitmap, tablename);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Next!!");
                builder.setMessage("Where are you off to??");
                builder.setNegativeButton("Plan New Trip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent1 = new Intent(WholeTrip.this, FirstActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        WholeTrip.this.finish();
                    }
                });
                builder.setNeutralButton("Stay here", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Exit App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(WholeTrip.this, FirstActivity.class);
                        startActivity(intent);
                        WholeTrip.this.finish();
                    }
                });
                builder.show();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    public LatLng giveLatLong(String cityname) {
        String city = cityname;

        Geocoder g = new Geocoder(WholeTrip.this);

        List<Address> placeaddress = null;
        try {
            placeaddress = g.getFromLocationName(city, 1);

        } catch (IOException e) {
            Toast.makeText(WholeTrip.this, "Location not found", Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();

        } finally {

            saddress = null;
            if (placeaddress != null) {
                saddress = placeaddress.get(0);

            }

            if (saddress.hasLatitude() && saddress.hasLongitude()) {
                double selectedLat = saddress.getLatitude();
                double selectedLng = saddress.getLongitude();
                sourcepoint = new LatLng(selectedLat, selectedLng);

                //LOC_URL = "location=" + sourcepoint.latitude + "," + sourcepoint.longitude;
                // Toast.makeText(this, placelatlng.toString(), Toast.LENGTH_SHORT).show();
                Log.v("Tag", "Place LatLOng:" + sourcepoint);
           /*     Marker Custom = map.addMarker(new MarkerOptions()
                        .position(Sourcepoint).title("Source Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .snippet("starting point"));*/
                //Toast.makeText(this, sourcepoint.toString(), Toast.LENGTH_SHORT).show();
                //LOC_URL = "location=" + sourcepoint.latitude + "," + sourcepoint.longitude;
                //   populate();
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sourcepoint;
    }
}
