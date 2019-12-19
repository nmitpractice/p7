package com.example.p7;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Button b;
    ListView lv;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        b = (Button) findViewById(R.id.fetch);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUrl = "https://api.androidhive.info/contacts/";
                new UrlHandler().execute(strUrl);
            }
        });
    }

    public class UrlHandler extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"id", "name", "email"},
                    new int[]{R.id.cid, R.id.cname, R.id.cemail});
            lv.setAdapter(adapter);
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                URL url = new URL(params[0]);

                URLConnection request = url.openConnection();
                request.connect();

                // Parse to a JSON Element
                JsonElement root = JsonParser.parseReader(
                        new InputStreamReader((InputStream) request.getContent()));

                Log.i("DATA", String.valueOf(root.getAsJsonObject()));

                // Assign to JsonObject of Gson
                JsonObject jsonObj = root.getAsJsonObject();

                // Getting JSON Array node
                JsonArray contacts = jsonObj.getAsJsonArray("contacts");

                // looping through All Contacts
                for (int i = 0; i < contacts.size(); i++) {
                    JsonObject c = contacts.get(i).getAsJsonObject();
                    String id = c.get("id").getAsString();
                    String name = c.get("name").getAsString();
                    String email = c.get("email").getAsString();

                    // tmp hash map for single contact
                    HashMap<String, String> contact = new HashMap<>();

                    // adding each child node to HashMap key => value
                    contact.put("id", id);
                    contact.put("name", name);
                    contact.put("email", email);

                    // adding contact to contact list
                    contactList.add(contact);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}