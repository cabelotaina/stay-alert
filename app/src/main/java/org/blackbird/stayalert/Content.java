package org.blackbird.stayalert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Content {

    private String _id, _title, _description, _status, _in_date, _update_date, _response, _label, _picture;
    private Double _latitude, _longitude;
    //private int _user_id;
    //private String _url;

    public void status(String status){ _status = status; }
    public String status(){ return _status;}
    public void in_date(String in_date){ _in_date = in_date; }
    public String in_date(){ return _in_date; }
    public void update_date(String update_date){ _update_date = update_date; }
    public String update_date(){ return _update_date;}
    public void response(String response){ _response = response;}
    public String response(){ return _response; }
    public void id(String id) { _id = id; }
    public String id(){ return _id; }
    public void title(String title ){ _title = title;}
    public void description(String description) { _description = description; }

    public void label(String label) { _label = label; }

    public String label() { return _label; }

    public void latlon(Double latitude, Double longitude) {
        _latitude = latitude;
        _longitude = longitude;
    }

    public void picture(String capturedImageURL) {
        Bitmap bm = BitmapFactory.decodeFile(capturedImageURL);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        _picture = "data:image/png;base64," + Base64.encodeToString(b, Base64.NO_WRAP);
    }

    //public void user_id(int user_id) {_user_id = user_id; }
    //public void url(String url) { _url = url; }
    //public String url() { return _url; }

    public String title(){ return _title;}
    public String description() {
        return _description;
    }
    public Double latitude(){
        return _latitude;
    }
    public Double longitude(){
        return _longitude;
    }
    public String json() {
        JSONObject header = new JSONObject();
        try {
            header.put(Settings.TITLE, _title);
            header.put(Settings.DESCRIPTION, _description);
            header.put(Settings.STATUS, _status);
            //header.put(Settings.LATITUDE, _latitude);
            //header.put(Settings.LONGITUDE, _longitude);
            //header.put(Settings.picture, _picture);
            //header.put(Settings.user_id, _user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject contentJSONString = new JSONObject();
        try {
            contentJSONString.put("content", header);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contentJSONString.toString();
    }
}
