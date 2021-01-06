package com.example.canlua;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class GridViewWeightAdapter extends ArrayAdapter<Double> {
    List<Double> listWeight;
    LayoutInflater inflater;

    public GridViewWeightAdapter(@NonNull Context context, int resource, @NonNull List<Double> objects) {
        super(context, resource, objects);
        listWeight = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.gridview_input_weight_item, parent, false);
            holder.textView_number = convertView.findViewById(R.id.gridview_item_textView_number);
            holder.textView_weight = convertView.findViewById(R.id.gridview_item_textView_weight);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.textView_weight.setText(listWeight.get(position).toString());
        int number = position + 1;
        holder.textView_number.setText("#" + number);
        return convertView;
    }

    class ViewHolder {
        TextView textView_number;
        TextView textView_weight;
    }
}
