package edu.ucsb.cs.cs184.jaredbjensen.ledger;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> categories;

    public CategoryAdapter(Context context, ArrayList<String> modelList) {
        this.context = context;
        this.categories = modelList;
    }
    @Override
    public int getCount() {
        return categories.size();
    }
    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_category, null);
            TextView name = convertView.findViewById(R.id.text_category_name);
            ImageView remove = convertView.findViewById(R.id.remove);

            String category = categories.get(position);

            name.setText(category);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String remove = categories.remove(position);
                    ((MainActivity)context).removeCategory(remove);
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }

}
