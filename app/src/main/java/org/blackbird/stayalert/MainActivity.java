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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    public static String TITLE = "titulo";
    public static String LIST_STATUS = "status";
    public static String IN_DATE = "dataCriacao";

    private ArrayList<Content> contents_list;
    private ArrayList<HashMap<String, String>> content_array;
    private HashMap<String, String> content_hash_map;
    private ListView list_view;
    private String _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.blackbird.stayalert.R.layout.activity_main);


        contents_list = new ArrayList<Content>();
        content_array = new ArrayList<HashMap<String, String>>();

        list_view = getListView();

        list_view.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                //TODO: make url hidden of user
                String _id = ((TextView) view.findViewById(R.id.id))
                        .getText().toString();
                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),
                        SingleContentActivity.class);
                in.putExtra("EXTRA_ID", _id);
                startActivity(in);

            }
        });

        // Calling async task to get json
        new GetContents().execute();
    }
    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(this, "OnResume", Toast.LENGTH_SHORT).show();

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
            Intent in = new Intent(MainActivity.this, UserSettingsActivity.class);
            startActivity(in);
        }
        else if(id == R.id.action_reload){
            //reload activity for load new contents
            finish();
            startActivity(getIntent());
        }

        return super.onOptionsItemSelected(item);
    }

    public void startProblemActivity(View view){
        Intent in = new Intent(MainActivity.this, ContentActivity.class);
        in.putExtra("EXTRA", org.blackbird.stayalert.R.string.text_problem_intro);
        startActivity(in);
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
                Log.e(STAYALERT, "Falha durante a criação do diretório de armazenamento.");
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ContentActivity.class));
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContents extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {


            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Por favor aguarde...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServerCaller called_from_get = new ServerCaller();

            // Making a request to url and getting response
            String jsonStr = called_from_get.makeServiceCall(Settings.url()+"reclamacao", ServerCaller.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray contents = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < contents.length(); i++) {
                        JSONObject content_from = contents.getJSONObject(i);
                        Content content_to = new Content();

                        content_to.id(content_from.getString(Settings.ID));
                        content_to.title(content_from.getString(Settings.TITLE));
                        content_to.status(content_from.getString(Settings.STATUS));
                        //TODO :(
                        //content_to.in_date(content_from.getString(Settings.IN_DATE));



                        // adding content to contents list
                        contents_list.add(content_to);

                        // tmp hashmap for single content_hash_map
                        content_hash_map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        content_hash_map.put(Settings.ID, content_to.id());
                        content_hash_map.put(Settings.TITLE, content_to.title());
                        content_hash_map.put(Settings.STATUS, content_to.status());
                        //content_hash_map.put(Settings.IN_DATE, content_to.in_date());

                        //TODO falta colocar o local
                        
                        // adding content_hash_map to content_hash_map list

                        content_array.add(content_hash_map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //TODO: how insert a Toast after the if fail?
                Log.e("ServerCaller", "Não foi possivel obter nenhum dado da URL");
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
            // TODO: insert field picture with thumb from paperclip
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, content_array,
                    R.layout.list_item, new String[] { ID, TITLE, LIST_STATUS, IN_DATE}, new int[] { R.id.id , R.id.title, R.id.list_status, R.id.in_date});
            setListAdapter(adapter);
        }

    }

}
