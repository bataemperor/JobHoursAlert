package com.tehnicomsoft.jobhoursalert.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tehnicomsoft.jobhoursalert.R;

import java.util.List;

/**
 * Created by aleksandar on 21.12.16..
 */

public class SpinnerMinutesAdapter extends ArrayAdapter {
    private Context context;
    private List<CharSequence> minutesList;
    private LayoutInflater inflater;

    public SpinnerMinutesAdapter(Context context, int resource, List<CharSequence> itemList) {
        super(context, resource, itemList);
        this.context = context;
        this.minutesList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_minutes_spinner_item, parent, false);
            holder = new ViewHolder();
            holder.tvMinutes = (TextView) convertView.findViewById(R.id.tvMinutes);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvMinutes.setText(minutesList.get(position));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_minutes_spinner_item, parent, false);
            holder = new ViewHolder();
            holder.tvMinutes = (TextView) convertView.findViewById(R.id.tvMinutes);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvMinutes.setText(minutesList.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tvMinutes;
    }
}
