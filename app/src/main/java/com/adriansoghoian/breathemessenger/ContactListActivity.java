package com.adriansoghoian.breathemessenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ContactListActivity extends ActionBarActivity {

    ArrayList<String> contactListContents = new ArrayList<>();
    List<Contact> contactList;
    CustomListAdapter contactListAdapter;
    ListView contactListView;
    Contact tempContact;
    String tempPin;
    String yourPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        SharedPreferences preferences = this.getSharedPreferences("com.adriansoghoian.breathemessenger", Context.MODE_PRIVATE);
        yourPin = preferences.getString("pin", null); // fetches current users pin to avoid displaying it

        Button newContact = (Button)findViewById(R.id.newContact);
        ListView contactListView = (ListView)findViewById(R.id.contactListView); // inflate view

        contactList = Contact.getAllContacts(); // fetches all contacts in DB (including user's own info)

        for (int i=0; i < contactList.size(); i++) {
            System.out.println("I: " + i);
            System.out.println("Your PIN:" + yourPin);
            tempPin = contactList.get(i).pin;
            System.out.println("Their pin: " + tempPin);
            if (!yourPin.equals(tempPin)) {
                contactListContents.add(contactList.get(i).pin);
            }
        }
        contactListAdapter = new CustomListAdapter(ContactListActivity.this, R.layout.custom_list, contactListContents);
        contactListView.setAdapter(contactListAdapter); // completes attaching adapter to listView

        newContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewContactActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_conversations) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CustomListAdapter extends ArrayAdapter {

        private Context mContext;
        private int id;
        private List<String> items ;

        public CustomListAdapter(Context context, int textViewResourceId , List<String> list )
        {
            super(context, textViewResourceId, list);
            mContext = context;
            id = textViewResourceId;
            items = list ;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent)
        {
            View mView = v;
            if(mView == null){
                LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(id, null);
            }

            TextView text = (TextView) mView.findViewById(R.id.textView);

            if(items.get(position) != null )
            {
                text.setTextColor(Color.WHITE/*BLACK*/);
                text.setText(items.get(position));
                // text.setBackgroundColor(Color.WHITE);
            }

            return mView;
        }

    }
}
