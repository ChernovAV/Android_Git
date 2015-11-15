package com.chernov.android.android_git;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Adapter extends ArrayAdapter<Item> {

    private final List<Item> items;
    private final Context context;

    public Adapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.name);
            holder.language = (TextView) convertView.findViewById(R.id.language);
            holder.forks = (TextView) convertView.findViewById(R.id.forks);
            holder.star = (TextView) convertView.findViewById(R.id.stars);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(items.get(position).getTitle());
        holder.language.setText(items.get(position).getLanguage());
        holder.forks.setText(items.get(position).getForks());
        holder.star.setText(items.get(position).getStars());

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView language;
        TextView forks;
        TextView star;
    }
}
