package com.example.ruint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SearchableRunAdapter extends RecyclerView.Adapter<SearchableRunAdapter.RunSearchViewHolder> implements Filterable {

    public interface OnMarkedChangedListener {
        void onMarkedChanged(Set<String> markedIds);
    }

    private final List<RunData> allRuns;
    private final List<RunData> filteredRuns;
    private final Set<String> markedRunIds;
    private final OnMarkedChangedListener markedChangedListener;

    public SearchableRunAdapter(List<RunData> runs, Set<String> markedRunIds, OnMarkedChangedListener listener) {
        this.allRuns = new ArrayList<>(runs);
        this.filteredRuns = new ArrayList<>(runs);
        this.markedRunIds = new HashSet<>(markedRunIds);
        this.markedChangedListener = listener;
    }

    @NonNull
    @Override
    public RunSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_run, parent, false);
        return new RunSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RunSearchViewHolder holder, int position) {
        RunData run = filteredRuns.get(position);
        boolean isMarked = markedRunIds.contains(run.getId());
        holder.bind(run, isMarked, v -> toggleMark(run));
    }

    @Override
    public int getItemCount() {
        return filteredRuns.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint != null ? constraint.toString().toLowerCase(Locale.getDefault()) : "";
                List<RunData> result = new ArrayList<>();

                if (query.isEmpty()) {
                    result.addAll(allRuns);
                } else {
                    for (RunData run : allRuns) {
                        if (matchesQuery(run, query)) {
                            result.add(run);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = result;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredRuns.clear();
                //noinspection unchecked
                filteredRuns.addAll((List<RunData>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void updateData(List<RunData> runs) {
        allRuns.clear();
        allRuns.addAll(runs);
        filteredRuns.clear();
        filteredRuns.addAll(runs);
        notifyDataSetChanged();
    }

    private void toggleMark(RunData run) {
        if (markedRunIds.contains(run.getId())) {
            markedRunIds.remove(run.getId());
        } else {
            markedRunIds.add(run.getId());
        }
        notifyDataSetChanged();
        if (markedChangedListener != null) {
            markedChangedListener.onMarkedChanged(new HashSet<>(markedRunIds));
        }
    }

    private boolean matchesQuery(RunData run, String query) {
        return run.getDateString().toLowerCase(Locale.getDefault()).contains(query)
                || run.getFormattedDistance().toLowerCase(Locale.getDefault()).contains(query)
                || run.getFormattedPace().toLowerCase(Locale.getDefault()).contains(query)
                || run.getFormattedDuration().toLowerCase(Locale.getDefault()).contains(query);
    }

    static class RunSearchViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;
        private final TextView tvDistance;
        private final TextView tvPace;
        private final TextView tvDuration;
        private final Button btnMark;

        RunSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvPace = itemView.findViewById(R.id.tvPace);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            btnMark = itemView.findViewById(R.id.btnMark);
        }

        void bind(RunData run, boolean isMarked, View.OnClickListener onClickListener) {
            tvDate.setText(run.getDateString());
            tvDistance.setText(run.getFormattedDistance());
            tvPace.setText(run.getFormattedPace());
            tvDuration.setText(run.getFormattedDuration());
            btnMark.setText(isMarked ? "Marcado" : "Marcar");
            btnMark.setEnabled(true);
            btnMark.setOnClickListener(onClickListener);
        }
    }
}
