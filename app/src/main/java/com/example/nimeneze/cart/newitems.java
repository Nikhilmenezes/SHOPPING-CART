package com.example.nimeneze.cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class newitems extends AppCompatActivity {

    String IP,CUSTNAME;
    ProgressDialog pd;
    Context ctx=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newitems);

        Bundle extras = getIntent().getExtras();
        IP = extras.getString("IP");
        CUSTNAME = extras.getString("CUSTNAME");
        this.setTitle("NEW ITEMS IN STORE");

        BackGround b = new BackGround();
        b.execute();
    }



    class BackGround extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(ctx);
            pd.setTitle("Recommended Items");
            pd.setMessage("Loading... Recommended Items.");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String data="";
            int tmp;
            try {
                URL url = new URL("http://"+IP+"/newitems.php");


                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                //os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while((tmp=is.read())!=-1){
                    data+= (char)tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return  data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s){
            String err=null;
            pd.dismiss();

            try {

                List<String> items = new ArrayList<>();
                ListView listView = (ListView) findViewById(R.id.lvrc);


//                JSONObject object = (JSONObject) new JSONTokener(s).nextValue();
//
//
//
//                for(int i=0;i<object.length();i++)
//                {
////                    JSONObject userdata = object.getJSONObject("user_data");
////                    items.add(userdata.getString("name") + "                " +
////                            "           " +userdata.getString("price") + "Rs" );
//                    JSONArray userdata = object.getJSONArray("user_data");
//                    items.add(userdata.getString(i));
//
//
//                }



                JSONArray jsonArray= (JSONArray) new JSONArray(s);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonobject= (JSONObject) jsonArray.get(i);
                    items.add(jsonobject.optString("name")+ "                      "+jsonobject.optInt("price")+" Rs");
                }



                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1,items);

                if (listView != null) {
                    listView.setAdapter(adapter);
                }



            } catch (JSONException e) {
                e.printStackTrace();
                err = "Exception: "+e.getMessage();
                //Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
        }
    }
}
