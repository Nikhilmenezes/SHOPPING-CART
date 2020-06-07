package com.example.nimeneze.cart;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scan extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    String IP, CUSTNAME;
    SurfaceView surfaceView;
    TextView txtBarcodeValue,note,exp;
    Button btnAction;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    Context ctx = this;
    ArrayList<String> arrayitem;
    ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Bundle extras = getIntent().getExtras();
        IP = extras.getString("IP");
        CUSTNAME = extras.getString("CUSTNAME");

        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);
        arrayitem = new ArrayList<String>();

    }


//intialize camera and barcode scanning

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanning started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(Scan.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(Scan.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }


            //stop the camera if back pressed
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        //function to perform if barcode detected.
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), " barcode scanning stopped", Toast.LENGTH_SHORT).show();
            }

            String barcodeData = "";
            Long scanTime = 0L;

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {


                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodeData.equals("") && scanTime.equals(0L)) {
                                barcodeData = barcodes.valueAt(0).displayValue;
                                scanTime = System.currentTimeMillis();
                                intentData = barcodes.valueAt(0).displayValue;
                                showdailog(intentData);
                            } else if (!barcodeData.equals(barcodes.valueAt(0).displayValue)) {
                                barcodeData = barcodes.valueAt(0).displayValue;
                                scanTime = System.currentTimeMillis();
                                intentData = barcodes.valueAt(0).displayValue;
                                showdailog(intentData);

                            } else if (barcodeData.equals(barcodes.valueAt(0).displayValue) && scanTime > (System.currentTimeMillis() - 2500)) {
                                //Do Nothing
                            } else {
                                barcodeData = "";
                                scanTime = 0L;

                            }

                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        //cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Toast.makeText(getApplicationContext(), newVal, Toast.LENGTH_SHORT).show();

    }


    //function used to select quantity.  (Number Picker and Dailog API is used)
    //Dailog is the popup
    public void showdailog(final String itm) {
        final Dialog d = new Dialog(Scan.this);
        d.setTitle("Number of Quantities?");
        d.setContentView(R.layout.popup_window);
        final String[] tempitem = itm.split(":");
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        TextView qnty = (TextView) d.findViewById(R.id.textView);
        note = (TextView) d.findViewById(R.id.note);
        exp = (TextView) d.findViewById(R.id.exp);


        if (tempitem.length == 4) {
            qnty.setText("Select Quantity for : " + tempitem[1] + " of Price : " + tempitem[2]);
            BackGroundA ba= new BackGroundA();
            ba.execute(tempitem[1]);
            String ExpDate = tempitem[3];
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
            Date date=new Date();
            try {
                Date d1 = sdformat.parse(ExpDate);
                if( d1.compareTo(date) < 0){
                    exp.setText("ALERT: "+tempitem[1]+" item is expired on "+ExpDate);
                }
                else{
                    Toast.makeText(ctx, tempitem[1]+" item will get expired on "+ExpDate, Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(100);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        //np.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (tempitem.length == 4) {
                    Integer i1= Integer.parseInt(tempitem[0])*np.getValue();
                    arrayitem.add(String.valueOf(i1));
                    arrayitem.add(tempitem[1]);
                    Integer i2= Integer.parseInt(tempitem[2])*np.getValue();
                    arrayitem.add(String.valueOf(i2));
                    arrayitem.add(String.valueOf(np.getValue()));
                    arrayitem.add(CUSTNAME);

                } else {
                    Toast.makeText(ctx, "Incorrect Barcode Format", Toast.LENGTH_SHORT).show();
                }
                onResume();
                d.dismiss();


            }
        });
        b2.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                onResume();
            }
        }));
        d.show();


    }


    public void addtocart(View v) {
        if (arrayitem.size() > 0) {
            BackGround bg = new BackGround();
            bg.execute();
        } else {
            Toast.makeText(ctx, "Please scan to add item to the cart", Toast.LENGTH_LONG).show();
        }
    }


    class BackGround extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(ctx);
            pd.setTitle("Inserting SCANNED ITEMS TO CART");
            pd.setMessage("inserting........");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String data="";
            int tmp;

            try {

                String urlParams="";


                for(int i=0;i<arrayitem.size();i+=5){
                    urlParams+="weight[]="+arrayitem.get(i)+
                            "&itemname[]="+arrayitem.get(i+1)+
                            "&price[]="+arrayitem.get(i+2)+
                            "&qnty[]="+arrayitem.get(i+3)+
                            "&customername[]="+arrayitem.get(i+4)+"&";
                }

                URL url = new URL("http://"+IP+"/additem.php?"+urlParams);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //httpURLConnection.setRequestMethod("POST");
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
                s="ITEM ADDED TO CART";
                finish();
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
                String URL="http://"+IP+"/recommend.php";
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
            if(s.equals("")){
                note.setText("NO RECOMMANDATION");
            }
            else {
                String ba = s.replaceAll("[^a-zA-Z0-9]", "");
                note.setText("RECOMMANDATION: " + ba + " is also available");
            }

        }
    }
}






