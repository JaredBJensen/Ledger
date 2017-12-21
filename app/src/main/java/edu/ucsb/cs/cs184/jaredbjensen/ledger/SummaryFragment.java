package edu.ucsb.cs.cs184.jaredbjensen.ledger;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SummaryFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TransactionDatabaseHelper.OnDatabaseChangeListener {

    View rootView;

    String whichPicker;
    int yearStart, yearEnd, monthStart, monthEnd, dayStart, dayEnd;
    long timestampStart, timestampEnd;

    Button dateStart, dateEnd;
    RecyclerView recyclerView;

    Calendar calendar;
    ArrayList<Transaction> transactions;
    ArrayList<Integer> colors;

    TableAdapter adapter;

    public SummaryFragment() {}

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Financial Summary");

        recyclerView = rootView.findViewById(R.id.recycler_table);

        dateStart = rootView.findViewById(R.id.datepicker_start);
        dateEnd = rootView.findViewById(R.id.datepicker_end);

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

        populateColors();

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

        TransactionDatabaseHelper.GetInstance().Subscribe(this);

        transactions = TransactionDatabaseHelper.GetInstance().getTransactions("WHERE date >= " + timestampStart + " AND date <= " + timestampEnd);
        setupLineChart();
        setupPieChart();
        setupTable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_summary, container, false);

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

        transactions = TransactionDatabaseHelper.GetInstance().getTransactions("WHERE date >= " + timestampStart + " AND date <= " + timestampEnd);
        setupLineChart();
        setupPieChart();
        setupTable();
    }

    public void setStartDate() {
        whichPicker = "start";
        new DatePickerDialog(getContext(), this, yearStart, monthStart, dayStart).show();
    }

    public void setEndDate() {
        whichPicker = "end";
        new DatePickerDialog(getContext(), this, yearEnd, monthEnd, dayEnd).show();
    }

    @Override
    public void OnDatabaseChange() {
        transactions = TransactionDatabaseHelper.GetInstance().getTransactions("WHERE date >= " + timestampStart + " AND date <= " + timestampEnd);
        setupLineChart();
        setupPieChart();
        setupTable();
    }

    public void setupLineChart() {
        List<Entry> entriesExpense = new ArrayList<>();

        float sumExpenses = 0f;

        for (Transaction transaction : transactions) {
            if (transaction.type.equals("expense")) {
                sumExpenses += transaction.amount;
                entriesExpense.add(new Entry(transaction.date, sumExpenses));
            }
        }

        LineDataSet dataSetExpenses = new LineDataSet(entriesExpense, "Total Expenses");

        dataSetExpenses.setColor(Color.parseColor("#FF0000"));
        dataSetExpenses.setCircleColor(Color.parseColor("#FF0000"));

        List<ILineDataSet> dataSets = new ArrayList<>();
        if (entriesExpense.size() > 0) dataSets.add(dataSetExpenses);
        LineData data = new LineData(dataSets);

        LineChart chart = rootView.findViewById(R.id.chart_line);

        YAxis yAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false);
        yAxis.setValueFormatter(new MyYAxisValueFormatter());

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        xAxis.setAxisMinimum(timestampStart - 1000);
        xAxis.setAxisMaximum(timestampEnd + 1000);
        xAxis.setLabelCount(3, true);
        xAxis.setDrawGridLines(false);

        chart.getDescription().setEnabled(false);
        chart.setData(data);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    public void setupPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (String category : ((MainActivity)getActivity()).categories) {
            float categorySum = TransactionDatabaseHelper.GetInstance().getTransactionSumByCategory("WHERE category = '" + category + "' AND TYPE = 'expense' AND date >= " + timestampStart + " AND date <= " + timestampEnd);
            if (categorySum > 0) entries.add(new PieEntry(categorySum, category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Breakdown");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);

        PieChart chart = rootView.findViewById(R.id.chart_pie);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setData(data);
        chart.invalidate();
    }

    public void setupTable() {
        ArrayList<Summary> summaries = new ArrayList<>();
        float categorySum;

        summaries.add(new Summary(getString(R.string.table_header_category), 0));

        for (String category : ((MainActivity)getActivity()).categories) {
            categorySum = TransactionDatabaseHelper.GetInstance().getTransactionSumByCategory("WHERE category = '" + category + "' AND TYPE = 'expense' AND date >= " + timestampStart + " AND date <= " + timestampEnd);
            summaries.add(new Summary(category, categorySum));
        }

        adapter = new TableAdapter(summaries, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void populateColors() {
        colors = new ArrayList<>();
        colors.add(Color.parseColor("#0000FF"));
        colors.add(Color.parseColor("#FF00FF"));
        colors.add(Color.parseColor("#00FFFF"));
        colors.add(Color.parseColor("#CC0000"));
        colors.add(Color.parseColor("#0000CC"));
        colors.add(Color.parseColor("#00CC00"));
    }
}
