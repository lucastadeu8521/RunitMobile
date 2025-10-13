package com.example.ruint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunViewHolder> {

    private List<RunData> runsList;

    public RunAdapter(List<RunData> runsList) {
        this.runsList = runsList;
    }

    @NonNull
    @Override
    public RunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_run, parent, false);
        return new RunViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RunViewHolder holder, int position) {
        RunData run = runsList.get(position);
        holder.bind(run);
    }

    @Override
    public int getItemCount() {
        return runsList.size();
    }

    public void updateData(List<RunData> newRuns) {
        this.runsList = newRuns;
        notifyDataSetChanged();
    }

    static class RunViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate, tvDistance, tvDuration, tvPace;

        public RunViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvPace = itemView.findViewById(R.id.tvPace);
        }

        public void bind(RunData run) {
            tvDate.setText(run.getDateString());
            tvDistance.setText(run.getFormattedDistance());
            tvDuration.setText(run.getFormattedDuration());
            tvPace.setText(run.getFormattedPace());
        }
    }
}