package com.ashweeza.tripplanner;

import android.content.Context;
import android.content.Intent;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ashweeza.tripplanner.R.layout.items;

public class PlannerActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String PLACE_DATA_KEY = "Weather Hour";
    private static final String TAG = "Tag";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCz19lSR3gBMyzIUmUg_kkSO_EU4LqgkdA";
    public static int keyid;
    public static int i = 1;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<PlaceTypeModel> data;
    private static ArrayList<Integer> removedItems;
    final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    AutoCompleteTextView place_auto;
    LatLng sourcepoint;
    ActionMode modeeeeee;
    Button showmapbtn, clearbtn, refinetripbtn, dayplanbtn;
    ArrayList<LatLng> locations = new ArrayList();
    ArrayList<PlaceData> places = new ArrayList();
    Address saddress;
    String placesjson;
    String selectedTimeofday;
    String selectedDay;
    ArrayList<PlaceData> selecteddayplanlist = new ArrayList<>();
    String KEY_URL = "&key=AIzaSyCz19lSR3gBMyzIUmUg_kkSO_EU4LqgkdA";
    ArrayList<PlaceData> placeDataFetched = new ArrayList<>();
    ListViewAdapter adapter;
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
    Button showselectedbtn, changecitybtn;
    PlaceData place = new PlaceData();
    ArrayList<Integer> placetypes = new ArrayList<>();
    TextView tv;
    Animation animation = null;
    View sharedView;
    private String[] timeofday = {"Morning", "Afternoon", "Evening", "Night Stay"};
    private Place locate;
    private Integer[] icons = {
            R.drawable.food, R.drawable.museum,
            R.drawable.amusement_park, R.drawable.hospital,
            R.drawable.zoo, R.drawable.one
    };
    private PlacetypeAdapter recycleadapter;
    private RecyclerView recyclerView;
    private String[] nameArray = {
            "Museums",
            "Restaurants",
            "Amuzement Parks",
            "Hospitals",
            "Tourist Places",
            "Zoo"};
    private Integer[] id_ = {R.drawable.museum, R.drawable.food, R.drawable.amusement_park, R.drawable.hospital, R.drawable.tour, R.drawable.zoo};
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        tv = (TextView) findViewById(R.id.txt_title);
        clearbtn = (Button) findViewById(R.id.clearbt);
        list = (ListView) findViewById(R.id.data_list);
        sharedView = list;
        list.setFadingEdgeLength(0);
        animation = AnimationUtils.loadAnimation(PlannerActivity.this, R.anim.pushleft_in);
        showselectedbtn = (Button) findViewById(R.id.showselectedplacesbtn);
        place_auto = (AutoCompleteTextView) findViewById(R.id.placeTV);
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
                String cittyyy = place_auto.getText().toString();
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
                    Animation anim = AnimationUtils.loadAnimation(PlannerActivity.this, R.anim.slidetop_to_bottom);
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
                            //  Toast.makeText(PlannerActivity.this, "CLicked:" + selectedName + " position:" + selectedItemPosition, Toast.LENGTH_SHORT).show();
                            tv.setText(selectedName);
                            switch (selectedName) {
                                case "Restaurants":
                                    TYPE_URL = "&radius=800&type=restaurant";
                                    FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                    Log.v("Tag", "FINAL URL in Trip Activity Restaurants:" + FINAL_URL);
                                    changedata(new GetRefinedPlaceData(PlannerActivity.this).execute(FINAL_URL));
//                typetv.setText("Restaurants");
                                    break;
                                case "Museums":
                                    TYPE_URL = "&radius=800&type=museum";
                                    FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                    Log.v("Tag", "FINAL URL in Trip Activity Museums:" + FINAL_URL);
                                    changedata(new GetRefinedPlaceData(PlannerActivity.this).execute(FINAL_URL));
                                    //    typetv.setText("Museums");
                                    break;
                                case "Amuzement Parks":
                                    TYPE_URL = "&radius=25000&type=amusement_park";
                                    FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                    Log.v("Tag", "FINAL URL in Trip Activity amusement_parks:" + FINAL_URL);
                                    changedata(new GetRefinedPlaceData(PlannerActivity.this).execute(FINAL_URL));
                                    //    typetv.setText("Amusement Parks");
                                    break;
                                case "Hospitals":
                                    TYPE_URL = "&radius=25000&type=hospital";
                                    FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                    Log.v("Tag", "FINAL URL in Trip Activity hospital:" + FINAL_URL);
                                    changedata(new GetRefinedPlaceData(PlannerActivity.this).execute(FINAL_URL));
                                    //  typetv.setText("Hospitals");
                                    break;
                                case "Zoo":
                                    TYPE_URL = "&radius=50000&type=zoo";
                                    FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                    Log.v("Tag", "FINAL URL in Trip Activity zoo:" + FINAL_URL);
                                    changedata(new GetRefinedPlaceData(PlannerActivity.this).execute(FINAL_URL));
                                    //  typetv.setText("Zoo");
                                    break;
                                case "Tourist Places":
                                    TYPE_URL = "&rankby=distance&keyword=tourist+places";
                                    FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                                    Log.v("Tag", "FINAL URL in Trip Activity Tourism:" + FINAL_URL);
                                    changedata(new GetRefinedPlaceData(PlannerActivity.this).execute(FINAL_URL));
                                    //   typetv.setText("Tourist Attractions");
                                    break;
                            }
                        }
                    });
                    //       recyclerView.setOnClickListener(this);

                    //                  gridview.setAdapter(new ImageAdapter(PlannerActivity.this, icons));
                    //                gridview.setOnItemClickListener(PlannerActivity.this);

                } else {
                    place_auto.setError("Please Enter City Name");
                }
            }
        });

        // showmapbtn.setEnabled(false);
        //refinetripbtn.setEnabled(false);
        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_auto.setText("");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus, menu);

        return super.onCreateOptionsMenu(menu);
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

        Geocoder g = new Geocoder(PlannerActivity.this);

        List<Address> placeaddress = null;
        try {
            placeaddress = g.getFromLocationName(place_auto.getText().toString(), 1);

        } catch (IOException e) {
            Toast.makeText(PlannerActivity.this, "Location not found", Toast.LENGTH_SHORT)
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
        adapter = new ListViewAdapter(PlannerActivity.this, items, dataaaaaaa, metrics);
        list.setAdapter(adapter);
        animation.setDuration(500);
        list.startAnimation(animation);
        // Item Click Listener for the listview
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                // Getting the Container Layout of the ListView
//                LinearLayout linearLayoutParent = (LinearLayout) container;

                // Getting the inner Linear Layout
            //    LinearLayout linearLayoutChild = (LinearLayout) linearLayoutParent.getChildAt(1);

                // Getting the Country TextView
                //  TextView tvCountry = (TextView) linearLayoutChild.getChildAt(0);
              //  ImageView icon = (ImageView) linearLayoutParent.getChildAt(0);

                // Toast.makeText(getBaseContext(), placeDataFetched.get(position).getPlaceid(), Toast.LENGTH_SHORT).show();

                // Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(PlannerActivity.this, icon, icon.getTransitionName()).toBundle();

                Intent intent = new Intent(PlannerActivity.this, DetailActivity.class);
                intent.putExtra(PLACE_DATA_KEY, placeDataFetched.get(position).getPlaceid());
                intent.putExtra("name", placeDataFetched.get(position).getName());
                intent.putExtra("vicinity", placeDataFetched.get(position).getVicinity());
                intent.putExtra("LATTITUDE", placeDataFetched.get(position).getLattitude());
                intent.putExtra("LONGITUDE", placeDataFetched.get(position).getLongitude());
                startActivity(intent);

                //startActivity(intent, bundle);

/*
                Dialog alertDialog = new Dialog(PlannerActivity.this);
                alertDialog.setTitle("Place Info");
                alertDialog.setContentView(R.layout.dialogimageview);
                final ImageView imageviw = (ImageView) alertDialog.findViewById(R.id.imageView2);
                Picasso.with(PlannerActivity.this).load(placeDataFetched.get(position).getIcon_url()).resize(150,150).into(imageviw);
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
                                    selectedplaces.add(i, adapter.getWorldPopulation().get(i));
                                }
               /* for (int j=itempositions;j<4;j++)
                {
                    selecteddayplanlist.add(j,null);
                }*/
                                Log.v("tag", "selectedplaceslist:" + selectedplaces);
                                // adapter.notifyDataSetChanged();
                                //addData();
                                SparseBooleanArray selected = adapter
                                        .getSelectedIds();
                                // Captures all selected ids with a loop
                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                    if (selected.valueAt(i)) {
                                        PlaceData selecteditem = adapter
                                                .getItem(selected.keyAt(i));
                                        // Remove selected items following the ids
                                        // adapter.remove(selecteditem);
                                        // Log.v("tag", "Num selected:" + adapter.getSelectedCount());
                                    }
                                }
                                modeeeeee.finish();
                                String transitionName = getString(R.string.transition_name);
                                Gson gson1 = new GsonBuilder().create();
                                String gsonStr = gson1.toJson(selectedplaces);
                                Intent intent = new Intent(PlannerActivity.this, DayPlanner.class);
                                intent.putExtra("selectedplaces", gsonStr);
                                place_name = saddress.getFeatureName();
                                Log.v("tag", "place name:" + place_name);
                                intent.putExtra("cityname", place_name);
                                // ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(PlannerActivity.this, sharedView, transitionName);
                                //startActivity(intent, transitionActivityOptions.toBundle());
                                //setResult(RESULT_OK, intent);
                                startActivity(intent);
