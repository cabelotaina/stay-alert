package org.blackbird.stayalert;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SingleContentActivity extends Activity {
//TODO: add a map (http://developer.android.com/intl/pt-br/training/maps/index.html)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_content);

        Bundle extras = getIntent().getExtras();
        String url = extras.getString("EXTRA_URL");

        ServerCaller called_from_get = new ServerCaller();

        Log.d("Response: ", "> " + url);
        //TODO: finish the single content activity, see main activity how example
       String jsonStr = called_from_get.makeServiceCall(url,ServerCaller.GET);

//        Log.d("Response: ", "> " + jsonStr);
//        TextView description = (TextView) findViewById(R.id.description);
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
}
