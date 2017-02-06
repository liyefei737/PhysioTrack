package com.pengona.sqtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mehmetatmaca on 2016-11-19.
 */

public class ItemArrayAdapter extends ArrayAdapter<String[]> {


    private List<String[]> scoreList = new ArrayList<String[]>();

    static class ItemViewHolder {
        TextView name;
        TextView score;
    }

    public ItemArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(String[] object) {
        scoreList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.scoreList.size();
    }

    @Override
    public String[] getItem(int position) {
        return this.scoreList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemViewHolder viewHolder;
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_list_item, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.name = (TextView) row.findViewById(R.id.name);
            viewHolder.score = (TextView) row.findViewById(R.id.score);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder) row.getTag();
        }

        String[] stat = getItem(position);
        viewHolder.name.setText(stat[0]);
        viewHolder.score.setText(stat[14]);
        return row;
    }

}

