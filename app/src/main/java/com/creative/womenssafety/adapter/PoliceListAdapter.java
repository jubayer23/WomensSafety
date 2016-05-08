package com.creative.womenssafety.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.creative.womenssafety.R;
import com.creative.womenssafety.model.Police;
import com.creative.womenssafety.userInfoView.HospitalInfo;

import java.util.List;


@SuppressLint("DefaultLocale")
public class PoliceListAdapter extends BaseAdapter {

    private List<Police> Displayedplaces;
    private List<Police> Originalplaces;
    private LayoutInflater inflater;
    @SuppressWarnings("unused")
    private Activity activity;


    public PoliceListAdapter(Activity activity, List<Police> polices) {
        this.activity = activity;
        this.Displayedplaces = polices;
        this.Originalplaces = polices;


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

            convertView = inflater.inflate(R.layout.list_police, parent, false);

            viewHolder = new ViewHolder();


            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.police_name);

            viewHolder.number = (TextView) convertView
                    .findViewById(R.id.police_number);

            viewHolder.icon = (ImageView) convertView
                    .findViewById(R.id.img);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Police police = Displayedplaces.get(position);

        viewHolder.name.setText(police.getName());
        viewHolder.number.setText(police.getNumber());



        if(HospitalInfo.police_OR_hospital.equalsIgnoreCase("hospital")){
                viewHolder.icon.setImageResource(R.drawable.hospital);
        }else{
            viewHolder.icon.setImageResource(R.drawable.rab);
        }


        return convertView;
    }

    public void addMore() {
        //this.Displayedplaces.addAll(places);
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        private TextView name;
        private TextView number;
        private ImageView icon;
    }


}