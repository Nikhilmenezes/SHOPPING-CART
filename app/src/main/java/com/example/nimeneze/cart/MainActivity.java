package com.example.nimeneze.cart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    //Text used to enter IP address
    EditText ET;

    //defining layout to load activity_main.xml from res folder and IP text field
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ET = findViewById(R.id.editText3);
    }


    //function after enter button pressed
    //intent is called to move from IP address page(MainActivity.java) to login page(Main2 activity.java)
    public void sendLogin(View v){
        Intent i = new Intent(MainActivity.this,Main2Activity.class);

        //take string entered in IP text field
        String ipadd=ET.getText().toString();

        //if IP text field is empty, a message is displayed.
        if(ipadd.isEmpty()){
            Toast.makeText(this,"Enter address in text field",Toast.LENGTH_SHORT).show();
            ET.requestFocus();
        }
        else{
            //send ip adress also to next page. start activity will go to next page.
            i.putExtra("IP",ipadd);
            startActivity(i);
        }

    }
}
