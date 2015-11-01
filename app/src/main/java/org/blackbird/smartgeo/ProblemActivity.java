package org.blackbird.smartgeo;

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
import android.widget.Toast;

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


public class ProblemActivity extends Activity {

    private final int PICK_IMAGE_REQUEST = 100;
    private static final int REQUEST_TAKE_PHOTO = 200;

    private static final String STAYALERT = "STAYALERT";

    private TextView address_aux;
    private ImageView picture_one;
    Double latitude, longitude;
    Uri picture_uri;


    String newSelectedImageURL;
    String capturedImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_problem);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses == null) {
                Log.w(STAYALERT, "GPS is false because address is null");
            } else {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                address_aux = (TextView) findViewById(R.id.address);
                address_aux.setText(address + ", " + city);
            }
        } catch (Exception e) {
            Log.e(STAYALERT, "exception: " + e.getMessage());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void sendMessage(View view) {

        EditText editDescription = (EditText) findViewById(R.id.edit_description);
        String description = editDescription.getText().toString();
        //Toast.makeText(this, "path: " + capturedImageURL, Toast.LENGTH_LONG).show();
        Content content = new Content();
        if (capturedImageURL != null){
            content.picture(capturedImageURL);
        }
        content.description(description);
        content.label("problem");
        content.lat_lon(latitude, longitude);
        content.user_id(1);
        content.tag_list("teste");


        makeRequest(Settings.url()+"contents.json", content.json() );
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


    public void addImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    public void loadCamera(View view) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
//        }


        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        newSelectedImageURL=null;
        //outfile where we are thinking of saving it
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        String newPicFile = "stay_alert"+ df.format(date) + ".png";


        String outPath =Environment.getExternalStorageDirectory() + "/myFolderName/"+ newPicFile ;
        File outFile = new File(outPath);

        capturedImageURL=outFile.toString();
        Uri outuri = Uri.fromFile(outFile);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outuri);
        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
    }

    private File getOutputPhotoFile() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getPackageName());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(STAYALERT, "Failed to create storage directory.");
                Toast.makeText(this, "Failed to create storage directory." +
                        null, Toast.LENGTH_LONG).show();
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.UK).format(new Date());
        return new File(directory.getPath() + File.separator + "IMG_"
                + timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        picture_one = (ImageView) findViewById(R.id.picture_one);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri uri = null;

                if (data != null) {
                    uri = data.getData();
                }
                if (uri == null && capturedImageURL != null) {
                    uri = Uri.fromFile(new File(capturedImageURL));
                }
                File file = new File(capturedImageURL);
                if (!file.exists()) {
                    if (file.mkdir()){
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                    }

                }

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    picture_one.setImageBitmap(bitmap);
                } catch (IOException ex) {
                    Log.e(STAYALERT, "exception: " + ex.getMessage());
                    Log.e(STAYALERT, "exception: " + ex.toString());
                    ex.printStackTrace();
                }
            }




        }

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            picture_uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picture_uri);
                picture_one.setImageBitmap(bitmap);
            } catch (IOException ex) {
                Log.e(STAYALERT, "exception: " + ex.getMessage());
                Log.e(STAYALERT, "exception: " + ex.toString());
                ex.printStackTrace();
            }
        }
    }





}
