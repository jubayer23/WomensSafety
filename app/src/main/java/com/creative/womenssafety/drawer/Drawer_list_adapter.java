package com.creative.womenssafety.drawer;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creative.womenssafety.MainActivity;
import com.creative.womenssafety.R;
import com.creative.womenssafety.appdata.AppConstant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Syed Ikhtiar Ahmed on 11/22/2015.
 */
public class Drawer_list_adapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;


    public Drawer_list_adapter(Context context, List<String> listDataHeader,
                               HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }


    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (this._listDataHeader == null) {
            Log.e("Debug", "mListDataHeader is null.");
            return 0;
        } else if (groupPosition < 0 || groupPosition >= this._listDataHeader.size()) {
            Log.e("Debug", "position invalid: " + groupPosition);
            return 0;
        } else if (this._listDataHeader.get(groupPosition) == null) {
            Log.e("Debug", "Value of mListDataHeader at position is null: " + groupPosition);
            return 0;
        } else if (this._listDataChild == null) {
            Log.e("Debug", "mListDataChild is null.");
            return 0;
        } else if (!this._listDataChild.containsKey(this._listDataHeader.get(groupPosition))) {
            Log.e("Debug", "No key: " + this._listDataHeader.get(groupPosition));
            return 0;
        } else if (this._listDataChild.get(this._listDataHeader.get(groupPosition)) == null) {
            Log.e("Debug", "Value at key is null: " + this._listDataHeader.get(groupPosition));
            return 0;
        } else {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        LinearLayout history_alert_layout = (LinearLayout) convertView.findViewById(R.id.drawer_item_layout);
        TextView history_alert_amount = (TextView) convertView.findViewById(R.id.drawer_item_history_available);
        ImageView drawer_item_icon = (ImageView) convertView.findViewById(R.id.drawer_item_icon);

        drawer_item_icon.setImageResource(getResourceId(headerTitle));

        if (AppConstant.NUM_OF_UNSEEN_HISTORY > 0 && headerTitle.equalsIgnoreCase(MainActivity.DRAWER_LIST_HISTORY)) {
            history_alert_layout.setVisibility(View.VISIBLE);

            history_alert_amount.setText(String.valueOf(AppConstant.NUM_OF_UNSEEN_HISTORY));
        } else {
            history_alert_layout.setVisibility(View.GONE);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.drawer_item_text);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    private int getResourceId(String headerTitle) {
        if (headerTitle.equalsIgnoreCase(MainActivity.DRAWER_LIST_HISTORY)) {
            return R.drawable.drawer_history;
        }
        if (headerTitle.equalsIgnoreCase(MainActivity.DRAWER_LIST_HOSPITAL)) {
            return R.drawable.drawer_history;
        }
        if (headerTitle.equalsIgnoreCase(MainActivity.DRAWER_LIST_INFORMATION)) {
            return R.drawable.drawer_information;
        }
        if (headerTitle.equalsIgnoreCase(MainActivity.DRAWER_LIST_LOGOUT)) {
            return R.drawable.drawer_logout;
        }
        if (headerTitle.equalsIgnoreCase(MainActivity.DRAWER_LIST_MANAGE_SMS)) {
            return R.drawable.drawer_sms;
        }
        if (headerTitle.equalsIgnoreCase(MainActivity.DRAWER_LIST_TUTORIAL)) {
            return R.drawable.drawer_howtouse;
        }
        if (headerTitle.equalsIgnoreCase(MainActivity.DRAWER_LIST_SETTING)) {
            return R.drawable.drawer_setting;
        }
        if (headerTitle.equalsIgnoreCase(MainActivity.DRAWER_LIST_ABOUTUS)) {
            return R.drawable.drawer_about_us;
        }

        return R.drawable.drawer_man;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.drawer_child_item_text);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
