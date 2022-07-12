package com.example.myapplication.adapter;

import static com.example.myapplication.MainActivity.databaseHandler;

import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.History;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {
    List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        History history = historyList.get(position);
        holder.tv_name.setText(history.getName());
        holder.tv_time.setText(history.getTime());
        holder.tv_type.setText(history.getType());
        if (history.getAction().equals("Generate")) {
            holder.imgv_action.setImageResource(R.drawable.ic_baseline_add_box_24);
        } else {
            holder.imgv_action.setImageResource(R.drawable.ic_baseline_qr_code_scanner_24);
        }

        holder.imgv_delete_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Delete history");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        History history1 = (History) historyList.toArray()[position];
                        historyList.remove(position);
                        databaseHandler.deleteHistory(history1.getId());
                        notifyDataSetChanged();
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class HistoryHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_time, tv_type;
        ImageView imgv_action;
        ImageView imgv_delete_history;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            imgv_action = itemView.findViewById(R.id.imgv_action);
            imgv_delete_history = itemView.findViewById(R.id.imgv_delete_history);
            tv_type = itemView.findViewById(R.id.tv_type);
        }
    }
}
