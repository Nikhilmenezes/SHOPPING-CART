package com.example.nimeneze.cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.EditText;
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

public class Main2Activity extends AppCompatActivity {

    String IP;
    EditText name, mobile;
    String namecmp,mobilecmp;
    String Name,Mobile;
    Context ctx=this;
    String NAME=null, MOBILE=null;
    boolean doubleBackToExitPressedOnce = false;
    ProgressDialog pd;

    ////defining layout to load activity_main2.xml from res folder and name and mobile text field
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle extras = getIntent().getExtras();
        IP = extras.getString("IP");

        name = findViewById(R.id.user);
        mobile = findViewById(R.id.pass);



    }

    //function after signin button pressed
    //intent is called to move from login page(Main2Activity.java) to main page(Main4activity.java)
    public void sendSignIn(View v){

        Name = name.getText().toString();
        Mobile = mobile.getText().toString();

        if(Name.isEmpty()) {
            Toast.makeText(ctx, "Enter Username", Toast.LENGTH_SHORT).show();
            name.requestFocus();
        }
        else if (Mobile.isEmpty()) {
            Toast.makeText(ctx, "Enter Password", Toast.LENGTH_SHORT).show();
            mobile.requestFocus();
        }
        else {
            BackGround b = new BackGround();
            b.execute(Name, Mobile);
        }

//        Intent i = new Intent(Main2Activity.this,Main4Activity.class);
//        i.putExtra("IP",IP);
//        startActivity(i);
    }


    //function after signup button pressed
    //intent is called to move from login  page(Main2Activity.java) to register page(Main3 activity.java)

    public void sendSignUp(View v){
        Intent i = new Intent(Main2Activity.this,Main3Activity.class);
        i.putExtra("IP",IP);
        startActivity(i);
    }


//below is code to perform background task to fetch data from database.

    class BackGround extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(ctx);
            pd.setTitle("Login");
            pd.setMessage("Authenticating... please wait");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String mobile = params[1];
            String data="";
            int tmp;
            namecmp=name;
            mobilecmp=mobile;
            try {
                URL url = new URL("http://"+IP+"/login.php");
                String urlParams = "name="+name+"&mobile="+mobile;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
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


                JSONObject object = (JSONObject) new JSONTokener(s).nextValue();
                JSONObject userdata = object.getJSONObject("user_data");

                NAME=userdata.getString("name");
                //MOBILE=userdata.getString("mobile");

            } catch (JSONException e) {
                e.printStackTrace();
                err = "Exception: "+e.getMessage();
                //Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
            }

            if(namecmp.compareTo(NAME)==0)
            {

                err="Welcome "+NAME;
                Intent i = new Intent(Main2Activity.this,Main4Activity.class);
                i.putExtra("IP",IP);
                i.putExtra("CUSTNAME",namecmp);
                startActivity(i);
            }
             else {
                err="Incorrect Username/password";

             }

            Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
