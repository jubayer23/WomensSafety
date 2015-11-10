package com.creative.womenssafety;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Ikhtiar on 11/9/2015.
 */
public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private double lattitude, langitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras.containsKey("lattitude") && extras.containsKey("langitude")) {
            lattitude = extras.getDouble("lattitude");
            langitude = extras.getDouble("langitude");
            setUpMapIfNeeded();
        } else {
            lattitude = 0;
            langitude = 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        LatLng position = new LatLng(lattitude, langitude);
        mMap.addMarker(new MarkerOptions().position(position).title("VICTIM"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }
}