/*


                                Intent intent = new Intent(ToDoListActivity.this, ToDoDetailsActivity.class);

                *//*
                 Pass in the text of the item that is clicked,
                 so that the text in Details view can be populated accordingly
                  *//*
                                String textOfClickedItem = mItemListAdapter.getItem(position);
                                intent.putExtra("Name", textOfClickedItem);*/


                                // finish();
                                //selecteddayplanlist.clear();
                                //adapter.notifyDataSetChanged();
                            } else if (itempositions < 1) {
                                Toast.makeText(PlannerActivity.this, "Please Select Atleast One Place", Toast.LENGTH_LONG).show();
                            }

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
                Intent intent = new Intent(PlannerActivity.this, DetailActivity.class);
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

    private void removeItem(View v) {

        // removedItems.add(selectedItemId);
        // data.remove(selectedItemPosition);
        // adapter.notifyItemRemoved(selectedItemPosition);
    }
   /* @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                TYPE_URL = "&radius=800&type=restaurant";
                FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                Log.v("Tag", "FINAL URL in Trip Activity Restaurants:" + FINAL_URL);
                changedata(new GetPlaceData(this).execute(FINAL_URL));
//                typetv.setText("Restaurants");
                break;
            case 1:
                TYPE_URL = "&radius=800&type=museum";
                FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                Log.v("Tag", "FINAL URL in Trip Activity Museums:" + FINAL_URL);
                changedata(new GetRefinedPlaceData(this).execute(FINAL_URL));
                //    typetv.setText("Museums");
                break;
            case 2:
                TYPE_URL = "&radius=25000&type=amusement_park";
                FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                Log.v("Tag", "FINAL URL in Trip Activity amusement_parks:" + FINAL_URL);
                changedata(new GetRefinedPlaceData(this).execute(FINAL_URL));
                //    typetv.setText("Amusement Parks");
                break;
            case 3:
                TYPE_URL = "&radius=25000&type=hospital";
                FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                Log.v("Tag", "FINAL URL in Trip Activity hospital:" + FINAL_URL);
                changedata(new GetRefinedPlaceData(this).execute(FINAL_URL));
                //  typetv.setText("Hospitals");
                break;
            case 4:
                TYPE_URL = "&radius=50000&type=zoo";
                FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                Log.v("Tag", "FINAL URL in Trip Activity zoo:" + FINAL_URL);
                changedata(new GetRefinedPlaceData(this).execute(FINAL_URL));
                //  typetv.setText("Zoo");
                break;
            case 5:
                TYPE_URL = "&rankby=distance&keyword=tourism+places";
                FINAL_URL = BASE_URL + LOC_URL + TYPE_URL + KEY_URL;
                Log.v("Tag", "FINAL URL in Trip Activity Tourism:" + FINAL_URL);
                changedata(new GetRefinedPlaceData(this).execute(FINAL_URL));
                //   typetv.setText("Tourist Attractions");
                break;
        }
    }*/

}