package edu.ub.pis2018.g5.a24hservice.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import Controller.Controller;
import Model.CustomServiceListAdapter;
import Model.Service;
import Model.ServiceListItem;
import edu.ub.pis2018.g5.a24hservice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceListFragment extends Fragment {

     private Controller controller;

     private View sLFragmentView;
     private ListView servicesLiVi;
     List<ServiceListItem> serviceItems;


    public ServiceListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sLFragmentView = inflater.inflate(R.layout.fragment_service_list, container, false);

        controller = Controller.getInstance();

        servicesLiVi = (ListView)sLFragmentView.findViewById(R.id.serviceList);

        updateInformation();

        servicesLiVi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                controller.setCurrentService(serviceItems.get(position).getService());
                controller.updateCurrentPosition(getContext());
                Intent serviceInfo = new Intent(getActivity(), ServiceInformationActivity.class);
                startActivity(serviceInfo);
            }
        });
        /*
        controller.getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

    */
        return sLFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO code her
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInformation();
    }

    public void updateInformation(){
        controller.sortServicesDistance(controller.getCurrentPosition());
        serviceItems = new ArrayList<>();
        for(Service ser : controller.getServices()){
            if(ser.isVisible()) {
                ServiceListItem item = new ServiceListItem(ser, this.getContext());
                serviceItems.add(item);
            }
        }

        final CustomServiceListAdapter adapter = new CustomServiceListAdapter(this.getContext(), serviceItems);
        servicesLiVi.setAdapter(adapter);
    }

}
