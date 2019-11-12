package com.example.admin.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Boolean flag=false;
    String uName="",pass="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button SubmitBtn = (Button)findViewById(R.id.SubmitBtn);
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText1=(EditText) findViewById(R.id.editText);
                EditText editText2=(EditText) findViewById(R.id.editText2);
                uName=editText1.toString();
                pass=editText2.toString();
                Intent gotoMap=new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(gotoMap);
            }
        });
        if(!uName.equals(""))
        {
            Intent gotoMap=new Intent(getApplicationContext(),MapsActivity.class);
            startActivity(gotoMap);
        }
    }
}
