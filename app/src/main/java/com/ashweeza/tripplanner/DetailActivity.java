package com.ashweeza.tripplanner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import static com.ashweeza.tripplanner.R.id.map;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    public final static String PLACE_DATA_KEY = "Weather Hour";
    final String BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    TextView dname, daddress, drating, dopeninghours;
    LatLng placelatlng;
    PlaceData object;
    //ArrayList<LatLng> locations = new ArrayList<>();
    String placesjson;
    GoogleMap gMap;
    Marker marker;
    SupportMapFragment mapFragment;
    Boolean status = false;
    ViewFlipper viewFlipper;
    String PLACEID_URL = "";
    String KEY_URL = "&key=AIzaSyCz19lSR3gBMyzIUmUg_kkSO_EU4LqgkdA";
    String FINAL_URL = "";
    ImageView dwebsite, dphone;
    String website;
    String phone;
    CollapsingToolbarLayout collapsingToolbar;
    Toolbar toolbar;
    String name = "";
    String vicinity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placedetails);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbar.setTitle("Details Activity");
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        Intent intent = getIntent();
        //object = (PlaceData) intent.getExtras().getSerializable(PlannerActivity.PLACE_DATA_KEY);
        PLACEID_URL = intent.getStringExtra(PLACE_DATA_KEY);
        // Log.v("tag", object.getPlaceid());
        placelatlng = new LatLng(intent.getDoubleExtra("LATTITUDE", 0.0), intent.getDoubleExtra("LONGITUDE", 0.0));
        name = intent.getStringExtra("name");
        name = intent.getStringExtra("vicinity");

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        FINAL_URL = BASE_URL + PLACEID_URL + KEY_URL;
        Log.v("tag", "Details Activity final URL:" + FINAL_URL);
        new GetPlaceDetails(DetailActivity.this).execute(FINAL_URL);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        dname = (TextView) findViewById(R.id.dnameTV);
        daddress = (TextView) findViewById(R.id.daddressTV);
        //   drating = (TextView) findViewById(R.id.dratingTV);
        dphone = (ImageView) findViewById(R.id.dphoneTV);
        dwebsite = (ImageView) findViewById(R.id.websiteTV);
        dopeninghours = (TextView) findViewById(R.id.dopeninghoursTV);
        //   dwebsite.setPaintFlags(dwebsite.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        // dphone.setPaintFlags(dwebsite.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }

    public void showDetails(PlaceDetails detailsList) {
        PlaceDetails details = detailsList;
        dname.setText(name);
        daddress.setText(details.getAddress());
        //  drating.setText(String.valueOf(object.getRating()));
        website = (details.getWebsite());
        phone = details.getPhonenumber();
        dphone.setContentDescription(phone);
        dwebsite.setContentDescription(website);
        //dwebsite.setText(website);
        dopeninghours.setText(details.getOpeninghours());
        String[] photosarray = details.getPhotos();
        for (int i = 0; i < photosarray.length; i++) {
            //  This will create dynamic image view and add them to ViewFlipper
            setFlipperImage(photosarray[i]);
        }
        viewFlipper.startFlipping();
        viewFlipper.setFlipInterval(1500);
        dwebsite.setOnClickListener(this);
        dphone.setOnClickListener(this);

    }

    private void setFlipperImage(String res) {
        // Log.i("Set Filpper Called", res + "");
        ImageView image = new ImageView(getApplicationContext());
        Picasso.with(DetailActivity.this).load(res).into(image);
        //image.setBackgroundResource(res);
        viewFlipper.addView(image);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.clear();

    /*    if (status) {
          *//*  LatLngBounds.Builder builder = new LatLngBounds.Builder();
            LatLngBounds bounds;
            for (int k = 0; k < locations.size(); k++) {
                marker = gMap.addMarker(new MarkerOptions().position(locations.get(k)));
                builder.include(marker.getPosition());
            }
            bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 20);

            gMap.animateCamera(cu);*/
        /*
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


        }*/
        marker = gMap.addMarker(new MarkerOptions().position(placelatlng));
        marker.setTitle(name);
        marker.setSnippet(vicinity);
        //marker.showInfoWindow();
        CameraUpdate cu2 = CameraUpdateFactory.newLatLngZoom(placelatlng, 18);
        gMap.moveCamera(cu2);
        gMap.animateCamera(cu2);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.websiteTV:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                //intent.setType("url");
                startActivity(intent);
                break;
            case R.id.dphoneTV:
                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent1);
                break;
        }
    }
/*
    @Override
    public void onInfoWindowClick(Marker markerinfo) {
        locname.setVisibility(View.VISIBLE);
        locvicinity.setVisibility(View.VISIBLE);
        locname.setText(markerinfo.getTitle());
        locvicinity.setText(markerinfo.getSnippet());
    }*/
}
