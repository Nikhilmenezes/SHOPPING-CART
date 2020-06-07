package com.example.nimeneze.cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class checkOutActivity extends AppCompatActivity {

    String IP,CUSTNAME;
    Context ctx=this;
    TextView ti,tw,tp,ba;
    AlertDialog.Builder builder;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        Bundle extras = getIntent().getExtras();
        IP = extras.getString("IP");
        CUSTNAME = extras.getString("CUSTNAME");

        ti=findViewById(R.id.ti);
        tw=findViewById(R.id.tw);
        tp=findViewById(R.id.tp);
        ba=findViewById(R.id.ba);

        BackGround b = new BackGround();
        b.execute(CUSTNAME);

        BackGround1 b1 = new BackGround1();
        b1.execute(CUSTNAME);

//        final Handler handler=new Handler();
//
//        final Runnable updateTask=new Runnable() {
//            @Override
//            public void run() {
//                BackGround1 b1 = new BackGround1();
//                b1.execute(CUSTNAME);
//                handler.postDelayed(this,30000);
//            }
//        };
//
//        handler.postDelayed(updateTask,2000);

    }




    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String data = "";
            int tmp;
            try {
                URL url = new URL("http://" + IP + "/checkout.php");
                String urlParams = "name=" + name;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            String err = null;

            try {


                JSONObject object = (JSONObject) new JSONTokener(s).nextValue();
                JSONObject userdata = object.getJSONObject("user_data");

                ti.setText("TOTAL ITEMS  : "+userdata.getString("qnty"));
                tw.setText("TOTAL WEIGHT : "+userdata.getString("weight"));
                tp.setText("TOTAL PRICE  : "+userdata.getString("price"));
                ba.setText("BALANCE AMOUNT : "+userdata.getString("amnt"));

            } catch (JSONException e) {
                e.printStackTrace();
                err = "Exception: " + e.getMessage();
                //Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
            }
        }
    }





    class BackGround1 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String data = "";
            int tmp;
            try {
                URL url = new URL("http://" + IP + "/pay.php");
                String urlParams = "name=" + name;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            String err = null;

            try {


                JSONObject object = (JSONObject) new JSONTokener(s).nextValue();
                JSONObject userdata = object.getJSONObject("user_data");

                int check = userdata.getInt("ext");

                int cartWght = Integer.parseInt(tw.getText().toString().replaceAll("[^0-9]", ""));
                int machineWght = userdata.getInt("weight");
                int maxval=((cartWght*10)/100)+cartWght;
                int minval=cartWght-((cartWght*10)/100);

                if (machineWght>=minval  && machineWght<=maxval) {
                    if (check == 1) {
                        Button b = findViewById(R.id.pay);
                        b.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("DAMN! ITEM WEIGHT is more in weighing machine!!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //BackGround1 b1 = new BackGround1();
                                    //b1.execute(CUSTNAME);
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("ALERT - Weight");
                    alert.show();

                }


            } catch (JSONException e) {
                e.printStackTrace();
                err = "Exception: " + e.getMessage();
                //Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void sendPay(View v){
        String price = tp.getText().toString().replaceAll("[^0-9]", "");
        BackGround2 b = new BackGround2();
        b.execute(CUSTNAME,price);
    }




    class BackGround2 extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(ctx);
            pd.setTitle("Processing");
            pd.setMessage("Loading... please wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String prc=params[1];
            String data = "";
            int tmp;
            try {
                URL url = new URL("http://" + IP + "/processend.php");
                String urlParams = "name=" + name+"&price="+prc;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            Toast.makeText(ctx, "Thank You for Shopping in 'Shop Karo'. Happy Day!", Toast.LENGTH_SHORT).show();
            finish();
//            try {
//                ListView lv = findViewById(R.id.lv1);
//                lv.destroyDrawingCache();
//                lv.setVisibility(ListView.INVISIBLE);
//                lv.setVisibility(ListView.VISIBLE);
//                finish();
//            }
//            catch (NullPointerException e){
//
//            }
        }
    }


}
