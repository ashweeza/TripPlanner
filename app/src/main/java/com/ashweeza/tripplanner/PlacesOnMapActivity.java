package com.ashweeza.tripplanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static com.ashweeza.tripplanner.R.id.mapfrag;

public class PlacesOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    String placesjson;
    ArrayList<PlaceData> places = new ArrayList();
    ListViewAdapter adapter;
    ListView lv;
    GoogleMap gMap;
    Marker marker;
    SupportMapFragment mapFragment;
    private DisplayMetrics metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_on_map);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        placesjson = getIntent().getExtras().getString("selectedplaces");
        lv = (ListView) this.findViewById(R.id.selectedplacesonmaplist);
        Gson gson = new GsonBuilder().create();
        final ArrayList<PlaceData> selectedplaces = gson.fromJson(placesjson, new TypeToken<ArrayList<PlaceData>>() {
        }.getType());
        places.addAll(selectedplaces);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(mapfrag);
        mapFragment.getMapAsync(this);
        //adapter = new JustAdapter(DayPlanner.this, R.layout.nearby_item, places);
        adapter = new ListViewAdapter(this, R.layout.items, places, metrics);
        lv.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.clear();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLngBounds bounds;
        for (int k = 0; k < places.size(); k++) {
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(places.get(k).getLattitude(), places.get(k).getLongitude())));
            marker.setSnippet(places.get(k).getVicinity());
            marker.setTitle(places.get(k).getName());
            marker.showInfoWindow();
            builder.include(marker.getPosition());
        }
        bounds = builder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 20);

        gMap.animateCamera(cu);


    }


}
