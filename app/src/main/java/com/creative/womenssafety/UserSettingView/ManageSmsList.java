package com.creative.womenssafety.UserSettingView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.creative.womenssafety.R;
import com.creative.womenssafety.sharedprefs.SaveManager;
import com.creative.womenssafety.utils.ContactListPickerUtils;

import java.util.ArrayList;

/**
 * Created by comsol on 21-Nov-15.
 */
public class ManageSmsList extends Activity {

    private LinearLayout btn_add;
    ListView listView;

    private static final int CONTACT_PICKER_CODE = 1001;

    SaveManager saveData;

    ArrayList<String> PnameList;
    ArrayList<String> PnumberList;
   ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managesmslist);

        init();

        PnameList = saveData.getPhoneNameArray();
        PnumberList = saveData.getPhoneNumberArray();
       // Log.d("DEBUG_phoneno3",PnumberList.get(0));

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,PnameList);
        listView.setAdapter(adapter);



        if(PnumberList.size() == 0)
        {
            Intent intent = new Intent(ManageSmsList.this, ContactListPickerUtils.class);
            startActivityForResult(intent, CONTACT_PICKER_CODE);
        }

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageSmsList.this, ContactListPickerUtils.class);
                startActivityForResult(intent, CONTACT_PICKER_CODE);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PnameList.remove(position);
                PnumberList.remove(position);

                saveData.setPhoneName(PnameList);
                saveData.setPhoneNumber(PnumberList);

                adapter.notifyDataSetChanged();

                Toast.makeText(ManageSmsList.this,"Contact Removed",Toast.LENGTH_LONG).show();

                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ManageSmsList.this,"Long Press To Remove The Contact From List",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {

        saveData = new SaveManager(this);

        btn_add = (LinearLayout) findViewById(R.id.btn_add);
        listView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == CONTACT_PICKER_CODE) {


                if (data != null) {

                    String[] temp = data.getStringArrayExtra("PICK_CONTACT");
                    ArrayList<String> nameList = data.getStringArrayListExtra(ContactListPickerUtils.KEY_NAME_LIST);
                    ArrayList<String> numberList = data.getStringArrayListExtra(ContactListPickerUtils.KEY_NUMBER_LIST);

                    PnameList.addAll(nameList);
                    PnumberList.addAll(numberList);
                   // Log.d("DEBUG", temp[0]);
                    saveData.setPhoneName(PnameList);
                    saveData.setPhoneNumber(PnumberList);

                    adapter.notifyDataSetChanged();
                }
            }

        }
    }
}
