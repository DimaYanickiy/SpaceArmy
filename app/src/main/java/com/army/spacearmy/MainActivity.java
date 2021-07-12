package com.army.spacearmy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.onesignal.OneSignal;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView buttonPlay, buttonNewGame, buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlay = (TextView) findViewById(R.id.buttonPlay);
        buttonNewGame = (TextView) findViewById(R.id.buttonNewGame);
        buttonExit = (TextView) findViewById(R.id.buttonExit);

        buttonPlay.setOnClickListener(this);
        buttonNewGame.setOnClickListener(this);
        buttonExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PlayActivity.class);
        switch (v.getId()){
            case R.id.buttonPlay:
                intent.putExtra("newGame", 0);
                startActivity(intent);
                break;
            case R.id.buttonNewGame:
                intent.putExtra("newGame", 1);
                startActivity(intent);
                break;
            case R.id.buttonExit:
                createDialog();
                break;
        }
    }

    public void createDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Do you realy want exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> MainActivity.super.onBackPressed()).create().show();
    }
}