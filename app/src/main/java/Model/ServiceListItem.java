package Model;

import android.content.Context;

import Controller.Controller;
import edu.ub.pis2018.g5.a24hservice.R;

public class ServiceListItem {
    private Service service;
    private Controller controller;
    private Context context;

    public ServiceListItem() {
        service = new Service();
        this.controller = Controller.getInstance();
        context = null;
    }

    public ServiceListItem(Service service, Context context) {
        this.service = service;
        this.controller = Controller.getInstance();
        this.context = context;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getServiceName(){
        return this.service.getName();
    }

    public String getServiceType(){
        return this.controller.getCategoriesListShort()[this.service.getType()];
    }

    public String getServiceDistance(){
        String distanceLabel =context.getResources().getString(R.string.distance_label);
        return distanceLabel + " " + controller.getServiceDistanceString(this.service, controller.getCurrentLocation());
    }

    public int getServiceTypePin(){
        return controller.getPinOfServiceRound(this.service.getType());
    }
}
