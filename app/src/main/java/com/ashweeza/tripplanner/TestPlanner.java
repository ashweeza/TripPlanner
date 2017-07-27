package com.ashweeza.tripplanner;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ashweeza.tripplanner.R.layout.items;

/**
 * Created by Ashweeza on 3/1/2017.
 */

public class TestPlanner extends AppCompatActivity implements View.OnClickListener {
    public final static String PLACE_DATA_KEY = "Weather Hour";
    public static final String MyPREFERENCES = "dayspref";
    public static final String numdays = "numdays";
    public static final String dbnameprefs = "dbnamepref";
    public static final String dbname = "dbname";
    private static final String TAG = "Tag";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCz19lSR3gBMyzIUmUg_kkSO_EU4LqgkdA";
    public static int i = 1;
    public static int keyid;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<PlaceTypeModel> data;
    private static ArrayList<Integer> removedItems;
    final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    AutoCompleteTextView place_auto;
    LatLng sourcepoint;
    Address saddress;
    String KEY_URL = "&key=AIzaSyCz19lSR3gBMyzIUmUg_kkSO_EU4LqgkdA";
    ArrayList<PlaceData> placeDataFetched = new ArrayList<>();
    ArrayList<PlaceData> museumsdata = new ArrayList<>();
    ArrayList<PlaceData> restaurantdata = new ArrayList<>();
    ArrayList<PlaceData> zoodata = new ArrayList<>();
    ArrayList<PlaceData> hospitaldata = new ArrayList<>();
    ArrayList<PlaceData> tourismdata = new ArrayList<>();
    ArrayList<PlaceData> hotelsdata = new ArrayList<>();
    ArrayList<PlaceData> parksdata = new ArrayList<>();
    AsyncTask<String, Void, ArrayList<PlaceData>> asyncdata;
    String LOC_URL = "";
    String TYPE_URL = "";
    String FINAL_URL = "";
    GridView gridview;
    LatLng placelatlng;
    Double lat;
    Double lng;
    TextView typetv, placetv;
    String place_name = "";
    ListView list;
    ArrayList<PlaceData> selectedplaces = new ArrayList<>();
    Button showselectedbtn, showtrip;
    PlaceData place = new PlaceData();
    ArrayList<Integer> placetypes = new ArrayList<>();
    TextView tv;
    Animation animation = null;
    View sharedView;
    Button clearbtn;
    ListView lv;
    EditText et1;
    Spinner dayspinner, timespinner;
    //long numofdays;
    ArrayList<PlaceData> places = new ArrayList();
    ArrayList<PlaceData> selecteddayplanlist = new ArrayList();
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
    int counter = 0;
    int museumcounter = 0, parkcounter = 0, hospitalcounter = 0, tourcounter = 0, zoocounter = 0, hotelcounter = 0;
    private PlacetypeAdapter recycleadapter;
    private RecyclerView recyclerView;
    private String[] nameArray = {
            "Museums",
            "Restaurants",
            "Amuzement Parks",
            "Hospitals",
            "Tourist Places",
            "Zoo", "Hotels"};
    private Integer[] id_ = {R.drawable.museum, R.drawable.food, R.drawable.amusement_park,
            R.drawable.hospital, R.drawable.tour, R.drawable.zoo, R.drawable.hotel};
    private String[] timeofday = {"Morning", "Lunch", "Afternoon", "Snack", "Evening", "Night Stay"};
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plannertest);
        //clearbtn = (Button) findViewById(R.id.clearbt);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tableprefs = getSharedPreferences(dbnameprefs, Context.MODE_PRIVATE);
        int numofdays = sharedPref.getInt(numdays, 1);
        tablename = tableprefs.getString(dbname, "tripdata_table");
        Log.d("tag", "Table name in Testplanner:" + tablename);
        Log.d("tag", "num of daysssss:" + numofdays);
        myDB = new DataBaseHelper(this);
        list = (ListView) this.findViewById(R.id.data_list);
        tv = (TextView) findViewById(R.id.typetv);
        showselectedbtn = (Button) findViewById(R.id.showselectedplacesbtn);
        showtrip = (Button) findViewById(R.id.showtrip);
        List<String> days = new ArrayList<>();
        for (int i = 1; i <= numofdays; i++) {
            days.add(String.valueOf(i));
        }

        dayspinner = (Spinner) findViewById(R.id.daynum2);
        timespinner = (Spinner) findViewById(R.id.timenum2);
        // Creating adapter for spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(TestPlanner.this, android.R.layout.simple_spinner_item, days);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayspinner.setAdapter(arrayAdapter);
        // Creating adapter for timespinner
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeofday);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timespinner.setAdapter(arrayAdapter1);

        sharedView = list;
        list.setFadingEdgeLength(0);
        animation = AnimationUtils.loadAnimation(TestPlanner.this, R.anim.pushleft_in);
        place_auto = (AutoCompleteTextView) findViewById(R.id.placeTV);
        place_auto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (place_auto.getRight() - place_auto.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        place_auto.setText("");

                        return false;
                    }
                }
                return false;
            }
        });
        //      gridview = (GridView) findViewById(R.id.gridview);
        //      myOnClickListener = new MyOnClickListener();
        showselectedbtn.setEnabled(false);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        //    recyclerView.setVisibility(View.INVISIBLE);

        // btn_add.setOnClickListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        place_auto.setAdapter(new AutoCompleteAdapter(this, R.layout.autolistitem));
        place_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String cittyyy = place_auto.getText().toString();
                //cityselect(cittyyy);
                cityname = place_auto.getText().toString();
                String str = (String) parent.getItemAtPosition(position);
                if (!str.isEmpty()) {
                    cityselect();
                    // recyclerView.setVisibility(View.VISIBLE);

                   /* for (int j = 0; j < icons.length; j++) {
                        placetypes.add(j, icons[j]);
                    }*/
                    data = new ArrayList<>();
                    for (int i = 0; i < nameArray.length; i++) {
                        data.add(new PlaceTypeModel(
                                nameArray[i],
                                id_[i]
                        ));

                    }

                    removedItems = new ArrayList<>();
                    recycleadapter = new PlacetypeAdapter(data);
                    recyclerView.setAdapter(recycleadapter);
                    Animation anim = AnimationUtils.loadAnimation(TestPlanner.this, R.anim.slidetop_to_bottom);
                    anim.setDuration(1000);
                    recyclerView.startAnimation(anim);
                    recycleadapter.setOnItemClickListener(new PlacetypeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View itemView, int position) {
                            int selectedItemPosition = recyclerView.getChildLayoutPosition(itemView);
                            RecyclerView.ViewHolder viewHolder
                                    = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
                            TextView textViewName
                                    = (TextView) viewHolder.itemView.findViewById(R.id.textViewName);
                            String selectedName = (String) textViewName.getText();
                            int selectedItemId = -1;
                            for (int i = 0; i < nameArray.length; i++) {
                                if (selectedName.equals(nameArray[i])) {
                                    selectedItemId = id_[i];
                                }
                            }
                            //  Toast.makeText(TestPlanner.this, "CLicked:" + selectedName + " position:" + selectedItemPosition, Toast.LENGTH_SHORT).show();
                            tv.setText(selectedName);
                            switch (selectedName) {
                                case "Restaurants":
                                    if (counter == 0) {
                                        TYPE_URL = "&rankby=distance&type=restaurant";
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity Restaurants:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                        restaurantdata = placeDataFetched;
                                        counter = 1;
                                    } else {
                                        populate(restaurantdata);

                                    }

//                typetv.setText("Restaurants");
                                    break;
                                case "Museums":
                                    if (museumcounter == 0) {
                                        TYPE_URL = "&radius=800&type=museum";
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity Museums:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                        museumsdata = placeDataFetched;
                                        museumcounter = 1;
                                    } else {
                                        populate(museumsdata);

                                    }

                                    //    typetv.setText("Museums");
                                    break;
                                case "Amuzement Parks":
                                    if (parkcounter == 0) {
                                        TYPE_URL = "&radius=15000&type=amusement_park";
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity amusement_parks:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                        parksdata = placeDataFetched;
                                        parkcounter = 1;
                                    } else {
                                        populate(parksdata);

                                    }
//    typetv.setText("Amusement Parks");
                                    break;
                                case "Hospitals":
                                    if (hospitalcounter == 0) {
                                        TYPE_URL = "&radius=25000&type=hospital";
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity hospital:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                        hospitaldata = placeDataFetched;
                                        hospitalcounter = 1;
                                    } else {
                                        populate(hospitaldata);

                                    }//  typetv.setText("Hospitals");
                                    break;
                                case "Zoo":
                                    if (zoocounter == 0) {
                                        TYPE_URL = "&radius=50000&type=zoo";
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity zoo:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                        zoodata = placeDataFetched;
                                        zoocounter = 1;
                                    } else {
                                        populate(zoodata);

                                    } //  typetv.setText("Zoo");
                                    break;
                                case "Tourist Places":
                                    if (tourcounter == 0) {
                                        TYPE_URL = "&rankby=distance&keyword=tourist+places";
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity Tourism:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                        tourismdata = placeDataFetched;
                                        tourcounter = 1;
                                    } else {
                                        populate(tourismdata);

                                    }//   typetv.setText("Tourist Attractions");
                                    break;
                                case "Hotels":
                                    if (hotelcounter == 0) {
                                        TYPE_URL = "&radius=800&type=lodging";
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity Restaurants:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                        hotelsdata = placeDataFetched;
                                        hotelcounter = 1;
                                    } else {
                                        populate(hotelsdata);

                                    }
//                typetv.setText("Restaurants");
                                    break;
                            }
                        }
                    });
                    //       recyclerView.setOnClickListener(this);

                    //                  gridview.setAdapter(new ImageAdapter(TestPlanner.this, icons));
                    //                gridview.setOnItemClickListener(TestPlanner.this);

                } else {
                    place_auto.setError("Please Enter City Name");
                }
            }
        });

        // showmapbtn.setEnabled(false);
        //refinetripbtn.setEnabled(false);
