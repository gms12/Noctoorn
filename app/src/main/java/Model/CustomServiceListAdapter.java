package Model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ub.pis2018.g5.a24hservice.R;

public class CustomServiceListAdapter extends BaseAdapter{
    private Context context;
    private List<ServiceListItem> services;
    private List<ServiceListItem>tmpServices;
    private CustomFilter cf;
    public CustomServiceListAdapter(Context context, List<ServiceListItem> list){
        this.context = context;
        this.services = list;
        this.tmpServices=list;
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return services.get(position);
    }

    @Override
    public long getItemId(int position) {
        return services.indexOf(getItem(position));
    }

    private static class ViewHolder{
        public ImageView typePictureImVi;
        public TextView serviceNameTeVi, serviceTypeTeVi, serviceDistanceTeVi;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            row = mInflater.inflate(R.layout.service_list_item, null);
            holder = new ViewHolder();

            holder.serviceNameTeVi = (TextView) row.findViewById(R.id.service_name);
            holder.serviceTypeTeVi = (TextView) row.findViewById(R.id.service_type);
            holder.serviceDistanceTeVi = (TextView) row.findViewById(R.id.service_distance);
            holder.typePictureImVi = (ImageView) row.findViewById(R.id.service_pin);

            ServiceListItem service_pos = services.get(position);
            holder.typePictureImVi.setImageResource(service_pos.getServiceTypePin());
            holder.serviceNameTeVi.setText(service_pos.getServiceName());
            holder.serviceTypeTeVi.setText(service_pos.getServiceType());
            holder.serviceDistanceTeVi.setText(service_pos.getServiceDistance());
        }else{
            row = convertView;
        }

        return row;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Filter getFilter(){
        if(cf==null){
            cf=new CustomFilter();
        }
        return cf;
    }
    //AQUEST ADAPTER NO FUNCIONA DEL TOT BE
    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results=new FilterResults();
            if(charSequence!=null && charSequence.length()>0) {
                charSequence = charSequence.toString().toUpperCase();
                List<ServiceListItem> filters = new ArrayList<>();
                for (int i = 0; i < tmpServices.size(); i++) {
                    if (tmpServices.get(i).getService().getName().toUpperCase().contains(charSequence)) {
                        Service service = tmpServices.get(i).getService();
                        ServiceListItem serviceListItem = new ServiceListItem(service, context);
                        filters.add(serviceListItem);
                    }
                }
                results.count=filters.size();
                results.values=filters;
            }
            else {
                results.count=tmpServices.size();
                results.values=tmpServices;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            services=(List<ServiceListItem>)filterResults.values;
            notifyDataSetChanged();
        }
    }
}
