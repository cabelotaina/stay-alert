package org.blackbird.stayalert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Content {

    private String _title, _description, _label, _picture;
    private int _id;
    private Double _latitude, _longitude;
    private int _user_id;
    private String _url;

    public void id(int id) {
        _id = id;
    }

    public void title(String title ){ _title = title;}

    public void description(String description) {
        _description = description;
    }

    public void label(String label) {
        _label = label;
    }

    public String label() {
        return _label;
    }

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

    public void user_id(int user_id) {
        _user_id = user_id;
    }

    public void url(String url) {
        _url = url;
    }

    public String url() {
        return _url;
    }

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
            header.put("description", _description);
            header.put("label", _label);
            header.put("latitude", _latitude);
            header.put("longitude", _longitude);
            header.put("picture", _picture);
            header.put("user_id", _user_id);
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
