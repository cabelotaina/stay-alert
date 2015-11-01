package org.blackbird.smartgeo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Content {
    private static final String TAG = "SMARTGEO";

    private static final String STAYALERT = "STAYALERT";

    private String _description, _label, _tag_list, _picture;
    private int _id;
    private Double _latitude, _longitude;
    private int _user_id;
    private String _url;
    private ArrayList<Tag> array_tag_objects= new ArrayList<Tag>();

    public void id(int id){
        _id = id;
    }
    public void description(String description) { _description = description;}
    public void label(String label) {_label = label;}
    public void tag_list(String tag_list){ _tag_list = tag_list;}
    public void tag_list(Tag tag){
        array_tag_objects.add(tag);
    }

    public void picture(String capturedImageURL) {
        Bitmap bm = BitmapFactory.decodeFile(capturedImageURL);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        _picture = "data:image/png;base64," + Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public void user_id(int user_id){
        _user_id =user_id;
    }

    public void url(String url){
        _url = url;
    }


    public String description(){
        return _description;
    }

    public void lat_lon(Double latitude, Double longitude){
        _latitude = latitude;
        _longitude = longitude;
    }

    public String json(){

        JSONObject header = new JSONObject();
        try {
            header.put("description", _description);
            header.put("label", _label);
            header.put("latitude", _latitude);
            header.put("longitude", _longitude);
            header.put("picture", _picture);
            header.put("tag_list",_tag_list);
            header.put("user_id" , _user_id);
        }catch (JSONException e){
            e.printStackTrace();
        }

        JSONObject contentJSONString = new JSONObject();
        try {
            contentJSONString.put("content", header);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return contentJSONString.toString();
    }




}
