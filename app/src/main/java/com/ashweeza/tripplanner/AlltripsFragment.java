package com.ashweeza.tripplanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class AlltripsFragment extends Fragment {
    ListView tripslistview;
    TripsAdapter adapter;
    ArrayList<Trip> tripslist = new ArrayList<>();
    DataBaseHelper mydb;
    //  ArrayList<String> trips = new ArrayList<>();
    String[] trips = new String[]{"San", "Houston", "RockyHill","New York"};
    Trip trip = new Trip();
    ActionMode modeeeeee;
    ArrayList<Trip> selectedtrips = new ArrayList<>();
    boolean isactive = false;
    Cursor res;

    public AlltripsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
       // viewAllData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alltrips, container, false);
        tripslistview = (ListView) rootView.findViewById(R.id.tripslist);
        mydb = new DataBaseHelper(getActivity());
        viewAllData();
        adapter = new TripsAdapter(getActivity(), R.layout.tripslistitem, tripslist);
        tripslistview.setAdapter(adapter);

        tripslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), WholeTrip.class);
                // intent.putExtra(PLACE_DATA_KEY, placeDataFetched.get(position));
                intent.putExtra("tablename", tripslist.get(position).getTripname());
                startActivity(intent);
            }
        });
        tripslistview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        tripslistview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                modeeeeee = mode;
                // Capture total checked items
                final int checkedCount = tripslistview.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                adapter.toggleSelection(position);
                int itempositions = adapter.getSelectedCount();
                if (itempositions > 0) {
                    for (int i = 0; i < itempositions; i++) {
                        selectedtrips.add(i, adapter.getWorldPopulation().get(i));
                    }
               /* for (int j=itempositions;j<4;j++)
                {
                    selecteddayplanlist.add(j,null);
                }*/
                    Log.v("tag", "selectedtripslist:" + selectedtrips);

                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menus, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = adapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Trip selecteditem = adapter.getItem(selected.keyAt(i));
                                mydb.deletefromtablesoftables(selecteditem.getTripname());
                                mydb.deleteTable(selecteditem.getTripname());
                                Log.v("tag", "id sent to db:" + String.valueOf(adapter.getPosition(selecteditem) + 1));
                                // Remove selected items following the ids
                                adapter.remove(selecteditem);

                            }
                        }
                        int count = adapter.getCount();
                        if(count==0)
                        {
                            mydb.droptableoftables();
                        }
                        // Close CAB
                        mode.finish();

                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.removeSelection();
            }
        });
        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();
        return rootView;
    }

    public void viewAllData() {
        setUserVisibleHint(false);
        res = mydb.getAllData("tableoftables");

        while (res.moveToNext()) {
            Trip trip = new Trip();
            trip.setTripname(res.getString(1));
            trip.setNumdays(String.valueOf(res.getString(2)));
            tripslist.add(trip);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isactive = true;
            if (res.getCount() == 0) {
                Log.v("tag", "Cursor No Data");
                showMessage("Info", "No Trips Found!!");
                // showMessage("Info", "NO Trips found");
            }

        } else {
            isactive = false;
        }
    }

    public void showMessage(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((FirstActivity) getActivity()).setCurrentItem(0, true);
            }
        });
        builder.show();
    }

}
