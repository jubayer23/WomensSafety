package com.creative.womenssafety.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creative.womenssafety.R;
import com.creative.womenssafety.model.History;
import com.creative.womenssafety.model.Police;

import java.util.List;


@SuppressLint("DefaultLocale")
public class HistoryListAdapter extends BaseAdapter {

    private List<History> Displayedplaces;
    private List<History> Originalplaces;
    private LayoutInflater inflater;
    @SuppressWarnings("unused")
    private Activity activity;


    public HistoryListAdapter(Activity activity, List<History> histories) {
        this.activity = activity;
        this.Displayedplaces = histories;
        this.Originalplaces = histories;


        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Displayedplaces.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_history, parent, false);

            viewHolder = new ViewHolder();


            viewHolder.ll_layout = (LinearLayout) convertView
                    .findViewById(R.id.history_layout);

            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.history_sms);

            viewHolder.time = (TextView) convertView
                    .findViewById(R.id.history_time);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        History history = Displayedplaces.get(position);

        if (history.getSeen().equalsIgnoreCase("false")) {
            viewHolder.ll_layout.setBackgroundColor(activity.getResources().getColor(R.color.unseen));
        } else {
            viewHolder.ll_layout.setBackgroundColor(activity.getResources().getColor(R.color.seen));
        }

        viewHolder.name.setText(history.getSms());
        viewHolder.time.setText(history.getEvent_time());


        return convertView;
    }

    public void addMore() {
        //this.Displayedplaces.addAll(places);
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        private TextView name;
        private TextView time;
        LinearLayout ll_layout;
    }


}