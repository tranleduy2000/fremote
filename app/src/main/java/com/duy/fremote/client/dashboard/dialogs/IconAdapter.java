package com.duy.fremote.client.dashboard.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.duy.fremote.R;

public class IconAdapter extends ArrayAdapter<Integer> {
    public IconAdapter(@NonNull Context context, int resource, @NonNull Integer[] objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.list_item_icon, parent, false);
            Integer resourceId = getItem(position);
            ImageView imageView = convertView.findViewById(R.id.img_icon);
            try {
                imageView.setImageResource(resourceId);
            } catch (Exception e) {

            }
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
