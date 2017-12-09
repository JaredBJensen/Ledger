package edu.ucsb.cs.cs184.jaredbjensen.ledger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private ArrayList<Transaction> transactions;
    private Context context;

    public TransactionAdapter(ArrayList<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View imageView = inflater.inflate(R.layout.item_transaction, parent, false);
        ViewHolder viewHolder = new ViewHolder(imageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TransactionAdapter.ViewHolder viewHolder, int position) {
        final int pos = position;
        final Transaction transaction = transactions.get(position);

        String date = new SimpleDateFormat(MainActivity.SIMPLE_DATE_FORMAT).format(new Date(transaction.date));
        String amount = NumberFormat.getCurrencyInstance().format(transaction.amount);

        viewHolder.textDate.setText(date);
        viewHolder.textAmount.setText(amount);
        viewHolder.textDescription.setText(transaction.description);
        viewHolder.textCategory.setText(transaction.category);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textDate, textAmount, textDescription, textCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.item_date);
            textAmount = itemView.findViewById(R.id.item_amount);
            textDescription = itemView.findViewById(R.id.item_description);
            textCategory = itemView.findViewById(R.id.item_category);
        }
    }

}
