package com.example.nimeneze.cart.m_MySQL;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by NIKHIL on 12/22/2016.
 */

public class Connector {
    public static HttpURLConnection connect(String urlAddress){
        try{
            URL url =new URL(urlAddress);
            HttpURLConnection con=(HttpURLConnection) url.openConnection();
            //con.setRequestMethod("POST");
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);
            con.setDoInput(true);


            return con;
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return  null;
    }
}
