package it.unipr.bozzolini.mobdev.mywallet;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecycleviewAdapter extends RecyclerView.Adapter<RecycleviewAdapter.ViewHolder> {

    private ArrayList<Expense> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewDate, textViewCategory, textViewAmount, textViewNotes;
        public ViewHolder(View v) {
            super(v);
            textViewDate = v.findViewById(R.id.textRecycleViewDate);
            textViewCategory = v.findViewById(R.id.textRecycleViewCategory);
            textViewAmount = v.findViewById(R.id.textRecycleViewAmount);
            textViewNotes = v.findViewById(R.id.textRecycleViewNotes);
        }
    }

    public RecycleviewAdapter(ArrayList<Expense> mDataset) {
        this.mDataset = mDataset;
    }


    @Override
    public RecycleviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.textViewDate.setText(mDataset.get(position).getDate());
        holder.textViewCategory.setText(mDataset.get(position).getCategory());
        holder.textViewAmount.setText(mDataset.get(position).getAmount());
        holder.textViewNotes.setText(mDataset.get(position).getNotes());

    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void removeItem(int position) {

        mDataset.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRangeChanged(position, mDataset.size());
        notifyItemRemoved(position);
    }
}
