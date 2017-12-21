package edu.ucsb.cs.cs184.jaredbjensen.ledger;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SetupFragment extends Fragment {

    View rootView;
    ListView listView;
    EditText editCategories;
    Button buttonClear;
    ImageView addCategory;

    ArrayList<String> categories;

    MainActivity activity;

    CategoryAdapter adapter;

    public SetupFragment() {}

    @Override
    public void onResume() {
        super.onResume();

        activity = ((MainActivity)getActivity());

        activity.getSupportActionBar().setTitle("Setup");

        listView = rootView.findViewById(R.id.list_categories);
        addCategory = rootView.findViewById(R.id.add);
        buttonClear = rootView.findViewById(R.id.button_clear);
        editCategories = rootView.findViewById(R.id.edit_categories);

        categories = ((MainActivity)getActivity()).categories;
        adapter = new CategoryAdapter(getContext(), categories);
        listView.setAdapter(adapter);

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategory = editCategories.getText().toString();
                if (!categories.contains(newCategory)) {
                    categories.add(newCategory);
                    adapter = new CategoryAdapter(getContext(), categories);
                    listView.setAdapter(adapter);
                    ((MainActivity)getContext()).addCategory(newCategory);
                }
                else {
                    Toast.makeText(getContext(),"That category already exists!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.preferences.edit().clear().apply();
                TransactionDatabaseHelper.GetInstance().clearDB();
                activity.resetApp();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_setup, container, false);

        if (container != null) {
            container.removeAllViews();
        }

        return rootView;
    }

}
