package com.example.myapplication.fragment;

import static com.example.myapplication.MainActivity.databaseHandler;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.HistoryAdapter;
import com.example.myapplication.model.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView rc_history;
    public static HistoryAdapter historyAdapter;
    private List<History> historyList;
    private TextView tv_delete_all;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        rc_history = view.findViewById(R.id.rc_field);
        tv_delete_all = view.findViewById(R.id.tv_delete_all);
        tv_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (historyList.isEmpty()) {
                    Toast.makeText(getActivity(), "No item to delete", Toast.LENGTH_SHORT).show();

                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                    alert.setTitle("Delete all");
                    alert.setMessage("Are you sure you want to delete all?");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
                            databaseHandler.deleteAllHistory();
                            historyList.clear();
                            historyAdapter.notifyDataSetChanged();
                        }
                    });
                    alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
            }
        });

        historyList = new ArrayList<>();
        historyList = databaseHandler.getAlHistory();
        historyAdapter = new HistoryAdapter(historyList);
        rc_history.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rc_history.setAdapter(historyAdapter);

        return view;
    }
}