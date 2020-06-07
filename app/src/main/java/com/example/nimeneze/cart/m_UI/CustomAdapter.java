package com.example.nimeneze.cart.m_UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nimeneze.cart.R;
import com.example.nimeneze.cart.m_DataObject.SpaceCraft;

import java.util.ArrayList;



/**
 * Created by NIKHIL on 12/22/2016.
 */

public class CustomAdapter extends BaseAdapter {
    Context c;
    ArrayList<SpaceCraft> spacecrafts;

    LayoutInflater inflater;

    public CustomAdapter(Context c, ArrayList<SpaceCraft> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;

        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return spacecrafts.size();
    }

    @Override
    public Object getItem(int position) {
        return spacecrafts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.model,parent,false);

        }
        TextView nametxt= (TextView) convertView.findViewById(R.id.nameTxt);
        TextView pricetxt=(TextView) convertView.findViewById(R.id.priceTxt);
        TextView qntytxt=(TextView) convertView.findViewById(R.id.qntyTxt);
        TextView weighttxt=(TextView) convertView.findViewById(R.id.weightTxt);

        //BIND DATA
        SpaceCraft spacecraft=spacecrafts.get(position);
        nametxt.setText(spacecraft.getName());
        pricetxt.setText("PRICE: "+spacecraft.getPrice());
        qntytxt.setText("QUANTITY: "+spacecraft.getQuantity());
        weighttxt.setText("WEIGHT(gm): "+spacecraft.getWeight());

        return convertView;
    }
}
