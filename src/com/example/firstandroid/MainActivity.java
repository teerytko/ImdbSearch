package com.example.firstandroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.net.http.AndroidHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import com.example.firstandroid.JSONParser;
import com.example.firstandroid.ImdbItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements OnItemClickListener {
	private EditText searchText;
	private ListView list;
	private ArrayList<ImdbItem> search_items;
	private static final String google_custom_search = "https://www.googleapis.com/customsearch/v1?key=AIzaSyAmGyK0ijPk4i9DC1z9Q6TNnNMQXc1nq1g&alt=json&cx=006788588923458170755:tqqrci-ddxa&q=";
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        ImdbItem clicked = this.search_items.get(position);
        Intent i = new Intent(getApplicationContext(), ImdbItemActivity.class);
        i.putExtra("title", clicked.get("title"));
        i.putExtra("snippet", clicked.get("snippet"));
        i.putExtra("link", clicked.get("link"));
        i.putExtra("thumbnail", clicked.get("thumbnail"));
        startActivity(i);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.searchText = (EditText) findViewById(R.id.searchText);
        this.list = (ListView) findViewById(R.id.listView);
        this.search_items = new ArrayList<ImdbItem>();
        ArrayAdapter<ImdbItem> adapter = new ArrayAdapter<ImdbItem>(this, 
                android.R.layout.simple_list_item_1, this.search_items);
        this.list.setAdapter(adapter);
        this.list.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onSearch(View view) throws UnsupportedEncodingException {
        String query_str = this.searchText.getText().toString().trim();
        query_str = URLEncoder.encode(query_str, "utf-8");
        //Starting the task. Pass an url as the parameter.
        this.search_items.clear();
        Log.d(getPackageName(), "Searching: "+google_custom_search+query_str);
        new HttpTask(this).execute(google_custom_search+query_str);
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.searchText.getWindowToken(), 0);
    }
    
    public void showNote(String message) {
        new AlertDialog.Builder(this).
        setTitle("Note").
        setMessage(message).
        setPositiveButton("OK", null).
        show();
    }
    
    // The definition of our task class
    private class HttpTask extends AsyncTask<String, Integer, String> {
        protected MainActivity activity = null;
        protected HttpResponse resp = null;
        HttpTask(MainActivity activity)
        {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            String url=params[0];
           AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
           HttpGet req = new HttpGet(url);
           try
           {
               this.resp = client.execute(req);
               try {
                   InputStream is = this.resp.getEntity().getContent();
                   JSONParser parser = new JSONParser();
                   JSONObject obj = parser.getJSONFromInput(is);
                   try {
                       // Getting Array of Contacts
                        JSONArray items = obj.getJSONArray("items");
                        // looping through All Contacts
                        for(int i = 0; i < items.length(); i++){
                            ImdbItem item = new ImdbItem();
                            JSONObject c = items.getJSONObject(i);
                            // Storing each json item in variable
                            item.put("title", c.getString("title"));
                            item.put("link", c.getString("link"));
                            item.put("snippet", c.getString("snippet"));
                            try {
                                // Try the get the thumbnail if it exists
                                String thumbnail = c.getJSONObject("pagemap").getJSONArray("cse_image").getJSONObject(0).getString("src");
                                item.put("thumbnail", thumbnail);
                            } catch (JSONException e) {}
                            this.activity.search_items.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                       e.printStackTrace();
                    }
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
               publishProgress();
           }
           catch (IOException e)
           {
               e.printStackTrace();
           }
           return "All Done!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
           super.onProgressUpdate(values);
           this.activity.list.setFocusable(true);
           this.activity.list.requestFocus();
           this.activity.list.setSelected(true);
           this.activity.list.setSelection(0);
        }
    }
}

