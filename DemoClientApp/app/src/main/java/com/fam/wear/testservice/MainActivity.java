package com.fam.wear.testservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fam.wear.quick.QWear;

public class MainActivity extends AppCompatActivity {
    QWear qWear;
    final String peerPkgName = "com.fam.first.harmony.demoharmonylite";
    final String peerFingerPrint = "com.fam.first.harmony.demoharmonylite_BP65WiBU/83PZp3xtNsDv+b3JV7njJxvGyHWgInzcovsjHmNY5QnaGiQ9yUCxdOd0gyPJmtr1dHgWeMC7fWS8oE=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWear();
    }

    public void initWear() {
        qWear = new QWear();
        qWear.init(this, new QWear.QWearInitListener() {
            @Override
            public void onInit(boolean result) {
                System.out.println("---init: "+result);
                if (result){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            qWear.setWearConfig(peerPkgName, peerFingerPrint);
                            qWear.startCommunication();
                        }
                    },1500);
                }
            }
        });
    }

    public void onDataUpdate(View view){
        EditText edit = findViewById(R.id.id_text_name);
        qWear.sendData(edit.getText().toString());

    }





}