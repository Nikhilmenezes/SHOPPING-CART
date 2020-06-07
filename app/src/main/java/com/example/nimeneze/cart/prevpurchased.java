package com.example.nimeneze.cart;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.nimeneze.cart.m_MySQL.Downloader;

public class prevpurchased extends AppCompatActivity {
    String IP,CUSTNAME;
    Context ctx=this;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prevpurchased);

        Bundle extras = getIntent().getExtras();
        IP = extras.getString("IP");
        CUSTNAME = extras.getString("CUSTNAME");
        this.setTitle("PREVIOUS PURCHASE");

        lv = (ListView) findViewById(R.id.lvpp);
        new Downloader(this,"http://"+IP+"/prevpur.php?cname="+CUSTNAME,lv).execute();
    }
}
