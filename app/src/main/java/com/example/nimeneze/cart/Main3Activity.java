package com.example.nimeneze.cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main3Activity extends AppCompatActivity {

    EditText name, mobile;
    String IP,Name, Mobile;
    Context ctx=this;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Bundle extras = getIntent().getExtras();
        IP = extras.getString("IP");

        name = findViewById(R.id.register_name);
        mobile =  findViewById(R.id.register_mobile);
    }

    public void register_register(View v){
        Name = name.getText().toString();
        Mobile = mobile.getText().toString();




        if(Name.isEmpty()) {
            Toast.makeText(ctx, "Enter Username", Toast.LENGTH_SHORT).show();
            name.requestFocus();
        }
        else if (Mobile.isEmpty()) {
            Toast.makeText(ctx, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
            mobile.requestFocus();
        }
        else {
            BackGround b = new BackGround();
            b.execute(Name, Mobile);
        }

    }

    class BackGround extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(ctx);
            pd.setTitle("REGISTER");
            pd.setMessage("REGISTERING username ... please wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String mobile = params[1];
            String data="";
            int tmp;

            try {
                URL url = new URL("http://"+IP+"/register.php");
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

                return data;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if(s.equals("")){
                s="Data saved successfully.";
                finish();
            }
            Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
        }
    }




}
