package com.solstice.rssreader;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RssArrayAdapter extends ArrayAdapter<Entry> {

    private final Context context;
    private Entry[] entries;

    public RssArrayAdapter(Context context, List<Entry> entries) {
        super(context, R.layout.rowlayout, entries);
        this.context = context;

        // if the entries list is null, initialize to empty array
        if (entries == null) {
            this.entries = new Entry[] {};
        } else {
            this.entries = entries.toArray(new Entry[] {});
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView titleView = (TextView) rowView.findViewById(R.id.title);
        TextView linkView = (TextView) rowView.findViewById(R.id.link);
        titleView.setText(entries[position].title);
        linkView.setText(entries[position].link);

        return rowView;
    }

}
