package org.blackbird.stayalert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleContentActivity extends Activity {


    private String url;
    private String DESCRIPTION = "description";
    private String LABEL = "label";
    private Content content_to = new Content();
    //TODO: add a map (http://developer.android.com/intl/pt-br/training/maps/index.html)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_content);

        Bundle extras = getIntent().getExtras();
        url = extras.getString("EXTRA_URL");
        new GetContent().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_content, menu);
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

        return super.onOptionsItemSelected(item);
    }


    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContent extends AsyncTask<String, Void, Content> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SingleContentActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Content doInBackground(String... arg0) {
            ServerCaller called_from_get = new ServerCaller();
            //TODO: finish the single content activity, see main activity how example
            String jsonStr = called_from_get.makeServiceCall(url,ServerCaller.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject content = new JSONObject(jsonStr);
                    content_to.description(content.getString(DESCRIPTION));
                    //content_to.label(content_from.getString(LABEL));
                    //Log.d("Exception", content_to.description());

                }catch (JSONException e){
                    Log.e("Exception", e.getMessage());
                }
            }


            return content_to;
        }

        protected void onPostExecute(Content content) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            //Update content
            TextView description = (TextView) findViewById(R.id.description);
            description.setText(content.description());
            //TODO: add other fields


        }
    }

}
