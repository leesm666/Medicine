package kr.co.company.medicine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ListItemRecyclerViewAdapter extends RecyclerView.Adapter<ListItemRecyclerViewAdapter.ListItemRecyclerViewHolder> {
    private ArrayList<ListItem> sList;
    private LayoutInflater sInflate;
    private Context sContext;
    MyDBHelper myHelper;

    public ListItemRecyclerViewAdapter(ArrayList<ListItem> sList, Context sContext) {
        this.sList = sList;
        this.sInflate = LayoutInflater.from(sContext);
        this.sContext = sContext;
    }

    @NonNull
    @Override
    public ListItemRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = sInflate.inflate(R.layout.rv_item_list, viewGroup, false);
        ListItemRecyclerViewHolder viewHolder = new ListItemRecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemRecyclerViewHolder holder, final int i) {
        holder.mediList.setText(sList.get(i).mediList);
        holder.mediName.setText(sList.get(i).mediName);
        holder.startDate.setText(sList.get(i).startDate);
        holder.endDate.setText(sList.get(i).endDate);
        holder.timesPerDay.setText(String.valueOf(sList.get(i).timesPerDay));
    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public static class ListItemRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView mediName, startDate, endDate, timesPerDay, mediList;
        public LinearLayout lay1;

        public ListItemRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            mediList = itemView.findViewById(R.id.txt_rv_item_list_mediList);
            mediName = itemView.findViewById(R.id.txt_rv_item_list_mediName);
            startDate = itemView.findViewById(R.id.txt_rv_item_list_startDate);
            endDate = itemView.findViewById(R.id.txt_rv_item_list_endDate);
            timesPerDay = itemView.findViewById(R.id.txt_rv_item_list_timesPerDay);
        }
    }
}
