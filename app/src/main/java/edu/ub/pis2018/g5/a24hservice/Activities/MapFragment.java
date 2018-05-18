package edu.ub.pis2018.g5.a24hservice.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import Controller.Controller;
import edu.ub.pis2018.g5.a24hservice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    private View mView;
    private Controller controller;
    private FloatingActionButton addServiceFlAcBu;
    private FloatingActionButton serviceInformationFlAcBu;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        controller = Controller.getInstance();
        addServiceFlAcBu = (FloatingActionButton) mView.findViewById(R.id.add_service);
        serviceInformationFlAcBu = (FloatingActionButton) mView.findViewById(R.id.info_service);
        controller.disableAddServiceButton(this, addServiceFlAcBu, View.GONE);
        serviceInformationFlAcBu.setVisibility(View.GONE);
        serviceInformationFlAcBu.setActivated(false);
        addServiceFlAcBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addService = new Intent(getActivity(), AddServiceActivity.class);
                startActivity(addService);
            }

        });

        serviceInformationFlAcBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceInfo = new Intent(getActivity(), ServiceInformationActivity.class);
                startActivity(serviceInfo);
            }
        });
        controller.setCurrentMapFragment(this);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        controller.showServicesOnMap(this.getContext(), mGoogleMap);
        controller.updateCurrentPosition(this.getContext());
        if (controller.isCentredAtPosition()) {
            controller.setMapOnPosition(this, mGoogleMap, controller.getCentredPosition());
        } else {
            controller.setMapOnUser(this, mGoogleMap);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setMyLocationEnabled(true);


        controller.mapReady(this.getContext(), mGoogleMap, serviceInformationFlAcBu);
        if(controller.isCentredAtPosition()){
            controller.setMapOnPosition(this, mGoogleMap, controller.getCentredPosition());
        }else{
            controller.setMapOnUser(this, mGoogleMap);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        controller.setCentredAtPosition(false);
        controller.setCurrentMapFragment(this);
    }

}
