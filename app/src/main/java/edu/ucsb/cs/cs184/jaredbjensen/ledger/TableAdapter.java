package edu.ucsb.cs.cs184.jaredbjensen.ledger;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private ArrayList<Summary> summaries;
    private Context context;

    public TableAdapter(ArrayList<Summary> summaries, Context context) {
        this.summaries = summaries;
        this.context = context;
    }

    @Override
    public TableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_summary, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TableAdapter.ViewHolder viewHolder, int position) {
        final int pos = position;
        final Summary summary = summaries.get(position);

        String expenses = NumberFormat.getCurrencyInstance().format(summary.expenses);

        if (summary.expenses == 0 && summary.category.equals(context.getString(R.string.table_header_category))) {
            viewHolder.textCategory.setText(context.getString(R.string.table_header_category));
            viewHolder.textExpenses.setText(context.getString(R.string.table_header_expenses));
            viewHolder.textCategory.setPaintFlags(viewHolder.textCategory.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            viewHolder.textExpenses.setPaintFlags(viewHolder.textExpenses.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }
        else {
            viewHolder.textCategory.setText(summary.category);
            viewHolder.textExpenses.setText(expenses);
        }
    }

    @Override
    public int getItemCount() {
        return summaries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textCategory, textExpenses;

        public ViewHolder(View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.summary_category);
            textExpenses = itemView.findViewById(R.id.summary_expenses);
        }
    }

}
