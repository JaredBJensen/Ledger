package edu.ucsb.cs.cs184.jaredbjensen.ledger;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

public class TransactionDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    View rootView;
    Button confirmButton, cancelButton, dateButton;
    EditText editAmount, editDescription;
    Spinner category;

    Calendar calendar;
    int year, month, day;
    String type;

    ArrayList<String> categories;

    public TransactionDialogFragment() {}

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        Point dimensions = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(dimensions);

        window.setLayout((int)(dimensions.x*.85), (int)(dimensions.y*.75));
        window.setGravity(Gravity.CENTER);

        type = "expense";

        dateButton = rootView.findViewById(R.id.datepicker_transaction);

        editAmount = rootView.findViewById(R.id.edit_amount);
        editDescription = rootView.findViewById(R.id.edit_description);

        category = rootView.findViewById(R.id.picker_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        confirmButton = rootView.findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(type, dateButton.getText().toString(), Float.valueOf(editAmount.getText().toString()), editDescription.getText().toString(), String.valueOf(category.getSelectedItem()));
                getDialog().dismiss();
            }
        });
        cancelButton = rootView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(year, month, day);
        dateButton.setText(DateFormat.format(MainActivity.SIMPLE_DATE_FORMAT, calendar));

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categories = getArguments().getStringArrayList("categories");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_dialog_transaction, container, false);
        return rootView;
    }

    public void sendResult(String type, String date, float amount, String description, String category) {
        ((MainActivity)getActivity()).onTransactionResult(type, date, amount, description, category);
    }

    public void onDateSet(DatePicker picker, int year, int month, int day) {
        year = picker.getYear();
        month = picker.getMonth();
        day = picker.getDayOfMonth();

        calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        dateButton.setText(DateFormat.format(MainActivity.SIMPLE_DATE_FORMAT, calendar));
    }

    public void setDate() {
        new DatePickerDialog(getContext(), this, year, month, day).show();
    }
}
