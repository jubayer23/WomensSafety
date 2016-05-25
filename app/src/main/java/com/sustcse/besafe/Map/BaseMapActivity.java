package com.sustcse.besafe.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sustcse.besafe.R;
import com.sustcse.besafe.sharedprefs.SaveManager;
import com.sustcse.besafe.utils.GPSTracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

/**
 * Created by Paul on 9/7/15.
 */
public abstract class BaseMapActivity extends AppCompatActivity {

    //protected LatLng mCenterLocation = new LatLng( 39.7392, -104.9903 );

    protected GoogleMap mGoogleMap;

    protected SaveManager saveManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( getMapLayoutId() );

        onNewIntent(getIntent());

        saveManager = new SaveManager(this);


        initMapIfNecessary();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMapIfNecessary();
    }

    protected void initMapIfNecessary() {
        if( mGoogleMap != null ) {
            return;
        }

        mGoogleMap = ( (MapFragment) getFragmentManager().findFragmentById( R.id.map ) ).getMap();

        initMapSettings();
        //initCamera();
    }

   // protected void initCamera() {
    //    CameraPosition position = CameraPosition.builder()
    //           .target( mCenterLocation )
    //           .zoom( getInitialMapZoomLevel() )
    //            .build();
//
   //     mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
   // }

    protected int getMapLayoutId() {
        return R.layout.activity_map;
    }

    protected float getInitialMapZoomLevel() {
        return 9.0f;
    }

    protected abstract void initMapSettings();
}
