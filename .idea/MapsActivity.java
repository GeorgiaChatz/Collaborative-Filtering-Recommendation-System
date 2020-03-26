package com.example.p3150107.myapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.widget.Button;
import	android.support.v4.app.ActivityCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import android.graphics.Bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private ArrayList<LatLng> latlngs = new ArrayList();
    private ArrayList<Marker> markers = new ArrayList();
    private ArrayList<String> names = new ArrayList<String>();
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<String> longs = new ArrayList<String>();
    private ArrayList<String> lands = new ArrayList<String>();
    private ArrayList<String> cat = new ArrayList<String>();
    double my_long;
    double my_lat;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        my_long = getIntent().getDoubleExtra("my_long",0);
        my_lat = getIntent().getDoubleExtra("my_lat",0);
        longs =  getIntent().getStringArrayListExtra("long");
        lands =  getIntent().getStringArrayListExtra("lang");
        names = getIntent().getStringArrayListExtra("name");
        cat = getIntent().getStringArrayListExtra("category");

        int z=lands.size();
        int s=longs.size();
        int a=names.size();
        for(int i = 0; i < names.size(); i++){
            latlngs.add(new LatLng(Double.parseDouble(lands.get(i)),Double.parseDouble(longs.get(i))));
        }

        for (int i=0; i<names.size(); i++){
            options.position(latlngs.get(i));    //syntetagmenes
            //info window:
            options.title(names.get(i));

            options.snippet( cat.get(i));


            markers.add(mMap.addMarker(options));


        }

        LatLng aueb = new LatLng(my_lat,my_long);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(aueb));
        mMap.addMarker(new MarkerOptions().position(aueb).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12),2000,null);
        mMap.setTrafficEnabled(true);



        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                )

        {
            return;
        }


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

}