/*
        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_auto.setText("");
            }
        });
*/

    }

    @Override
    public void onClick(View v) {
        if (removedItems.size() != 0) {
            addRemovedItemToList();
        } else {
            Toast.makeText(this, "Nothing to add", Toast.LENGTH_SHORT).show();
        }
    }

    private void addRemovedItemToList() {
        int addItemAtListPosition = 0;
        data.add(addItemAtListPosition, new PlaceTypeModel(
                nameArray[removedItems.get(0)],
                id_[removedItems.get(0)]
        ));
        recycleadapter.notifyItemInserted(addItemAtListPosition);
        removedItems.remove(0);
    }

    public void cityselect() {
        //String city = cityname;

        Geocoder g = new Geocoder(TestPlanner.this);

        List<Address> placeaddress = null;
        try {
            placeaddress = g.getFromLocationName(place_auto.getText().toString(), 1);

        } catch (IOException e) {
            Toast.makeText(TestPlanner.this, "Location not found", Toast.LENGTH_SHORT)
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

                LOC_URL = "location=" + sourcepoint.latitude + "," + sourcepoint.longitude;
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

    }

    public void changedata(AsyncTask<String, Void, ArrayList<PlaceData>> data) {
        try {
            //placeDataFetched = new ArrayList<>();
            placeDataFetched = data.get();
            populate(placeDataFetched);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected void populate(ArrayList<PlaceData> dataaaaaaa) {
        //ArrayList<PlaceData> plalalala = dataaaaaaa;

        //ListView list = (ListView) findViewById(R.id.data_list);
      /*  try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        adapter = new ListViewAdapter(TestPlanner.this, items, dataaaaaaa, metrics);
        list.setAdapter(adapter);
        animation.setDuration(500);
        list.startAnimation(animation);
        // Item Click Listener for the listview
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                // Getting the Container Layout of the ListView
                RelativeLayout relativeLayout = (RelativeLayout) container;

                // Getting the inner Linear Layout
           //     LinearLayout linearLayoutChild = (LinearLayout) relativeLayout.getChildAt(1);

                // Getting the Country TextView
                //  TextView tvCountry = (TextView) linearLayoutChild.getChildAt(0);
                ImageView icon = (ImageView) relativeLayout.getChildAt(0);

                // Toast.makeText(getBaseContext(), placeDataFetched.get(position).getPlaceid(), Toast.LENGTH_SHORT).show();

                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(TestPlanner.this, icon, icon.getTransitionName()).toBundle();

                Intent intent = new Intent(TestPlanner.this, DetailActivity.class);
                intent.putExtra(PLACE_DATA_KEY, placeDataFetched.get(position));
                startActivity(intent, bundle);

/*
                Dialog alertDialog = new Dialog(TestPlanner.this);
                alertDialog.setTitle("Place Info");
                alertDialog.setContentView(R.layout.dialogimageview);
                final ImageView imageviw = (ImageView) alertDialog.findViewById(R.id.imageView2);
                Picasso.with(TestPlanner.this).load(placeDataFetched.get(position).getIcon_url()).resize(150,150).into(imageviw);
                //imageviw.setImageDrawable(placeDataFetched.get(position).getIcon_url());
                alertDialog.show();*/
            }
        };

        // Setting the item click listener for the listview
        list.setOnItemClickListener(itemClickListener);
        //  animation = null;
        //list.setItemAnimator(new DefaultItemAnimator());
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                modeeeeee = mode;
                // Capture total checked items
                final int checkedCount = list.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                adapter.toggleSelection(position);
                if (checkedCount > 0) {
                    showselectedbtn.setEnabled(true);
                    showselectedbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*String cittyyy = place_auto.getText().toString();
                            cityselect(cittyyy);*/
                            //sleep(80);
                            int itempositions = adapter.getSelectedCount();
                            if (itempositions > 0) {
                                for (int i = 0; i < itempositions; i++) {
                                    selecteddayplanlist.add(i, adapter.getWorldPopulation().get(i));
                                }
               /* for (int j=itempositions;j<4;j++)
                {
                    selecteddayplanlist.add(j,null);
                }*/
                                Log.v("tag", "selectedplaceslist:" + selecteddayplanlist);
                                // adapter.notifyDataSetChanged();
                                addData();
                                showtrip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent intent = new Intent(TestPlanner.this, WholeTrip.class);
                                        intent.putExtra("tablename", tablename);
                                        startActivity(intent);
                                    }
                                });
                                SparseBooleanArray selected = adapter
                                        .getSelectedIds();
                                // Captures all selected ids with a loop
                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    if (selected.valueAt(i)) {
                                        PlaceData selecteditem = adapter
                                                .getItem(selected.keyAt(i));
                                        // Remove selected items following the ids
                                        adapter.remove(selecteditem);
                                        // Log.v("tag", "Num selected:" + adapter.getSelectedCount());
                                    }
                                }
                                modeeeeee.finish();
                                if (timespinner.getSelectedItemPosition() < (timespinner.getAdapter().getCount() - 1)) {
                                    Log.v("tag", "selected item position:" + timespinner.getSelectedItemPosition() + "Adapter count:" + timespinner.getAdapter().getCount());

                                    timespinner.setSelection(timespinner.getSelectedItemPosition() + 1);
                                    if (timespinner.getSelectedItemPosition() == 1) {
                                        // recyclerView.scrollToPosition(0);
                                        tv.setText("Lunch");
                                        TYPE_URL = "&rankby=distance&type=restaurant";
                                        int addedsize = selecteddayplanlist.size();
                                        LOC_URL = "location=" + selecteddayplanlist.get(addedsize - 1).getLattitude() + "," + selecteddayplanlist.get(addedsize - 1).getLongitude();
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity Restaurants:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                    } else if (timespinner.getSelectedItemPosition() == 3) {
                                        tv.setText("Snacks");
                                        TYPE_URL = "&rankby=distance&type=bakery";
                                        int addedsize = selecteddayplanlist.size();
                                        LOC_URL = "location=" + selecteddayplanlist.get(addedsize - 1).getLattitude() + "," + selecteddayplanlist.get(addedsize - 1).getLongitude();
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity Bakery:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                    } else if (timespinner.getSelectedItemPosition() == 5) {
                                        tv.setText("Night Stay");
                                        TYPE_URL = "&rankby=distance&type=lodging";
                                        int addedsize = selecteddayplanlist.size();
                                        LOC_URL = "location=" + selecteddayplanlist.get(addedsize - 1).getLattitude() + "," + selecteddayplanlist.get(addedsize - 1).getLongitude();
                                        FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                        Log.v("Tag", "FINAL URL in Trip Activity Hotels:" + FINAL_URL);
                                        changedata(new getTestPlaceData(TestPlanner.this).execute(FINAL_URL));
                                    }
                                } else if (timespinner.getSelectedItemPosition() == (timespinner.getAdapter().getCount() - 1)) {
                                    Log.v("tag", "selected item position:" + timespinner.getSelectedItemPosition() + "Adapter count:" + timespinner.getAdapter().getCount());

                                    if (dayspinner.getAdapter().getCount() != 1 && dayspinner.getSelectedItemPosition() < (dayspinner.getAdapter().getCount() - 1)) {
                                        dayspinner.setSelection(dayspinner.getSelectedItemPosition() + 1);
                                        timespinner.setSelection(0);
                                    } else {
                                        timespinner.setSelection(0);
                                        dayspinner.setSelection(0);
                                        Toast.makeText(TestPlanner.this, "All Days are scheduled", Toast.LENGTH_LONG).show();
                                    }

                                }
                            } else if (itempositions < 1) {
                                Toast.makeText(TestPlanner.this, "Please Select ATLEAST One Place", Toast.LENGTH_LONG).show();
                            } else if (itempositions > 4) {
                                Toast.makeText(TestPlanner.this, "Please Select ATMOST 4 Places", Toast.LENGTH_LONG).show();
                            }
                            /*String transitionName = getString(R.string.transition_name);
                            Gson gson1 = new GsonBuilder().create();
                            String gsonStr = gson1.toJson(selectedplaces);
                            Intent intent = new Intent(TestPlanner.this, DayPlanner.class);
                            intent.putExtra("selectedplaces", gsonStr);
                            place_name = saddress.getFeatureName();
                            Log.v("tag", "place name:" + place_name);
                            intent.putExtra("cityname", place_name);
                            // ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(TestPlanner.this, sharedView, transitionName);
                            //startActivity(intent, transitionActivityOptions.toBundle());
                            //setResult(RESULT_OK, intent);
                            startActivity(intent);*/

                        }
                    });
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //mode.getMenuInflater().inflate(R.menu.menus, menu);
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
          /*  list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TestPlanner.this, DetailActivity.class);
                intent.putExtra(PLACE_DATA_KEY, placeDataFetched.get(position));
                intent.putExtra("source", "item");
                startActivity(intent);
            }
        });*/
        /*list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(TripDataSelect.this, placeDataFetched.get(position).getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/

        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();
    }

    public void addData() {
        boolean isInserted;
        selectedDay = String.valueOf(dayspinner.getSelectedItemPosition() + 1);
        selectedTimeofday = timespinner.getSelectedItem().toString();
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

    private void removeItem(View v) {

        // removedItems.add(selectedItemId);
        // data.remove(selectedItemPosition);
        // adapter.notifyItemRemoved(selectedItemPosition);
    }


}
