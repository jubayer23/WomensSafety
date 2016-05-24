package com.sustcse.besafe.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.sustcse.besafe.R;

import java.util.ArrayList;

/**
 * Created by comsol on 22-Nov-15.
 */
public class ContactListPickerUtils extends AppCompatActivity implements View.OnClickListener {

    LinearLayout btn_save;
    ListView listView;

    // List variables
    public String[] Contacts = {};
    public int[] to = {};
    //public ListView myListView;

    //Button save_button;
    //private TextView phone;
    // private String phoneNumber;
    //private Cursor cursor;
    //EditText editText;

    public static  final String KEY_NAME_LIST = "NAME_LIST";
    public static  final String KEY_NUMBER_LIST = "NUMBER_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_contact);

        btn_save = (LinearLayout) findViewById(R.id.btn_done);
        btn_save.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);

        //editText = (EditText) findViewById(R.id.editText);

        Cursor mCursor = getContacts();
        startManagingCursor(mCursor);

        final ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                mCursor,
                Contacts = new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                to = new int[]{android.R.id.text1});

        //setListAdapter(adapter);
        listView.setAdapter(adapter);
        //myListView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setTextFilterEnabled(true);


    }

    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME};
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        return managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
    }

    @Override
    public void onClick(View src) {
        long[] id = listView.getCheckedItemIds();//  i get the checked contact_id instead of position
        String[] phoneNumber = new String[id.length];

        ArrayList<String> nameList = new ArrayList<String>();
        ArrayList<String> NumberList = new ArrayList<String>();

        for (int i = 0; i < id.length; i++) {

            phoneNumber[i] = getPhoneNumber(id[i]); // get phonenumber from selected id
            nameList.add(getDisplayName(id[i]));
            NumberList.add(getPhoneNumber(id[i]));

        }

        Intent pickContactIntent = new Intent();
        pickContactIntent.putExtra("PICK_CONTACT", phoneNumber);// Add checked phonenumber in intent and finish current activity.
        pickContactIntent.putExtra(KEY_NAME_LIST,nameList);
        pickContactIntent.putExtra(KEY_NUMBER_LIST,NumberList);
        setResult(RESULT_OK, pickContactIntent);
        finish();

    }

    private String getPhoneNumber(long id) {
        String phone = null;
        Cursor phonesCursor = null;
        phonesCursor = queryPhoneNumbers(id);
        if (phonesCursor == null || phonesCursor.getCount() == 0) {
            // No valid number
            //signalError();
            return null;
        } else if (phonesCursor.getCount() == 1) {
            // only one number, call it.
            phone = phonesCursor.getString(phonesCursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        } else {
            phonesCursor.moveToPosition(-1);
            while (phonesCursor.moveToNext()) {

                // Found super primary, call it.
                phone = phonesCursor.getString(phonesCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                break;

            }
        }

        return phone;
    }
    private String getDisplayName(long id) {
        String phone = null;
        Cursor phonesCursor = null;
        phonesCursor = queryPhoneName(id);
        if (phonesCursor == null || phonesCursor.getCount() == 0) {
            // No valid number
            //signalError();
            return null;
        } else if (phonesCursor.getCount() == 1) {
            // only one number, call it.
            phone = phonesCursor.getString(phonesCursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        } else {
            phonesCursor.moveToPosition(-1);
            while (phonesCursor.moveToNext()) {

                // Found super primary, call it.
                phone = phonesCursor.getString(phonesCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                break;

            }
        }

        return phone;
    }
    private Cursor queryPhoneNumbers(long contactId) {
        ContentResolver cr = getContentResolver();
        Uri baseUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY);

        Cursor c = cr.query(dataUri, new String[] { ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY, ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.LABEL }, ContactsContract.Data.MIMETYPE + "=?",
                new String[] { ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE }, null);
        if (c != null && c.moveToFirst()) {
            return c;
        }
        return null;
    }
    private Cursor queryPhoneName(long contactId) {
        ContentResolver cr = getContentResolver();
        Uri baseUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY);

        Cursor c = cr.query(dataUri, new String[] { ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY, ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.LABEL }, ContactsContract.Data.MIMETYPE + "=?",
                new String[] { ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE }, null);
        if (c != null && c.moveToFirst()) {
            return c;
        }
        return null;
    }
}