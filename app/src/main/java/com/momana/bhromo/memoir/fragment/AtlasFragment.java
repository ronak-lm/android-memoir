package com.momana.bhromo.memoir.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.momana.bhromo.memoir.R;
import com.momana.bhromo.memoir.activity.ViewActivity;
import com.momana.bhromo.memoir.database.NotesDatabase;
import com.momana.bhromo.memoir.model.Note;

import java.util.ArrayList;

public class AtlasFragment extends Fragment implements OnMapReadyCallback, OnMarkerClickListener {

    public static final int PERMISSION_ACCESS_COARSE_LOCATION = 11;
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 12;

    private ArrayList<Note> noteList;
    private MapView mMapView;
    private GoogleMap mMap;
    private Bundle mBundle;

    // Fragment Life Cycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_atlas, container, false);

        MapsInitializer.initialize(getActivity());
        mMapView = (MapView) v.findViewById(R.id.map_view);
        mMapView.onCreate(mBundle);
        mMapView.getMapAsync(this);

        return v;
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mMap != null) {
            refreshMarkers();
        }
    }
    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    // Map functions
    public void refreshMarkers() {
        mMap.clear();

        // Find notes with location
        noteList = new ArrayList<>();
        ArrayList<Note> notes = NotesDatabase.getInstance(getContext()).getNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).location.placeName.length() != 0) {
                noteList.add(notes.get(i));
                LatLng latLng = new LatLng(notes.get(i).location.latitude, notes.get(i).location.longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title(notes.get(i).title));
            }
        }

        if (noteList.size() > 0) {
            // Zoom to markers
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    mMap.setOnCameraChangeListener(null);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (int i = 0; i < noteList.size(); i++) {
                        builder.include(new LatLng(noteList.get(i).location.latitude, noteList.get(i).location.longitude));
                    }
                    LatLngBounds bounds = builder.build();
                    int padding = 200; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);
                }
            });
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        refreshMarkers();

        // On Marshmallow request for permissions first before showing location
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_ACCESS_FINE_LOCATION);
            } else {
                return;
            }
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_COARSE_LOCATION);
            } else {
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        for (int i = 0; i < noteList.size(); i++) {
            if (noteList.get(i).title.equals(title)) {
                Intent intent = new Intent(getActivity(), ViewActivity.class);
                intent.putExtra(ViewActivity.ACTION_TYPE_KEY, ViewActivity.ACTION_TYPE_EDIT);
                intent.putExtra(ViewActivity.NOTE_ID_KEY, noteList.get(i).id);
                startActivity(intent);
            }
        }
        return false;
    }

}
