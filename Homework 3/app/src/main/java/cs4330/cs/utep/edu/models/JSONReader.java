package cs4330.cs.utep.edu.models;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class JSONReader {

    JSONParser parser = new JSONParser();
    JSONArray jObjt = null;

    public String loadJSONFromAsset(Context ctx, String filename) {
        String json = null;
        try {
//            InputStream is = ctx.getAssets().open(filename);
            FileInputStream is = new FileInputStream(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public JSONReader(Context ctx, String fileName) {
        try{

            JSONObject helper = (JSONObject) parser.parse(loadJSONFromAsset(ctx, fileName));
            this.jObjt = (JSONArray) helper.get("item");
        }catch ( ParseException e){
            e.printStackTrace();
        }
    }

    public JSONArray getArray(){
        return this.jObjt;
    }


}

