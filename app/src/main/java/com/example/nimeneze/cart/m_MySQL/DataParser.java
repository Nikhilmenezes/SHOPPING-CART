package com.example.nimeneze.cart.m_MySQL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.example.nimeneze.cart.Main4Activity;
import com.example.nimeneze.cart.R;
import  com.example.nimeneze.cart.m_DataObject.SpaceCraft;
import com.example.nimeneze.cart.m_UI.CustomAdapter;

/**
 * Created by NIKHIL on 12/22/2016.
 */

public class DataParser extends AsyncTask<Void, Void, Integer> {

        Context c;
        String jsonData;
        ListView lv;

        ProgressDialog pd;
        ArrayList<SpaceCraft> spacecrafts=new ArrayList<>();

        public DataParser(Context c, String jsonData, ListView lv){
        this.c=c;
        this.jsonData = jsonData;
        this.lv=lv;
        }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd=new ProgressDialog(c);
        pd.setTitle("Loading");
        pd.setMessage("Loading... please wait");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return this.parseData();
    }


    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        CustomAdapter adapter=new CustomAdapter(c,spacecrafts);
        pd.dismiss();
        if(result==0){
            Toast.makeText(c,"NO ITEMS IN CART", Toast.LENGTH_SHORT).show();
        }else {
            //BIND DATA TO LISTVIEW

            lv.setAdapter(adapter);

            //View v = LayoutInflater.from(c).inflate(R.layout.activity_main4, null);
            //v.findViewById(R.id.checkout).setVisibility(v.VISIBLE);


        }

    }

    private int parseData()
    {
        try{
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            SpaceCraft spacecraft;

            for(int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);

                String name=jo.getString("item_name");
                String price=jo.getString("price");
                String weight=jo.getString("weight");
                String quantity=jo.getString("qnty");



                spacecraft=new SpaceCraft();
                spacecraft.setName(name);
                spacecraft.setPrice(price);
                spacecraft.setQuantity(quantity);
                spacecraft.setWeight(weight);
                spacecrafts.add(spacecraft);
            }
            return 1;

        }catch (JSONException e){
            e.printStackTrace();
        }

        return  0;
    }

}
