package com.example.nimeneze.cart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.nimeneze.cart.m_MySQL.Downloader;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main4Activity extends AppCompatActivity {
    String IP,CUSTNAME;
    Context ctx=this;
    boolean doubleBackToExitPressedOnce = false;
    ListView lv;
    ProgressDialog pd;
    TextView itemincart,balanceAmount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        itemincart = (TextView) findViewById(R.id.textView2);
        balanceAmount = (TextView) findViewById(R.id.textView7);
        Bundle extras = getIntent().getExtras();
        IP = extras.getString("IP");
        CUSTNAME = extras.getString("CUSTNAME");





        lv = (ListView) findViewById(R.id.lv1);
        new Downloader(this,"http://"+IP+"/selectitem.php?cname="+CUSTNAME,lv).execute();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView n = (TextView) view.findViewById(R.id.nameTxt);
                String name = n.getText().toString();
                TextView w = (TextView) view.findViewById(R.id.weightTxt);
                int wght = Integer.parseInt(w.getText().toString().replaceAll("[^0-9]", ""));
                TextView p = (TextView) view.findViewById(R.id.priceTxt);
                int prc = Integer.parseInt(p.getText().toString().replaceAll("[^0-9]", ""));
                TextView q = (TextView) view.findViewById(R.id.qntyTxt);
                int qnt = Integer.parseInt(q.getText().toString().replaceAll("[^0-9]", ""));

                int wght1=wght/qnt;
                int prc1=prc/qnt;
                showdailog(name,wght1,prc1,qnt,id+1);
            }
        });
        fetch();


    }


    public void fetch()
    {
        BackGroundA b5 = new BackGroundA();
        b5.execute(CUSTNAME);
    }


    @Override
    public void onResume()
    {
        super.onResume();

        //new Downloader(ctx,"http://"+IP+"/selectitem.php?cname="+CUSTNAME,lv).execute();
        fetch();


        ListView lv5 = (ListView) findViewById(R.id.lv1);
        lv5.setAdapter(null);
        //lv5.getAdapter().notifyAll();
        new Downloader(ctx,"http://"+IP+"/selectitem.php?cname="+CUSTNAME,lv5).execute();



    }

    public void sendCheckOut(View v){

        if(lv.getAdapter()!=null){
            Intent i = new Intent(Main4Activity.this, checkOutActivity.class);
            i.putExtra("IP", IP);
            i.putExtra("CUSTNAME", CUSTNAME);
            startActivity(i);
        }
        else {
            Toast.makeText(ctx, "SCAN and ADD ITEM TO CHECKOUT", Toast.LENGTH_LONG).show();

        }
    }


    //function to create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //function if any of the menubutton pressed.
    //finish() method wil perform logout.
    //scan is used to perform barcode scan.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.scan:
                Intent i = new Intent(Main4Activity.this,Scan.class);
                i.putExtra("IP",IP);
                i.putExtra("CUSTNAME",CUSTNAME);
                startActivity(i);

                break;
            case R.id.reset_id:
                Toast.makeText(Main4Activity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                BackGround b = new BackGround();
                b.execute(CUSTNAME,"http://"+IP+"/removeitem.php");

                break;
            case R.id.gultoo:
                Toast.makeText(Main4Activity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.resamnt:
                BackGround b1 = new BackGround();
                b1.execute(CUSTNAME,"http://"+IP+"/resetamount.php");
                BackGround b2 = new BackGround();
                b2.execute(CUSTNAME,"http://"+IP+"/fetchamount.php");
                break;
            case R.id.prvpur:
                Intent i1 = new Intent(Main4Activity.this,prevpurchased.class);
                i1.putExtra("IP",IP);
                i1.putExtra("CUSTNAME",CUSTNAME);
                startActivity(i1);
                break;
            case R.id.newitm:
                Intent i2 = new Intent(Main4Activity.this,newitems.class);
                i2.putExtra("IP",IP);
                i2.putExtra("CUSTNAME",CUSTNAME);
                startActivity(i2);
                break;

        }
        return super.onOptionsItemSelected(item);

    }





    class BackGround extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(ctx);
            pd.setTitle("Processing");
            pd.setMessage("Processing... please wait");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String phpurl =params[1];
            String data="";
            int tmp;

            try {
                URL url = new URL(phpurl);
                String urlParams = "name="+name;

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

                if(phpurl.contains("resetamount")){
                    data="1";

                }
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
                s="ITEM REMOVED FROM CART";
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
                Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
            }
            else if(s.equals("1")){
                s="YOUR AMOUNT IS RESET TO 2000";
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
                Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
            }

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




    public void showdailog(final String itm,final int wght,final int prc, final int qnty, final long rowid) {
        final Dialog d = new Dialog(ctx);
        Window window = d.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(10, 10);
        d.setTitle("Change Quantity or DELETE ITEM! "+itm);
        d.setContentView(R.layout.popedit_window);

        Button b1 = (Button) d.findViewById(R.id.chgButton);
        Button b2 = (Button) d.findViewById(R.id.delButton);
        final EditText qtyText = (EditText) d.findViewById(R.id.qty);
        final TextView itmText = (TextView) d.findViewById(R.id.textView6);
        itmText.setText("CHANGE "+itm.toUpperCase()+" QNTY: ");
        qtyText.setText(String.valueOf(qnty));
        qtyText.requestFocus();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackGround1 bg1=new BackGround1();
                int q1=Integer.parseInt(qtyText.getText().toString());
                int wght1=wght*q1;
                int prc1=prc*q1;
                if(q1==0){
                    BackGround1 bg2=new BackGround1();
                    bg2.execute("del",String.valueOf(rowid),CUSTNAME);
                }
                bg1.execute("upd",String.valueOf(rowid),CUSTNAME,String.valueOf(wght1),String.valueOf(prc1),String.valueOf(q1));
                d.dismiss();
                onResume();
            }
        });
        b2.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackGround1 bg1=new BackGround1();
                bg1.execute("del",String.valueOf(rowid),CUSTNAME);
                d.dismiss();
                onResume();
            }
        }));
        d.show();

    }








    class BackGround1 extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(ctx);
            pd.setTitle("ITEM CHANGE");
            pd.setMessage("processing... please wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String check = params[0];

            String data="";
            int tmp;

            try {
                String URL=null;
                String urlParams=null;
                if(check.equals("del")){
                    String id = params[1];
                    String name = params[2];
                    URL="http://"+IP+"/deleteitem.php";
                    urlParams = "name="+name+"&id="+id;
                }
                else if(check.equals("upd")){
                    String id = params[1];
                    String name = params[2];
                    String wght = params[3];
                    String prc = params[4];
                    String qnty= params[5];
                    URL="http://"+IP+"/updateitem.php";
                    urlParams = "name="+name+"&id="+id+"&wght="+wght+"&prc="+prc+"&qnty="+qnty;
                }


                URL url = new URL(URL);
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
                s="ITEM UPDATED";
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
            }
            Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
        }
    }






    class BackGroundA extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            String name = params[0];

            String data="";
            int tmp;

            try {
                String URL="http://"+IP+"/fetchamount.php";
                String urlParams="name="+name;

                URL url = new URL(URL);
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
            int ba = Integer.parseInt(s.replaceAll("[^0-9]", ""));
            balanceAmount.setText("BALANCE AMOUNT: "+ba);
        }
    }



}
