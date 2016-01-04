package org.blackbird.stayalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ContentActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int REQUEST_TAKE_PHOTO = 200;

    private static final String STAYALERT = "STAYALERT";

    private String _captured_picture_url;

    private Double edit_longitude;
    private Double edit_latitude;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(org.blackbird.stayalert.R.layout.activity_content);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt("EXTRA");
            TextView title = (TextView) findViewById(R.id.title);
            title.setText(getText(type));
        }

       /* TODO enable latitude longitude
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //TODO This is not good :(
            edit_longitude = location.getLongitude();
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(edit_latitude, edit_longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                if (addresses == null) {
                    Log.w(STAYALERT, getString(org.blackbird.stayalert.R.string.gps_false));
                } else {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    TextView edit_address = (TextView) findViewById(org.blackbird.stayalert.R.id.edit_address);
                    edit_address.setText(address + ", " + city);
                }
            } catch (Exception e) {
                Log.e(STAYALERT, "exception: " + e.getMessage());
            }
        }
        else{
            TextView label_address = (TextView) findViewById(org.blackbird.stayalert.R.id.label_address);
            String aux = getResources().getString(R.string.dont_have_gps);
            label_address.setText(aux);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(org.blackbird.stayalert.R.menu.menu_content, menu);
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

    public void sendMessage(View view) {
        EditText edit_title = (EditText) findViewById(R.id.edit_title);
        String title = edit_title.getText().toString();

        EditText edit_description = (EditText) findViewById(org.blackbird.stayalert.R.id.edit_description);
        String description = edit_description.getText().toString();

        Content content = new Content();
        if (_captured_picture_url != null){
            content.picture(_captured_picture_url);
        }
        content.description(description);

        if(type== org.blackbird.stayalert.R.string.text_problem_intro){
            content.label("problem");
        }else {
            content.label("issue");
        }

        content.latlon(edit_latitude, edit_longitude);
        //TODO add user information to problem - content.user_id(1);

        //TODO: change for ServerCaller.java
        makeRequest(Settings.url() + "reclamacao", content.json());
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    public static void makeRequest(String uri, String json) {
        class OneShotTask implements Runnable {
            String _uri;
            String _json;
            OneShotTask(String uri, String json) { _uri = uri; _json = json;}
            public void run() {
                someFunc(_uri,_json);
            }
            public void someFunc(String _uri,String _json){
                try {
                    HttpPost httpPost = new HttpPost(_uri);
                    httpPost.setEntity(new StringEntity(_json));
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");
                    new DefaultHttpClient().execute(httpPost);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Thread t = new Thread(new OneShotTask(uri,json));
        t.start();
    }

    public void loadCamera(View view) {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        //outfile where we are thinking of saving it
        Date date = new Date();
        //TODO: insert the locale by latlon (https://developers.google.com/maps/documentation/timezone/intro)
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US);

        String newPicFile = "stay_alert"+ df.format(date) + ".png";


        String out_path =Environment.getExternalStorageDirectory() + "/stay_alert/"+ newPicFile ;
        File out_file = new File(out_path);

        _captured_picture_url = out_file.toString();
        Uri out_uri = Uri.fromFile(out_file);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, out_uri);
        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        ImageView edit_picture = (ImageView) findViewById(org.blackbird.stayalert.R.id.picture);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK && _captured_picture_url != null) {
                File file = new File(_captured_picture_url);
                if (!file.exists()) {
                    if (file.mkdir()){
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                    }
                }
                try {
                        Uri uri = (data!= null) ? data.getData() : Uri.fromFile(new File(_captured_picture_url));
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        edit_picture.setImageBitmap(bitmap);


                } catch (IOException ex) {
                    Log.e(STAYALERT, "exception: " + ex.getMessage());
                    Log.e(STAYALERT, "exception: " + ex.toString());
                    ex.printStackTrace();
                }
            }

        }

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            Uri edit_picture_uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), edit_picture_uri);
                edit_picture.setImageBitmap(bitmap);
            } catch (IOException ex) {
                Log.e(STAYALERT, "exception: " + ex.getMessage());
                Log.e(STAYALERT, "exception: " + ex.toString());
                ex.printStackTrace();
            }
        }
    }
}