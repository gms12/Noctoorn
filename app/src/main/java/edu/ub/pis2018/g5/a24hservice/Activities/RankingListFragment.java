package edu.ub.pis2018.g5.a24hservice.Activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Model.Database;
import Model.User;
import Model.UserSort;
import edu.ub.pis2018.g5.a24hservice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankingListFragment extends Fragment {

    private UserSort sorter = new UserSort();
    public RankingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_ranking_list, container, false);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    alltimeRanking(v);
                }
                if(position == 1){
                    /*monthlyRanking(v);*/
                }
                if(position == 2){
                    /*weeklyRanking(v);*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void alltimeRanking(View v){
        ListView rankingListView = (ListView) v.findViewById(R.id.rankingListView);
        Database.getInstance().updateUsers();
        ArrayList<User> userList = Database.getInstance().getUsers();
        User[] array = new User[userList.size()];
        array = userList.toArray(array);
        ArrayList<User> sortedUsers = sorter.alltimeSort(array);
        LinkedHashMap<String,String> rankingUsers = new LinkedHashMap<>();
        for(int i=sortedUsers.size()-1; i > -1; i--){
            rankingUsers.put(sortedUsers.get(i).getName(),String.valueOf(sortedUsers.get(i).getPoints()));
        }
        List<LinkedHashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(v.getContext(),listItems,R.layout.ranking_list_item,
                new String[]{"User","Points"},
                new int[]{R.id.user,R.id.points});
        Iterator it = rankingUsers.entrySet().iterator();
        while (it.hasNext()){
            LinkedHashMap<String,String> resultsMap = new LinkedHashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("User",pair.getKey().toString());
            resultsMap.put("Points",pair.getValue().toString());
            listItems.add(resultsMap);
        }
        rankingListView.setAdapter(adapter);
    }

    /*private void monthlyRanking(View v){
        ListView rankingListView = (ListView) v.findViewById(R.id.rankingListView);
        Database.getInstance().updateUsers();
        ArrayList<User> userList = Database.getInstance().getUsers();
        User[] array = new User[userList.size()];
        array = userList.toArray(array);
        ArrayList<User> sortedUsers = sorter.monthlySort(array);
        LinkedHashMap<String,String> rankingUsers = new LinkedHashMap<>();
        for(int i=sortedUsers.size()-1; i > -1; i--){
            rankingUsers.put(sortedUsers.get(i).getName(),String.valueOf(sortedUsers.get(i).getPoints()));
        }
        List<LinkedHashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(v.getContext(),listItems,R.layout.ranking_list_item,
                new String[]{"User","Points"},
                new int[]{R.id.user,R.id.points});
        Iterator it = rankingUsers.entrySet().iterator();
        while (it.hasNext()){
            LinkedHashMap<String,String> resultsMap = new LinkedHashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("User",pair.getKey().toString());
            resultsMap.put("Points",pair.getValue().toString());
            listItems.add(resultsMap);
        }
        rankingListView.setAdapter(adapter);
    }

    private void weeklyRanking(View v){
        ListView rankingListView = (ListView) v.findViewById(R.id.rankingListView);
        Database.getInstance().updateUsers();
        ArrayList<User> userList = Database.getInstance().getUsers();
        User[] array = new User[userList.size()];
        array = userList.toArray(array);
        ArrayList<User> sortedUsers = sorter.weeklySort(array);
        LinkedHashMap<String,String> rankingUsers = new LinkedHashMap<>();
        for(int i=sortedUsers.size()-1; i > -1; i--){
            rankingUsers.put(sortedUsers.get(i).getName(),String.valueOf(sortedUsers.get(i).getPoints()));
        }
        List<LinkedHashMap<String, String>> listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(v.getContext(),listItems,R.layout.ranking_list_item,
                new String[]{"User","Points"},
                new int[]{R.id.user,R.id.points});
        Iterator it = rankingUsers.entrySet().iterator();
        while (it.hasNext()){
            LinkedHashMap<String,String> resultsMap = new LinkedHashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("User",pair.getKey().toString());
            resultsMap.put("Points",pair.getValue().toString());
            listItems.add(resultsMap);
        }
        rankingListView.setAdapter(adapter);
    }
*/
}
