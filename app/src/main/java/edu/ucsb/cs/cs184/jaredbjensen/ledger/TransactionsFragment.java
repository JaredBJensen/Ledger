package edu.ucsb.cs.cs184.jaredbjensen.ledger;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class TransactionsFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TransactionDatabaseHelper.OnDatabaseChangeListener {

    View rootView;
    Button dateStart, dateEnd;
    RecyclerView recyclerView;
    Spinner categorySpinner;

    String whichPicker;
    Calendar calendar;
    int yearStart, yearEnd, monthStart, monthEnd, dayStart, dayEnd;
    long timestampStart, timestampEnd;

    ArrayList<Transaction> transactions;
    ArrayList<String> categories;
    TransactionAdapter adapter;

    public TransactionsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        categories = new ArrayList<>(args.getStringArrayList("categories"));
        categories.add(0, "All");
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Transactions");

        dateStart = rootView.findViewById(R.id.datepicker_start_trans);
        dateEnd = rootView.findViewById(R.id.datepicker_end_trans);

        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartDate();
            }
        });
        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndDate();
            }
        });

        calendar = Calendar.getInstance();
        yearStart = yearEnd = calendar.get(Calendar.YEAR);
        monthStart =  monthEnd = calendar.get(Calendar.MONTH);
        dayStart = 1;
        dayEnd = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.set(yearStart, monthStart, dayStart);
        timestampStart = calendar.getTimeInMillis();
        dateStart.setText(DateFormat.format(MainActivity.SIMPLE_DATE_FORMAT, calendar));
        calendar.set(yearStart, monthStart, dayEnd);
        timestampEnd = calendar.getTimeInMillis();
        dateEnd.setText(DateFormat.format(MainActivity.SIMPLE_DATE_FORMAT, calendar));

        ArrayList<String> types = new ArrayList<>();
        types.add("All");
        types.add("Expenses");
        types.add("Income");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        recyclerView = rootView.findViewById(R.id.recycler_transactions);

        TransactionDatabaseHelper.GetInstance().Subscribe(this);

        refreshList("WHERE date >= " + timestampStart + " AND date <= " + timestampEnd);
    }

    @Override
    public void onStart() {
        super.onStart();

        categorySpinner = rootView.findViewById(R.id.spinner_category);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String category = categories.get(position);
                if (!category.equals("All")) {
                    refreshList("WHERE date >= " + timestampStart + " AND date <= " + timestampEnd + " AND category = '" + category + "'");
                }
                else refreshList("WHERE date >= " + timestampStart + " AND date <= " + timestampEnd);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_transactions, container, false);

        if (container != null) {
            container.removeAllViews();
        }

        return rootView;
    }

    public void onDateSet(DatePicker picker, int year, int month, int day) {
        if (whichPicker.equals("start")) {
            yearStart = picker.getYear();
            monthStart = picker.getMonth();
            dayStart = picker.getDayOfMonth();

            calendar = Calendar.getInstance();
            calendar.set(yearStart, monthStart, dayStart);
            timestampStart = calendar.getTimeInMillis();
            dateStart.setText(DateFormat.format(MainActivity.SIMPLE_DATE_FORMAT, calendar));
        }
        else if (whichPicker.equals("end")) {
            yearEnd = picker.getYear();
            monthEnd = picker.getMonth();
            dayEnd = picker.getDayOfMonth();

            calendar = Calendar.getInstance();
            calendar.set(yearEnd, monthEnd, dayEnd);
            timestampEnd = calendar.getTimeInMillis();
            dateEnd.setText(DateFormat.format(MainActivity.SIMPLE_DATE_FORMAT, calendar));
        }

        refreshList("WHERE date >= " + timestampStart + " AND date <= " + timestampEnd);
    }

    public void setStartDate() {
        whichPicker = "start";
        new DatePickerDialog(getContext(), this, yearStart, monthStart, dayStart).show();
    }

    public void setEndDate() {
        whichPicker = "end";
        new DatePickerDialog(getContext(), this, yearEnd, monthEnd, dayEnd).show();
    }

    public void refreshList(String filter) {
        transactions = TransactionDatabaseHelper.GetInstance().getTransactions(filter);
        adapter = new TransactionAdapter(transactions, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void OnDatabaseChange() {
        refreshList("WHERE date >= " + timestampStart + " AND date <= " + timestampEnd);
    }
}
