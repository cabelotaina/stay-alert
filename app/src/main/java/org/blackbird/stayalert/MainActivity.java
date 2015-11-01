package org.blackbird.stayalert;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;



public class MainActivity extends ListActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    //private static final int RESULT_OK= 1;
    private static final String STAYALERT = "STAYALERT";

    private static String ID = "id";
    private static String DESCRIPTION = "description";
    private static String TAGS = "tags";
    private static String PICTURE = "picture";
    private static String LATITUDE = "latitude";
    private static String LONGITUDE = "longitude";
    private static String USER_ID = "user_id";
    private static String URL = "url";

    ProgressDialog pDialog;
    ArrayList<Content> contents_list;
    ArrayList<HashMap<String, String>> content_array;
    HashMap<String, String> content_hash_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.blackbird.stayalert.R.layout.activity_main);


        contents_list = new ArrayList<Content>();
        content_array = new ArrayList<HashMap<String, String>>();

        ListView list_view = getListView();

        // I dissable this because i dont need this functionality
        // Listview on item click listener
        /*list_view.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String description = ((TextView) view.findViewById(R.id.description))
                        .getText().toString();

                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        SingleContentActivity.class);
                in.putExtra(DESCRIPTION, );
                in.putExtra(TAG_EMAIL, cost);
                in.putExtra(TAG_PHONE_MOBILE, description);
                startActivity(in);

            }
        });*/

        // Calling async task to get json
        new GetContents().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(org.blackbird.stayalert.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == org.blackbird.stayalert.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startProblemActivity(View view){
        Intent i = new Intent(MainActivity.this, ContentActivity.class);
        i.putExtra("EXTRA", org.blackbird.stayalert.R.string.text_problem_intro);
        startActivity(i);
    }


    public void startIssueActivity(View view){
        Intent i = new Intent(MainActivity.this, ContentActivity.class);
        i.putExtra("EXTRA", org.blackbird.stayalert.R.string.text_issue_intro);
        startActivity(i);
    }


    public void loadCamera(View view){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri image_path = Uri.fromFile(getOutputPhotoFile());
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_path);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File getOutputPhotoFile() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getPackageName());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(STAYALERT, "Failed to create storage directory.");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.UK).format(new Date());
        return new File(directory.getPath() + File.separator + "IMG_"
                + timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView picture_one = (ImageView) findViewById(org.blackbird.stayalert.R.id.picture);
        //TODO: it works?
       // if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            picture_one.setImageBitmap(imageBitmap);
      //  }

    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContents extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServerCaller called_from_get = new ServerCaller();

            // Making a request to url and getting response
            String jsonStr = called_from_get.makeServiceCall(Settings.url()+"/contents.json", ServerCaller.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray contents = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < contents.length(); i++) {
                        JSONObject content_from = contents.getJSONObject(i);
                        Content content_to = new Content();

                        content_to.id(content_from.getInt(ID));
                        content_to.description(content_from.getString(DESCRIPTION));
                        //TODO: create for statement to get all tags from content insert it on Array

                        JSONArray tags = content_from.getJSONArray(TAGS);
/*
                        for (int j = 0; j < tags.length(); j++ ) {
                            JSONObject tag = tags.getJSONObject(i);
                            content_to.tagList(new Tag(tag.getInt("id"),
                                    tag.getString("name"),
                                    tag.getInt("taggings_count")));
                        }*/
                        //content_to.picture(content_from.getString(PICTURE));
//                        content_to.latLon(content_from.getDouble(LATITUDE),
//                                content_from.getDouble(LONGITUDE));
//                        content_to.user_id(content_from.getInt(USER_ID));
                        //                       content_to.url(content_from.getString(URL));


                        // adding content to contents list
                        contents_list.add(content_to);

                        // tmp hashmap for single content_hash_map
                        content_hash_map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        content_hash_map.put(DESCRIPTION, content_to.description());

                        // adding content_hash_map to content_hash_map list

                        content_array.add(content_hash_map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServerCaller", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, content_array,
                    R.layout.list_item, new String[] { DESCRIPTION }, new int[] { R.id.description });

            setListAdapter(adapter);

            //Log.e("VIXEEEEEE", content_array.toString());
        }

    }

}
