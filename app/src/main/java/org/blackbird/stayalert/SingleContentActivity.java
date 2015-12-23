package org.blackbird.stayalert;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SingleContentActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private String url;

    private Double latitude , longitude ;

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
            String jsonStr = called_from_get.makeServiceCall(url,ServerCaller.GET);
            if (jsonStr != null) {
                try {
                    JSONObject content = new JSONObject(jsonStr);
                    content_to.description(content.getString(Settings.DESCRIPTION));
                    content_to.label(content.getString(Settings.STATUS));
                    content_to.latlon(Double.parseDouble(content.getString(Settings.LATITUDE)),
                            Double.parseDouble(content.getString(Settings.LONGITUDE)));

                }catch (JSONException e){
                    Log.e("Exception", e.getMessage());
                }
            }


            return content_to;
        }

        protected void onPostExecute(Content content) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            TextView description = (TextView) findViewById(R.id.description);
            description.setText(content.description());
            TextView label = (TextView) findViewById(R.id.label);
            label.setText(content.label());
            latitude =  content.latitude();
            longitude = content.longitude();

            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
